/*
 * Created on 06-Aug-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.codehaus.timtam;

import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;

/**
 * @author MelamedZ
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TimTamApplication implements IPlatformRunnable {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IPlatformRunnable#run(java.lang.Object)
	 */
	
	public Object run(Object args) throws Exception {
		WorkbenchAdvisor workbenchAdvisor = new TimTamWorkbenchAdvisor();
		Display display = PlatformUI.createDisplay();
		// this sucks - must make sure image reg is created on the ui thread...
		TimTamPlugin.getInstance().init();
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, workbenchAdvisor);
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IPlatformRunnable.EXIT_RESTART;
			} else {
				return IPlatformRunnable.EXIT_OK;
			}
		} finally {
			display.dispose();
		}
	}

}