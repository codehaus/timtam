/*
 * Created on Jun 6, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.codehaus.timtam.model.search;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;


public class SearchResultBrowserInput implements IEditorInput {
	private String url;
	/**
	 * @param result
	 */
	public SearchResultBrowserInput(String url) {
		this.url = url;
	}
	
	public boolean exists() {
		return false;
	}
	public Object getAdapter(Class adapter) {
		return null;
	}
	
	public ImageDescriptor getImageDescriptor() {
		return null;
	}
	public String getName() {
		return "Search Results Browser";
	}
	
	public IPersistableElement getPersistable() {
		return new IPersistableElement() {
			public String getFactoryId() {
				return SearchElementFactory.ID;
			}
			public void saveState(IMemento memento) {
				memento.putString("url", url);
			}
		};
	}
	public boolean equals(Object o) {
		if ((o != null) && (o instanceof SearchResultBrowserInput)) {
			return true;
		}
		return false;
	}
	public String getToolTipText() {
		return null; //$NON-NLS-1$	
	}
	/**
	 * @return Returns the url.
	 */
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url; 
	}

	
}