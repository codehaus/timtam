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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.atlassian.confluence.remote.RemotePage;
import com.atlassian.confluence.remote.RemotePageSummary;

/**
 * @author zohar melamed
 *  
 */
public class PageAdapter implements IEditorInput, ConfluencePage ,TreeAdapter {
	private PageContainer conatiner;
	private RemotePageSummary pageSummary;
	private PageAdapter parent;
	private RemotePage page;
	private boolean dirty;
	private boolean homePage;
	private ConfluenceService service;
	private SpaceAdapter space;
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
		
		conatiner = new PageContainer(service,spaceAdapter,this);
		
		this.pageSummary = pageSummary;
		this.service = service;

		List childPages = space.getChildren(pageSummary.id);
		for (Iterator iter = childPages.iterator(); iter.hasNext();) {
			RemotePageSummary summary = (RemotePageSummary) iter.next();
			conatiner.addPage(summary);
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

	public RemotePage getPage() {
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
		return getPage().title;
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

	public String getContent() {
		return getPage().content;
	}

	public Date getCreated() {
		return getPage().created;
	}

	public String getCreator() {
		return getPage().creator;
	}

	public Date getModified() {
		return getPage().modified;
	}

	public String getModifier() {
		return getPage().modifier;
	}

	public int getVersion() {
		return getPage().version;
	}

	public boolean isHomePage() {
		return homePage;
	}

	public void save() {
		page = service.storePage(page);
		dirty = false;
	}

	public void refresh() {
		page = null;
		dirty = false;
	}

	public void setContent(String content) {
		getPage().content = content;
	}

	public void setDirty() {
		dirty = true;
	}

	public void setHomePage(boolean homePage) {
		getPage().homePage = homePage;
	}

	public String getTitle() {
		return getName();
	}

	public void setTitle(String title) {
		getPage().title = title;
	}

	public long getId() {
		return getPage().id;
	}

	public long getParentId() {
		return getPage().parentId;
	}

	public String getSpace() {
		return getPage().space;
	}

	public String getUrl() {
		return getPage().url;
	}

	public String renderContent() {
		RemotePage myPage = getPage();
		return service.renderContent(myPage.space, myPage.id, myPage.content);
	}

	public String renderContent(String content) {
		RemotePage myPage = getPage();
		return service.renderContent(myPage.space, myPage.id, content);
	}

	public Object createChild(String name) {
		return conatiner.createPage(name);
	}

	public Integer getType() {
		return PAGE;
	}

	public void refresh(IProgressMonitor monitor) {
		monitor.subTask("Refreshong Page " + pageSummary.title);
		refresh();
	}

	public void rename(String name) {
		String oldName = pageSummary.title;
		try {
			page.title = name;
			page = service.storePage(page);
		} catch (RuntimeException e) {
			page.title = oldName;
			throw e;
		}
	}

	public void delete() {
		// del bottom up
		conatiner.deleteAll();
		service.deletePage(getPage().id);
		if (parent != null) {
			parent.removeChild(this);
		} else {
			space.removePage(this);
		}
	}
	
	private void removeChild(PageAdapter adapter) {
		conatiner.removePage(adapter);
	}

	public boolean readOnly() {
		return space.isReadOnly();
	}

	public Object[] getChildren() {
		return conatiner.getChildren();
	}

	public boolean hasChildren() {
		return conatiner.hasChildren();
	}
}

