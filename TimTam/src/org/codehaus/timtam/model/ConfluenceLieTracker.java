/*
 * Created on Sep 19, 2004
 *
 */
package org.codehaus.timtam.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ILineTracker;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;


/**
 * @author Zohar
 *
 */
public class ConfluenceLieTracker implements ILineTracker{
	private final static String[] DELIMITERS= { "\r", "\n", "\r\n"}; //$NON-NLS-3$ //$NON-NLS-1$ //$NON-NLS-2$
	private Document document;
	
	/**
	 * Combines the information of the occurrence of a line delimiter.
	 * <code>delimiterIndex</code> is the index where a line delimiter
	 * starts, whereas <code>delimiterLength</code>, indicates the length
	 * of the delimiter. 
	 */
	protected static class DelimiterInfo {
		public int delimiterIndex;
		public int delimiterLength;
		public String delimiter;
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
				return "index = "+delimiterIndex+" length = "+delimiterLength;
		}
	}
		
	
	/** The line information */
	private List fLines= new ArrayList();
	/** The length of the tracked text */
	private int fTextLength;
	
	
	/**
	 * Binary search for the line at a given offset.
	 *
	 * @param offset the offset whose line should be found
	 * @return the line of the offset
	 */
	private int findLine(int offset) {
		
		if (fLines.size() == 0)
			return -1;
			
		int left= 0;
		int right= fLines.size() -1;
		int mid= 0;
		Line line= null;
		
		while (left < right) {
			
			mid= (left + right) / 2;
				
			line= (Line) fLines.get(mid);
			if (offset < line.offset) {
				if (left == mid)
					right= left;
				else
					right= mid -1;
			} else if (offset > line.offset) {
				if (right == mid)
					left= right;
				else
					left= mid  +1;
			} else if (offset == line.offset) {
				left= right= mid;
			}
		}
		
		line= (Line) fLines.get(left);
		if (line.offset > offset)
			-- left;		
		return left;
	}
	
	/**
	 * Returns the number of lines covered by the specified text range.
	 *
	 * @param startLine the line where the text range starts
	 * @param offset the start offset of the text range
	 * @param length the length of the text range
	 * @return the number of lines covered by this text range
	 * @exception BadLocationException if range is undefined in this tracker
	 */
	private int getNumberOfLines(int startLine, int offset, int length) throws BadLocationException {
		
		if (length == 0)
			return 1;
			
		int target= offset + length;
		
		Line l= (Line) fLines.get(startLine);
		
		if (l.delimiter == null)
			return 1;
		
		if (l.offset + l.length > target)
			return 1;
		
		if (l.offset + l.length == target)
			return 2;
			
		return getLineNumberOfOffset(target) - startLine + 1;
	}
	
	/*
	 * @see org.eclipse.jface.text.ILineTracker#getLineLength(int)
	 */
	public int getLineLength(int line) throws BadLocationException {
		
		int lines= fLines.size();
		
		if (line < 0 || line > lines)
			throw new BadLocationException();
		
		if (lines == 0 || lines == line)
			return 0;
				
		Line l= (Line) fLines.get(line);
		return l.length;
	}
		
	/*
	 * @see org.eclipse.jface.text.ILineTracker#getLineNumberOfOffset(int)
	 */
	public int getLineNumberOfOffset(int position) throws BadLocationException {
				
		if (position > fTextLength)
			throw new BadLocationException();
			
		if (position == fTextLength) {
			
			int lastLine= fLines.size() - 1;
			if (lastLine < 0)
				return 0;
							
			Line l= (Line) fLines.get(lastLine);
			return (l.delimiter != null ? lastLine + 1 : lastLine);
		}
		
		return findLine(position);
	}
	
	/*
	 * @see org.eclipse.jface.text.ILineTracker#getLineInformationOfOffset(int)
	 */
	public IRegion getLineInformationOfOffset(int position) throws BadLocationException {		
		if (position > fTextLength)
			throw new BadLocationException();
			
		if (position == fTextLength) {
			int size= fLines.size();
			if (size == 0)
				return new Region(0, 0);
			Line l= (Line) fLines.get(size - 1);
			return (l.delimiter != null ? new Line(fTextLength, 0) : new Line(fTextLength - l.length, l.length));
		}	
		
		return getLineInformation(findLine(position));
	}
	
	/*
	 * @see org.eclipse.jface.text.ILineTracker#getLineInformation(int)
	 */
	public IRegion getLineInformation(int line) throws BadLocationException {
		
		int lines= fLines.size();
		
		if (line < 0 || line > lines)
			throw new BadLocationException();
			
		if (lines == 0)
			return new Line(0, 0);
			
		if (line == lines) {
			Line l= (Line) fLines.get(line - 1);
			return new Line(l.offset + l.length, 0);
		}
		
		Line l= (Line) fLines.get(line);
		return (l.delimiter != null ? new Line(l.offset, l.length - l.delimiter.length()) : l);
	}
		
	/*
	 * @see org.eclipse.jface.text.ILineTracker#getLineOffset(int)
	 */
	public int getLineOffset(int line) throws BadLocationException {
		
		int lines= fLines.size();
		
		if (line < 0 || line > lines)
			throw new BadLocationException();
		
		if (lines == 0)
			return 0;
			
		if (line == lines) {
			Line l= (Line) fLines.get(line - 1);
			return l.offset + l.length;
		}
		
		Line l= (Line) fLines.get(line);
		return l.offset;
	}
		
	/*
	 * @see org.eclipse.jface.text.ILineTracker#getNumberOfLines()
	 */
	public int getNumberOfLines() {
		
		int lines= fLines.size();
		
		if (lines == 0)
			return 1;
			
		Line l= (Line) fLines.get(lines - 1);
		return (l.delimiter != null ? lines + 1 : lines);
	}
		
	/*
	 * @see org.eclipse.jface.text.ILineTracker#getNumberOfLines(int, int)
	 */
	public int getNumberOfLines(int position, int length) throws BadLocationException {
		
		if (position < 0 || position + length > fTextLength)
			throw new BadLocationException();
			
		if (length == 0) // optimization
			return 1;
			
		return getNumberOfLines(getLineNumberOfOffset(position), position, length);
	}
	
	/*
	 * @see org.eclipse.jface.text.ILineTracker#computeNumberOfLines(java.lang.String)
	 */
	public int computeNumberOfLines(String text) {
		int count= 0;
		int start= 0;
		DelimiterInfo delimiterInfo= nextDelimiterInfo(text, start);
		while (delimiterInfo != null && delimiterInfo.delimiterIndex > -1) {
			++count;			
			start= delimiterInfo.delimiterIndex + delimiterInfo.delimiterLength;
			delimiterInfo= nextDelimiterInfo(text, start);
		}
		return count;	
	}
	
	
	/* ----------------- manipulation ------------------------------ */
	
	/**
	 * Creates the line structure for the given text. Newly created lines
	 * are inserted into the line structure starting at the given
	 * position. Returns the number of newly created lines.
	 *
	 * @param text the text for which to create a line structure
	 * @param insertPosition the position at which the newly created lines are inserted
	 * 		into the tracker's line structure
	 * @param offset the offset of all newly created lines
	 * @return the number of newly created lines
	 */
	private int createLines(String text, int insertPosition, int offset) {
		
		int count= 0;
		int start= 0;
		DelimiterInfo delimiterInfo= nextDelimiterInfo(text, 0);
		
		
		while (delimiterInfo != null && delimiterInfo.delimiterIndex > -1) {
			
			int index= delimiterInfo.delimiterIndex + (delimiterInfo.delimiterLength - 1);
			
			if (insertPosition + count >= fLines.size())
				fLines.add(new Line(offset + start, offset + index, delimiterInfo.delimiter));
			else 
				fLines.add(insertPosition + count, new Line(offset + start, offset + index, delimiterInfo.delimiter));
				
			++count;
			start= index + 1;
			delimiterInfo= nextDelimiterInfo(text, start);
		}
		
		if (start < text.length()) {
			if (insertPosition + count < fLines.size()) {
				// there is a line below the current
				Line l= (Line) fLines.get(insertPosition + count);
				int delta= text.length() - start;
				l.offset -= delta;
				l.length += delta;
			} else {
				fLines.add(new Line(offset + start, offset + text.length() - 1, null));
				++count;
			}
		}
		
		return count;
	}
	
	/**
	 * Keeps track of the line information when text is inserted.
	 * Returns the number of inserted lines.
	 *
	 * @param lineNumber the line at which the insert happens
	 * @param offset at which the insert happens
	 * @param text the inserted text
	 * @return the number of inserted lines
	 * @exception BadLocationException if offset is invalid in this tracker
	 */
	private int insert(int lineNumber, int offset, String text) throws BadLocationException {
		
		if (text == null || text.length() == 0)
			return 0;
			
		fTextLength += text.length();
		
		int size= fLines.size();
		
		if (size == 0 || lineNumber >= size)
			return createLines(text, size, offset);
					
		Line line= (Line) fLines.get(lineNumber);
		DelimiterInfo delimiterInfo= nextDelimiterInfo(text, 0);
		if (delimiterInfo == null || delimiterInfo.delimiterIndex == -1) {
			line.length += text.length();
			return 0;
		
		}
		
		
		// as there is a line break, split line but do so only if rest of line is not of length 0
		int restLength= line.offset + line.length - offset;
		if (restLength > 0) {
			// determine start and end of the second half of the splitted line
			Line lineRest= new Line(offset, restLength);
			lineRest.delimiter= line.delimiter;
			// shift it by the inserted text
			lineRest.offset += text.length();
			//  and insert in line structure
			fLines.add(lineNumber + 1, lineRest);
		}
		
		// adapt the beginning of the splitted line
		line.delimiter= delimiterInfo.delimiter;
		int nextStart= offset + delimiterInfo.delimiterIndex + delimiterInfo.delimiterLength;
		line.length= nextStart - line.offset;
		
		// insert lines for the remaining text
		//text= text.substring(delimiterInfo.delimiterIndex + delimiterInfo.delimiterLength);
		return createLines(text, lineNumber + 1, nextStart) + 1;
	}
		
	/**
	 * @param lineNumber
	 * @return
	 */
	private int findLineBreak(int lineNumber) {
		try {
			int lineOffset = getLineOffset(lineNumber);
			String line = document.get().substring(lineOffset,lineOffset+80);
			return 	lineOffset + findLineBreak(line);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	private int findLineBreak(String line) {
		int lastSpace = line.lastIndexOf(" ") ;
		return lastSpace == -1?0:lastSpace;
	}

	/**
	 * Keeps track of the line information when text is removed. Returns 
	 * whether the line at which the deletion start will thereby be deleted.
	 *
	 * @param lineNumber the lineNumber at which the deletion starts
	 * @param offset the offset of the first deleted character
	 * @param length the number of deleted characters 
	 * @return <code>true</code> if the start line has been deleted, <code>false</code> otherwise
	 * @exception BadLocationException if position is unknown to the tracker
	 */
	private boolean remove(int lineNumber, int offset, int length) throws BadLocationException {
		
		if (length == 0)
			return false;		
		
		int removedLineEnds= getNumberOfLines(lineNumber, offset, length) - 1;
		Line line= (Line) fLines.get(lineNumber);
		
		if ((lineNumber == fLines.size() - 1) && removedLineEnds > 0) {
			line.length -= length; 
			line.delimiter= null;
		} else {
			
			++ lineNumber;
			for (int i= 1; i <= removedLineEnds; i++) {
				
				if (lineNumber == fLines.size()) {
					line.delimiter= null;
					break;
				}
				
				Line line2= (Line) fLines.get(lineNumber);
				line.length += line2.length;
				line.delimiter= line2.delimiter;
				fLines.remove(lineNumber);
			}
			line.length -= length;
		}
		
		fTextLength -= length;
		
		if (line.length == 0) {
			fLines.remove(line);
			return true;
		}
		
		return false;	
	}
	
	/**
	 * Adapts the offset of all lines with line numbers greater than the specified
	 * one to the given delta.
	 *
	 * @param lineNumber the line number after which to start
	 * @param delta the offset delta to be applied
	 */
	private void adaptLineOffsets(int lineNumber, int delta) {
		int size= fLines.size();
		for (int i= lineNumber + 1; i < size; i++) {
			Line l= (Line) fLines.get(i);
			l.offset += delta;
		}
	}
	
	/*
	 * @see org.eclipse.jface.text.ILineTracker#replace(int, int, java.lang.String)
	 */
	public void replace(int position, int length, String text) throws BadLocationException {
		
		int firstLine= getLineNumberOfOffset(position);
		int insertLineNumber= firstLine;
		
		if (remove(firstLine, position, length))
			-- firstLine;
			
		int lastLine= firstLine + insert(insertLineNumber, position, text);
		
		
		int delta= -length;
		if (text != null)
			delta= text.length() + delta;
		
		if (delta != 0)
			adaptLineOffsets(lastLine, delta);
	}
	
	/*
	 * @see org.eclipse.jface.text.ILineTracker#set(java.lang.String)
	 */
	public void set(String text) {
		fLines.clear();
		if (text != null) {
			fTextLength= text.length();
			createLines(text, 0, 0);
		}
	}
	
	/*
	 * @see org.eclipse.jface.text.ILineTracker#getLineDelimiter(int)
	 */
	public String getLineDelimiter(int line) throws BadLocationException {
		
		int lines= fLines.size();
		
		if (line < 0 || line > lines)
			throw new BadLocationException();
			
		if (lines == 0)
			return null;
			
		if (line == lines)
			return null;
					
		Line l= (Line) fLines.get(line);
		return l.delimiter;
	}

	/**
	 * @param document
	 */
	public ConfluenceLieTracker(Document document) {
		super();
		this.document = document;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.AbstractLineTracker#nextDelimiterInfo(java.lang.String, int)
	 */
	protected DelimiterInfo nextDelimiterInfo(String text, int offset) {
		int[] info= TextUtilities.indexOf(DELIMITERS, text, offset);
		if (info[0] == -1 && text.length() - offset < 80){
			System.out.println("ConfluenceLieTracker.nextDelimiterInfo() - returnning null - "+text.substring(offset));
			return null;
		}
		
		DelimiterInfo delimiterInfo = new DelimiterInfo();		
		if(info[0] != -1 && info[0] - offset < 80){
			delimiterInfo.delimiterIndex= info[0];
			delimiterInfo.delimiter= DELIMITERS[info[1]];
			delimiterInfo.delimiterLength= delimiterInfo.delimiter.length();
		}else{
			delimiterInfo.delimiterIndex = offset+findLineBreak(text.substring(offset,offset+80));
			delimiterInfo.delimiter= DELIMITERS[0];
			delimiterInfo.delimiterLength= 1;
			
		}

		System.out.println("ConfluenceLieTracker.nextDelimiterInfo() - "+delimiterInfo);
		System.out.println(text.substring(offset,delimiterInfo.delimiterIndex));
		return delimiterInfo;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.ILineTracker#getLegalLineDelimiters()
	 */
	public String[] getLegalLineDelimiters() {

		return DELIMITERS;
	}

}

class Line implements IRegion {
	
	/** The offset of the line */
	public int offset;
	/** The length of the line */
	public int length;
	/** The delimiter of this line */
	public String delimiter;
	
	/**
	 * Creates a new Line.
	 *
	 * @param offset the offset of the line
	 * @param end the last including character offset of the line
	 * @param delimiter the line's delimiter
	 */
	public Line(int offset, int end, String delimiter) {
		this.offset= offset;
		this.length= (end - offset) +1;
		this.delimiter= delimiter;
	}
	
	/**
	 * Creates a new Line.
	 *
	 * @param offset the offset of the line
	 * @param length the length of the line
	 */
	public Line(int offset, int length) {
		this.offset= offset;
		this.length= length;
		this.delimiter= null;
	}
		
	/*
	 * @see org.eclipse.jface.text.IRegion#getOffset()
	 */
	public int getOffset() {
		return offset;
	}
	
	/*
	 * @see org.eclipse.jface.text.IRegion#getLength()
	 */
	public int getLength() {
		return length;
	}
}

