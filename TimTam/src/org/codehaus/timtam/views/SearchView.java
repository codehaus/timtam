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
package org.codehaus.timtam.views;
import java.util.Collection;
import java.util.HashMap;

import org.codehaus.timtam.TimTamPlugin;
import org.codehaus.timtam.editors.SearchResultBrowser;
import org.codehaus.timtam.model.SearchResult;
import org.codehaus.timtam.model.SearchResultBrowserInput;
import org.codehaus.timtam.model.TimTamModel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.atlassian.confluence.remote.RemoteException;



public class SearchView extends ViewPart {
	private TableViewer viewer;
	private Action search;
	private Action openPage;
	Combo searchText;
	public static final String CONFLUENCE_SEARCHVIEW_ID = "org.codehaus.timtam.views.SearchView";
	protected static final int NOT_FOUND = -1;
	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */
	static class ViewContentProvider implements IStructuredContentProvider {
		private Collection searchResults;
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput != null && newInput instanceof Collection) {
				searchResults = (Collection) newInput;
			}
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			if (searchResults != null) {
				return searchResults.toArray();
			}
			return new Object[]{};
		}
	}
	static class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		private static final int TITLE = 0;
		private static final int SERVER = 1;
		private static final int EXCERPT = 2;
		private HashMap imageMap = new HashMap();
		public ViewLabelProvider() {
			super();
			imageMap.put("page", TimTamPlugin.IMG_PAGE);
			imageMap.put("comment", TimTamPlugin.IMG_COMMENT);
			imageMap.put("spacedesc", TimTamPlugin.IMG_SPACE);
			imageMap.put("attachment", TimTamPlugin.IMG_ATTACHMENT);
			imageMap.put("userinfo", TimTamPlugin.IMG_USER);
			imageMap.put("blogpost", TimTamPlugin.IMG_BLOGENTRY);
		}
		public String getColumnText(Object obj, int index) {
			SearchResult result = (SearchResult) obj;
			String res = null;
			switch (index) {
				case TITLE :
					res = result.getTitle();
					break;
				case EXCERPT :
					res =  result.getExcerpt();
					break;
				case SERVER :
					res = result.getServer();
					break;
			}
			if(res == null){
				res = "";
			}
			return res;
		}
		public Image getColumnImage(Object obj, int index) {
			if (index != 0) {
				return null;
			}
			SearchResult result = (SearchResult) obj;
			return TimTamPlugin.getInstance().loadImage((String) imageMap.get(result.getType()));
		}
	}
	static class NameSorter extends ViewerSorter {
	}
	/**
	 * The constructor.
	 */
	public SearchView() {
	}
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
		
		TableColumn column = new TableColumn(viewer.getTable(), SWT.LEFT);
		column.setText("Title");
		column.setWidth(150);

		column = new TableColumn(viewer.getTable(), SWT.LEFT);
		column.setText("Excerpt");
		column.setWidth(300);
		
		column = new TableColumn(viewer.getTable(), SWT.LEFT);
		column.setText("Server");
		column.setWidth(150);
		
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
	}
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				SearchView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}
	private void fillContextMenu(IMenuManager manager) {
		manager.add(search);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	

	public void search(final String query) {
		Display.getCurrent().asyncExec(new Runnable(){
			public void run() {
				Collection results = null;
                try {
                    results = TimTamModel.getInstace().search(query);
                } catch (RemoteException e) {
                    TimTamPlugin plugin = TimTamPlugin.getInstance();
                    plugin.logException("Search failed",e,true);
                }
                viewer.setInput(results);
				TableColumn[] columns = viewer.getTable().getColumns();
				for (int i = 0; i < columns.length; i++) {
					TableColumn column = columns[i];
					column.pack();
				}

			}
		});
		
	}
	
	private void makeActions() {
		openPage = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				SearchResult result = (SearchResult) obj;
				SearchResultBrowser browser = getSearchBrowser(result);
			}
		};
	}
	SearchResultBrowser getSearchBrowser(SearchResult result) {
		IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		workbenchPage.setEditorAreaVisible(true);
		IEditorReference[] editors = workbenchPage.getEditorReferences();
		for (int i = 0; i < editors.length; i++) {
			IEditorReference reference = editors[i];
			if (reference.getId().equals(SearchResultBrowser.ID)) {
				IEditorPart editor = reference.getEditor(true);
				workbenchPage.activate(editor);
				((SearchResultBrowser)editor).navigateTo(result.getUrl());
				return (SearchResultBrowser)editor; 
			}
		}
		IEditorPart part = null;
		try {
			part = workbenchPage.openEditor(new SearchResultBrowserInput(result.getUrl()),
											SearchResultBrowser.ID);
		} catch (PartInitException e) {
			TimTamPlugin.getInstance().logException("failed to open a search browser",e, true);
		}
		return (SearchResultBrowser) part;
	}
	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				openPage.run();
			}
		});
	}
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}