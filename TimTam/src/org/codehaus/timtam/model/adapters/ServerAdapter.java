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
import java.util.Collection;

import org.codehaus.timtam.TimTamPlugin;
import org.codehaus.timtam.model.ConfluenceService;
import org.codehaus.timtam.model.SearchResult;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;

import com.atlassian.confluence.remote.RemoteException;
import com.atlassian.confluence.remote.RemoteSearchResult;
import com.atlassian.confluence.remote.RemoteSpaceSummary;

/**
 * @author zohar melamed
 *
 */
public class ServerAdapter implements TreeAdapter {
	private static Image icon;
	private ConfluenceService service;
	private Collection spaces = new ArrayList();
	private String serverUrl;
	private String userName; 

	public ServerAdapter(String serverUrl,String userName, ConfluenceService service) {
		this.serverUrl = serverUrl;
		this.userName = userName;
		this.service = service;
		if (icon == null) {
			icon = TimTamPlugin.getInstance().getImageRegistry().get(TimTamPlugin.IMG_SERVER);
		}
	}

	public void loadSpaces(IProgressMonitor monitor) {
		RemoteSpaceSummary[] spaceSummaries = null;
		try {
			monitor.setTaskName("Retrieving Spaces...");
			spaceSummaries = service.getSpaces();
			spaces.clear();
		} catch (Exception e) {
			TimTamPlugin.getInstance().logException("failed to get spaces", e, true);
			return;
		}
		monitor.beginTask("Loading "+spaceSummaries.length+" Spaces",spaceSummaries.length);
		for (int i = 0; i < spaceSummaries.length; i++) {
			
			RemoteSpaceSummary summary = spaceSummaries[i];
			monitor.setTaskName(summary.name);
			SpaceAdapter adapter = new SpaceAdapter(summary, this,service);
			monitor.internalWorked(1);
			spaces.add(adapter);
		}
		monitor.done();
	}
	
	public void refresh(IProgressMonitor monitor) throws RemoteException {
		RemoteSpaceSummary[] spaceSummaries = null;
		try {
			monitor.setTaskName("Retrieving  Spaces...");
			spaceSummaries = service.getSpaces();
			spaces.clear();
			monitor.beginTask("Loading "+spaceSummaries.length+" Spaces",spaceSummaries.length);
		} catch (Exception e) {
			TimTamPlugin.getInstance().logException("failed to get spaces", e, true);
			return;
		}

		for (int i = 0; i < spaceSummaries.length; i++) {
			RemoteSpaceSummary summary = spaceSummaries[i];
			SpaceAdapter adapter = new SpaceAdapter(summary, this,service);
			adapter.refresh(monitor);
			monitor.internalWorked(1);
			spaces.add(adapter);
		}
		monitor.done();
	}
	
	public Object getParent() {
		return null;
	}

	public Image getImage() {
		return icon;
	}

	public String getText() {
		return userName+"@"+serverUrl;
	}
	public Integer  getType() {
		return SERVER;
	}

	public Object[] getChildren() {
		return spaces.toArray();
	}

	public boolean hasChildren() {
		return !spaces.isEmpty();
	}

	/**
	 * @param searchTerm
	 * @throws RemoteException
	 */
	public SearchResult[] search(String query) throws RemoteException {
		RemoteSearchResult[] remoteResults = service.search(query,100);
		SearchResult[] results = new SearchResult[remoteResults.length];
		for (int i = 0; i < results.length; i++) {
			results[i] = new SearchResult(serverUrl,remoteResults[i]);
			
		}
		return results;
	}

	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.adapters.TreeAdapter#getUrl()
	 */
	public String getUrl() {
		return serverUrl;
	}
}