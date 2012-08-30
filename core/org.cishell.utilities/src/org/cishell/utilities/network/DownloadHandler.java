package org.cishell.utilities.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * <p>
 * DownloadHandler define the handling procedure for downloading an item 
 * through network. The default behavior for a download failed is to retry 
 * three attends. Caller can also can apply their own retry behavior. 
 * </p>
 * <p>
 * Caller to startConnect methods is responsible to close the connection.
 * </p>
 * @author kongch
/**
 * @deprecated see
 *            {@link url http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities}
 */
@Deprecated
public final class DownloadHandler {
	public static final int DEFAULT_NUMBER_OF_RETRIES = 3;
	public static final int BUFFER_SIZE = 4096;
	
	private DownloadHandler() { 
		// Utility class. Do not instantiate. 
	}
	
	/**
	 * Download the content for the given {@linkplain HttpURLConnection
	 * connection}. The numberOfRetries defines the number of attends if the
	 * network timeout is reach. Return the response as a String.
	 * 
	 * @throws IOException
	 *             if failed to read the downloaded content
	 * @throws NetworkConnectionException
	 *             if download timeout due to network connection issue
	 * @throws InvalidUrlException
	 *             if the given URL is not valid
	 */
	public static String getResponse(HttpURLConnection connection, int numberOfRetries) 
			throws IOException, InvalidUrlException,
			NetworkConnectionException {

		/* Start connection and read from socket */
		startConnect(connection, numberOfRetries);

		InputStream inputStream = connection.getInputStream();
		StringBuilder response = new StringBuilder();

		int size;
		byte[] buffer = new byte[BUFFER_SIZE];
		while ((size = inputStream.read(buffer)) != -1) {
			response.append(new String(buffer, 0, size));
		}
		connection.disconnect();

		return response.toString();
	}
	
	/**
	 * Download the content for the given {@linkplain HttpURLConnection
	 * connection}. Perform default 3 attends if the network timeout. Return the
	 * response as a String.
	 * 
	 * @throws IOException
	 *             if failed to read the downloaded content
	 * @throws NetworkConnectionException
	 *             if download timeout due to network connection issue
	 * @throws InvalidUrlException
	 *             if the given URL is not valid
	 */
	public static String getResponse(HttpURLConnection connection) 
			throws IOException, InvalidUrlException, NetworkConnectionException {
		return getResponse(connection, DEFAULT_NUMBER_OF_RETRIES);
	}
	
	/**
	 * Download the content for the given {@linkplain URL url}. The
	 * numberOfRetries defines the number of attends if the network timeout is
	 * reach. Return the response as a String.
	 * 
	 * @throws IOException
	 *             if failed to read the downloaded content
	 * @throws NetworkConnectionException
	 *             if download timeout due to network connection issue
	 * @throws InvalidUrlException
	 *             if the given URL is not valid
	 */
	public static String getResponse(URL url, int numberOfRetries) 
			throws IOException, InvalidUrlException, NetworkConnectionException {	
		
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		return getResponse(connection, numberOfRetries);
	}
	
	/**
	 * Download the content for the given {@linkplain URL url}. Perform default
	 * 3 attends if the network timeout. Return the response as a String.
	 * 
	 * @throws IOException
	 *             if failed to read the downloaded content
	 * @throws NetworkConnectionException
	 *             if download timeout due to network connection issue
	 * @throws InvalidUrlException
	 *             if the given URL is not valid
	 */
	public static String getResponse(URL url) 
			throws IOException, InvalidUrlException, NetworkConnectionException {
		return getResponse(url, DEFAULT_NUMBER_OF_RETRIES);
	}
	
	/**
	 * Perform the communication to the {@linkplain URL url} defined in the given
	 * {@linkplain HttpURLConnection connection}. Perform default 3 attends if
	 * the network timeout. Throw exception if connection failed else do nothing
	 * 
	 * Caller is responsible to close the connection if connection success
	 * 
	 * @throws IOException
	 *             if failed to read the downloaded content
	 * @throws NetworkConnectionException
	 *             if download timeout due to network connection issue
	 * @throws InvalidUrlException
	 *             if the given URL is not valid
	 */
	public static void startConnect(HttpURLConnection connection) 
			throws InvalidUrlException, NetworkConnectionException {
			
		startConnect(connection, DEFAULT_NUMBER_OF_RETRIES);
	}
	
	/**
	 * Perform the communication to the {@linkplain URL url} defined in the
	 * given {@linkplain HttpURLConnection connection}. The numberOfRetries
	 * defines the number of attends if the network timeout is reach. Throw
	 * exception if connection failed else do nothing
	 * 
	 * Caller is responsible to close the connection if connection success
	 * 
	 * @throws NetworkConnectionException
	 *             if download timeout due to network connection issue
	 * @throws InvalidUrlException
	 *             if the given URL is not valid
	 */
	public static void startConnect(HttpURLConnection connection, int numberOfRetries) 
			throws InvalidUrlException, NetworkConnectionException {		
		if (numberOfRetries < 0) {
			throw new IllegalArgumentException("numberOfRetries must be more than 0.");
		}
		int retry = numberOfRetries;
		
		/* Start connection and read from socket */
		while (retry > 0) {
			try {
				connection.connect();
				break;
			} catch (IOException e) {
				/* Retry */
				connection.disconnect();
				retry--;
				
				/* Throw exception if no more retry is allowed */
				if (retry == 0) {
					if (e instanceof SocketTimeoutException) {
						throw new NetworkConnectionException(
								"Failed to connect to "
								+ connection.getURL()
								+ ". The host or your local network connection is too weak.",
								e);
					} else {
						throw new InvalidUrlException(
								"Failed to connect to "
								+ connection.getURL()
								+ ". Please valify the URL.", 
								e);
					}
				}
			}
		}
	}
	
	/**
	 * Connection error caused by network issue.
	 */
	public static class NetworkConnectionException extends Exception {
		private static final long serialVersionUID = -7623325107609518773L;
		
		public NetworkConnectionException(String message, Throwable reason) {
			super(message, reason);
		}
	}
	
	/**
	 * Connection error due to URL is not found.
	 */
	public static class InvalidUrlException extends Exception {
		private static final long serialVersionUID = -5974867330254562053L;

		public InvalidUrlException(String message, Throwable reason) {
			super(message, reason);
		}
	}
}
