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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.timtam.TimTamPlugin;
import org.codehaus.timtam.editors.SpaceEditor;
import org.codehaus.timtam.editors.TimTamPageEditor;
import org.codehaus.timtam.model.ConfluencePage;
import org.codehaus.timtam.model.ConfluenceSpace;
import org.codehaus.timtam.model.TimTamModel;
import org.codehaus.timtam.model.adapters.ServerAdapter;
import org.codehaus.timtam.model.adapters.TreeAdapter;
import org.codehaus.timtam.util.GUIUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.navigator.LocalSelectionTransfer;

import com.atlassian.confluence.remote.NotPermittedException;
import com.atlassian.confluence.remote.RemoteException;

public class ConfluenceView extends ViewPart {
    TreeViewer viewer;
    DrillDownAdapter drillDownAdapter;
    TimTamModel model = TimTamModel.getInstace();
    public final static String CONFLUENCE_TREEVIEW_ID = "org.codehaus.timtam.views.ConfluenceView";
    private Action refreshNode;
    private Action addServer;
    TimTamPlugin plugin;
    private Map actionsMap = new HashMap();

    class NameSorter extends ViewerSorter {
    }

    /**
     * The constructor.
     */
    public ConfluenceView() {
        plugin = TimTamPlugin.getInstance();
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent) {
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        drillDownAdapter = new DrillDownAdapter(viewer);
        viewer.setContentProvider(model.getContentProvider());
        viewer.setLabelProvider(model.getLabelProvider());
        viewer.setSorter(new NameSorter());
        viewer.setInput(model);
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();

        Transfer[] transfers = new Transfer[] { LocalSelectionTransfer
                .getInstance() };
        viewer.addDragSupport(DND.DROP_MOVE, transfers,
                new ConfluenceDragListner(this));
        viewer.addDropSupport(DND.DROP_MOVE, transfers,
                new ConfluenceDropAdapter(this));
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                ConfluenceView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalToolBar(bars.getToolBarManager());
    }

    protected void fillContextMenu(IMenuManager manager) {
        ISelection selection = viewer.getSelection();
        if (selection != null) {
            TreeAdapter adapter = (TreeAdapter) ((IStructuredSelection) selection)
                    .getFirstElement();
            List actions = (List) actionsMap.get(adapter.getType());
            if (actions != null) {
                for (Iterator iter = actions.iterator(); iter.hasNext();) {
                    manager.add((Action) iter.next());
                }
                manager.add(new Separator());
            }
        }
        manager.add(refreshNode);
        manager.add(addServer);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(refreshNode);
        manager.add(addServer);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
    }

    private void makeActions() {
        // general actions
        refreshNode = new Action() {
            public void run() {
                TreeAdapter node = getSelectedNode();
                model.refresh(node);
                viewer.setInput(model);
                viewer.expandToLevel(2);
            }
        };
        refreshNode.setText("Refresh");
        refreshNode.setToolTipText("Refresh The Data For The Selected Item");
        refreshNode.setImageDescriptor(plugin
                .loadImageDescriptor(TimTamPlugin.IMG_REFRESH_NODE));
        refreshNode.setEnabled(model.getServerCount() > 0);
        addServer = new Action() {
            public void run() {
                addServer();
            }
        };
        addServer.setText("Add Confluence Server");
        addServer.setToolTipText("Add a connection to a confluence server");
        addServer.setImageDescriptor(plugin
                .loadImageDescriptor(TimTamPlugin.IMG_ADDSERVER));
        //node specific
        List actions = makePageActions();
        actionsMap.put(TreeAdapter.PAGE, actions);
        actions = makeSpaceActions();
        actionsMap.put(TreeAdapter.SPACE, actions);
        actions = makeServerActions();
        actionsMap.put(TreeAdapter.SERVER, actions);
    }

    protected TreeAdapter getSelectedNode() {
        ISelection selection = viewer.getSelection();
        Object node = ((IStructuredSelection) selection).getFirstElement();
        return (TreeAdapter) node;
    }

    protected TreeAdapter[] getSelectedNodes() {
        IStructuredSelection selection = (IStructuredSelection) viewer
                .getSelection();
        return selectionToAdapters(selection);
    }

    /**
     * @param selection
     * @return
     */
    private TreeAdapter[] selectionToAdapters(IStructuredSelection selection) {
        TreeAdapter selectedNodes[] = new TreeAdapter[selection.size()];
        int i = 0;
        for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
            TreeAdapter element = (TreeAdapter) iterator.next();
            selectedNodes[i++] = element;
        }
        return selectedNodes;
    }

    private List makePageActions() {
        List actionList = new ArrayList();
        Action action = new Action() {
            public void run() {
                openPage();
            }
        };
        action.setText("Edit");
        action
                .setToolTipText("Opens the Selected Pages in a Confluence Editor");
        action.setImageDescriptor(plugin
                .loadImageDescriptor(TimTamPlugin.IMG_EDIT_PAGE));
        actionList.add(action);
        action = new Action() {
            public void run() {
                deletePage();
            }
        };
        action.setText("Delete ...");
        action.setToolTipText("Deletes The Selected Pages");
        action.setImageDescriptor(plugin
                .loadImageDescriptor(TimTamPlugin.IMG_DEL_PAGE));
        actionList.add(action);
        action = new Action() {
            public void run() {
                addChildPage();
            }
        };
        action.setText("Add Child Page...");
        action.setToolTipText("Adds a Child Page To The Selected Page");
        action.setImageDescriptor(plugin
                .loadImageDescriptor(TimTamPlugin.IMG_ADD_CHILD_PAGE));
        actionList.add(action);
        action = new Action() {
            public void run() {
                renamePage();
            }
        };
        action.setText("Rename Page...");
        action.setToolTipText("Rename The Selected Page");
        action.setImageDescriptor(plugin
                .loadImageDescriptor(TimTamPlugin.IMG_RENAME_PAGE));
        // rename is disabled due to (another ) bug in conf CONF-974
        actionList.add(action);
        return actionList;
    }

    private List makeServerActions() {
        List actionList = new ArrayList();
        Action action = new Action() {
            public void run() {
                delServer();
            }
        };
        action.setText("Delete Servers...");
        action.setToolTipText("Delete This Server Entry");
        action.setImageDescriptor(plugin
                .loadImageDescriptor(TimTamPlugin.IMG_DEL_SERVER));
        actionList.add(action);
        return actionList;
    }

    /**
     *  
     */
    public void delServer() {

        ListSelectionDialog dialog = new ListSelectionDialog(getSite()
                .getShell(), model.getServers(), new ArrayContentProvider(),
                model.getLabelProvider(), "Delete Servers");

        //dialog.getOkButton().setText("Delete");
        dialog.setTitle("Delete Servers");
        dialog.setInitialSelections(getSelectedNodes());
        if (dialog.open() == Window.OK) {
            final Object[] serversToDelete = dialog.getResult();
            for (int i = 0; i < serversToDelete.length; i++) {
                ServerAdapter adapter = (ServerAdapter) serversToDelete[i];
                model.deleteServer(adapter);
            }
            viewer.refresh();
        }
        refreshNode.setEnabled(model.getServerCount() > 0);
    }

    private List makeSpaceActions() {
        List actionList = new ArrayList();
        Action action = new Action() {
            public void run() {
                addPage();
            }
        };
        action.setText("Add Page");
        action.setToolTipText("Add a Page To This Space");
        action.setImageDescriptor(plugin
                .loadImageDescriptor(TimTamPlugin.IMG_ADD_PAGE));
        actionList.add(action);
        return actionList;
    }

    /**
     *  
     */
    public void addPage() {
        TreeAdapter selectedNode = getSelectedNode();
        if (selectedNode == null || !(selectedNode instanceof ConfluenceSpace)) {
            MessageDialog.openError(getSite().getShell(), "Add Page",
                    "No space was selected");
            return;
        }

        ConfluenceSpace space = (ConfluenceSpace) selectedNode;
        InputDialog dialog = new InputDialog(getSite().getShell(),
                "New Page Name ?", "Enter New Page Name", null, null);
        if (dialog.open() == Window.OK) {
            Object newPage;
            try {
                newPage = space.createPage(dialog.getValue());
                viewer.refresh();
                viewer.setSelection(new StructuredSelection(
                        new Object[] { newPage }), true);
                openEditor(newPage, TimTamPageEditor.EDITOR_ID);

            } catch (NotPermittedException e) {
                plugin.logException(
                        "You are not premitted to create this page", e, true);
            } catch (RemoteException e) {
                plugin.logException("Create page failed", e, true);
            }
        }
    }

    /**
     *  
     */
    protected void renamePage() {
        ConfluencePage page = (ConfluencePage) getSelectedNode();
        InputDialog dialog = new InputDialog(getSite().getShell(),
                "New Name ?", "Enter New Name", page.getTitle(), null);
        if (dialog.open() == Window.OK) {
            try {
                page.rename(dialog.getValue());
            } catch (Exception e) {
                MessageDialog.openError(getSite().getShell(),
                        "Rename Page Fail",
                        "Failed to Rename Page , See Erorr  Log For Details");
                plugin.logException("failed deleteing pages", e, true);
            }
            viewer.refresh();
        }
    }

    /**
     *  
     */
    public void addChildPage() {
        TreeAdapter selectedNode = getSelectedNode();
        if (selectedNode == null || !(selectedNode instanceof ConfluenceSpace)) {
            MessageDialog.openError(getSite().getShell(), "Add Chils Page",
                    "Please select a parent page");
            return;
        }

        InputDialog dialog = new InputDialog(getSite().getShell(),
                "New Name ?", "Enter New Name", null, null);
        if (dialog.open() == Window.OK) {
            ConfluencePage page = (ConfluencePage) selectedNode;
            Object child;
            try {
                child = page.createPage(dialog.getValue());
                viewer.refresh();
                viewer.setSelection(new StructuredSelection(
                        new Object[] { child }), true);
                openEditor(child, TimTamPageEditor.EDITOR_ID);
            } catch (NotPermittedException e) {
                plugin.logException(
                        "You do not have permission to create a page", e, true);
            } catch (RemoteException e) {
                plugin.logException("Create page failed", e, true);
            }
        }
    }

    /**
     *  
     */
    public void deletePage() {
        final TreeAdapter[] adapters = getSelectedNodes();

        if (adapters.length == 0) {
            MessageDialog.openError(getSite().getShell(), "Delete Pages",
                    "No pages selected");
            return;
        }

        for (int i = 0; i < adapters.length; i++) {
            TreeAdapter adapter = adapters[i];
            if (adapter.getType() != TreeAdapter.PAGE) {
                MessageDialog.openError(getSite().getShell(), "Delete Pages",
                        "Only pages should be selected ");
                return;
            }
        }
        int totalPageCount = totalPageCount(adapters);
        Shell shell = getSite().getShell();
        if (!MessageDialog.openQuestion(shell, "Delete Pages?", "Delete "
                + totalPageCount + " Selected Page"
                + (totalPageCount > 1 ? "s" : ""))) {
            return;
        }
        try {
            new ProgressMonitorDialog(shell).run(false, true,
                    new IRunnableWithProgress() {
                        public void run(IProgressMonitor monitor) {
                            monitor.beginTask("Deleting Pages...",
                                    adapters.length);
                            IWorkbenchPage workbenchPage = PlatformUI
                                    .getWorkbench().getActiveWorkbenchWindow()
                                    .getActivePage();
                            ITreeContentProvider provider = model
                                    .getContentProvider();
                            for (int i = 0; i < adapters.length; i++) {
                                closeAllEditors(workbenchPage, provider,
                                        adapters[i]);
                                ConfluencePage page = (ConfluencePage) adapters[i];
                                monitor.setTaskName("Deleting "
                                        + page.getTitle());
                                try {
                                    page.delete();
                                } catch (NotPermittedException e) {
                                    plugin.logException(
                                            "You do not have permission to delete "
                                                    + page.getTitle(), e, true);
                                } catch (RemoteException e) {
                                    plugin.logException("Failed deleting "
                                            + page.getTitle(), e, true);
                                }
                                monitor.worked(1);
                            }
                            viewer.refresh();
                        }

                        private void closeAllEditors(
                                IWorkbenchPage workbenchPage,
                                ITreeContentProvider provider, Object node) {
                            IEditorPart part = workbenchPage
                                    .findEditor((IEditorInput) node);
                            if (part != null) {
                                workbenchPage.closeEditor(part, true);
                            }
                            if (provider.hasChildren(node)) {
                                Object[] nodes = provider.getChildren(node);
                                for (int i = 0; i < nodes.length; i++) {
                                    closeAllEditors(workbenchPage, provider,
                                            nodes[i]);
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            MessageDialog.openError(shell, "Delete Pages Fail",
                    "Failed to Delete Pages , See Erorr  Log For Details");
            plugin.logException("failed deleteing pages", e, true);
        }
    }

    /**
     * @param adapters
     * @return
     */
    private int totalPageCount(TreeAdapter[] adapters) {
        ITreeContentProvider provider = model.getContentProvider();
        int total = 0;
        for (int i = 0; i < adapters.length; i++) {
            TreeAdapter adapter = adapters[i];
            total += countNodes(provider, adapter);
        }
        return total;
    }

    /**
     * @param provider
     * @param adapter
     * @return
     */
    private int countNodes(ITreeContentProvider provider, Object node) {
        int count = 1;
        if (provider.hasChildren(node)) {
            Object[] nodes = provider.getChildren(node);
            for (int i = 0; i < nodes.length; i++) {
                count += countNodes(provider, nodes[i]);
            }
            return count;
        }
        return 1;
    }

    protected void openPage() {
        TreeAdapter[] adapters = getSelectedNodes();
        for (int i = 0; i < adapters.length; i++) {
            openEditor(adapters[i], TimTamPageEditor.EDITOR_ID);
        }
    }

    /**
     * @param obj
     */
    protected void openEditor(Object obj, String editorId) {
        try {
            IWorkbenchPage workbenchPage = PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getActivePage();
            workbenchPage.setEditorAreaVisible(true);
            workbenchPage.openEditor((IEditorInput) obj, editorId);
        } catch (WorkbenchException e) {
            plugin.logException("failed to open an editor", e, true);
        }
    }

    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                final TreeAdapter adapter = getSelectedNode();
                Integer type = adapter.getType();
                if (type.equals(TreeAdapter.SERVER)) {
                    IRunnableWithProgress op = new IRunnableWithProgress() {
                        public void run(IProgressMonitor monitor) {
                            ((ServerAdapter) adapter).loadSpaces(monitor);
                        }
                    };
                    GUIUtil.runOperationWithProgress(op, null);
                    viewer.setInput(model);
                    viewer.expandToLevel(2);
                } else if (type.equals(TreeAdapter.SPACE)) {
                    openSpace(adapter);
                } else if (type.equals(TreeAdapter.PAGE)) {
                    openPage();
                }
            }
        });
    }

    protected void openSpace(TreeAdapter adapter) {
        openEditor(adapter, SpaceEditor.EDITOR_ID);
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    /**
     * @return
     */
    public Viewer getViewer() {
        return viewer;
    }

    /**
     * @param srcAdapters
     *  
     */
    public boolean doDragAndDrop(final TreeAdapter targetAdapter,
            IStructuredSelection srcAdapters, final int operation) {
        TreeAdapter[] adapters = selectionToAdapters(srcAdapters);
        // no droping into a selected node
        for (int i = 0; i < adapters.length; i++) {
            TreeAdapter adapter = adapters[i];
            if (adapter == targetAdapter) {
                MessageDialog.openInformation(getSite().getShell(),
                        "Invalid Page Tranfer",
                        "Same Page Can Not Be Both Target And Source");
                return false;
            }

        }

        StringBuffer title = new StringBuffer("Selecting OK Will ");
        title.append(operation == DND.DROP_COPY ? "Copy" : "Move");
        title.append(" The Following Pages To : ");
        title.append(targetAdapter.getText());
        ListSelectionDialog dialog = new ListSelectionDialog(getSite()
                .getShell(), adapters, new ArrayContentProvider(), model
                .getLabelProvider(), title.toString());
        dialog.setInitialSelections(adapters);
        if (dialog.open() == Window.OK) {
            final Object[] pagesToCopy = dialog.getResult();
            ProgressMonitorDialog progressMonitor = new ProgressMonitorDialog(
                    getSite().getShell());
            try {
                progressMonitor.run(true, false, new IRunnableWithProgress() {
                    public void run(IProgressMonitor monitor)
                            throws InvocationTargetException,
                            InterruptedException {
                        model.transferPages(targetAdapter, pagesToCopy,
                                operation == DND.DROP_MOVE, monitor);
                    }
                });
            } catch (Exception e) {
                GUIUtil.reportException("Tranfering Pages Failed", e);
                return false;
            }
            viewer.refresh();
            viewer.setSelection(new StructuredSelection(targetAdapter), true);
            return true;
        }
        return false;
    }

    /**
     * @return
     */
    public ISelection getSelection() {
        return viewer.getSelection();
    }

    public void addServer() {
        NewServerConnectionWizard wizard = new NewServerConnectionWizard();
        // Instantiates the wizard container with the wizard and opens
        // it
        WizardDialog dialog = new WizardDialog(getViewSite().getShell(), wizard);
        dialog.create();
        dialog.setTitle("Add New Confluence Server Connection");
        if (dialog.open() == Window.OK) {
            viewer.setInput(model);
            viewer.expandToLevel(2);
            refreshNode.setEnabled(model.getServerCount() > 0);
        }
    }

    /**
     *  
     */
    public void refresh() {
        TreeAdapter[] selectedNodes = getSelectedNodes();
        if (selectedNodes.length == 0) {// refresh all servers
            model.refresh();
        } else {
            for (int i = 0; i < selectedNodes.length; i++) {
                TreeAdapter adapter = selectedNodes[i];
                model.refresh(adapter);
            }
        }
    }
}