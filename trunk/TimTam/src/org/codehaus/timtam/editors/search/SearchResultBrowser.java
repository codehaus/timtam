/*
 * 
 * Copyright 2003(c) Zohar Melamed All rights reserved.
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
package org.codehaus.timtam.editors.search;

import org.codehaus.timtam.model.search.SearchResultBrowserInput;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * @see EditorPart
 */
public class SearchResultBrowser extends EditorPart {
	private Browser browser;
	
	public static final String ID = "org.codehaus.timtam.editors.SearchResultBrowser";

	/**
	 *
	 */
	public SearchResultBrowser() {
	}

	/**
	 * @see EditorPart#createPartControl
	 */
	public void createPartControl(Composite parent)  {
		browser = new Browser(parent,SWT.NULL);
		SearchResultBrowserInput browserInput =  (SearchResultBrowserInput) getEditorInput();
		if(browserInput != null){
			String url = browserInput.getUrl();
			navigateTo(url);
		}

	}

	/**
	 * @see EditorPart#setFocus
	 */
	public void setFocus()  {
		browser.setFocus();
	}

	/**
	 * @see EditorPart#doSave
	 */
	public void doSave(IProgressMonitor monitor)  {
	}

	/**
	 * @see EditorPart#doSaveAs
	 */
	public void doSaveAs()  {
	}

	/**
	 * @see EditorPart#isDirty
	 */
	public boolean isDirty()  {
		return false;
	}

	/**
	 * @see EditorPart#isSaveAsAllowed
	 */
	public boolean isSaveAsAllowed()  {
		return false;
	}

	/**
	 * @see EditorPart#init
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	public void navigateTo(String url){
		browser.setUrl(url);
		setPartName(url);
		SearchResultBrowserInput browserInput =  (SearchResultBrowserInput) getEditorInput();
		browserInput.setUrl(url);

	}
}
