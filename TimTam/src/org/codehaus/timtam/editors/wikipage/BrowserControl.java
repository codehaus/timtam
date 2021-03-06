
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
package org.codehaus.timtam.editors.wikipage;
import org.codehaus.timtam.TimTamPlugin;
import org.codehaus.timtam.model.ConfluencePage;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IActionBars;

import com.atlassian.confluence.remote.RemoteException;
/**
 * @author Zohar
 * 
 * 
 * Preferences - Java - Code Generation - Code and Comments
 */
public class BrowserControl extends Composite {
	/** The browser widget used for the preview */
	Browser browser;
	ConfluencePage page;
	private Action back;
	private Action forward;
	private Action stop;
	private Action refresh;
	private int navigationCount;
	private boolean backPressed;
	
	/**
	 * @param parent
	 * @param style
	 */
	public BrowserControl(Composite parent, int style, IActionBars actionBars, ConfluencePage page) {
		super(parent, style);
		createControls(actionBars);
		this.page = page;
	}
	/**
	 *  
	 */
	private void createControls(final IActionBars actionBars) {
		FormLayout formLayout = new FormLayout();
		formLayout.marginHeight = 3;
		formLayout.marginWidth = 3;
		setLayout(formLayout);
		ToolBarManager manager = new ToolBarManager(SWT.FLAT);
		createBrowserToolBar(manager);
			
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = GridData.CENTER;
		gd.horizontalAlignment = GridData.FILL;
		ToolBar toolBar = manager.createControl(this);
		FormData data = new FormData();
		toolBar.setLayoutData(data);
		browser = new Browser(this, SWT.NONE);
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(toolBar, 0, SWT.DEFAULT);
		data.bottom = new FormAttachment(100, 0);
		browser.setLayoutData(data);
		browser.addProgressListener(new ProgressAdapter() {
			IProgressMonitor monitor = actionBars.getStatusLineManager().getProgressMonitor();
			boolean working = false;
			int workedSoFar;
			public void changed(ProgressEvent event) {
				if (event.total == 0)
					return;
				if (!working) {
					if (event.current == event.total)
						return;
					monitor.beginTask("", event.total); //$NON-NLS-1$
					workedSoFar = 0;
					working = true;
					stop.setEnabled(true);
				}
				
				monitor.worked(event.current - workedSoFar);
				workedSoFar = event.current;
			}
			public void completed(ProgressEvent event) {
				monitor.done();
				working = false;
				stop.setEnabled(false);
				if(backPressed){
					navigationCount--;
					backPressed = false;
				}else{
					String url = browser.getUrl();
					if(url.startsWith("http")){// we mive somewhere
						navigationCount++;
					}
				}
				updateNavigationStatus();
			}
		});
		browser.addStatusTextListener(new StatusTextListener() {
			IStatusLineManager status = actionBars.getStatusLineManager();
			public void changed(StatusTextEvent event) {
				status.setMessage(event.text);
			}
		});
		browser.addLocationListener(new LocationAdapter() {
			public void changed(LocationEvent event) {
				updateNavigationStatus();
			}
		});
	}
	/**
	 * 
	 */
	private void updateNavigationStatus() {
		back.setEnabled(navigationCount > 0);
		forward.setEnabled(browser.isForwardEnabled());
	}
	
	private void createBrowserToolBar(ContributionManager manager) {

		back = new Action() {
			public void run() {
				backPressed = true;
				if(navigationCount > 1){
					browser.back();
				}else{
					try {
                        browser.setText(page.renderContent());
                    } catch (RemoteException e) {
                        TimTamPlugin.getInstance().logException("failed to get page content",e,true);
                    }
				}
				
				updateNavigationStatus() ;
			}
		};
		back.setToolTipText("Back");
		ImageRegistry imageRegistry = TimTamPlugin.getInstance().getImageRegistry();
		back.setImageDescriptor(imageRegistry.getDescriptor(TimTamPlugin.IMG_BROWSER_BACK));
		back.setEnabled(false);
		manager.add(back);
		forward = new Action() {
			public void run() {
				browser.forward();
				updateNavigationStatus();
			}
		};
		forward.setToolTipText("Forward");
		forward.setImageDescriptor(imageRegistry.getDescriptor(TimTamPlugin.IMG_BROWSER_FORWARD));
		forward.setEnabled(false);
		manager.add(forward);
		stop = new Action() {
			public void run() {
				browser.stop();
				backPressed = false;
			}
		};
		stop.setToolTipText("Stop");
		stop.setEnabled(false);
		stop.setImageDescriptor(imageRegistry.getDescriptor(TimTamPlugin.IMG_BROWSER_STOP));
		manager.add(stop);
		refresh = new Action() {
			public void run() {
				if (navigationCount == 0){
					page.refresh();
					try {
                        browser.setText(page.renderContent());
                    } catch (RemoteException e) {
                        TimTamPlugin.getInstance().logException("failed to get page content",e,true);
                    }
				} else {
					browser.refresh();
				}
			}
		};
		refresh.setToolTipText("Refresh");
		refresh.setImageDescriptor(imageRegistry.getDescriptor(TimTamPlugin.IMG_BROWSER_REFRESH));
		manager.add(refresh);
		refresh.setEnabled(true);
		manager.update(false);
	}
	/**
	 * @param string
	 */
	public void setText(String text) {
		browser.setText(text);
		updateNavigationStatus();
	}
}