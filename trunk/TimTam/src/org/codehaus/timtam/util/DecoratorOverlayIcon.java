/*
 * 
 * Copyright 2003(c) Zohar Melamed All rights reserved.
 * 
 * 
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 * 
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. Due credit should be given to The Codehaus and Contributors
 * http://timtam.codehaus.org/
 * 
 * THIS SOFTWARE IS PROVIDED BY THE CODEHAUS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE CODEHAUS OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * 
 *  
 */
package org.codehaus.timtam.util;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
/**
 * @author Zohar
 *  
 */
public class DecoratorOverlayIcon extends CompositeImageDescriptor {
	// the base image
	private Image base;
	// the overlay images
	private ImageDescriptor overlay;
	// the size
	private Point size;
	/**
	 * OverlayIcon constructor.
	 * 
	 * @param base
	 *            the base image
	 * @param overlays
	 *            the overlay images
	 * @param locations
	 *            the location of each image
	 * @param size
	 *            the size
	 */
	public DecoratorOverlayIcon(Image baseImage, ImageDescriptor overlay,
			Point sizeValue) {
		this.base = baseImage;
		this.overlay = overlay;
		this.size = sizeValue;
	}
	/**
	 * Draw the overlays for the reciever.
	 */
	protected void drawOverlay() {
		if (overlay == null)
			return;
		ImageData overlayData = overlay.getImageData();
		//Use the missing descriptor if it is not there.
		if (overlayData == null)
			overlayData = ImageDescriptor.getMissingImageDescriptor()
					.getImageData();
		//bottom left
		drawImage(overlayData, 0, size.y - overlayData.height);
	}
	
	protected void drawCompositeImage(int width, int height) {
		drawImage(base.getImageData(), 0, 0);
		drawOverlay();
	}
	protected Point getSize() {
		return size;
	}
}