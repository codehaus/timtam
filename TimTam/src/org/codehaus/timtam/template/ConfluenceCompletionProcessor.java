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
package org.codehaus.timtam.template;

import org.codehaus.timtam.TimTamPlugin;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.templates.ContextType;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.swt.graphics.Image;

/**
 * @author Zohar
 *
 */
public class ConfluenceCompletionProcessor extends TemplateCompletionProcessor{

	private char[] completionChars = new char[]{'{'};
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getTemplates(java.lang.String)
	 */
	protected Template[] getTemplates(String contextTypeId) {
		return TimTamPlugin.getInstance().getTemplateStore().getTemplates();
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getContextType(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 */
	protected ContextType getContextType(ITextViewer viewer, IRegion region) {
		return TimTamPlugin.getInstance().getContextTypeRegistry().getContextType(ConfluenceContextType.CONTEXT_TYPE);	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getImage(org.eclipse.jface.text.templates.Template)
	 */
	protected Image getImage(Template template) {
		return TimTamPlugin.getInstance().getCompletionProcessorImage();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return completionChars;
	}
}
