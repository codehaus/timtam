/*
 * Created on Sep 12, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.codehaus.timtam.editors.wikipage;

import java.util.LinkedList;

import org.codehaus.timtam.TimTamPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IFormattingContext;

/**
 * @author Sarah
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TimtamtFormatingStrategy extends ContextBasedFormattingStrategy{
	/** Documents to be formatted by this strategy */
	private final LinkedList fDocuments= new LinkedList();
	private int maxLineLength = 80;
		
	public void format() {
		super.format();
		
		final IDocument document= (IDocument) fDocuments.removeFirst();
			
		if (document != null) {
			int numberOfLines = document.getNumberOfLines();
			for(int lineNumber = 0; lineNumber <  numberOfLines; ++lineNumber){
				try {
					String line = document.get(document.getLineOffset(lineNumber),document.getLineLength(lineNumber));
					if(line.length()> maxLineLength){
						
					}
				} catch (BadLocationException e) {
					TimTamPlugin.getInstance().logException("Failed to format document",e,true);
				} 
			}
		}
	}

	/*
	 * @see org.eclipse.jface.text.formatter.IFormattingStrategyExtension#formatterStarts(org.eclipse.jface.text.formatter.IFormattingContext)
	 */
	public void formatterStarts(IFormattingContext context) {
		super.formatterStarts(context);
		fDocuments.addLast(context.getProperty(FormattingContextProperties.CONTEXT_MEDIUM));
	}

	
	/*
	 * @see org.eclipse.jface.text.formatter.IFormattingStrategyExtension#formatterStops()
	 */
	public void formatterStops() {
		fDocuments.clear();
		super.formatterStops();
	}
}
