package com.chua.common.support.protocol.ftp.client;

import java.net.Socket;

/**
 * A package reserved {@link com.chua.common.support.protocol.ftp.server.FtpConnection} provider, used internally by the
 * client to obtain connections for data transfer purposes.
 *
 * @author cpelliccia
 */
interface FtpDataTransferConnectionProvider {

	/**
	 * Returns the connection.
	 *
	 * @return The connection.
	 * @throws FtpDataTransferException If an unexpected error occurs.
	 */
	Socket openDataTransferConnection() throws FtpDataTransferException;

	/**
	 * Terminates the provider.
	 */
	void dispose();

}
