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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

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
    public static final String IMG_BROKEN_SPACE = "icons/broken-space.gif";
    public static final String IMG_SPACEHOME = "icons/spaceHome.gif";
    public static final String IMG_PAGE = "icons/page.gif";
    public static final String IMG_EDIT_PAGE = "icons/editPage.gif";
    public static final String IMG_DEL_PAGE = "icons/delPage.gif";
    public static final String IMG_DEL_SERVER = "icons/delServer.gif";
    public static final String IMG_ADD_PAGE = "icons/addPage.gif";
    public static final String IMG_ADD_CHILD_PAGE = "icons/addChildPage.gif";
    public static final String IMG_COMMENT = "icons/comment.gif";
    public static final String IMG_ATTACHMENT = "icons/attachment.gif";
    public static final String IMG_USER = "icons/user.gif";
    public static final String IMG_BLOGENTRY = "icons/blogentry.gif";
    public static final String IMG_RENAME_PAGE = "icons/renamePage.gif";
    public static final String IMG_REFRESH = "icons/refresh.gif";
    public static final String IMG_SERVER = "icons/server.gif";
    public static final String IMG_ADDSERVER = "icons/addServer.gif";
    public static final String IMG_REFRESH_NODE = "icons/refreshServer.gif";
    public static final String IMG_SEARCH = "icons/search.gif";
    // browser
    public static final String IMG_BROWSER_FORWARD = "icons/forward.gif";
    public static final String IMG_BROWSER_BACK = "icons/back.gif";
    public static final String IMG_BROWSER_STOP = "icons/stop.gif";
    public static final String IMG_BROWSER_REFRESH = "icons/browser-refresh.gif";

    public static final String P_USE_PROXY = "use_proxy";
    public static final String P_PROXY_HOST = "proxy_host";
    public static final String P_PROXY_PORT = "proxy_port";
    public static final String P_PROXY_USER = "proxy_user";
    public static final String P_PROXY_PASSWORD = "proxy_password";
    public static final String P_USE_HTTP_AUTH = "use_http_auth";
    public static final String P_USE_PROXY_FOR_HTTP_AUTH = "use_proxy_settings_for_http_auth";
    public static final String P_HTTP_USER = "http_user";
    public static final String P_HTTP_PASSWORD = "http_password";
	public static final String P_USE_WORD_WRAP = "use_wordwrap";
	
    private static final String IMG_READONLY_DECORATOR = "icons/readonly-decorator.gif";
    private static final String IMG_READONLY_PAGE = "readonlypage";
    private static final String IMG_READONLY_HOMEPAGE = "readonlyhomepage";
    private static final String IMG_READONLY_SPACE = "readonlyspace";

    private static final String CUSTOM_TEMPLATES_KEY = "org.codehaus.timtam.customtemplates"; //$NON-NLS-1$

    static {
        String value = Platform.getDebugOption("org.codehaus.timtam/trace"); //$NON-NLS-1$
        if (value != null && value.equalsIgnoreCase("true")) //$NON-NLS-1$
            trace = true;
    }

    /**
     * The constructor.
     */
    public TimTamPlugin() {
        plugin = this;
        try {
            resourceBundle = ResourceBundle
                    .getBundle("org.codehaus.timtam.TimTamPluginResources");
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
            templateStore = new ContributionTemplateStore(getPreferenceStore(),
                    CUSTOM_TEMPLATES_KEY);
            try {
                templateStore.load();
            } catch (IOException e) {
                logException("failed to load temaplates", e, true);
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
            ctxTypeRegistry.addContextType(new ConfluenceContextType());
        }
        return ctxTypeRegistry;
    }

    public static void trace(String message) {
        if (trace) {
            System.out.println(message);
        }
    }

    public void logException(final String message, final Throwable t,
            boolean tellUser) {
        IStatus status = new Status(IStatus.ERROR, getBundle()
                .getSymbolicName(), 0, message, t); //$NON-NLS-1$
        getLog().log(status);
        if (tellUser) {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    MessageDialog.openError(null, "TimTam Error " + message, t.getMessage());
                }
            });
        }

    }

    public Image getPageIcon(ConfluencePage page) {
        ImageRegistry registry = getImageRegistry();
        if (page.isHomePage()) {
            return page.isReadOnly() ? registry.get(IMG_READONLY_HOMEPAGE)
                    : registry.get(IMG_SPACEHOME);
        } 
        
        return page.isReadOnly() ? registry.get(IMG_READONLY_PAGE): registry.get(IMG_PAGE);
    }

    public Image getSpaceIcon(ConfluenceSpace space) {
        ImageRegistry registry = getImageRegistry();
        if (!space.isHealty()) {
            return registry.get(IMG_BROKEN_SPACE);
        }

        return space.isReadOnly() ? registry.get(IMG_READONLY_SPACE) : registry
                .get(IMG_SPACE);
    }

    public Image loadImage(String path) {
        return loadImageDescriptor(path).createImage();
    }

    public ImageDescriptor loadImageDescriptor(String path) {
        URL url;
        try {
            url = new URL(getBundle().getEntry("/"), path);
            return ImageDescriptor.createFromURL(url);
        } catch (MalformedURLException e) {
            logException("failed to load image " + path, e, true);
        }

        return null;
    }


    /**
     * @return
     */
    public boolean shouldUseHTTPAuthentication() {
        return getPreferenceStore().getBoolean(P_USE_HTTP_AUTH);
    }

    /**
     * @return
     */
    public String getHTTPUser() {
        if (getPreferenceStore().getBoolean(P_USE_PROXY_FOR_HTTP_AUTH)) {
            return getProxyUser();
        }

        return getPreferenceStore().getString(P_HTTP_USER);
    }

    /**
     * @return
     */
    public String getHTTPPassword() {
        if (getPreferenceStore().getBoolean(P_USE_PROXY_FOR_HTTP_AUTH)) {
            return getProxyPassword();
        }

        return getPreferenceStore().getString(P_HTTP_PASSWORD);
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

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        ClassLoaders.setContextClassLoader(getClass().getClassLoader());
        getPreferenceStore().setDefault(P_USE_WORD_WRAP,true);
        // workaround for the rcp / non rcp diff in behave
        if (Display.getCurrent() != null) {
            init();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
     */
    protected void initializeImageRegistry(ImageRegistry registry) {
        registry.put(IMG_SERVER, loadImage(IMG_SERVER));
        registry.put(IMG_PAGE, loadImage(IMG_PAGE));
        registry.put(IMG_SPACE, loadImage(IMG_SPACE));
        registry.put(IMG_BROKEN_SPACE, loadImage(IMG_BROKEN_SPACE));
        registry.put(IMG_SPACEHOME, loadImage(IMG_SPACEHOME));
        registry.put(IMG_REFRESH, loadImage(IMG_REFRESH));
        registry.put(IMG_COMMENT, loadImage(IMG_COMMENT));
        registry.put(IMG_ATTACHMENT, loadImage(IMG_ATTACHMENT));
        registry.put(IMG_USER, loadImage(IMG_USER));
        registry.put(IMG_BLOGENTRY, loadImage(IMG_BLOGENTRY));

        registry.put(IMG_BROWSER_FORWARD,
                loadImageDescriptor(IMG_BROWSER_FORWARD));
        registry.put(IMG_BROWSER_BACK, loadImageDescriptor(IMG_BROWSER_BACK));
        registry.put(IMG_BROWSER_STOP, loadImageDescriptor(IMG_BROWSER_STOP));
        registry.put(IMG_BROWSER_REFRESH,
                loadImageDescriptor(IMG_BROWSER_REFRESH));
        registry.put(IMG_SEARCH, loadImageDescriptor(IMG_SEARCH));

        ImageDescriptor readOnlyOverlay = loadImageDescriptor(IMG_READONLY_DECORATOR);
        Point size = new Point(16, 16);
        DecoratorOverlayIcon temp = new DecoratorOverlayIcon(registry
                .get(IMG_PAGE), readOnlyOverlay, size);
        registry.put(IMG_READONLY_PAGE, temp.createImage());

        temp = new DecoratorOverlayIcon(registry.get(IMG_SPACEHOME),
                readOnlyOverlay, size);
        registry.put(IMG_READONLY_HOMEPAGE, temp.createImage());

        temp = new DecoratorOverlayIcon(registry.get(IMG_SPACE),
                readOnlyOverlay, size);
        registry.put(IMG_READONLY_SPACE, temp.createImage());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        TimTamModel.getInstace().shutdown();
    }

    /**
     *  
     */
    public void init() {
        getImageRegistry();
        TimTamModel.getInstace().startup();
    }

}