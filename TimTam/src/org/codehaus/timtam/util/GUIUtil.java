/*
 *
 * Copyright 2003(c)  Zohar Melamed
 * All rights reserved.
 *

 Redistribution and use of this software and associated documentation
 ("Software"), with or without modification, are permitted provided
 that the following conditions are met:

 1. Redistributions of source code must retain copyright
 statements and notices.  Redistributions must also contain a
 copy of this document.

 2. Redistributions in binary form must reproduce the
 above copyright notice, this list of conditions and the
 following disclaimer in the documentation and/or other
 materials provided with the distribution.

 3. Due credit should be given to The Codehaus and Contributors
 http://timtam.codehaus.org/

 THIS SOFTWARE IS PROVIDED BY THE CODEHAUS AND CONTRIBUTORS
 ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 THE CODEHAUS OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 OF THE POSSIBILITY OF SUCH DAMAGE.

 *
 *
 */
package org.codehaus.timtam.util;

import org.codehaus.timtam.TimTamPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Zohar
 *  
 */
public class GUIUtil {
	public static void runOperationWithProgress(
			IRunnableWithProgress operation, final Shell shell) {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.run(true, false, operation);
		} catch (final Exception e) {
			reportException("TimTam Error", e);
		}

	}

	public static void execOnDidplayThread(Runnable op, boolean async) {
		if (async) {
			Display.getDefault().asyncExec(op);
		} else {
			Display.getDefault().syncExec(op);
		}
	}

	/**
	 * @param string
	 * @param e
	 */
	public static void reportException(final String title, final Exception e) {
		execOnDidplayThread(new Runnable() {
			public void run() {
				MessageDialog.openError(null, title, e.getMessage());
			}
		}, false);

		TimTamPlugin.getInstance().logException(title, e, true);

	}

	public static void safeWarn(final String title, final String msg) {
		execOnDidplayThread(new Runnable() {
			public void run() {
				MessageDialog.openWarning(
						Display.getDefault().getActiveShell(), title, msg);
			}
		}, false);
	}

	public static IViewPart getView(String viewId) {
		try {
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().showView(viewId);
		} catch (PartInitException e) {
			TimTamPlugin.getInstance().logException("searching for view "+viewId,e, true);
		}
		return null;
	}

}