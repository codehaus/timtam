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

package org.codehaus.timtam.views;

import org.codehaus.timtam.TimTamPlugin;
import org.codehaus.timtam.model.LoginFailureException;
import org.codehaus.timtam.model.TimTamModel;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * @author zohar melamed
 *
 */
public class TestServerConnectionPage extends WizardPage {
	ServerDetailsPage details;
	ListViewer eventList;
	/**
	 * @param pageName
	 */
	protected TestServerConnectionPage(String pageName, ServerDetailsPage serverDetails) {
		super(pageName);
		setDescription("Test Conecction to the Confluence Server ");
		setTitle("Confluence Server Connection Test");
		details = serverDetails;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		eventList = new ListViewer(container);
		Display.getDefault().asyncExec(new Runnable(){
			public void run() {
				eventList.add("Connecting to "+details.server);
				try {
					TimTamModel.getInstace().addServer(details.server, details.user, details.password);
				} catch (LoginFailureException e) {
					String errorMessage = "Connection to " + details.server + " failed " + e.getMessage();
					MessageDialog.openError(getShell(), "Error Connecting to Server", errorMessage);
					TimTamPlugin.getInstance().logException("failed to add a coneection to " + details.server, e, true);
				}
			}
		});
		
		setControl(container);
	}

}
