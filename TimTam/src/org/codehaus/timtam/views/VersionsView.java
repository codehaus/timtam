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
package org.codehaus.timtam.views;


import java.text.DateFormat;

import org.codehaus.timtam.TimTamPlugin;
import org.codehaus.timtam.model.ConfluencePage;
import org.codehaus.timtam.model.TimTamHistoryItem;
import org.eclipse.compare.EditionSelectionDialog;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.atlassian.confluence.remote.RemoteException;
import com.atlassian.confluence.remote.RemotePageHistory;



public class VersionsView extends ViewPart implements IPartListener {
	private TableViewer viewer;
	private Action doubleClickAction;
	private static RemotePageHistory[] EMPTY= new RemotePageHistory[0];  
	public static final String CONFLUENCE_VERSIONSVIEW_ID = "org.codehaus.timtam.views.VersionsView";
	private ConfluencePage confPage;
	private RemotePageHistory[] histories;

	 
	class VersionsContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			return (Object[])parent;
		}
	}
	
	class VersionsLabelProvider extends LabelProvider implements ITableLabelProvider {
		private static final int VERSION = 0;
		private static final int MODIFIER = 1;
		private static final int DATE= 2;
		
		public String getColumnText(Object obj, int index) {
			RemotePageHistory history = (RemotePageHistory) obj;
			switch(index){
				case VERSION:
					return Integer.toString(history.version);
				case MODIFIER:
					return history.modifier;
					case DATE:
						return DateFormat.getDateTimeInstance().format(history.modified);
						
			}
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return null;
		}
	}

	/**
	 * The constructor.
	 */
	public VersionsView() {
	}

	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL|SWT.FULL_SELECTION);
		viewer.setContentProvider(new VersionsContentProvider());
		viewer.setLabelProvider(new VersionsLabelProvider());
		viewer.setInput(EMPTY);
		TableColumn column = new TableColumn(viewer.getTable(), SWT.LEFT);
		column.setText("Ver");
		column.setWidth(60);
		column = new TableColumn(viewer.getTable(), SWT.LEFT);
		column.setText("Modifier");
		column.setWidth(300);
		column = new TableColumn(viewer.getTable(), SWT.LEFT);
		column.setText("Date");
		column.setWidth(300);
		
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		

	
		Action compare = new Action() {
				public void run() {
					if(histories == null){
						return;
					}
					
					EditionSelectionDialog dialog = new EditionSelectionDialog(getSite().getShell(),
																			   TimTamPlugin.getInstance().getResourceBundle());
					dialog.setEditionTitleArgument(confPage.getTitle());
					dialog.setEditionTitleImage(TimTamPlugin.getInstance().getPageIcon(confPage));
					dialog.setBlockOnOpen(true);
					TimTamHistoryItem items[] = TimTamHistoryItem.creatHistoryItems(confPage,histories);
					try {
						dialog.selectEdition(new TimTamHistoryItem(confPage),items,null);
						dialog.open();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
		};
		
		compare.setText("View Diff");
		compare.setToolTipText("View Differences between versions");
		compare.setImageDescriptor(TimTamPlugin.getInstance().loadImageDescriptor(TimTamPlugin.IMG_USER));

		getViewSite().getActionBars().getToolBarManager().add(compare);
		hookDoubleClickAction();
	}



	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IViewPart#init(org.eclipse.ui.IViewSite)
	 */
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(this);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		super.dispose();
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if(activePage!= null){
			activePage.removePartListener(this);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partActivated(IWorkbenchPart part) {
		IEditorInput input = (IEditorInput) part.getAdapter(IEditorInput.class);
		if(input != null){
			confPage = (ConfluencePage) input.getAdapter(ConfluencePage.class);
			if(confPage!=null){
				try {
                    histories = confPage.getPageHistory();
                } catch (RemoteException e) {
                    TimTamPlugin.getInstance().logException("failed to get versions for page "+confPage.getTitle(),
                            e,true);
                }
				viewer.setInput(histories);
				TableColumn[] columns = viewer.getTable().getColumns();
				for (int i = 0; i < columns.length; i++) {
					TableColumn column = columns[i];
					column.pack();
				}
				
			}
		}		
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partBroughtToTop(IWorkbenchPart part) {
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partClosed(IWorkbenchPart part) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partDeactivated(IWorkbenchPart part) {
		//viewer.setInput(EMPTY);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partOpened(IWorkbenchPart part) {
	}

}