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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.timtam.TimTamPlugin;
import org.codehaus.timtam.model.ConfluenceService;
import org.codehaus.timtam.model.ConfluenceSpace;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.graphics.Image;
import com.atlassian.confluence.remote.RemotePage;
import com.atlassian.confluence.remote.RemotePageSummary;
import com.atlassian.confluence.remote.RemoteSpace;
import com.atlassian.confluence.remote.RemoteSpaceSummary;
/**
 * @author zohar melamed
 *  
 */
public class SpaceAdapter extends TreeAdapter implements ConfluenceSpace {
	private static Image spaceIcon;
	private static Image brokenSpaceIcon;
	private RemoteSpaceSummary spaceSummary;
	private RemoteSpace space;
	private ServerAdapter parent;
	private Map pages = new HashMap();
	private ConfluenceService service;
	private boolean loadedOk;
	/**
	 * @param spaceSummary
	 */
	public SpaceAdapter(RemoteSpaceSummary spaceSummary, ServerAdapter parent, ConfluenceService service) {
		this.service = service;
		this.parent = parent;
		this.spaceSummary = spaceSummary;
		if (spaceIcon == null) {
			spaceIcon = TimTamPlugin.getInstance().getImageRegistry().get(TimTamPlugin.IMG_SPACE);
		}
		
		if (brokenSpaceIcon == null) {
			brokenSpaceIcon = TimTamPlugin.getInstance().getImageRegistry().get(TimTamPlugin.IMG_BROKEN_SPACE);
		}
		
	}
	public void refresh(IProgressMonitor monitor) {
		
		pages.clear();
		loadedOk = false;
		monitor.setTaskName("Loading Space " + spaceSummary.name);
		space = service.getSpace(spaceSummary.key);
		monitor.subTask("Getting Pages ...");
		try {
			buildPageMap(service.getPages(spaceSummary.key), monitor);
			List rootPages = (List) pages.get(new Long(0));
			children = new Object[rootPages.size()];
			int i = 0;
			for (Iterator iter = rootPages.iterator(); iter.hasNext();) {
				RemotePageSummary summary = (RemotePageSummary) iter.next();
				children[i] = new PageAdapter(summary, this, null, service);
				++i;
			}
			loadedOk = true;
		} catch (Exception e) {
			ErrorDialog.openError(null, "Error Loading Space : "+spaceSummary.name,
					e.getMessage(), new Status(IStatus.WARNING,"TimTam", IStatus.OK, "Error getting pages", e));
		}
	}
	/**
	 * @param summaries
	 */
	private void buildPageMap(RemotePageSummary[] summaries, IProgressMonitor monitor) {
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
		return loadedOk ? spaceIcon:brokenSpaceIcon;
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
	public Integer getType() {
		return SPACE;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.codehaus.timtam.model.ConfluenceSpace#addPage(java.lang.String)
	 */
	public Object addPage(String name) {
		RemotePage newPage = new RemotePage();
		newPage.content = newPageContent();
		newPage.space = space.key;
		newPage.title = name;
		newPage = service.storePage(newPage);
		Object temp[] = new Object[children.length + 1];
		System.arraycopy(children, 0, temp, 0, children.length);
		PageAdapter adapter = new PageAdapter(newPage, this, null, service);
		temp[children.length] = adapter;
		children = temp;
		return adapter;
	}
	private String newPageContent() {
		// TODO Auto-generated method stub
		return "Created by [~" + service.getUser() + "]\\\\On " + Calendar.getInstance().getTime()
				+ "\\\\Using {color:blue}TimTam{color}";
	}
	/**
	 * @param adapter
	 */
	public void removePage(PageAdapter adapter) {
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
