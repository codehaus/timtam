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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

/**
 * @author MelamedZ
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ConfluenceMacroScanner extends RuleBasedScanner {
	private static Set headerChars = new HashSet();
	static {
		headerChars.add(new Character('1'));
	}
	ConfluenceMacroScanner() {


		IToken macro = new Token(new TextAttribute(ColorManager.getColor(ConfluencePartitionScanner.MACRO)));
		IToken link = new Token(new TextAttribute(ColorManager.getColor(ConfluencePartitionScanner.LINK)));
		IToken header = new Token(new TextAttribute(ColorManager.getColor(ConfluencePartitionScanner.HEADINGS)));

		IRule[] rules = new IRule[3];
		// Add rule for macros
		int i = 0;
		rules[i++] = new SingleLineRule("{", "}", macro, '\\');
		rules[i++] = new SingleLineRule("[", "]", link, '\\');

		
		WordRule headingRule = new WordRule(new IWordDetector() {
			public boolean isWordStart(char c) {
				return c == 'h';
			}

			public boolean isWordPart(char c) {
				return Character.isDigit(c) || c == '.';
			}
		});
		
		headingRule.setColumnConstraint(0);
		for(int h = 1; h < 7;++h){
			headingRule.addWord("h"+Integer.toString(h)+".",header);
		}

		rules[i++] = headingRule; 
		setRules(rules);
	}
}
