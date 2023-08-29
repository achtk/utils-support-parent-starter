package com.chua.common.support.protocol.ftp.client.listparsers;

import com.chua.common.support.protocol.ftp.client.FtpFile;
import com.chua.common.support.protocol.ftp.client.FtpListParseException;
import com.chua.common.support.protocol.ftp.client.FtpListParser;

import java.util.Date;
import java.util.StringTokenizer;

/**
 * This parser can handle the EPLF format.
 *
 * @author Carlo Pelliccia
 */
public class EPLFListParser implements FtpListParser {

    public FtpFile[] parse(String[] lines) throws FtpListParseException {
        int size = lines.length;
        FtpFile[] ret = null;
        for (int i = 0; i < size; i++) {
            String l = lines[i];
            // Validate the plus sign.
            if (l.charAt(0) != '+') {
                throw new FtpListParseException();
            }
            // Split the facts from the filename.
            int a = l.indexOf('\t');
            if (a == -1) {
                throw new FtpListParseException();
            }
            String facts = l.substring(1, a);
            String name = l.substring(a + 1, l.length());
            // Parse the facts.
            Date md = null;
            boolean dir = false;
            long fileSize = 0;
            StringTokenizer st = new StringTokenizer(facts, ",");
            while (st.hasMoreTokens()) {
                String f = st.nextToken();
                int s = f.length();
                if (s > 0) {
                    if (s == 1) {
                        if (f.equals("/")) {
                            // This is a directory.
                            dir = true;
                        }
                    } else {
                        char c = f.charAt(0);
                        String value = f.substring(1, s);
                        if (c == 's') {
                            // Size parameter.
                            try {
                                fileSize = Long.parseLong(value);
                            } catch (Throwable t) {
                                ;
                            }
                        } else if (c == 'm') {
                            // Modified date.
                            try {
                                long m = Long.parseLong(value);
                                md = new Date(m * 1000);
                            } catch (Throwable t) {
                                ;
                            }
                        }
                    }
                }
            }
            // Create the related FtpFile object.
            if (ret == null) {
                ret = new FtpFile[size];
            }
            ret[i] = new FtpFile();
            ret[i].setName(name);
            ret[i].setModifiedDate(md);
            ret[i].setSize(fileSize);
            ret[i].setType(dir ? FtpFile.TYPE_DIRECTORY : FtpFile.TYPE_FILE);
        }
        return ret;
    }
}
