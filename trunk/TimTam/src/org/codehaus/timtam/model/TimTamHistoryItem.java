/*
 * Created on Jun 7, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.codehaus.timtam.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.codehaus.timtam.TimTamPlugin;
import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

import com.atlassian.confluence.remote.RemoteException;
import com.atlassian.confluence.remote.RemotePageHistory;

/**
 * @author Sarah
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TimTamHistoryItem implements IStreamContentAccessor, ITypedElement, IModificationDate {

	private  String content;
	private  ConfluencePage page;
	private  String version;
	private  long   modeDate;
	private  long   pageId;

	/**
	 * @param history
	 * @param page
	 */
	public TimTamHistoryItem(RemotePageHistory history, ConfluencePage page) {
		this.page     = page;
		this.version  = Long.toString(history.version);
		this.modeDate = history.modified.getTime();
		this.pageId   = history.id;
	}

	/**
	 * @param confPage
	 * @throws RemoteException
	 */
	public TimTamHistoryItem(ConfluencePage page) throws RemoteException {
		this.page = page;
		this.version  = "Current";
		this.modeDate = page.getModified().getTime();
		this.pageId   = page.getId();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.compare.IStreamContentAccessor#getContents()
	 */
	public InputStream getContents() throws CoreException {
		if(content == null){
			try {
				content = page.getContent(pageId);
			} catch (RemoteException e) {
				//TODO fill this in 
			}
		}
		return new ByteArrayInputStream(content.getBytes());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.compare.ITypedElement#getName()
	 */
	public String getName() {
		return page.getTitle()+" - Version "+version;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.compare.ITypedElement#getImage()
	 */
	public Image getImage() {
		return TimTamPlugin.getInstance().getPageIcon(page);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.compare.ITypedElement#getType()
	 */
	public String getType() {
		return "Confluence Page";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.compare.IModificationDate#getModificationDate()
	 */
	public long getModificationDate() {
		return modeDate;
	}

	/**
	 * @param histories
	 * @return
	 */
	public static TimTamHistoryItem[] creatHistoryItems(ConfluencePage page,RemotePageHistory[] histories) {
		TimTamHistoryItem [] results = new TimTamHistoryItem[histories.length];
		for (int i = 0; i < histories.length; i++) {
			RemotePageHistory history = histories[i];
			results[i] = new TimTamHistoryItem(history,page);
		}

		return results;
	}

}
