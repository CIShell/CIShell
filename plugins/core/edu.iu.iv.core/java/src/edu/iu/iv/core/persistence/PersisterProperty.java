/*
 * Created on Jun 9, 2004
 * Shashikant Penumarthy
 */
package edu.iu.iv.core.persistence;

import java.net.URL;

import edu.iu.iv.common.property.Property;
import edu.iu.iv.common.property.URLProperty;
import edu.iu.iv.core.datamodels.DataModelType;

/**
 * This class defines properties that each persister can choose to provide.
 * Almost all of the properties here are to help a user choose among multiple
 * persisters with differing capabilities.
 * 
 * @author Team IVC
 * @version 0.1
 *  
 */
public class PersisterProperty {

    /**
     * The name of the format that this persister can persist data as. <br>
     * 
     * Example: Harwell-Boeing Sparse Matrix Format
     */
    public static final Property FORMAT_NAME = new Property("Format name.",
            String.class, 1);

    /**
     * The description of this format in short. This property should include a
     * short description that lets the user know if she should use this
     * persister to store her data. Certain formats may not be able to save
     * everything and hence this property should let the user know about a
     * persister's specifics. However, detailed format descriptions should be
     * left to the documentation pages. <br>
     * 
     * Ex: Stores a sparse matrix in coordinate wise format, plus row and column
     * labels.
     */
    public static final Property FORMAT_DESCRIPTION = new Property(
            "Format description", String.class, 4);

    /**
     * The name of the data model that can be restored by a persister. This
     * should be short and should allow a user to get enough idea about what
     * type of data this is.
     * 
     * <pre>
     * 
     *  
     *   &lt;b&gt;Examples:&lt;/b&gt; 
     *  
     * <br>
     * 
     *  
     *   &lt;i&gt;OK&lt;/i&gt;: Graph (JUNG API) 
     *  
     * <br>
     * 
     *  
     *   &lt;i&gt;OK&lt;/i&gt;: Matrix (IVC) 
     *  
     * <br>
     * 
     *  
     *   &lt;i&gt;Not OK&lt;/i&gt;: Really efficient tree model.
     *   
     *  
     * </pre>
     * 
     * @see edu.iu.iv.core.persistence.PersisterProperty.RESTORABLE_MODEL_DESCRIPTION
     */
    public static final Property RESTORABLE_MODEL_NAME = new Property(
            "Restorable model name", String.class, 2);

    /**
     * The DataModelType for this Persister. 
     */
    public static final Property RESTORABLE_MODEL_TYPE = new Property(
            "Restorable model type", DataModelType.class, 11);
    
    /**
     * The description of the data model that can be restored by a persister.
     * This description should offer more details than
     * {@link edu.iu.iv.core.persistence.PersisterProperty.RESTORABLE_MODEL_NAME}
     * but should still be short enough for the user to quickly glance through.
     * Detailed descriptions of the model and its limitations should not be
     * stored using this property (that should go on the documentation pages),
     * but it should allow the user to figure out if she needs to find out more
     * before using the data model for her analysis.
     * 
     * <pre>
     * 
     *  
     *   &lt;b&gt;Examples:&lt;/b&gt; 
     *  
     * <br>
     * 
     *  
     *   &lt;i&gt;OK&lt;/i&gt;: Undirected graph from the JUNG API that stores only structure information. UserData is not filled in.
     *   &lt;i&gt;OK&lt;/i&gt;: Sparse matrix format using compressed column storage. Access time for elements is not constant.
     *   &lt;i&gt;Not ok&lt;/i&gt;: Tree model with color stored using key &quot;color&quot;, size stored using key &quot;size&quot;, border color for nodes
     *   stored using key &quot;borderColor&quot;...
     *   
     *  
     * </pre>
     * 
     * @see edu.iu.iv.core.persistence.PersisterProperty#RESTORABLE_MODEL_NAME
     */
    public static final Property RESTORABLE_MODEL_DESCRIPTION = new Property(
            "Restorable model description", String.class, 5);    

    /**
     * The file extension supported by this persister. Each persister must
     * choose a particular file extension. This has been done to accomodate
     * several commonly used file formats which do not carry any metadata inside
     * the file which can help decipher their type, such as CSV (Comma Separated
     * Values) or TXT (Plain Text Files). <br>
     * 
     * Examples: .hbf (the '.' must be part of this string)
     */
    public static final Property SUPPORTED_FILE_EXTENSION = new Property(
            "Supported file extension.", String.class, 3);

    /**
     * The name of this persister. In certain cases, just knowing the name of
     * the file format might no be enough for a user to choose a persister. For
     * example two different persisters might be able to restore from the same
     * resource to the same data model. But these persisters might have made
     * different trade-offs with respect to speed and robustness, etc. In such a
     * case, a user might be able to identify the persister she needs based on
     * the name. This name should be short and should allow a user to simply
     * identify what persister it is. <br>
     * Examples: IVC Oracle Bridge
     * 
     * @see edu.iu.iv.core.persistence.PersisterProperty#PERSISTER_DESCRIPTION
     */
    public static final Property PERSISTER_NAME = new Property(
            "Persister name", String.class, 6);

    /**
     * This property provides additional information and complements the
     * <code>PERSISTER_NAME</code> property. This should be a brief
     * description of the persister. <br>
     * Note that this property is different from the
     * <code>FORMAT_DESCRIPTION property</code>. The
     * <code>FORMAT_DESCRIPTION</code> property describes the actual format
     * itself, while this property describes what this persister does. The same
     * format might be processed differently by different persisters. An example
     * is a persister that writes formatted data to a network stream. <br>
     * 
     * Example: Uses JDBC to write tabular data to an Oracle database.
     * 
     * @see edu.iu.iv.core.persistence.PersisterProperty#FORMAT_DESCRIPTION
     */
    public static final Property PERSISTER_DESCRIPTION = new Property(
            "Persister description", String.class, 7);

    /**
     * The name of the author who created/owns the format. This property allows
     * a user to know if the format comes from a standard specification or if
     * its a convenience format that is not supported universally. <br>
     * 
     * Examples: W3C XML Committee
     */
    public static final Property FORMAT_CREATOR = new Property(
            "Format Creator", String.class, 8);
    
    public static final Property FORMAT_DOCUMENTATION_LINK = new URLProperty(
            "Format Documentation", URL.class, 9);
    /**
     * The URL to a resource that describes this persister and format in detail.
     * <br>
     * Example: http://graphml.graphdrawing.org/
     */
    public static final Property PERSISTER_DOCUMENTATION_LINK = new URLProperty(
            "Persister Documentation", URL.class, 10);
}