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


package org.codehaus.timtam.perspective;

import org.codehaus.timtam.views.SearchView;
import org.codehaus.timtam.views.VersionsView;
import org.codehaus.timtam.views.confluencetree.ConfluenceView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @see IPerspectiveFactory
 */
public class TimTamPerspectiveFactory implements IPerspectiveFactory {
	/**
	 *
	 */
	public TimTamPerspectiveFactory() {
	}

	/**
	 * @see IPerspectiveFactory#createInitialLayout
	 */
	public void createInitialLayout(IPageLayout layout)  {
		layout.addShowViewShortcut(ConfluenceView.CONFLUENCE_TREEVIEW_ID);
		layout.addShowViewShortcut(SearchView.CONFLUENCE_SEARCHVIEW_ID);
		layout.addShowViewShortcut(VersionsView.CONFLUENCE_VERSIONSVIEW_ID);
		
		// Editors are placed for free.
		String editorArea = layout.getEditorArea();

		// Top left.
		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, (float)0.26, editorArea);//$NON-NLS-1$
		topLeft.addView(ConfluenceView.CONFLUENCE_TREEVIEW_ID);
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, (float)0.80,//$NON-NLS-1$
		editorArea);//$NON-NLS-1$
		
		bottom.addView(SearchView.CONFLUENCE_SEARCHVIEW_ID);		
		bottom.addView(VersionsView.CONFLUENCE_VERSIONSVIEW_ID);		
	}
}
