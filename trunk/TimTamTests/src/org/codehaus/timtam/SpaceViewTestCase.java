/*
 * Created on Oct 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.codehaus.timtam;

import org.codehaus.timtam.model.exceptions.LoginFailureException;
import org.codehaus.timtam.views.confluencetree.ConfluenceView;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;

import com.atlassian.confluence.remote.RemoteException;
import com.atlassian.confluence.remote.RemoteSpaceSummary;


/**
 * @author Sarah
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SpaceViewTestCase extends TimTamAbstractTestCase {
	public void testViewDisplaysSpacesInTree() throws LoginFailureException, InterruptedException, RemoteException {

		// expectations

		RemoteSpaceSummary spaceSummary = new RemoteSpaceSummary();
		spaceSummary.key = "TST";
		spaceSummary.name="Testy";
		spaceSummary.url="testurl";
		mockService.getSpaces();
		serverControl.setReturnValue(new RemoteSpaceSummary[]{spaceSummary});
		serverControl.replay();

		//test
		model.addServer("testserver","testuser","testpassword",false);		
		ConfluenceView confluenceView = getConfluenceView();
		TreeViewer confTree = (TreeViewer) confluenceView.getViewer();
		ITreeContentProvider contentProvider = (ITreeContentProvider )confTree.getContentProvider();
		ILabelProvider labelProvider = (ILabelProvider )confTree.getLabelProvider();
		Object servers[] = contentProvider.getElements(confTree.getInput());
		assertEquals("we expect to see a single server as root",1,servers.length);
		assertEquals("named testuser@testserver","testuser@testserver",labelProvider.getText(servers[0]));
		Object[] spaces =  contentProvider.getChildren(servers[0]);
		assertEquals("we expect to see a single space ",1,spaces.length);
		assertEquals("named Testy","Testy",labelProvider.getText(spaces[0]));
		//verify
		serverControl.verify();
	}

}
