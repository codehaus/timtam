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



package org.codehaus.timtam.editors;

import org.codehaus.timtam.TimTamPlugin;
import org.codehaus.timtam.model.ConfluencePage;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.atlassian.confluence.remote.RemoteException;

/**
 * An example showing how to create a multi-page editor. This example has 3
 * pages:
 * <ul>
 * <li>page 0 contains the confluence text editor.
 * <li>page 1 contraions the preview browser
 * </ul>
 */
public class TimTamPageEditor extends MultiPageEditorPart implements IResourceChangeListener , IDocumentListener{

	/** The text editor for the conf page */
	protected ConflunceMarkupEditor editor;
	private BrowserControl browser;
	public static final String EDITOR_ID = "org.codehaus.timtam.editors.TimTamPageEditor";
	//private RemotePage page;

	private int previewPageIndex = -1;
	private ConfluencePage page;
	private boolean needsRendering = true;
	/**
	 * Creates a multi-page editor example.
	 */
	public TimTamPageEditor() {
		super();
	}

	void createEditorPage() {
		try {
			editor = new ConflunceMarkupEditor();
			IEditorInput input = getEditorInput();
			int index = addPage(editor, input);
			setPageText(index, "Ed&it");
			IDocumentProvider provider = editor.getDocumentProvider();
			IDocument document = provider.getDocument(input);
			document.addDocumentListener(this);			
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus());
		}
	}

	void createPreviewPage() {
		Composite composite = new Composite(getContainer(), SWT.NONE);
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);
		browser = new BrowserControl(composite, SWT.NONE,getEditorSite().getActionBars(),page);
		previewPageIndex = addPage(composite);
		setPageText(previewPageIndex, "&Preview");
	}
	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createEditorPage();
		createPreviewPage();
		setActivePage(1);
		pageChange(1);
	}
	/**
	 * The <code>MultiPageEditorPart</code> implementation of this <code>IWorkbenchPart</code>
	 * method disposes all nested editors. Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		// make sure the document is in synch with the page  
		IDocumentProvider provider = editor.getDocumentProvider();
		IDocument document = provider.getDocument(editor.getEditorInput());
		try {
            document.set(page.getContent());
        } catch (RemoteException e) {
            TimTamPlugin.getInstance().logException("page getcontent failed in dispose",e,false);
        }
		super.dispose();
	}
	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		getEditor(0).doSave(monitor);
	}
	/**
	 * Saves the multi-page editor's document as another file. Also updates the
	 * text for page 0's tab, and updates this multi-page editor's input to
	 * correspond to the nested editor's.
	 */
	public void doSaveAs() {
		//		IEditorPart editor = getEditor(0);
		//		editor.doSaveAs();
		//		setPageText(0, editor.getTitle());
		//		setInput(editor.getEditorInput());
	}
	/*
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}
	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		super.init(site, editorInput);
		page = (ConfluencePage) editorInput.getAdapter(ConfluencePage.class);
		setPartName(page.getTitle());
	}
	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}
	/**
	 *  
	 */
	protected void pageChange(int newPageIndex) {
		
		super.pageChange(newPageIndex);
		if (newPageIndex == previewPageIndex) {
			IDocumentProvider provider = editor.getDocumentProvider();
			IDocument document = provider.getDocument(editor.getEditorInput());
			if(!needsRendering){
				// nothing to do as no doc change and we have rendered already  
				return;
			}
			
			try {
                browser.setText(page.renderContent(document.get()));
            } catch (RemoteException e) {
                TimTamPlugin.getInstance().logException("render page failed",e,false);
            }
			needsRendering = false;
		}
	}
	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i < pages.length; i++) {
						if (((FileEditorInput) editor.getEditorInput())
							.getFile()
							.getProject()
							.equals(event.getResource())) {
							IEditorPart editorPart = pages[i].findEditor(editor.getEditorInput());
							pages[i].closeEditor(editorPart, true);
						}
					}
				}
			});
		}
	}

	public void documentAboutToBeChanged(DocumentEvent event) {
	}

	public void documentChanged(DocumentEvent event) {
		needsRendering = true;
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if(adapter == IEditorInput.class){
			return getEditorInput();
		}
		
		return super.getAdapter(adapter);
	}
}
