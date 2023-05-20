package com.chua.common.support.protocol.ftp.client;

/**
 * Implement this interface to build a new LIST parser. List parsers are called
 * to parse the result of a FTP LIST command send to the server in the list()
 * method. You can add a custom parser to your instance of FTPClient calling on
 * it the method addListParser.
 *
 * @author Carlo Pelliccia
 * @see FTPClient#addListParser(FTPListParser)
 */
public interface FTPListParser {

	/**
	 * Parses a LIST command response and builds an array of FTPFile objects.
	 *
	 * @param lines The response to parse, splitted by line.
	 * @return An array of FTPFile objects representing the result of the
	 * operation.
	 * @throws FTPListParseException If this parser cannot parse the given response.
	 */
	public FTPFile[] parse(String[] lines) throws FTPListParseException;

}
