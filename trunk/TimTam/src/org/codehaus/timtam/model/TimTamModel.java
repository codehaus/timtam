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
package org.codehaus.timtam.model;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtil;
import org.codehaus.timtam.TimTamPlugin;
import org.codehaus.timtam.model.adapters.ServerAdapter;
import org.codehaus.timtam.model.adapters.TreeAdapter;
import org.codehaus.timtam.util.GUIUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.atlassian.confluence.remote.RemoteException;
import com.thoughtworks.xstream.XStream;
/**
 * @author zohar melamed
 *  
 */
class AccountDetails {
	String url;
	String user;
	String password;
	/**
	 * @param url
	 * @param user
	 * @param password
	 */
	public AccountDetails(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}
	public boolean equals(Object that) {
		if (this == that)
			return true;
		if (!(that instanceof AccountDetails))
			return false;
		AccountDetails details = (AccountDetails) that;
		return url.equals(details.url) && user.equals(details.user) && password.equals(details.password);
	}
}
public class TimTamModel {
	private TimTamContentProvider contentProvider = new TimTamContentProvider();
	private LabelProvider labelProvider = new TimTamLabelProvider();
	private static TimTamModel instance = new TimTamModel();
	protected List serverAdapters = new ArrayList();
	protected List accountDetails = new ArrayList();
	Map adapterToAccountDetails = new HashMap();
	final TimTamPlugin plugin = TimTamPlugin.getInstance();
	private TimTamModel() {
	}
	/**
	 * @param server
	 * @param user
	 * @param password
	 */
	public void addServer(String server, String user, String password) throws LoginFailureException {
		AccountDetails details = new AccountDetails(server, user, password);
		loadServerData(addServer(details));
		accountDetails.add(details);
	}
	private ServerAdapter addServer(AccountDetails account) throws LoginFailureException {
		ConfluenceService service = ConfluenceService.getService(account.url, account.user, account.password);
		final ServerAdapter adapter = new ServerAdapter(account.url, account.user , service);
		adapterToAccountDetails.put(adapter, account);
		serverAdapters.add(adapter);
		return adapter;
	}
	public void refresh() {
		for (Iterator iter = serverAdapters.iterator(); iter.hasNext();) {
			final ServerAdapter adapter = (ServerAdapter) iter.next();
			loadServerData(adapter);
		}
	}
	public int getServerCount() {
		return serverAdapters.size();
	}
	/**
	 * @param adapter
	 */
	private void loadServerData(final ServerAdapter adapter) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IRunnableWithProgress op = new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) {
						adapter.loadSpaces(monitor);
					}
				};
				GUIUtil.runOperationWithProgress(op, null);
			}
		});
	}
	public ITreeContentProvider getContentProvider() {
		return contentProvider;
	}
	public ILabelProvider getLabelProvider() {
		return labelProvider;
	}
	/**
	 * @return
	 */
	public static TimTamModel getInstace() {
		return instance;
	}
	/**
	 *  
	 */
	public void startup() {
		File accountsFile = TimTamPlugin.getInstance().getStateLocation().append("accounts.xml").toFile();
		if (!accountsFile.exists()) {
			TimTamPlugin.trace("no accounts found at : " + accountsFile.getAbsolutePath());
			return;
		}
		String accountDetailsAsXML = "";
		try {
			accountDetailsAsXML = IOUtil.toString(new FileReader(accountsFile));
		} catch (Exception e) {
			TimTamPlugin.getInstance().logException("faild loading accounts", e, true);
		}
		if (accountDetailsAsXML.length() > 0) {
			XStream xstream = new XStream();
			accountDetails = (List) xstream.fromXML(accountDetailsAsXML);
			for (Iterator iter = accountDetails.iterator(); iter.hasNext();) {
				AccountDetails details = (AccountDetails) iter.next();
				try {
					addServer(details);
				} catch (LoginFailureException e) {
					plugin.logException("failed connecting to server", e, true);
				}
			}
		}
	}
	/**
	 *  
	 */
	public void shutdown() {
		XStream xstream = new XStream();
		String accountDetailsAsXML = xstream.toXML(accountDetails);
		File accountsFile = plugin.getStateLocation().append("accounts.xml").toFile();
		FileWriter writer;
		try {
			writer = new FileWriter(accountsFile);
			writer.write(accountDetailsAsXML);
			writer.close();
		} catch (IOException e) {
			plugin.logException("failed saving accounts", e, true);
		}
	}
	/**
	 * @param adapter
	 */
	public void refresh(final TreeAdapter adapter) {
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) {
				try {
                    adapter.refresh(monitor);
                } catch (RemoteException e) {
                    plugin.logException("Refersh failed",e,true);
                }
			}
		};
		GUIUtil.runOperationWithProgress(op, null);
	}
	/**
	 * @param adapter
	 */
	public void deleteServer(ServerAdapter adapter) {
		serverAdapters.remove(adapter);
		accountDetails.remove(adapterToAccountDetails.get(adapter));
	}

	
	public boolean transferPages(TreeAdapter targetAdapter, Object[] pagesToCopy, boolean move, IProgressMonitor monitor) {
		PageContainer container = (PageContainer) targetAdapter;
		try {
			container.transferPages(pagesToCopy, move, monitor);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * @param string
	 * @throws RemoteException
	 */
	public Collection search(String query) throws RemoteException {
		// run search on all servers and agg results
		ArrayList result = new ArrayList();
		for (Iterator adapter = serverAdapters.iterator(); adapter.hasNext();) {
			ServerAdapter server = (ServerAdapter) adapter.next();
			SearchResult[] results = server.search(query);
			result.addAll(Arrays.asList(results));
		}
		
		return result;
	}
	/**
	 * @return
	 */
	public ServerAdapter[] getServers() {
		return (ServerAdapter[]) serverAdapters.toArray(new ServerAdapter[serverAdapters.size()]);
	}
}
class TimTamLabelProvider extends LabelProvider {
	public Image getImage(Object element) {
		TreeAdapter adapter = (TreeAdapter) element;
		return adapter.getImage();
	}
	public String getText(Object element) {
		TreeAdapter adapter = (TreeAdapter) element;
		return adapter.getText();
	}
}
class TimTamContentProvider implements ITreeContentProvider {
	public Object[] getChildren(Object parentElement) {
		TreeAdapter adapter = (TreeAdapter) parentElement;
		return adapter.getChildren();
	}
	public Object getParent(Object element) {
		TreeAdapter adapter = (TreeAdapter) element;
		return adapter.getParent();
	}
	public boolean hasChildren(Object element) {
		TreeAdapter adapter = (TreeAdapter) element;
		return adapter.hasChildren();
	}
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof TimTamModel) {
			TimTamModel root = (TimTamModel) inputElement;
			return root.serverAdapters.toArray();
		}
		TreeAdapter adapter = (TreeAdapter) inputElement;
		return adapter.getChildren();
	}
	public void dispose() {
		// TODO Auto-generated method stub
	}
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		//like i care ...
	}
}