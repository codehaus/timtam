/*
 * Created on Jun 7, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.codehaus.timtam.model;

import java.io.InputStream;

import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

/**
 * @author Sarah
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TimTamHistoryItem implements IStreamContentAccessor, ITypedElement, IModificationDate {

	/* (non-Javadoc)
	 * @see org.eclipse.compare.IStreamContentAccessor#getContents()
	 */
	public InputStream getContents() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.compare.ITypedElement#getName()
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.compare.ITypedElement#getImage()
	 */
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.compare.ITypedElement#getType()
	 */
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.compare.IModificationDate#getModificationDate()
	 */
	public long getModificationDate() {
		// TODO Auto-generated method stub
		return 0;
	}

}
