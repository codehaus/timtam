/*
 * Created on Oct 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.codehaus.timtam;

import junit.framework.TestCase;

import org.codehaus.timtam.model.ConfluenceService;
import org.codehaus.timtam.model.TimTamModel;
import org.codehaus.timtam.model.TimTamServiceFactory;
import org.codehaus.timtam.views.confluencetree.ConfluenceView;
import org.easymock.MockControl;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Sarah
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TimTamAbstractTestCase extends TestCase {

	private static final String TIMTAM_PERSPECTIVE = "org.codehaus.timtam.perspective.TimTamPerspective";

	private IWorkbenchWindow activeWorkbenchWindow;

	private static final String CONFLUENCE_VIEW = "org.codehaus.timtam.views.ConfluenceView";

	protected ConfluenceService mockService;

	protected TimTamModel model;

	private TimTamServiceFactory original;

	protected MockControl serverControl;

	/*
	 * open the timtam perspective, and hook up a mook confluence server
	 *  
	 */
	protected void setUp() throws Exception {
		serverControl = MockControl
						.createControl(ConfluenceService.class);
		final ConfluenceService mockService = (ConfluenceService) serverControl
				.getMock();
		this.mockService = mockService;
		model = TimTamModel.getInstance();
		original = model.getServiceFactory();
		model.setServiceFactory(new TimTamServiceFactory() {
			public ConfluenceService getService(String server, String user,
					String password, boolean useProxy) {
				// return mock of ConfluenceService
				return mockService;
			}
		});

		activeWorkbenchWindow = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		PlatformUI.getWorkbench().showPerspective(TIMTAM_PERSPECTIVE,
				activeWorkbenchWindow);
	}

	/*
	 * Disconect from the mock timtam server and close the timtam perspective
	 */
	protected void tearDown() throws Exception {
		model.setServiceFactory(original);

	}

	/**
	 *  
	 */
	protected ConfluenceView getConfluenceView() {
		return (ConfluenceView) activeWorkbenchWindow.getActivePage().findView(
				CONFLUENCE_VIEW);
	}
}