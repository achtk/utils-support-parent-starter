package com.chua.common.support.protocol.ftp.client.listparsers;

import com.chua.common.support.protocol.ftp.client.FtpFile;
import com.chua.common.support.protocol.ftp.client.FtpListParseException;
import com.chua.common.support.protocol.ftp.client.FtpListParser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This parser can handle the standard MLST/MLSD responses (RFC 3659).
 *
 * @author Carlo Pelliccia
 * @since 1.5
 */
public class MLSDListParser implements FtpListParser {

    /**
     * Date format 1 for MLSD date facts (supports millis).
     */
    private static final DateFormat MLSD_DATE_FORMAT_1 = new SimpleDateFormat("yyyyMMddHHmmss.SSS Z");

    /**
     * Date format 2 for MLSD date facts (doesn't support millis).
     */
    private static final DateFormat MLSD_DATE_FORMAT_2 = new SimpleDateFormat("yyyyMMddHHmmss Z");

    public FtpFile[] parse(String[] lines) throws FtpListParseException {
        ArrayList list = new ArrayList();
        for (int i = 0; i < lines.length; i++) {
            FtpFile file = parseLine(lines[i]);
            if (file != null) {
                list.add(file);
            }
        }
        int size = list.size();
        FtpFile[] ret = new FtpFile[size];
        for (int i = 0; i < size; i++) {
            ret[i] = (FtpFile) list.get(i);
        }
        return ret;
    }

    /**
     * Parses a line ad a MLSD response element.
     *
     * @param line The line.
     * @return The file, or null if the line has to be ignored.
     * @throws FtpListParseException If the line is not a valid MLSD entry.
     */
    private FtpFile parseLine(String line) throws FtpListParseException {
        // According to Extensions to Ftp RFC 3659, the file name in a MLSD list response line come after the first space in the line  : https://tools.ietf.org/html/rfc3659
        // List line format is <FACTS WITH NO SPACES SEPARATED WITH SEMICOLON> <SPACE> <FILENAME>
        // Example of line that failed before: Type=file;Size=25730;Modify=19940728095854;Perm=; cap;mux.tar.z
        int nameIndex = line.indexOf(" ");

        // Throw exception if no name in response line
        if (nameIndex == -1) {
            throw new FtpListParseException();
        }

        // Extract the file name.
        String name = line.substring(nameIndex + 1);

        // Extract the facts string
        String factsLine = line.substring(0, nameIndex);
        ArrayList list = new ArrayList();
        StringTokenizer st = new StringTokenizer(factsLine, ";");
        while (st.hasMoreElements()) {
            String aux = st.nextToken().trim();
            if (aux.length() > 0) {
                list.add(aux);
            }
        }

        // If no facts, throw exception
        if (list.size() == 0) {
            throw new FtpListParseException();
        }

        // Parses the facts.
        Properties facts = new Properties();
        for (Iterator i = list.iterator(); i.hasNext(); ) {
            String aux = (String) i.next();
            int sep = aux.indexOf('=');
            if (sep == -1) {
                throw new FtpListParseException();
            }
            String key = aux.substring(0, sep).trim();
            String value = aux.substring(sep + 1, aux.length()).trim();
            if (key.length() == 0 || value.length() == 0) {
                throw new FtpListParseException();
            }
            facts.setProperty(key, value);
        }
        // Type.
        int type;
        String typeString = facts.getProperty("type");
        if (typeString == null) {
            throw new FtpListParseException();
        } else if ("file".equalsIgnoreCase(typeString)) {
            type = FtpFile.TYPE_FILE;
        } else if ("dir".equalsIgnoreCase(typeString)) {
            type = FtpFile.TYPE_DIRECTORY;
        } else if ("cdir".equalsIgnoreCase(typeString)) {
            // Current directory. Skips...
            return null;
        } else if ("pdir".equalsIgnoreCase(typeString)) {
            // Parent directory. Skips...
            return null;
        } else {
            // Unknown... (link?)... Skips...
            return null;
        }
        // Last modification date.
        Date modifiedDate = null;
        String modifyString = facts.getProperty("modify");
        if (modifyString != null) {
            modifyString += " +0000";
            try {
                synchronized (MLSD_DATE_FORMAT_1) {
                    modifiedDate = MLSD_DATE_FORMAT_1.parse(modifyString);
                }
            } catch (ParseException e1) {
                try {
                    synchronized (MLSD_DATE_FORMAT_2) {
                        modifiedDate = MLSD_DATE_FORMAT_2.parse(modifyString);
                    }
                } catch (ParseException e2) {
                    ;
                }
            }
        }
        // Size.
        long size = 0;
        String sizeString = facts.getProperty("size");
        if (sizeString != null) {
            try {
                size = Long.parseLong(sizeString);
            } catch (NumberFormatException e) {
                ;
            }
            if (size < 0) {
                size = 0;
            }
        }
        // Done!
        FtpFile ret = new FtpFile();
        ret.setType(type);
        ret.setModifiedDate(modifiedDate);
        ret.setSize(size);
        ret.setName(name);
        return ret;
    }

}
