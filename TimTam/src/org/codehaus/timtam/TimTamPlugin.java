
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.codehaus.timtam.model.ConfluencePage;
import org.codehaus.timtam.model.ConfluenceSpace;
import org.codehaus.timtam.model.TimTamModel;
import org.codehaus.timtam.template.ConfluenceContextType;
import org.codehaus.timtam.util.DecoratorOverlayIcon;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import electric.util.classloader.ClassLoaders;

/**
 * The main plugin class to be used in the desktop.
 */
public class TimTamPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static TimTamPlugin plugin;
	private TemplateStore templateStore;
	//The context type registry. 
	private ContextTypeRegistry ctxTypeRegistry;	
	//Resource bundle.
	private ResourceBundle resourceBundle;
	private static boolean trace;
	public static final String IMG_SPACE = "icons/space.gif";
	public static final String IMG_BROKEN_SPACE= "icons/broken-space.gif";
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
	// browser
	public static final String IMG_BROWSER_FORWARD = "icons/forward.gif";
	public static final String IMG_BROWSER_BACK= "icons/back.gif";
	public static final String IMG_BROWSER_STOP= "icons/stop.gif";
	public static final String IMG_BROWSER_REFRESH= "icons/browser-refresh.gif";

	
	public static final String P_USE_PROXY = "use_proxy";
	public static final String P_PROXY_HOST = "proxy_host";
	public static final String P_PROXY_PORT = "proxy_port";
	public static final String P_PROXY_USER = "proxy_user";
	public static final String P_PROXY_PASSWORD = "proxy_password";

	private static final String IMG_READONLY_DECORATOR = "icons/readonly-decorator.gif";
	private static final String IMG_READONLY_PAGE = "readonlypage"; 
	private static final String IMG_READONLY_HOMEPAGE = "readonlyhomepage"; 
	private static final String IMG_READONLY_SPACE = "readonlyspace"; 

	private static final String CUSTOM_TEMPLATES_KEY= "org.codehaus.timtam.customtemplates"; //$NON-NLS-1$


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
	
	/**
	 * Returns this plug-in's template store.
	 * 
	 * @return the template store of this plug-in instance
	 */
	public TemplateStore getTemplateStore() {
		if (templateStore == null) {
			templateStore= new TemplateStore(getPreferenceStore(), CUSTOM_TEMPLATES_KEY);
			try {
				templateStore.load();
			} catch (IOException e) {
				logException("failed to load temaplates", e);
			}
		}
		return templateStore;
	}
	
	/**
	 * Returns this plug-in's context type registry.
	 * 
	 * @return the context type registry for this plug-in instance
	 */
	public ContextTypeRegistry getContextTypeRegistry() {
		if (ctxTypeRegistry == null) {
			// create an configure the contexts available in the template editor
			ctxTypeRegistry = new ContextTypeRegistry();
			ctxTypeRegistry.addContextType(ConfluenceContextType.CONTEXT_TYPE);
		}
		return ctxTypeRegistry;
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
		registry.put(IMG_PAGE, loadImage(IMG_PAGE));
		registry.put(IMG_SPACE, loadImage(IMG_SPACE));
		registry.put(IMG_BROKEN_SPACE, loadImage(IMG_BROKEN_SPACE));
		registry.put(IMG_SPACEHOME, loadImage(IMG_SPACEHOME));
		registry.put(IMG_REFRESH, loadImage(IMG_REFRESH));

		registry.put(IMG_BROWSER_FORWARD,loadImageDescriptor(IMG_BROWSER_FORWARD));
		registry.put(IMG_BROWSER_BACK,loadImageDescriptor(IMG_BROWSER_BACK));
		registry.put(IMG_BROWSER_STOP,loadImageDescriptor(IMG_BROWSER_STOP));
		registry.put(IMG_BROWSER_REFRESH,loadImageDescriptor(IMG_BROWSER_REFRESH));

		
		
		ImageDescriptor readOnlyOverlay = loadImageDescriptor(IMG_READONLY_DECORATOR);		
		Point size = new Point(16,16);
		DecoratorOverlayIcon temp = new DecoratorOverlayIcon(registry.get(IMG_PAGE),readOnlyOverlay,size);
		registry.put(IMG_READONLY_PAGE,temp.createImage());
		
		temp = new DecoratorOverlayIcon(registry.get(IMG_SPACEHOME),readOnlyOverlay,size);
		registry.put(IMG_READONLY_HOMEPAGE,temp.createImage());

		temp = new DecoratorOverlayIcon(registry.get(IMG_SPACE),readOnlyOverlay,size);
		registry.put(IMG_READONLY_SPACE,temp.createImage());
	
		ClassLoaders.setContextClassLoader(getClass().getClassLoader());

		TimTamModel.getInstace().startup();
	}

	

	public void shutdown() throws CoreException {
		super.shutdown();
		TimTamModel.getInstace().shutdown();
	}

	public Image getPageIcon(ConfluencePage page) {
		ImageRegistry registry = getImageRegistry();
		if(page.isHomePage()){
			return page.isReadOnly()?registry.get(IMG_READONLY_HOMEPAGE):registry.get(IMG_SPACEHOME);
		}else{
			return page.isReadOnly()?registry.get(IMG_READONLY_PAGE):registry.get(IMG_PAGE);
		}
	}

	public Image getSpaceIcon(ConfluenceSpace space) {
		ImageRegistry registry = getImageRegistry();
		if(!space.isHealty()){
			return registry.get(IMG_BROKEN_SPACE);
		}
		
		return space.isReadOnly()?registry.get(IMG_READONLY_SPACE):registry.get(IMG_SPACE);
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

	/**
	 * @return
	 */
	public Image getCompletionProcessorImage() {
		return getImageRegistry().get(IMG_SPACE);
	}

}
