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
import org.codehaus.timtam.model.ConfluencePage;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;

import com.atlassian.confluence.remote.NotPermittedException;
import com.atlassian.confluence.remote.RemoteException;

/**
 * @author MelamedZ
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ConfluenceDocumentProvider extends AbstractDocumentProvider {



	/**
	 * @author Sarah
	 *
	 * TODO To change the template for this generated type comment go to
	 * Window - Preferences - Java - Code Style - Code Templates
	 */
	private static class ConfluenceDocumet extends Document {
		
			/**
		 * 
		 */
		public ConfluenceDocumet() {
			super();
			//setLineTracker(new ConfluenceLieTracker(this));
		}
}
	
	private ConfluencePage getPage(Object element) {
		IEditorInput input = (IEditorInput) element;
		return (ConfluencePage) input.getAdapter(ConfluencePage.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IDocumentProviderExtension#isModifiable(java.lang.Object)
	 */
	public boolean isModifiable(Object element) {
		ConfluencePage page = (ConfluencePage) element;
		return !page.isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IDocumentProviderExtension#isReadOnly(java.lang.Object)
	 */
	public boolean isReadOnly(Object element) {
		return false;
	}

	protected IAnnotationModel createAnnotationModel(Object element)  {
		return new AnnotationModel();
	}

	protected IDocument createDocument(Object element)  {
		ElementInfo info = getElementInfo(element);
		if(info != null){
			return info.fDocument;
		}
		
		Document doc = new ConfluenceDocumet();
			try {
            doc.set(getPage(element).getContent());
        } catch (RemoteException e) {
            TimTamPlugin.getInstance().logException("Failed to load page ",e, true);
        }
		
		return doc;
	}

	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite){
		ConfluencePage page = getPage(element);
		TimTamPlugin plugin = TimTamPlugin.getInstance();
		try {		
		    page.setContent(document.get());
            page.save();
        } catch (NotPermittedException e) {
            plugin.logException("You are not allowed to save this page",e, true);
        } catch (RemoteException e) {
            plugin.logException("Error saving page",e, true);
        }
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#getOperationRunner(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IRunnableContext getOperationRunner(IProgressMonitor monitor) {
		return null;
	}

}
