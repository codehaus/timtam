/*
 * Created on Oct 23, 2004
 *
 * 
 */
package org.codehaus.timtam;

import com.atlassian.confluence.remote.AuthenticationFailedException;
import com.atlassian.confluence.remote.IConfluenceSoapService;
import com.atlassian.confluence.remote.InvalidSessionException;
import com.atlassian.confluence.remote.NotPermittedException;
import com.atlassian.confluence.remote.RemoteAttachment;
import com.atlassian.confluence.remote.RemoteBlogEntry;
import com.atlassian.confluence.remote.RemoteBlogEntrySummary;
import com.atlassian.confluence.remote.RemoteComment;
import com.atlassian.confluence.remote.RemoteException;
import com.atlassian.confluence.remote.RemoteLock;
import com.atlassian.confluence.remote.RemotePage;
import com.atlassian.confluence.remote.RemotePageHistory;
import com.atlassian.confluence.remote.RemotePageSummary;
import com.atlassian.confluence.remote.RemoteSearchResult;
import com.atlassian.confluence.remote.RemoteServerInfo;
import com.atlassian.confluence.remote.RemoteSpace;
import com.atlassian.confluence.remote.RemoteSpaceSummary;
import com.atlassian.confluence.remote.RemoteUser;
import com.atlassian.confluence.remote.VersionMismatchException;

/**
 * A mock confluence server used to test the plugin   
 * @author Zohar
 *
 */
public class MockConfluenceServer implements IConfluenceSoapService {

	
	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#getPermissions(java.lang.String, java.lang.String)
	 */
	public String[] getPermissions(String arg0, String arg1)
			throws InvalidSessionException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#search(java.lang.String, java.lang.String, int)
	 */
	public RemoteSearchResult[] search(String arg0, String arg1, int arg2)
			throws InvalidSessionException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#getUser(java.lang.String, java.lang.String)
	 */
	public RemoteUser getUser(String arg0, String arg1)
			throws InvalidSessionException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#login(java.lang.String, java.lang.String)
	 */
	public String login(String arg0, String arg1)
			throws AuthenticationFailedException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#getServerInfo(java.lang.String)
	 */
	public RemoteServerInfo getServerInfo(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#addUser(java.lang.String, com.atlassian.confluence.remote.RemoteUser, java.lang.String)
	 */
	public void addUser(String arg0, RemoteUser arg1, String arg2)
			throws NotPermittedException, InvalidSessionException,
			RemoteException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#addGroup(java.lang.String, java.lang.String)
	 */
	public boolean addGroup(String arg0, String arg1)
			throws NotPermittedException, InvalidSessionException,
			RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#getSpace(java.lang.String, java.lang.String)
	 */
	public RemoteSpace getSpace(String arg0, String arg1)
			throws InvalidSessionException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#getPage(java.lang.String, long)
	 */
	public RemotePage getPage(String arg0, long arg1)
			throws InvalidSessionException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#getLocks(java.lang.String, long)
	 */
	public RemoteLock[] getLocks(String arg0, long arg1)
			throws InvalidSessionException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#getAttachments(java.lang.String, long)
	 */
	public RemoteAttachment[] getAttachments(String arg0, long arg1)
			throws InvalidSessionException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#getComments(java.lang.String, long)
	 */
	public RemoteComment[] getComments(String arg0, long arg1)
			throws InvalidSessionException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#getSpaces(java.lang.String)
	 */
	public RemoteSpaceSummary[] getSpaces(String arg0)
			throws InvalidSessionException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#getPages(java.lang.String, java.lang.String)
	 */
	public RemotePageSummary[] getPages(String arg0, String arg1)
			throws InvalidSessionException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#logout(java.lang.String)
	 */
	public boolean logout(String arg0) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#getBlogEntries(java.lang.String, java.lang.String)
	 */
	public RemoteBlogEntrySummary[] getBlogEntries(String arg0, String arg1)
			throws InvalidSessionException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#getBlogEntry(java.lang.String, long)
	 */
	public RemoteBlogEntry getBlogEntry(String arg0, long arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#getPageHistory(java.lang.String, long)
	 */
	public RemotePageHistory[] getPageHistory(String arg0, long arg1)
			throws InvalidSessionException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#renderContent(java.lang.String, java.lang.String, long, java.lang.String)
	 */
	public String renderContent(String arg0, String arg1, long arg2, String arg3)
			throws InvalidSessionException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#deletePage(java.lang.String, long)
	 */
	public Boolean deletePage(String arg0, long arg1)
			throws NotPermittedException, InvalidSessionException,
			RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#storeBlogEntry(java.lang.String, com.atlassian.confluence.remote.RemoteBlogEntry)
	 */
	public RemoteBlogEntry storeBlogEntry(String arg0, RemoteBlogEntry arg1)
			throws VersionMismatchException, NotPermittedException,
			InvalidSessionException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#storePage(java.lang.String, com.atlassian.confluence.remote.RemotePage)
	 */
	public RemotePage storePage(String arg0, RemotePage arg1)
			throws VersionMismatchException, NotPermittedException,
			InvalidSessionException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#getUserGroups(java.lang.String, java.lang.String)
	 */
	public String[] getUserGroups(String arg0, String arg1)
			throws NotPermittedException, InvalidSessionException,
			RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#addUserToGroup(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void addUserToGroup(String arg0, String arg1, String arg2)
			throws NotPermittedException, InvalidSessionException,
			RemoteException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.atlassian.confluence.remote.IConfluenceSoapService#addSpace(java.lang.String, com.atlassian.confluence.remote.RemoteSpace)
	 */
	public RemoteSpace addSpace(String arg0, RemoteSpace arg1)
			throws NotPermittedException, InvalidSessionException,
			RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
