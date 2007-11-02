package werkzeugkasten.dblauncher;

import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import werkzeugkasten.common.resource.LogUtil;
import werkzeugkasten.common.ui.ImageLoader;
import werkzeugkasten.common.util.StringUtil;
import werkzeugkasten.dblauncher.launch.TerminateListener;
import werkzeugkasten.dblauncher.nls.Images;
import werkzeugkasten.dblauncher.preferences.DbPreferences;
import werkzeugkasten.dblauncher.preferences.impl.DbPreferencesImpl;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The shared instance
	private static Activator plugin;

	private static Map<String, IProject> urlToProjectCache;

	private TerminateListener terminateListener = new TerminateListener();

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		DebugPlugin.getDefault().addDebugEventListener(terminateListener);
		ImageLoader.load(plugin, Images.class);
		urlToProjectCache = new Hashtable<String, IProject>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		DebugPlugin.getDefault().removeDebugEventListener(terminateListener);
		urlToProjectCache.clear();
		urlToProjectCache = null;
		ImageLoader.unload(plugin, Images.class);
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public static void refreshActions() {
		getDefault().refreshPluginActions();
	}

	public static DbPreferences getPreferences(IProject project) {
		return new DbPreferencesImpl(project);
	}

	public static void log(String msg) {
		LogUtil.log(getDefault(), msg);
	}

	public static void log(Throwable throwable) {
		LogUtil.log(getDefault(), throwable);
	}

	public static void setLaunch(IProject project, ILaunch launch) {
		try {
			if (project != null) {
				project.setSessionProperty(Constants.KEY_SERVER_STATE, launch);
			}
		} catch (CoreException e) {
			log(e);
		}
	}

	public static ILaunch getLaunch(IProject project) {
		ILaunch result = null;
		try {
			if (project != null) {
				result = (ILaunch) project
						.getSessionProperty(Constants.KEY_SERVER_STATE);
			}
		} catch (CoreException e) {
			log(e);
		}
		return result;
	}

	public static void entry(IProject project, URL url) {
		urlToProjectCache.put(url.toExternalForm(), project);
	}

	public static IProject findProject(String url) {
		if (StringUtil.isEmpty(url) == false) {
			return urlToProjectCache.get(url);
		}
		return null;
	}

	public synchronized static void reject(IProject project) {
		if (project == null) {
			return;
		}
		List<String> l = new ArrayList<String>();
		for (String key : urlToProjectCache.keySet()) {
			if (project.equals(urlToProjectCache.get(key))) {
				l.add(key);
			}
		}
		for (String key : l) {
			urlToProjectCache.remove(key);
		}
	}
}