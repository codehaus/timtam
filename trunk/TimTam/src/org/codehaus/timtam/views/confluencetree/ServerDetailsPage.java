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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author zohar melamed
 *  
 */
public class ServerDetailsPage extends WizardPage {

	protected String server = "http://confluence.atlassian.com";
	protected String user = "melamedz";
	protected String password = "trustno1";
	protected boolean useProxy = false;
	
	private Text serverText;
	private Text userText;
	private Text passwordText;
	private Button useProxyCheck;
	/**
	 * @param pageName
	 */
	protected ServerDetailsPage(String pageName) {
		super(pageName);
		setDescription("Specifiy Details of a Confluence Server");
		setTitle("New Confluence Server Details");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		container.setLayout(layout);
		
		Label label = new Label(container, SWT.NULL);
		label.setText("&Server Url:");
		serverText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		serverText.setLayoutData(gd);
		serverText.setText(server);
		
		serverText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				server = serverText.getText(); 
				dialogChanged();
			}
		});

		label = new Label(container, SWT.NULL);
		label.setText("&User name:");

		userText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		userText.setLayoutData(gd);
		userText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				user = userText.getText();
				dialogChanged();
			}
		});

		label = new Label(container, SWT.NULL);
		label.setText("&Password :");

		passwordText = new Text(container, SWT.BORDER | SWT.SINGLE|SWT.PASSWORD);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		passwordText.setLayoutData(gd);
		passwordText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				password= passwordText.getText();
				dialogChanged();
			}
		});
	
		useProxyCheck = new Button(container, SWT.CHECK | SWT.LEFT);
		useProxyCheck.setText("Use Proxy ?");
		useProxyCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				useProxy = useProxyCheck.getSelection();
			}
		});
		
		dialogChanged();
		setControl(container);
	}

	
	/**
	 * Ensures that both text fields are set.
	 */

	protected void dialogChanged() {
		if (server.length() == 0) {
			updateStatus("Server url must be specified");
			return;
		}
		
		if (user.length() == 0) {
			updateStatus("User name must be specified");
			return;
		}

		if (password.length() == 0) {
			updateStatus("Password must be specified");
			return;
		}
		
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	
}
