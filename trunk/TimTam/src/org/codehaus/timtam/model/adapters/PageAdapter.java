/*
*
* Copyright 2003(c)  Zohar Melamed
* All rights reserved.
*

 Redistribution and use of this software and associated documentation
 ("Software"), with or without modification, are permitted provided
 that the following conditions are met:

 1. Redistributions of source code must retain copyright
    statements and notices.  Redistributions must also contain a
    copy of this document.

 2. Redistributions in binary form must reproduce the
    above copyright notice, this list of conditions and the
    following disclaimer in the documentation and/or other
    materials provided with the distribution.

 3. Due credit should be given to The Codehaus and Contributors
    http://timtam.codehaus.org/

 THIS SOFTWARE IS PROVIDED BY THE CODEHAUS AND CONTRIBUTORS
 ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 THE CODEHAUS OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 OF THE POSSIBILITY OF SUCH DAMAGE.

*
*
*/

package org.codehaus.timtam.model.adapters;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.codehaus.timtam.TimTamPlugin;
import org.codehaus.timtam.model.ConfluencePage;
import org.codehaus.timtam.model.ConfluenceService;
import org.codehaus.timtam.model.PageContainer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.atlassian.confluence.remote.NotPermittedException;
import com.atlassian.confluence.remote.RemoteException;
import com.atlassian.confluence.remote.RemotePage;
import com.atlassian.confluence.remote.RemotePageHistory;
import com.atlassian.confluence.remote.RemotePageSummary;

/**
 * @author zohar melamed
 *  
 */
public class PageAdapter implements IEditorInput, ConfluencePage ,TreeAdapter,PageContainer {
	PageContainerImpl childPages;
	RemotePageSummary pageSummary;
	PageAdapter parent;
	private RemotePage page;
	private boolean dirty;
	private boolean homePage;
	private ConfluenceService service;
	SpaceAdapter space;
	private RemotePageHistory[] history;
	
	
	/**
	 * @param spaceSummary
	 */
	public PageAdapter(
		RemotePageSummary pageSummary,
		SpaceAdapter spaceAdapter,
		PageAdapter parentPage,
		ConfluenceService service) {
		
		space = spaceAdapter;
		parent = parentPage;
		homePage = (pageSummary.id == spaceAdapter.getHomepageId()); 
		
		childPages = new PageContainerImpl(service,spaceAdapter,this);
		
		this.pageSummary = pageSummary;
		this.service = service;

		List pages = space.getChildren(pageSummary.id);
		for (Iterator iter = pages.iterator(); iter.hasNext();) {
			RemotePageSummary summary = (RemotePageSummary) iter.next();
			childPages.addPage(summary);
		}
	}

	
	public Image getImage() {
		return TimTamPlugin.getInstance().getPageIcon(this);
	}

	public Object getParent() {
		return parent;
	}

	public String getText() {
		return pageSummary.title;
	}

	public RemotePage getPage() throws RemoteException {
		if (page == null) {
			page = service.getPage(pageSummary.id);
		}
		return page;
	}

	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		final Image image = TimTamPlugin.getInstance().getPageIcon(this);
		return new ImageDescriptor(){
			public ImageData getImageData() {
				return image.getImageData();
			}
		};
	}

	public String getName() {
		return pageSummary.title;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		StringBuffer buffer = new StringBuffer();
		
		try {
            buffer.append(getTitle()).append("\nVersion : ").append(getVersion());
            buffer.append("\nLast modified by ").append(getModifier()).append(" - On ").append(getModified());
            buffer.append("\nCreated by ").append(getCreator()).append(" On ").append(getCreated());
        } catch (RemoteException e) {
            TimTamPlugin.getInstance().logException("failed to get page",e, true);
        }
        
		return buffer.toString();
	}

	public Object getAdapter(Class adapter) {
		if (adapter == ConfluencePage.class)
			return this;

		return null;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public String getContent() throws RemoteException {
		return getPage().content;
	}

	public Date getCreated() throws RemoteException {
		return getPage().created;
	}

	public String getCreator() throws RemoteException {
		return getPage().creator;
	}

	public Date getModified() throws RemoteException {
		return getPage().modified;
	}

	public String getModifier() throws RemoteException {
		return getPage().modifier;
	}

	public int getVersion() throws RemoteException {
		return getPage().version;
	}

	public boolean isHomePage() {
		return homePage;
	}

	public void save() throws NotPermittedException, RemoteException {
		page = service.storePage(page);
		pageSummary = page;
		history = null;
		dirty = false;
	}

	public void refresh() {
		page = null;
		dirty = false;
		history = null;
	}

	public void setContent(String content) throws RemoteException {
		getPage().content = content;
	}

	public void setDirty() {
		dirty = true;
	}

	public void setHomePage(boolean homePage) throws RemoteException {
		getPage().homePage = homePage;
	}

	public String getTitle() {
		return getName();
	}

	public void setTitle(String title) throws RemoteException {
		getPage().title = title;
	}

	public long getId() throws RemoteException {
		return getPage().id;
	}

	public long getParentId() throws RemoteException {
		return getPage().parentId;
	}

	public String getSpace(){
		return space.getSpaceKey();
	}

	public String getUrl() throws RemoteException {
		return getPage().url;
	}

	public String renderContent() throws RemoteException {
		RemotePage myPage = getPage();
		return service.renderContent(myPage.space, myPage.id, myPage.content);
	}

	public String renderContent(String content) throws RemoteException {
		RemotePage myPage = getPage();
		return service.renderContent(myPage.space, myPage.id, content);
	}

	public Object createPage(String name) throws NotPermittedException, RemoteException {
		return childPages.createPage(name);
	}

	public Integer getType() {
		return PAGE;
	}

	public void refresh(IProgressMonitor monitor) {
		monitor.subTask("Refreshong Page " + pageSummary.title);
		refresh();
	}

	public void rename(String name) throws NotPermittedException, RemoteException {
		String oldName = pageSummary.title;
		try {
			page.title = name;
			page = service.storePage(page);
		} catch (RuntimeException e) {
			page.title = oldName;
			throw e;
		}
	}

	public void delete() throws NotPermittedException, RemoteException {
		// del bottom up
		childPages.deleteAll();
		service.deletePage(getPage().id);
		if (parent != null) {
			parent.removePage(this);
		} else {
			space.removePage(this);
		}
	}
	
	public void removePage(PageAdapter adapter) {
		childPages.removePage(adapter);
	}

	public boolean isReadOnly() {
		return space.isReadOnly();
	}

	public Object[] getPages() {
		return childPages.getPages();
	}

	public boolean hasPages() {
		return childPages.hasPages();
	}
	public Object[] getChildren() {
		return getPages();
	}

	public boolean hasChildren() {
		return hasPages();
	}


	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.PageContainer#transferPages(java.lang.Object[], boolean, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void transferPages(Object[] pagesToCopy, boolean move, IProgressMonitor monitor) throws RemoteException {
		childPages.transferPages(pagesToCopy, move, monitor);
	}


	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.ConfluencePage#getPageHistory()
	 */
	public RemotePageHistory[] getPageHistory() throws RemoteException {
		if(history == null){
			history  = service.getPageHistory(getId());
		}
		return history;
	}
	

	public void reparent(PageAdapter newParent) throws NotPermittedException, RemoteException{
	    // first change confluence
	    // set new parent and save refelcting  
        if (newParent!= null) {
        	System.out.println("moving page "+getName()+" from parent "+getPage().parentId +" to parent "+newParent.getId());
        	getPage().parentId = newParent.getId();
        } else {
        	getPage().parentId = 0;
        }
        
        save();
        // now update our local model
        // adjust tree model to reflect change
        if (parent != null) {
        	parent.removePage(this);
        } 
        // reparent the moved page
        parent = newParent;
	}
}

