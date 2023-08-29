package com.chua.common.support.protocol.ftp.client;

/**
 * Exception thrown by the list() method in FTPClient objects when the response
 * sent by the server to a FTP list command is not parseable through the known
 * parsers.
 *
 * @author Carlo Pelliccia
 */
public class FtpListParseException extends Exception {

	private static final long serialVersionUID = 1L;

}
