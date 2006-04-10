package $packageName$;

import edu.iu.iv.provider.PersisterProvider;

/**
 * The main class that will provide persisters in this project
 * to the IVC.
 */
public class $pluginClassName$ extends PersisterProvider {
    //The shared instance.
    private static $pluginClassName$ plugin;  

    /**
     * The constructor.
     */
    public $pluginClassName$() {
        super("$packageName$.$pluginClassName$");
        plugin = this;
    }
    
    /**
     * Returns the shared instance.
     */
    public static $pluginClassName$ getDefault() {
        return plugin;
    }
}