/*
 * Created on Oct 23, 2004
 *
 * 
 * Copyright (c) Zohar Melamed All rights reserved.
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

package org.codehaus.timtam.model;

import com.atlassian.confluence.remote.NotPermittedException;
import com.atlassian.confluence.remote.RemoteAttachment;
import com.atlassian.confluence.remote.RemoteBlogEntry;
import com.atlassian.confluence.remote.RemoteBlogEntrySummary;
import com.atlassian.confluence.remote.RemoteException;
import com.atlassian.confluence.remote.RemotePage;
import com.atlassian.confluence.remote.RemotePageHistory;
import com.atlassian.confluence.remote.RemotePageSummary;
import com.atlassian.confluence.remote.RemoteSearchResult;
import com.atlassian.confluence.remote.RemoteSpace;
import com.atlassian.confluence.remote.RemoteSpaceSummary;

/**
 * @author Sarah
 *
 */
public interface ConfluenceService {
	/**
	 * @return Returns the user.
	 */
	String getUser();

	RemoteSearchResult[] search(String query, int maxResults)
			throws RemoteException;

	boolean logout();

	RemotePage getPage(long pageId) throws RemoteException;

	RemoteSpace getSpace(String spaceId) throws RemoteException;

	RemoteSpaceSummary[] getSpaces() throws RemoteException;

	RemotePageSummary[] getPages(String spaceId) throws RemoteException;

	RemotePageHistory[] getPageHistory(long pageId) throws RemoteException;

	RemotePage storePage(RemotePage page) throws NotPermittedException,
			RemoteException;

	String renderContent(String spaceId, long pageId, String content)
			throws RemoteException;

	Boolean deletePage(long pageId) throws NotPermittedException,
			RemoteException;

	String[] getPermissions(String spaceId) throws RemoteException;

	RemoteAttachment[] getAttachments(long pageId) throws RemoteException;

	RemoteBlogEntrySummary[] getBlogEntries(String spaceId)
			throws RemoteException;

	RemoteBlogEntry getBlogEntry(long blogEntryId);
}