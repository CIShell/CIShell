package $packageName$;

import edu.iu.iv.provider.SampleDataProvider;
import java.io.File;

/**
 * Class to provide the sampledata in the sampledata folder
 * of this project to the IVC.
 * 
 * @author Team IVC
 */
public class $sampledataClassName$ extends SampleDataProvider {
    private static final String ID_PLUGIN = "$pluginId$";
    private static final String SUBDIRECTORY = "$sampledataType$" + File.separator + "$subtype$";
    
    public $sampledataClassName$() {
        super(ID_PLUGIN, SUBDIRECTORY);
    }
}
