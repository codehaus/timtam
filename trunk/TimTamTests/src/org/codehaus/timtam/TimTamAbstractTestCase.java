/*
 * Created on Oct 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.codehaus.timtam;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import junit.framework.TestCase;

/**
 * @author Sarah
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TimTamAbstractTestCase extends TestCase{
	
	private MockConfluenceServer mockConfServer;
	private static final String TIMTAM_PERSPECTIVE = "org.codehaus.timtam.perspective.TimTamPerspective";

	/*  
	 * open the timtam perspective, 
	 * and hook up a mook confluence server 
	 * 
	 */
	protected void setUp() throws Exception {
		mockConfServer = new MockConfluenceServer();
		
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		PlatformUI.getWorkbench().showPerspective(TIMTAM_PERSPECTIVE,activeWorkbenchWindow);
	}

	/* Disconect from the mock timtam server and
	 * close the timtam perspective
	 */
	protected void tearDown() throws Exception {
		mockConfServer = null;
	}
	
	/**
	 * @return Returns the mockConfServer.
	 */
	public MockConfluenceServer getMockConfServer() {
		return mockConfServer;
	}
}
