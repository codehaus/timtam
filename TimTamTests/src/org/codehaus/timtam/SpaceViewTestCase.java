/*
 * Created on Oct 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.codehaus.timtam;

import org.codehaus.timtam.model.ConfluenceSpace;
import org.codehaus.timtam.views.confluencetree.ConfluenceView;
import org.easymock.MockControl;
import org.eclipse.jface.viewers.TreeViewer;


/**
 * @author Sarah
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SpaceViewTestCase extends TimTamAbstractTestCase {
	public void testViewDisplaysSpacesInTree() {
		///setup 
		MockControl control = MockControl.createControl(ConfluenceSpace.class);
		ConfluenceSpace mockSpace = (ConfluenceSpace) control.getMock();
		// expectations
		mockSpace.getName();
		control.setReturnValue("MockSpace");
		control.replay();
		//test
		getMockConfServer().addSpace(mockSpace);
		ConfluenceView confluenceView = getConfluenceView();
		TreeViewer confTree = (TreeViewer) confluenceView.getViewer();
		Object[] expandedElements = confTree.getExpandedElements();
		assertEquals("we expect to see a server and a single space",2,expandedElements.length);
		
		//verify 
		control.verify();
	}

}
