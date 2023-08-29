package com.chua.common.support.protocol.ftp.client;

/**
 * Implement this interface to build a new LIST parser. List parsers are called
 * to parse the result of a Ftp LIST command send to the server in the list()
 * method. You can add a custom parser to your instance of FtpClient calling on
 * it the method addListParser.
 *
 * @author Carlo Pelliccia
 * @see FtpClient#addListParser(FtpListParser)
 */
public interface FtpListParser {

	/**
	 * Parses a LIST command response and builds an array of FtpFile objects.
	 *
	 * @param lines The response to parse, splitted by line.
	 * @return An array of FtpFile objects representing the result of the
	 * operation.
	 * @throws FtpListParseException If this parser cannot parse the given response.
	 */
	FtpFile[] parse(String[] lines) throws FtpListParseException;

}
