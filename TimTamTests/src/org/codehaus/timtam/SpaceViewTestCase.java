/*
 * Created on Oct 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.codehaus.timtam;

import org.codehaus.timtam.model.ConfluenceService;
import org.codehaus.timtam.model.ConfluenceSpace;
import org.codehaus.timtam.model.TimTamModel;
import org.codehaus.timtam.model.TimTamServiceFactory;
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

		
		
		// Hacky way of pushing in a mock...
		TimTamServiceFactory original = TimTamModel.getInstance().getServiceFactory();
		TimTamModel.getInstance().setServiceFactory(new TimTamServiceFactory(){
			public ConfluenceService getService(String server, String user,String password, boolean useProxy) {
				// return mock of ConfluenceService
				return getMockConfServer();
			}
		});
		
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

		// you want to do this in a teardown
		TimTamModel.getInstance().setServiceFactory(original);
	}

}
