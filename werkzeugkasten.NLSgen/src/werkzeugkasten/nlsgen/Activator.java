package werkzeugkasten.nlsgen;

import static werkzeugkasten.nlsgen.Constants.EXT_RESOURCE_GENERATOR;
import static werkzeugkasten.nlsgen.Constants.ID_PLUGIN;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import werkzeugkasten.common.runtime.ExtensionAcceptor;
import werkzeugkasten.common.runtime.LogUtil;
import werkzeugkasten.common.util.StringUtil;
import werkzeugkasten.nlsgen.listener.PropertiesChangeListener;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The shared instance
	private static Activator plugin;

	protected PropertiesChangeListener propertiesChangeListener = new PropertiesChangeListener();

	protected Map<String, ResourceGeneratorDesc> extensionpoints = null;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public static void registerListener() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(
				getDefault().propertiesChangeListener,
				IResourceChangeEvent.PRE_BUILD
						| IResourceChangeEvent.POST_CHANGE
						| IResourceChangeEvent.PRE_REFRESH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.removeResourceChangeListener(this.propertiesChangeListener);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public static void log(String msg) {
		LogUtil.log(getDefault(), msg);
	}

	public static void log(Throwable throwable) {
		LogUtil.log(getDefault(), throwable);
	}

	public static ResourceGenerator createResourceGenerator(String key) {
		if (StringUtil.isEmpty(key)) {
			return null;
		}
		if (getDefault().extensionpoints == null) {
			initializeExtensionPoint();
		}
		ResourceGeneratorDesc desc = getDefault().extensionpoints.get(key);
		return desc != null ? desc.getInstance() : null;
	}

	protected synchronized static void initializeExtensionPoint() {
		final Map<String, ResourceGeneratorDesc> map = new LinkedHashMap<String, ResourceGeneratorDesc>();
		ExtensionAcceptor.accept(ID_PLUGIN, EXT_RESOURCE_GENERATOR,
				new ExtensionAcceptor.ExtensionVisitor() {
					public boolean visit(IConfigurationElement e) {
						ResourceGeneratorDesc desc = new ResourceGeneratorDesc(
								e);
						map.put(desc.getId(), desc);
						return true;
					}
				});
		getDefault().extensionpoints = map;
	}

	public static Collection<ResourceGeneratorDesc> getGeneratorTypes() {
		if (getDefault().extensionpoints == null) {
			initializeExtensionPoint();
		}
		return getDefault().extensionpoints.values();
	}
}
