/*
 * Created on Jun 6, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.codehaus.timtam.model.search;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 * @author Sarah
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SearchElementFactory implements IElementFactory{

	public final static String ID =  "org.codehaus.timtam.model.SearchElementFactory";
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IElementFactory#createElement(org.eclipse.ui.IMemento)
	 */
	public IAdaptable createElement(IMemento memento) {
		SearchResultBrowserInput browserInput = new SearchResultBrowserInput(memento.getString("url"));
		return browserInput;
	}

}
