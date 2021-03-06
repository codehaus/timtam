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
import java.util.Calendar;
import java.util.Collection;

import org.codehaus.timtam.model.ConfluenceService;
import org.codehaus.timtam.model.PageContainer;
import org.codehaus.timtam.util.GUIUtil;
import org.eclipse.core.runtime.IProgressMonitor;

import com.atlassian.confluence.remote.NotPermittedException;
import com.atlassian.confluence.remote.RemoteException;
import com.atlassian.confluence.remote.RemotePage;
import com.atlassian.confluence.remote.RemotePageSummary;
/**
 * @author Zohar Melamed
 *  
 */
class PageContainerImpl implements PageContainer {
	private ConfluenceService service;
	private Collection pages = new ArrayList();
	private SpaceAdapter spaceAdapter;
	private PageAdapter pageAdapter;
	public PageContainerImpl(ConfluenceService service, SpaceAdapter spaceAdapter, PageAdapter pageAdapter) {
		super();
		this.service = service;
		this.spaceAdapter = spaceAdapter;
		this.pageAdapter = pageAdapter;
	}
	public boolean hasPages() {
		return !pages.isEmpty();
	}
	/**
	 * @return
	 */
	public Object[] getPages() {
		return pages.toArray();
	}
	/**
	 * @param adapter
	 */
	public void removePage(PageAdapter adapter) {
		pages.remove(adapter);
	}
	private PageAdapter createPage(RemotePage newPage) throws NotPermittedException, RemoteException {
		newPage = service.storePage(newPage);
		PageAdapter adapter = new PageAdapter(newPage, spaceAdapter, pageAdapter, service);
		pages.add(adapter);
		return adapter;
	}
	/**
	 * @param name
	 * @return
	 * @throws RemoteException
	 * @throws NotPermittedException
	 */
	public Object createPage(String name) throws NotPermittedException, RemoteException {
		RemotePage newPage = populatePageInfo(name);
		return createPage(newPage);
	}
	/**
	 * @param name
	 * @return
	 * @throws RemoteException
	 */
	private RemotePage populatePageInfo(String name) throws RemoteException {
		RemotePage newPage = new RemotePage();
		newPage.content = newPageContent();
		newPage.space = spaceAdapter.getSpaceKey();
		newPage.title = name;
		if (pageAdapter != null) {
			newPage.parentId = pageAdapter.getId();
		}
		return newPage;
	}
	private String newPageContent() {
		return "Created by [~" + service.getUser() + "]\n On " + Calendar.getInstance().getTime()
				+ "\nUsing {color:blue}TimTam{color}";
	}
	/**
	 * @param summary
	 */
	public void addPage(RemotePageSummary summary) {
		pages.add(new PageAdapter(summary, spaceAdapter, pageAdapter, service));
	}
	public void deleteAll() throws NotPermittedException, RemoteException {
		// we make a stable copy as the recursive delete modifies the parent
		// page container ( us )
		Object[] pagesCopy = pages.toArray();
		for (int i = 0; i < pagesCopy.length; ++i) {
			PageAdapter page = (PageAdapter) pagesCopy[i];
			page.delete();
		}
	}
	/**
	 *  
	 */
	public void clear() {
		pages.clear();
	}
	public boolean isReadOnly() {
		return pageAdapter == null ? spaceAdapter.isReadOnly() : pageAdapter.isReadOnly();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.codehaus.timtam.model.PageContainer#transferPages(java.lang.Object[],
	 *      boolean, org.eclipse.core.runtime.IProgressMonitor) No Copy support
	 *      at the moment...
	 */
	public void transferPages(Object[] pagesToMove, boolean move, IProgressMonitor monitor) throws RemoteException {
	    monitor.beginTask("Transfering Pages ", pagesToMove.length);
		for (int i = 0; i < pagesToMove.length; i++) {
			PageAdapter pageToMove = (PageAdapter) pagesToMove[i];
			monitor.setTaskName("Moving " + pageToMove.getName());
			
			if (!pageToMove.space.equals(spaceAdapter)) {
				GUIUtil.safeWarn("Invalid Page Tranfer", "Pages Can Only Be Moved Within a Space");
				return;
			}

			pageToMove.reparent(pageAdapter);
			pages.add(pageToMove);

			monitor.worked(1);
		}
		monitor.done();
	}
}