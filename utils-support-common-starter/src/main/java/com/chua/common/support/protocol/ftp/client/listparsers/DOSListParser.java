package com.chua.common.support.protocol.ftp.client.listparsers;

import com.chua.common.support.protocol.ftp.client.FtpFile;
import com.chua.common.support.protocol.ftp.client.FtpListParseException;
import com.chua.common.support.protocol.ftp.client.FtpListParser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This parser can handle the MSDOS-style LIST responses.
 *
 * @author Carlo Pelliccia
 */
public class DOSListParser implements FtpListParser {

	private static final Pattern PATTERN = Pattern
			.compile("^(\\d{2})-(\\d{2})-(\\d{2})\\s+(\\d{2}):(\\d{2})(AM|PM)\\s+"
					+ "(<DIR>|\\d+)\\s+([^\\\\/*?\"<>|]+)$");

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"MM/dd/yy hh:mm a");

	public FtpFile[] parse(String[] lines) throws FtpListParseException {
		int size = lines.length;
		FtpFile[] ret = new FtpFile[size];
		for (int i = 0; i < size; i++) {
			Matcher m = PATTERN.matcher(lines[i]);
			if (m.matches()) {
				String month = m.group(1);
				String day = m.group(2);
				String year = m.group(3);
				String hour = m.group(4);
				String minute = m.group(5);
				String ampm = m.group(6);
				String dirOrSize = m.group(7);
				String name = m.group(8);
				ret[i] = new FtpFile();
				ret[i].setName(name);
				if (dirOrSize.equalsIgnoreCase("<DIR>")) {
					ret[i].setType(FtpFile.TYPE_DIRECTORY);
					ret[i].setSize(0);
				} else {
					long fileSize;
					try {
						fileSize = Long.parseLong(dirOrSize);
					} catch (Throwable t) {
						throw new FtpListParseException();
					}
					ret[i].setType(FtpFile.TYPE_FILE);
					ret[i].setSize(fileSize);
				}
				String mdString = month + "/" + day + "/" + year + " " + hour
						+ ":" + minute + " " + ampm;
				Date md;
				try {
					synchronized (DATE_FORMAT) {
						md = DATE_FORMAT.parse(mdString);
					}
				} catch (ParseException e) {
					throw new FtpListParseException();
				}
				ret[i].setModifiedDate(md);
			} else {
				throw new FtpListParseException();
			}
		}
		return ret;
	}

}
