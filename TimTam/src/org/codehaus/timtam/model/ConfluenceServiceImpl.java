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
package org.codehaus.timtam.model;

import org.codehaus.timtam.TimTamPlugin;

import com.atlassian.confluence.remote.ConfluenceSoapHelper;
import com.atlassian.confluence.remote.IConfluenceSoapService;
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

import electric.glue.context.ProxyContext;
import electric.registry.Registry;
import electric.xml.io.Mappings;

/**
 * @author zohar melamed
 *  
 */
public class ConfluenceServiceImpl implements ConfluenceService {
    private String token;
    private IConfluenceSoapService service;
    protected String server;
    protected String user;
    protected String password;
    private long lastLogin;
    private static final long SEESION_EXPIRY = 3 * 60000;
    private static final String CONFLUENCE_SOAP_EP = "/rpc/soap/confluenceservice-v1.wsdl";
    
	/**
	 * @param server
	 * @param user
	 * @param password
	 */
	public ConfluenceServiceImpl(String server, String user, String password) {
		this.user = user;
		this.password = password;
	    this.server = server.endsWith(".wsdl") ? server : server+ CONFLUENCE_SOAP_EP;

	}
    /**
     * renew our session token every SEESION_EXPIRY msc
     */
    private String getToken() {
        long currTime = System.currentTimeMillis();
        if (currTime - lastLogin > SEESION_EXPIRY) {
            try {
                token = service.login(user, password);
                lastLogin = currTime;
            } catch (Exception e) {
                TimTamPlugin.getInstance()
                        .logException("failed to re-login", e, true);
            }
        }
        return token;
    }

    /**
     * @return Returns the user.
     */
    public String getUser() {
        return user;
    }

    protected void connectAndLogin(boolean useProxy) throws LoginFailureException {
        if (service == null) {
            TimTamPlugin plugin = TimTamPlugin.getInstance();
            ProxyContext context = new ProxyContext();
            if (useProxy) {
                context.setProxyHost(plugin.getProxyHost());
                context.setProxyPort(plugin.getProxyPort());
                context.setProxyUser(plugin.getProxyUser());
                context.setProxyPassword(plugin.getProxyPassword());
            }
            if (plugin.shouldUseHTTPAuthentication()) {
                context.setAuthPassword(plugin.getHTTPPassword());
                context.setAuthUser(plugin.getHTTPUser());
            }

            try {
                Mappings.readMappings("ConfluenceSoap.map");
                service = (IConfluenceSoapService) Registry.bind(server,
                        IConfluenceSoapService.class, context);
                ConfluenceSoapHelper.bind(server);
            } catch (Throwable t) {
                String message = "failed to bind to : " + server;
                plugin.logException(message, t, true);
                throw new LoginFailureException(message, t);
            }

            try {
                token = service.login(user, password);
                lastLogin = System.currentTimeMillis();
            } catch (Exception e) {
                StringBuffer buffer = new StringBuffer("failed to login to : ");
                buffer.append(server).append(" as:  ").append(user);
                buffer.append(" with password : ").append(password);
                buffer.append(" error : " + e.getMessage());
                TimTamPlugin.getInstance().logException(buffer.toString(), e, true);
                throw new LoginFailureException(buffer.toString(), e);
            }
        }
    }

    public RemoteSearchResult[] search(String query, int maxResults)throws RemoteException {
        return service.search(getToken(), query, maxResults);
    }

    public boolean logout() {
        try {
            return service.logout(getToken());
        } catch (RemoteException e) {
            // yes we have no bannans today
        }
        return false;
    }

    public RemotePage getPage(long pageId) throws RemoteException {
        return service.getPage(getToken(), pageId);
    }

    public RemoteSpace getSpace(String spaceId) throws RemoteException {
        return service.getSpace(getToken(), spaceId);
    }

    public RemoteSpaceSummary[] getSpaces() throws RemoteException {
        return service.getSpaces(getToken());
    }

    public RemotePageSummary[] getPages(String spaceId) throws RemoteException {
        return service.getPages(getToken(), spaceId);
    }

    public RemotePageHistory[] getPageHistory(long pageId)throws RemoteException {
        return service.getPageHistory(getToken(), pageId);
    }

    public RemotePage storePage(RemotePage page) throws NotPermittedException,RemoteException {
        return service.storePage(getToken(), page);
    }

    public String renderContent(String spaceId, long pageId, String content)throws RemoteException {
        return service.renderContent(getToken(), spaceId, pageId, content);
    }

    public Boolean deletePage(long pageId) throws NotPermittedException,RemoteException {
        return service.deletePage(getToken(), pageId);
    }

    public String[] getPermissions(String spaceId) throws RemoteException {
        return service.getPermissions(getToken(), spaceId);
    }

    public RemoteAttachment[] getAttachments(long pageId) throws RemoteException {
        return service.getAttachments(getToken(), pageId);
    }

    public RemoteBlogEntrySummary[] getBlogEntries( String spaceId) throws RemoteException{
        return service.getBlogEntries( getToken(), spaceId);
    }
    
    public RemoteBlogEntry getBlogEntry( long blogEntryId){
        return service.getBlogEntry(getToken(),blogEntryId);
    }

}