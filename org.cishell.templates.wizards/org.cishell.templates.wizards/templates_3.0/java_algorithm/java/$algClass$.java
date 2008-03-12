package $algPkg$;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.data.Data;

public class $algClass$ implements Algorithm {
    Data[] data;
    Dictionary parameters;
    CIShellContext context;
    
    public $algClass$(Data[] data, Dictionary parameters, CIShellContext context) {
        this.data = data;
        this.parameters = parameters;
        this.context = context;
    }

    public Data[] execute() throws AlgorithmExecutionException {
        return null;
    }
}