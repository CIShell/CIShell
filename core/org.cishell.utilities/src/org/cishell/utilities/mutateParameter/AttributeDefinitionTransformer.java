package org.cishell.utilities.mutateParameter;

import org.osgi.service.metatype.AttributeDefinition;

/**
 * This interface and its sub-interfaces correspond to constructors of BasicObjectClassDefinition.
/**
 * @deprecated see
 *            {@link url http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities}
 */
@Deprecated
public interface AttributeDefinitionTransformer {
	public boolean shouldTransform(AttributeDefinition ad);	
	
	/**
	 * It's the responsibility of the implementing class to call any of the other transformation methods from this one!
	 * <p>
	 * Also, this method should check shouldTransform before doing any transformations!
	 * @param oldAD
	 * @return
	 */
	public AttributeDefinition transform(AttributeDefinition oldAD);
	
	public String transformID(String oldID);
	public String transformName(String oldName);
	public String transformDescription(String oldDescription);
	public int transformType(int oldType);
}



