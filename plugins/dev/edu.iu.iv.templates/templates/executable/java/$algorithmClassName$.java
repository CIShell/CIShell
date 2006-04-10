/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu)
 */
package $packageName$;

$extraImports$
import edu.iu.iv.core.algorithm.AbstractAlgorithm;
import edu.iu.iv.core.algorithm.AlgorithmProperty;
import edu.iu.iv.core.util.staticexecutable.StaticExecutableRunner;

/**
 * Class to implement the Algorithm for this IVC Plug-in.
 * 
 * @author $pluginAuthor$
 */
public class $algorithmClassName$ extends AbstractAlgorithm {
    public static final String NAME = "$algorithmName$";

	/**
	 * Creates a new $algorithmName$.
	 */
	public $algorithmClassName$() { 
	    propertyMap.put(AlgorithmProperty.LABEL, NAME);
        
        $guiCode$
    }

	/**
	 * Executes this $algorithmName$.
	 * 
	 * @return true if the Algorithm was successful, false if not
	 */
	public boolean execute() {
        StaticExecutableRunner runner = new StaticExecutableRunner();

        runner.execute($pluginClassName$.PLUGIN_ID);
        
		return true;
	}
}