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

package org.codehaus.timtam.editors.wikipage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * @author MelamedZ
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ColorManager {
	
	protected static Map fColorTable = new HashMap(10);
	
	static{
		fColorTable.put(ConfluencePartitionScanner.MARKUP,new Color(Display.getCurrent(), new RGB(0,0,255)));
		fColorTable.put(ConfluencePartitionScanner.LINK,new Color(Display.getCurrent(), new RGB(0,128,0)));
		fColorTable.put(ConfluencePartitionScanner.MACRO,new Color(Display.getCurrent(), new RGB(0,0,255)));
		fColorTable.put(ConfluencePartitionScanner.HEADINGS,new Color(Display.getCurrent(), new RGB(255,0,0)));
	}
	
	public void dispose() {
		Iterator e = fColorTable.values().iterator();
		while (e.hasNext())
			 ((Color) e.next()).dispose();
	}

	/**
	 * @param string
	 * @return
	 */
	public static Color getColor(String key) {
		return (Color) fColorTable.get(key);
	}

}
