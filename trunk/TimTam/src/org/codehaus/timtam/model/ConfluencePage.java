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

import com.atlassian.confluence.remote.NotPermittedException;
import com.atlassian.confluence.remote.RemoteException;
import com.atlassian.confluence.remote.RemotePageHistory;

/**
 * @author MelamedZ
 *
 */
public interface ConfluencePage {
	
	public long getId() throws RemoteException;
	public long getParentId() throws RemoteException;
	public String getSpace();
	public String getUrl() throws RemoteException;
	
	
	public String getContent() throws RemoteException;
	public String getContent(long version) throws RemoteException;
	public String renderContent() throws RemoteException;
	public String renderContent(String content) throws RemoteException;
	public void setContent(String content) throws RemoteException;
	
	public String getTitle();
	public void setTitle(String title) throws RemoteException;
	
	
	public java.util.Date getCreated() throws RemoteException;
	public String getCreator() throws RemoteException;
	public boolean isHomePage();
	public void setHomePage(boolean homePage) throws RemoteException;
	public java.util.Date getModified() throws RemoteException;
	public String getModifier() throws RemoteException;
	public int getVersion() throws RemoteException;
	public RemotePageHistory[] getPageHistory() throws RemoteException;
	
	
	public void save() throws NotPermittedException, RemoteException;
	public void rename(String name) throws NotPermittedException, RemoteException;
	public Object createPage(String name) throws NotPermittedException, RemoteException;
	
	public void refresh();
	public void setDirty();
	public boolean isDirty();
	public void delete() throws NotPermittedException, RemoteException;
	public boolean isReadOnly();


	
}
