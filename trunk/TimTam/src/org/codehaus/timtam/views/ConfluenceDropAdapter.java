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
package org.codehaus.timtam.views;

import org.codehaus.timtam.model.adapters.PageAdapter;
import org.codehaus.timtam.model.adapters.SpaceAdapter;
import org.codehaus.timtam.model.adapters.TreeAdapter;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;

/**
 * @author Zohar
 *
 */
public class ConfluenceDropAdapter extends ViewerDropAdapter {
	private TreeAdapter targetAdapter;
	/**
	 * @param viewer
	 */
	protected ConfluenceDropAdapter(Viewer viewer) {
		super(viewer);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerDropAdapter#performDrop(java.lang.Object)
	 */
	public boolean performDrop(Object data) {
		System.out.println("ConfluenceDropAdapter.performDrop()" + data);
		return true;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerDropAdapter#validateDrop(java.lang.Object, int, org.eclipse.swt.dnd.TransferData)
	 */
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		
		targetAdapter = (TreeAdapter) target;
		System.out.println("ConfluenceDropAdapter.validateDrop() "+target);
		// we can only drop page[s] into a read enabled space/page ( a pagecontainer )
		Integer type = targetAdapter.getType();
		if(type == TreeAdapter.PAGE){
			PageAdapter page = (PageAdapter) targetAdapter;
			return !page.isReadOnly();
		}
		
		if(type == TreeAdapter.SPACE){
			SpaceAdapter space = (SpaceAdapter) targetAdapter;
			return !space.isReadOnly();
		}
		
		return false;
	}
}
