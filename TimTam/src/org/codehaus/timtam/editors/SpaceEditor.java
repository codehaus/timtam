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
package org.codehaus.timtam.editors;
import org.codehaus.timtam.TimTamPlugin;
import org.codehaus.timtam.model.ConfluenceSpace;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.EditorPart;

import com.atlassian.confluence.remote.RemoteBlogEntry;


public class SpaceEditor extends EditorPart implements IHyperlinkListener{
	
	public static final String EDITOR_ID = "org.codehaus.timtam.editors.SpaceEditor";
	private ConfluenceSpace space;
	private FormToolkit toolkit;
	private ScrolledForm form;
  
	public SpaceEditor() {
		super();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	public void doSaveAs() {
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
	 *      org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setInput(input);
		setSite(site);
		space = (ConfluenceSpace) input.getAdapter(ConfluenceSpace.class);
		if (space == null) {
			throw new PartInitException("The space editor can only be backed by a space");
		}
		setPartName(space.getName());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	public boolean isDirty() {
		return false;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		fillBody(new ManagedForm(toolkit,form),toolkit);
		form.setText(space.getName());
	}
	
	
	private void fillBody(IManagedForm form, FormToolkit toolkit) {
		Composite body = form.getForm().getBody();
		TableWrapLayout layout = new TableWrapLayout();
		layout.bottomMargin = 10;
		layout.topMargin = 5;
		layout.leftMargin = 10;
		layout.rightMargin = 10;
		layout.numColumns = 2;
		layout.verticalSpacing = 20;
		layout.horizontalSpacing = 10;
		body.setLayout(layout);
		createNewPagesSection(form, body, toolkit);
	}

    	
	private void createNewPagesSection(IManagedForm managedForm,Composite parent, FormToolkit toolkit) {

	    Section section = createStaticSection(parent, toolkit);
		section.setText("Space Blog Entries"); //$NON-NLS-1$
		ImageHyperlink info = new ImageHyperlink(section, SWT.NULL);
		toolkit.adapt(info, true, true);
		Image image = TimTamPlugin.getInstance().getSpaceIcon(space);
		info.setImage(image);
		info.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
			    System.out.println(e);
			}
		});
		
		info.setBackground(section.getTitleBarGradientBackground());
		createClient(section , toolkit);
	}
	private Section createStaticSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		//section.clientVerticalSpacing = PDESection.CLIENT_VSPACING;
		toolkit.createCompositeSeparator(section);
		return section;
	}
	
	private FormText createClient(Section section, FormToolkit toolkit) {
		FormText text = toolkit.createFormText(section, true);
		try {
		    RemoteBlogEntry[] blogEntries = space.getBlogEntries();
		    StringBuffer formText = new StringBuffer("<form>");
            for (int i = blogEntries.length-1; i >=0; i--) {
                RemoteBlogEntry entry = blogEntries[i];
                formText.append("<p><b>").append(entry.title).append("</b></p>");
                formText.append("<p>");
                formText.append(entry.content.substring(0,Math.min(128,entry.content.length())));
                formText.append("...").append("</p>");
            }
			formText.append("</form>");
			text.setText(formText.toString(), true, true);
		} catch (Exception e) {
			text.setText(e.getMessage(), false, false);
		}
		section.setClient(text);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		text.addHyperlinkListener(this);
		return text;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.events.HyperlinkListener#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
	 */
	public void linkActivated(HyperlinkEvent e) {
//		String href = (String) e.getHref();
		// try page references
//		if (href.equals("dependencies")) //$NON-NLS-1$
//			getEditor().setActivePage(DependenciesPage.PAGE_ID);
//		else if (href.equals("runtime")) //$NON-NLS-1$
//			getEditor().setActivePage(RuntimePage.PAGE_ID);
//		else if (href.equals("extensions")) //$NON-NLS-1$
//			getEditor().setActivePage(ExtensionsPage.PAGE_ID);
//		else if (href.equals("ex-points")) //$NON-NLS-1$
//			getEditor().setActivePage(ExtensionPointsPage.PAGE_ID);
//		else if (href.equals("build")) //$NON-NLS-1$
//			getEditor().setActivePage(BuildPage.PAGE_ID);
//		else if (href.equals("action.run")) //$NON-NLS-1$
//			getLaunchShortcut().run();
//		else if (href.equals("action.debug")) //$NON-NLS-1$
//			getLaunchShortcut().debug();
//		else if (href.equals("export")) //$NON-NLS-1$
//			getExportAction().run();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.events.HyperlinkListener#linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent)
	 */
	public void linkEntered(HyperlinkEvent e) {
		IStatusLineManager mng = getEditorSite().getActionBars().getStatusLineManager();
		mng.setMessage(e.getLabel());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.events.HyperlinkListener#linkExited(org.eclipse.ui.forms.events.HyperlinkEvent)
	 */
	public void linkExited(HyperlinkEvent e) {
		IStatusLineManager mng = getEditorSite().getActionBars().getStatusLineManager();
		mng.setMessage(null);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
	}
}