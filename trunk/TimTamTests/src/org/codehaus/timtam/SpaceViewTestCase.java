/*
 * Created on Oct 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.codehaus.timtam;

import org.codehaus.timtam.views.confluencetree.ConfluenceView;
import org.eclipse.jface.viewers.TreeViewer;


/**
 * @author Sarah
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SpaceViewTestCase extends TimTamAbstractTestCase {
	public void testViewDisplaysSpacesInTree() {
		ConfluenceView confluenceView = getConfluenceView();
		TreeViewer confTree = (TreeViewer) confluenceView.getViewer();
		//make sure tree has expected spaces
	}

}
