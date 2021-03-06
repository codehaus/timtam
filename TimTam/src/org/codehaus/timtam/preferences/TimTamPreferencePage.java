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



package org.codehaus.timtam.preferences;

import org.codehaus.timtam.TimTamPlugin;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>,
 * we can use the field support built into JFace that allows us to create a
 * page that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class TimTamPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private StringFieldEditor proxyHost;
	private StringFieldEditor proxyPort;
	private StringFieldEditor proxyUser;
	private StringFieldEditor proxyPasssword;
	private BooleanFieldEditor useHttpAuth;
	private StringFieldEditor httpUser;
	private StringFieldEditor httpPassword;
	private BooleanFieldEditor useProxySettings;

	public TimTamPreferencePage() {
		super(GRID);
		setPreferenceStore(TimTamPlugin.getInstance().getPreferenceStore());
		//setDescription("Connection Properties");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */

	public void createFieldEditors() {
		
		Composite editorParent = getFieldEditorParent();

		addField(new BooleanFieldEditor(TimTamPlugin.P_USE_WORD_WRAP, "Use Word Wrap?  ", editorParent));
		
		proxyHost = new StringFieldEditor(TimTamPlugin.P_PROXY_HOST, "Proxy Host", editorParent);
		addField(proxyHost);
		
		proxyPort = new StringFieldEditor(TimTamPlugin.P_PROXY_PORT, "Proxy Port", editorParent);
		addField(proxyPort);
		
		proxyUser = new StringFieldEditor(TimTamPlugin.P_PROXY_USER, "Proxy User Name", editorParent);
		addField(proxyUser);
		
		proxyPasssword = new StringFieldEditor(TimTamPlugin.P_PROXY_PASSWORD, "Proxy Password", editorParent);
		proxyPasssword.getTextControl(editorParent).setEchoChar('*');
		addField(proxyPasssword);
		

		
		useHttpAuth = new BooleanFieldEditor(TimTamPlugin.P_USE_HTTP_AUTH, "Use HTTP Authentication?  ", editorParent);
		addField(useHttpAuth);
		useProxySettings = new BooleanFieldEditor(TimTamPlugin.P_USE_PROXY_FOR_HTTP_AUTH, "Use Proxy User/Password?  ", editorParent);
		addField(useProxySettings);
		httpUser = new StringFieldEditor(TimTamPlugin.P_HTTP_USER, "HTTP User", editorParent);
		addField(httpUser);
		httpPassword = new StringFieldEditor(TimTamPlugin.P_HTTP_PASSWORD, "HTTP Password", editorParent);
		httpPassword.getTextControl(editorParent).setEchoChar('*');
		addField(httpPassword);
		
	}

	protected void initialize() {
	    super.initialize();
		setHttpFieldsState(getFieldEditorParent());
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		setHttpFieldsState(getFieldEditorParent());
	}

	/**
	 * @param editorParent
	 */
	private void setHttpFieldsState(Composite editorParent) {
		boolean httpAuthEnabled = useHttpAuth.getBooleanValue();
		boolean enabled = httpAuthEnabled && (! useProxySettings.getBooleanValue());
		httpPassword.setEnabled(enabled,editorParent);
		httpUser.setEnabled(enabled,editorParent);
	    useProxySettings.setEnabled(httpAuthEnabled, editorParent);
	}

	/**
	 * @param editorParent
	 */

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
        
    }
	
}