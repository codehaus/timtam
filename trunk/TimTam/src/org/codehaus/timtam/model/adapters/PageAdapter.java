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

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.codehaus.timtam.TimTamPlugin;
import org.codehaus.timtam.model.ConfluencePage;
import org.codehaus.timtam.model.ConfluenceService;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.atlassian.confluence.remote.RemotePage;
import com.atlassian.confluence.remote.RemotePageSummary;

/**
 * @author zohar melamed
 *  
 */
public class PageAdapter extends TreeAdapter implements IEditorInput, ConfluencePage {

	private static Image pageIcon;
	private static Image homePageIcon;
	private static ImageDescriptor pageIconDescriptor;
	private RemotePageSummary pageSummary;
	private PageAdapter parent;
	private boolean spaceHome;
	private RemotePage page;
	private boolean dirty;
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

		this.pageSummary = pageSummary;
		this.service = service;
		spaceHome = space.getHomepageId() == pageSummary.id;

		if (pageIcon == null) {
			pageIcon = TimTamPlugin.getInstance().getImageRegistry().get(TimTamPlugin.IMG_PAGE);
			homePageIcon = TimTamPlugin.getInstance().getImageRegistry().get(TimTamPlugin.IMG_SPACEHOME);
			pageIconDescriptor = TimTamPlugin.getInstance().loadImageDescriptor(TimTamPlugin.IMG_PAGE);
		}

		List childPages = space.getChildren(pageSummary.id);
		children = new Object[childPages.size()];
		int i = 0;
		for (Iterator iter = childPages.iterator(); iter.hasNext();) {
			RemotePageSummary summary = (RemotePageSummary) iter.next();
			children[i] = new PageAdapter(summary, space, this, service);
			++i;
		}
	}
	public Image getImage() {
		return spaceHome ? homePageIcon : pageIcon;
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
		return pageIconDescriptor;
	}

	public String getName() {
		return getPage().title;
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
		return getPage().homePage;
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
		return getPage().title;
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

	public Object createChild(String name) {
		RemotePage newPage = new RemotePage();
		newPage.content = newPageContent();
		newPage.space = getPage().space;
		newPage.title = name;
		newPage.parentId = getPage().id;
		newPage = service.storePage(newPage);

		Object temp[] = new Object[children.length + 1];
		System.arraycopy(children, 0, temp, 0, children.length);
		PageAdapter pageAdapter = new PageAdapter(newPage, space, this, service);
		temp[children.length] = pageAdapter;
		children = temp;

		return pageAdapter;
	}

	private String newPageContent() {
		return "Created by [~"
			+ service.getUser()
			+ "]\\\\On "
			+ Calendar.getInstance().getTime()
			+ "\\\\Using {color:blue}TimTam{color}";
	}

	public Integer getType() {
		return PAGE;
	}

	public void refresh(IProgressMonitor monitor) {
		monitor.subTask("Refreshong Page " + pageSummary.title);
		refresh();
	}

	public void rename(String name) {
		String oldName = page.title;
		try {
			page.title = name;
			page = service.storePage(page);
		} catch (RuntimeException e) {
			page.title = oldName;
			throw e;
		}
	}

	public void delete() {
		service.deletePage(getPage().id);
		if (parent != null) {
			parent.removeChild(this);
		} else {
			space.removePage(this);
		}
		// recurse 
		for (int i = 0; i < children.length; i++) {
			PageAdapter child = (PageAdapter) children[i];
			child.delete();
		}
	}
	/**
	 * @param adapter
	 */
	private void removeChild(PageAdapter adapter) {
		Object temp[] = new Object[children.length - 1];
		int ti = 0;
		for (int i = 0; i < children.length; i++) {
			PageAdapter pageAdapter = (PageAdapter) children[i];
			if (pageAdapter != adapter) {
				temp[ti++] = pageAdapter;
			}
			children[i] = null;
		}

		children = temp;
	}
	
}

