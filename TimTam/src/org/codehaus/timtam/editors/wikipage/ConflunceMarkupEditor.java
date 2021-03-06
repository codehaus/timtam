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

package org.codehaus.timtam.editors.wikipage;

import org.codehaus.timtam.TimTamPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author zohar melamed
 *
 */
public class ConflunceMarkupEditor extends AbstractTextEditor{
	
	private ConflunceMarkupOutline contentOutline;

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class required) {
		if (required.equals(IContentOutlinePage.class)) {
			return contentOutline; 
		}
		return super.getAdapter(required);
	}

	/**
	 * 
	 */
	public ConflunceMarkupEditor() {
		super();
		setDocumentProvider(new ConfluenceDocumentProvider());
		setSourceViewerConfiguration(new ConfluenceSourceViewerConfiguration());
	}
	
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		setWordWrap();
	}

	private void setWordWrap() {
		if (getSourceViewer() != null) {
			getSourceViewer().getTextWidget().setWordWrap(TimTamPlugin.getInstance().getPreferenceStore().getBoolean(TimTamPlugin.P_USE_WORD_WRAP));
		}
	}

}
