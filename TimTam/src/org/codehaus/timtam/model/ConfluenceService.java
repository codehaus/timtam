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
import com.atlassian.confluence.remote.RemoteAttachment;
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
public abstract class ConfluenceService {
	private String token;
	private IConfluenceSoapService service;
	private static final String CONFLUENCE_SOAP_EP = "/rpc/soap/confluenceservice-v1.wsdl";
	String server;
	String user;
	String password;

	static class ConcreteService extends ConfluenceService {
	}

	/**
	 * @return Returns the user.
	 */
	public String getUser() {
		return user;
	}

	public static ConfluenceService getService(String server, String user, String password) throws LoginFailureException {

		ConcreteService instance = new ConcreteService();
		instance.password = password;
		instance.user = user;
		instance.server = server.endsWith(".wsdl")?server:server+CONFLUENCE_SOAP_EP;
		instance.connectAndLogin();
		return instance;
	}

	protected void connectAndLogin() throws LoginFailureException {
		if (service == null) {
			TimTamPlugin plugin = TimTamPlugin.getInstance();
			ProxyContext context = new ProxyContext();
			if(plugin.shouldUseProxy()){
				context.setProxyHost(plugin.getProxyHost());
				context.setProxyPort(plugin.getProxyPort());
				context.setProxyUser(plugin.getProxyUser());
				context.setProxyPassword(plugin.getProxyPassword());
			}
			
			try {
				//TODO loos the below hack by getting atlassian to sort it out
				Mappings.readMappings("ConfluenceSoap.map");
				service = (IConfluenceSoapService) Registry.bind(server, IConfluenceSoapService.class, context);
				ConfluenceSoapHelper.bind(server);
			} catch (Exception e) {
				String message = "failed to bind to : " + server;
				plugin.logException(message, e);
				throw new LoginFailureException(message, e);
			}

			try {
				token =  service.login(user,password);
			} catch (Exception e) {
				StringBuffer buffer = new StringBuffer("failed to login to : ");
				buffer.append(server).append(" as:  ").append(user);
				buffer.append(" with password : ").append(password);
				buffer.append(" error : "+e.getMessage());
				plugin.logException(buffer.toString(), e);
				throw new LoginFailureException(buffer.toString(), e);
			}
		}
	}

	public RemoteSearchResult[] search(String query, int maxResults) {
		return service.search(token, query, maxResults);
	}

	public boolean logout() {
		return service.logout(token);
	}
	public RemotePage getPage(long pageId) {
		return service.getPage(token, pageId);
	}
	public RemoteSpace getSpace(String spaceId) {
		return service.getSpace(token, spaceId);
	}
	public RemoteSpaceSummary[] getSpaces() {
		return service.getSpaces(token);
	}
	public RemotePageSummary[] getPages(String spaceId) {
		return service.getPages(token, spaceId);
	}
	public RemotePageHistory[] getPageHistory(long pageId) {
		return service.getPageHistory(token, pageId);
	}
	public RemotePage storePage(RemotePage page) {
		return service.storePage(token, page);
	}
	public String renderContent(String spaceId, long pageId, String content) {
		return service.renderContent(token, spaceId, pageId, content);
	}
	public Boolean deletePage(long pageId) {
		return service.deletePage(token, pageId);
	}

	public String[] getPermissions(String spaceId) {
		return service.getPermissions(token, spaceId);
	}

	public RemoteAttachment[] getAttachments(long pageId) {
		return service.getAttachments(token, pageId);
	}
}