
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


package org.codehaus.timtam;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.codehaus.timtam.model.TimTamModel;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import electric.util.classloader.ClassLoaders;

/**
 * The main plugin class to be used in the desktop.
 */
public class TimTamPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static TimTamPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	private static boolean trace;
	public static final String IMG_SPACE = "icons/space.gif";
	public static final String IMG_SPACEHOME = "icons/spaceHome.gif";
	public static final String IMG_PAGE = "icons/page.gif";
	public static final String IMG_EDIT_PAGE= "icons/editPage.gif";
	public static final String IMG_DEL_PAGE= "icons/delPage.gif";
	public static final String IMG_DEL_SERVER= "icons/delServer.gif";
	public static final String IMG_ADD_PAGE= "icons/addPage.gif";
	public static final String IMG_ADD_CHILD_PAGE= "icons/addChildPage.gif";
	public static final String IMG_RENAME_PAGE= "icons/renamePage.gif";
	public static final String IMG_REFRESH = "icons/refresh.gif";
	public static final String IMG_SERVER = "icons/server.gif";
	public static final String IMG_ADDSERVER= "icons/addServer.gif";
	public static final String IMG_REFRESH_NODE = "icons/refreshServer.gif";
	
	public static final String P_USE_PROXY = "use_proxy";
	public static final String P_PROXY_HOST = "proxy_host";
	public static final String P_PROXY_PORT = "proxy_port";
	public static final String P_PROXY_USER = "proxy_user";
	public static final String P_PROXY_PASSWORD = "proxy_password";






	static {
		String value = Platform.getDebugOption("org.codehaus.timtam/trace"); //$NON-NLS-1$
		if (value != null && value.equalsIgnoreCase("true")) //$NON-NLS-1$
			trace = true;
	}

	/**
	 * The constructor.
	 */
	public TimTamPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("org.codehaus.timtam.TimTamPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static TimTamPlugin getInstance() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = TimTamPlugin.getInstance().getResourceBundle();
		try {
			return (bundle != null ? bundle.getString(key) : key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public static void trace(String message) {
		if (trace) {
			System.out.println(message);
		}
	}

	public void logException(String message, Exception e) {
		IStatus status = new Status(IStatus.ERROR, getDescriptor().getUniqueIdentifier(), 0, message, e); //$NON-NLS-1$
		getLog().log(status);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#startup()
	 */
	public void startup() throws CoreException {
		super.startup();
		ImageRegistry registry = getImageRegistry();
		registry.put(IMG_SERVER, loadImage(IMG_SERVER));
		registry.put(IMG_PAGE, loadImage((IMG_PAGE)));
		registry.put(IMG_SPACE, loadImage((IMG_SPACE)));
		registry.put(IMG_SPACEHOME, loadImage((IMG_SPACEHOME)));
		registry.put(IMG_REFRESH, loadImage((IMG_REFRESH)));

	
		ClassLoaders.setContextClassLoader(getClass().getClassLoader());

		TimTamModel.getInstace().startup();
	}

	
	

	public void shutdown() throws CoreException {
		super.shutdown();
		TimTamModel.getInstace().shutdown();
	}

	public Image loadImage(String path) {
		return loadImageDescriptor(path).createImage();
	}

	public ImageDescriptor loadImageDescriptor(String path) {
		URL url;
		try {
			url = new URL(getDescriptor().getInstallURL(), path);
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logException("failed to load image " + path, e);
		}

		return null;
	}

	/**
	 * @return
	 */
	public boolean shouldUseProxy() {
		return getPreferenceStore().getBoolean(P_USE_PROXY);
	}

	/**
	 * @return
	 */
	public String getProxyHost() {
		return getPreferenceStore().getString(P_PROXY_HOST);
	}

	/**
	 * @return
	 */
	public int getProxyPort() {
		return getPreferenceStore().getInt(P_PROXY_PORT);
	}

	/**
	 * @return
	 */
	public String getProxyUser() {
		return getPreferenceStore().getString(P_PROXY_USER);
	}

	/**
	 * @return
	 */
	public String getProxyPassword() {
		return getPreferenceStore().getString(P_PROXY_PASSWORD);
	}

}
