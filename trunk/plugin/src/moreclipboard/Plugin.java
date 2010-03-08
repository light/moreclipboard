package moreclipboard;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main class of the plugin.
 * 
 * @author Mikhail Barg
 * 
 */
public class Plugin extends AbstractUIPlugin
{
	//TODO: put strings to bundle
	public static final String ClearContentsAction_text = "Remove all";
	public static final String RemoveCurrentItemAction_text = "Remove current";

	
	/** Singleton instance */
	private static Plugin INSTANCE;
	
	/**
	 * Gets the plugin singleton.
	 * 
	 * @return the default instance
	 */
	static public Plugin getInstance()
	{
		return INSTANCE;
	}
	
	/**
	 * Creates the Plugin singleton
	 */
	public Plugin()
	{
		if (INSTANCE == null)
		{
			INSTANCE = this;
		}
	}

	/** The instance of the Contents to store the clipboard data */
	private Contents m_contents;
	
	/**
	 * @return current MoreClipboard contents
	 */
	public Contents getContents()
	{
		return m_contents;
	}

	@Override
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		
		if (m_contents != null)
		{
			throw new IllegalStateException("Attempt to start the plugin second time!");
		}
		
		m_contents = new Contents();
	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		if (m_contents != null)
		{
			m_contents.dispose();
			m_contents = null;
		}
		super.stop(context);
	}
}