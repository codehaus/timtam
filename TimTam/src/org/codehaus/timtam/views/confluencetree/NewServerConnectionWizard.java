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

package org.codehaus.timtam.views.confluencetree;

import java.lang.reflect.InvocationTargetException;

import org.codehaus.timtam.TimTamPlugin;
import org.codehaus.timtam.model.TimTamModel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;

/**
 * @author zohar melamed
 *  
 */
public class NewServerConnectionWizard extends Wizard {
	ServerDetailsPage page;

	/**
	 * Constructor for SampleNewWizard.
	 */
	public NewServerConnectionWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page = new ServerDetailsPage("Server Details");
		addPage(page);
	}

	
	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	public boolean performFinish() {

		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				doFinish(page.server, page.user, page.password, page.useProxy,monitor);
				monitor.done();
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			return false;
		}
		return true;
	}

	
	/**
	 * The worker method. It will find the container, create the file if
	 * missing or just replace its contents, and open the editor on the newly
	 * created file.
	 * @param useProxy
	 */

	protected void doFinish(String server, String user, String password, boolean useProxy, IProgressMonitor monitor) throws InvocationTargetException {
		monitor.setTaskName("Trying to connect to " + server + " ...");
		monitor.worked(1);
		try {
 			TimTamModel.getInstance().addServer(server, user, password, useProxy);
		} catch (Exception e) {
			final String errorMessage = "Connection to " + server + " failed :\n" + e.getMessage();
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(getShell(), "Error Connecting to Server", errorMessage);
				}

			});
			TimTamPlugin.getInstance().logException("failed to add a connection to " + server, e, true);
			throw new InvocationTargetException(e);
		}
	}

}
