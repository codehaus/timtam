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
package org.codehaus.timtam.model.adapters;

import org.codehaus.timtam.TimTamPlugin;
import org.codehaus.timtam.model.ConfluenceService;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;

import com.atlassian.confluence.remote.RemoteSpaceSummary;

/**
 * @author zohar melamed
 *
 */
public class ServerAdapter extends TreeAdapter {
	private String name;
	private static Image icon;
	private ConfluenceService service;

	public ServerAdapter(String name, ConfluenceService service) {
		this.service = service;
		this.name = name;
		if (icon == null) {
			icon = TimTamPlugin.getInstance().getImageRegistry().get(TimTamPlugin.IMG_SERVER);
		}
	}

	public void refresh(IProgressMonitor monitor) {
		RemoteSpaceSummary[] spaces = null;
		try {
			monitor.setTaskName("Retreiving Space Summaries...");
			spaces = service.getSpaces();
			monitor.beginTask("Loading "+spaces.length+" Spaces",spaces.length);
		} catch (Exception e) {
			TimTamPlugin.getInstance().logException("failed to get spaces", e);
			return;
		}

		children = new Object[spaces.length];
		for (int i = 0; i < spaces.length; i++) {
			RemoteSpaceSummary summary = spaces[i];
			SpaceAdapter adapter = new SpaceAdapter(summary, this,service);
			adapter.refresh(monitor);
			monitor.internalWorked(1);
			children[i] = adapter;
		}
		monitor.done();
	}

	public Object getParent() {
		return null;
	}

	public Image getImage() {
		return icon;
	}

	public String getText() {
		return name;
	}
	public Integer  getType() {
		return SERVER;
	}
}