/*
 * Created on Oct 23, 2004
 *
 * 
 */
package org.codehaus.timtam.mocks;

import org.codehaus.timtam.model.ConfluenceService;
import org.codehaus.timtam.model.ConfluenceSpace;

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
 * A mock confluence server used to test the plugin   
 * @author Zohar
 *
 */
public class MockConfluenceServer implements ConfluenceService{

	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.ConfluenceService#getUser()
	 */
	public String getUser() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.ConfluenceService#search(java.lang.String, int)
	 */
	public RemoteSearchResult[] search(String query, int maxResults) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.ConfluenceService#logout()
	 */
	public boolean logout() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.ConfluenceService#getPage(long)
	 */
	public RemotePage getPage(long pageId) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.ConfluenceService#getSpace(java.lang.String)
	 */
	public RemoteSpace getSpace(String spaceId) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.ConfluenceService#getSpaces()
	 */
	public RemoteSpaceSummary[] getSpaces() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.ConfluenceService#getPages(java.lang.String)
	 */
	public RemotePageSummary[] getPages(String spaceId) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.ConfluenceService#getPageHistory(long)
	 */
	public RemotePageHistory[] getPageHistory(long pageId) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.ConfluenceService#storePage(com.atlassian.confluence.remote.RemotePage)
	 */
	public RemotePage storePage(RemotePage page) throws NotPermittedException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.ConfluenceService#renderContent(java.lang.String, long, java.lang.String)
	 */
	public String renderContent(String spaceId, long pageId, String content) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.ConfluenceService#deletePage(long)
	 */
	public Boolean deletePage(long pageId) throws NotPermittedException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.ConfluenceService#getPermissions(java.lang.String)
	 */
	public String[] getPermissions(String spaceId) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.ConfluenceService#getAttachments(long)
	 */
	public RemoteAttachment[] getAttachments(long pageId) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.ConfluenceService#getBlogEntries(java.lang.String)
	 */
	public RemoteBlogEntrySummary[] getBlogEntries(String spaceId) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.codehaus.timtam.model.ConfluenceService#getBlogEntry(long)
	 */
	public RemoteBlogEntry getBlogEntry(long blogEntryId) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param space
	 */
	public void addSpace(ConfluenceSpace space) {
	}
}