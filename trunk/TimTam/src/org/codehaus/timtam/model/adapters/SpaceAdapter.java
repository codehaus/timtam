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
import org.codehaus.timtam.util.GUIUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.atlassian.confluence.remote.RemotePageSummary;
import com.atlassian.confluence.remote.RemoteSpace;
import com.atlassian.confluence.remote.RemoteSpaceSummary;
/**
 * @author zohar melamed
 *  
 */
public class SpaceAdapter implements ConfluenceSpace, TreeAdapter {
	private RemoteSpaceSummary spaceSummary;
	private RemoteSpace space;
	private ServerAdapter parent;
	private PageContainer container;
	private Map pages = new HashMap();
	private ConfluenceService service;
	private boolean spaceOk = true;
	private boolean pagesLoaded;
	private boolean readOnly = true;
	/**
	 * @param spaceSummary
	 */
	public SpaceAdapter(RemoteSpaceSummary spaceSummary, ServerAdapter parent,
			ConfluenceService service) {
		this.service = service;
		this.parent = parent;
		this.spaceSummary = spaceSummary;
		container = new PageContainer(service, this, null);
		String[] permissions = service.getPermissions(spaceSummary.key);
		for (int i = 0; i < permissions.length; i++) {
			String permission = permissions[i];
			if (permission.equals("modify")) {
				readOnly = false;
			}
		}
	}
	public void refresh(IProgressMonitor monitor) {
		pages.clear();
		container.clear();
		spaceOk = false;
		monitor.setTaskName("Loading Space " + spaceSummary.name);
		space = service.getSpace(spaceSummary.key);
		monitor.subTask("Getting Pages ...");
		try {
			buildPageMap(service.getPages(spaceSummary.key), monitor);
			List rootPages = (List) pages.get(new Long(0));
			for (Iterator iter = rootPages.iterator(); iter.hasNext();) {
				container.addPage((RemotePageSummary) iter.next());
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
			List pageList = (List) pages.get(key);
			if (pageList == null) {
				pageList = new ArrayList();
				pages.put(key, pageList);
			}
			pageList.add(summary);
		}
	}
	List getChildren(long parentId) {
		List childrenList = (List) pages.get(new Long(parentId));
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
		return container.createPage(name);
	}
	public void removePage(PageAdapter adapter) {
		container.removePage(adapter);
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public boolean isHealty() {
		return spaceOk;
	}
	public Object[] getChildren() {
		if (!pagesLoaded) {
			IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					refresh(monitor);
				}
			};
			GUIUtil.runOperationWithProgress(op, null);
		}
		return container.getChildren();
	}
	public boolean hasChildren() {
		if(!pagesLoaded && spaceOk){
			return true;
		}
		
		return container.hasChildren();
	}
}