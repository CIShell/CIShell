package edu.iu.iv.core.persistence;

//ndeckard
//modified Shashikant Penumarthy

//FIXME: this interface isn't used yet. just a dummy for now.
/**
 * A descriptor that holds all data necessary for persisteing to and restoring from a database.
 * <i>This is currently not implemented.</i>
 * 
 * @author Team IVC
 * @version 0.1
 *
 */
public interface DatabaseResourceDescriptor extends ResourceDescriptor {

	/**
	 * Gets the name of the database.
	 * 
	 * @return The name of the database.
	 */
	public String getDatabaseName();
	
	/**
	 * Gets the host name on which the databse is hosted.
	 * 
	 * @return The hostname of the DB.
	 */
	public String getHostName();
	
	/**
	 * Gets the port through which the database is to be accessed.
	 * 
	 * @return The port of the DB.
	 */
	public int getPort();

}
