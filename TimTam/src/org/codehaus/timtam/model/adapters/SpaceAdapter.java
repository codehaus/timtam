/*
 * 
 * Copyright 2003(c) Zohar Melamed All rights reserved.
 * 
 * 
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 * 
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. Due credit should be given to The Codehaus and Contributors
 * http://timtam.codehaus.org/
 * 
 * THIS SOFTWARE IS PROVIDED BY THE CODEHAUS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE CODEHAUS OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * 
 *  
 */
package org.codehaus.timtam.model.adapters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.timtam.TimTamPlugin;
import org.codehaus.timtam.model.ConfluenceService;
import org.codehaus.timtam.model.ConfluenceSpace;
import org.codehaus.timtam.model.PageContainer;
import org.codehaus.timtam.util.GUIUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.atlassian.confluence.remote.RemotePageSummary;
import com.atlassian.confluence.remote.RemoteSpace;
import com.atlassian.confluence.remote.RemoteSpaceSummary;
/**
 * @author zohar melamed
 *  
 */
public class SpaceAdapter implements ConfluenceSpace, IEditorInput, TreeAdapter , PageContainer{
	private RemoteSpaceSummary spaceSummary;
	private RemoteSpace space;
	private ServerAdapter parent;
	private PageContainerImpl childPages;
	private Map parentToChildPageMap = new HashMap();
	private ConfluenceService service;
	private boolean spaceOk = true;
	private boolean pagesLoaded;
	private boolean readOnly;
	/**
	 * @param spaceSummary
	 */
	public SpaceAdapter(RemoteSpaceSummary spaceSummary, ServerAdapter parent,
			ConfluenceService service) {
		this.service = service;
		this.parent = parent;
		this.spaceSummary = spaceSummary;
		childPages = new PageContainerImpl(service, this, null);
	}
	
	public void refresh(IProgressMonitor monitor) {
		parentToChildPageMap.clear();
		childPages.clear();
		spaceOk = false;
		readOnly = true;
		space = service.getSpace(spaceSummary.key);
		monitor.setTaskName("Loading Space " + spaceSummary.name);
		String[] permissions = service.getPermissions(spaceSummary.key);
		for (int i = 0; i < permissions.length; i++) {
			String permission = permissions[i];
			if (permission.equals("modify")) {
				readOnly = false;
			}
		}
		
		space = service.getSpace(spaceSummary.key);
		monitor.subTask("Getting Pages ...");
		try {
			buildPageMap(service.getPages(spaceSummary.key), monitor);
			List rootPages = (List) parentToChildPageMap.get(new Long(0));
			for (Iterator iter = rootPages.iterator(); iter.hasNext();) {
				childPages.addPage((RemotePageSummary) iter.next());
			}
			spaceOk = pagesLoaded = true;
		} catch (final Exception e) {
			Display.getDefault().syncExec(new Runnable(){
				public  void run(){
					ErrorDialog.openError(null, "Error Loading Space : "
							+ spaceSummary.name, e.getMessage(), new Status(
							IStatus.WARNING, "TimTam", IStatus.OK,
							"Error getting pages", e));
				
				}
			});
		}
	}
	/**
	 * @param summaries
	 */
	private void buildPageMap(RemotePageSummary[] summaries,
			IProgressMonitor monitor) {
		monitor.subTask("Loaded " + summaries.length + " Page Summaries");
		for (int i = 0; i < summaries.length; i++) {
			RemotePageSummary summary = summaries[i];
			Long key = new Long(summary.parentId);
			List pageList = (List) parentToChildPageMap.get(key);
			if (pageList == null) {
				pageList = new ArrayList();
				parentToChildPageMap.put(key, pageList);
			}
			pageList.add(summary);
		}
	}
	List getChildren(long parentId) {
		List childrenList = (List) parentToChildPageMap.get(new Long(parentId));
		return childrenList == null ? Collections.EMPTY_LIST : childrenList;
	}
	public Image getImage() {
		return TimTamPlugin.getInstance().getSpaceIcon(this);
	}
	public Object getParent() {
		return parent;
	}
	public String getText() {
		return spaceSummary.name;
	}
	public long getHomepageId() {
		return space.homePage;
	}
	public String getSpaceKey() {
		return space.key;
	}
	public Integer getType() {
		return SPACE;
	}
	public Object createPage(String name) {
		return childPages.createPage(name);
	}
	public void removePage(PageAdapter adapter) {
		childPages.removePage(adapter);
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public boolean isHealty() {
		return spaceOk;
	}
	public Object[] getPages() {
		if (!pagesLoaded) {
			IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					refresh(monitor);
				}
			};
			GUIUtil.runOperationWithProgress(op, null);
		}
		return childPages.getPages();
	}
	public boolean hasPages() {
		if(!pagesLoaded && spaceOk){
			return true;
		}
		
		return childPages.hasPages();
	}
	
	public Object[] getChildren() {
		return getPages();
	}
	public boolean hasChildren() {
		return hasPages();
	}
	
	/**
	 * @param pagesToCopy
	 * @return
	 */
	public boolean copy(Object[] pagesToCopy, IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return false;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		if(arg0 instanceof SpaceAdapter){
			if(space == null){
				return super.equals(arg0);
			}
			SpaceAdapter otherSpace = (SpaceAdapter) arg0;
			if(otherSpace == null || otherSpace.space == null){
				return false;
			}
			return space.url.equals(otherSpace.space.url);
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.PageContainer#transferPages(java.lang.Object[], boolean, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void transferPages(Object[] pagesToCopy, boolean move, IProgressMonitor monitor) {
		childPages.transferPages(pagesToCopy, move, monitor);
	}
	
	
	///////// EditorInput 
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	public boolean exists() {
		return false;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		final Image image = TimTamPlugin.getInstance().getSpaceIcon(this);
		return new ImageDescriptor(){
			public ImageData getImageData() {
				return image.getImageData();
			}
		};
	}	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	public String getName() {
		return getText();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable() {
		return null;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		return getName();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (adapter == ConfluenceSpace.class)
			return this;
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.ConfluenceSpace#getDescription()
	 */
	public String getDescription() {
		return space.description;
	}

	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.ConfluenceSpace#getUrl()
	 */
	public String getUrl() {
		return space.url;
	}

	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.ConfluenceSpace#getKey()
	 */
	public String getKey() {
		return space.key;
	}
}