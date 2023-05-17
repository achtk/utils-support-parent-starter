/*
  DBFReader
  Class for reading the records assuming that the given
	InputStream comtains DBF data.

  This file is part of JavaDBF packege.

  Author: anil@linuxense.com
  License: LGPL (http://www.gnu.org/copyleft/lesser.html)

  $Id: DBFReader.java,v 1.8 2004/03/31 10:54:03 anil Exp $
*/

package com.chua.common.support.file.javadbf;

import com.chua.common.support.utils.IoUtils;
import lombok.Getter;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.GregorianCalendar;

/**
 * DBFReader class can creates objects to represent DBF data.
 * <p>
 * This Class is used to read data from a DBF file. Meta data and
 * records can be queried against this document.
 *
 * <p>
 * DBFReader cannot write anythng to a DBF file. For creating DBF files
 * use DBFWriter.
 *
 * <p>
 * Fetching rocord is possible only in the forward direction and
 * cannot re-wound. In such situation, a suggested approach is to reconstruct the object.
 *
 * <p>
 * The nextRecord() method returns an array of Objects and the types of these
 * Object are as follows:
 *
 * <table>
 * <tr>
 * <th>xBase Type</th><th>Java Type</th>
 * </tr>
 *
 * <tr>
 * <td>C</td><td>String</td>
 * </tr>
 * <tr>
 * <td>N</td><td>Integer</td>
 * </tr>
 * <tr>
 * <td>F</td><td>Double</td>
 * </tr>
 * <tr>
 * <td>L</td><td>Boolean</td>
 * </tr>
 * <tr>
 * <td>D</td><td>java.util.Date</td>
 * </tr>
 * </table>
 * @author Administrator
 */
public class DbfReader extends DbfBase implements AutoCloseable {

    DataInputStream dataInputStream;
    @Getter
    DbfHeader header;

    /**
     * Class specific variables
     */
    boolean isClosed = true;

    /**
     * Initializes a DBFReader object.
     * <p>
     * When this constructor returns the object
     * will have completed reading the hader (meta date) and
     * header information can be quried there on. And it will
     * be ready to return the first row.
     *
     * @param in where the data is read from.
     */
    public DbfReader(InputStream in) throws DbfException {

        try {

            this.dataInputStream = new DataInputStream(in);
            this.isClosed = false;
            this.header = new DbfHeader();
            this.header.read(this.dataInputStream);

            /* it might be required to leap to the start of records at times */
            int tDataStartIndex = this.header.headerLength - (32 + (32 * this.header.fieldArray.length)) - 1;
            if (tDataStartIndex > 0) {

                dataInputStream.skip(tDataStartIndex);
            }
        } catch (IOException e) {

            throw new DbfException(e.getMessage());
        }
    }


    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer(this.header.year + "/" + this.header.month + "/" + this.header.day + "\n"
                + "Total records: " + this.header.numberOfRecords +
                "\nHEader length: " + this.header.headerLength +
                "");

        for (int i = 0; i < this.header.fieldArray.length; i++) {

            sb.append(this.header.fieldArray[i].getName());
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Returns the number of records in the DBF.
     */
    public int getRecordCount() {

        return this.header.numberOfRecords;
    }

    /**
     * Returns the asked Field. In case of an invalid index,
     * it returns a ArrayIndexOutofboundsException.
     *
     * @param index Index of the field. Index of the first field is zero.
     */
    public DbfField getField(int index)
            throws DbfException {

        if (isClosed) {

            throw new DbfException("Source is not open");
        }

        return this.header.fieldArray[index];
    }

    /**
     * Returns the number of field in the DBF.
     */
    public int getFieldCount()
            throws DbfException {

        if (isClosed) {

            throw new DbfException("Source is not open");
        }

        if (this.header.fieldArray != null) {

            return this.header.fieldArray.length;
        }

        return -1;
    }

    /**
     * Reads the returns the next row in the DBF stream.
     *
     * @returns The next row as an Object array. Types of the elements
     * these arrays follow the convention mentioned in the class description.
     */
    public Object[] nextRecord()
            throws DbfException {

        if (isClosed) {

            throw new DbfException("Source is not open");
        }

        Object[] recordObjects = new Object[this.header.fieldArray.length];

        try {

            boolean isDeleted = false;
            do {

                if (isDeleted) {

                    dataInputStream.skip(this.header.recordLength - 1);
                }

                int readByte = dataInputStream.readByte();
                if (readByte == END_OF_DATA) {

                    return null;
                }

                isDeleted = (readByte == '*');
            } while (isDeleted);

            for (int i = 0; i < this.header.fieldArray.length; i++) {

                switch (this.header.fieldArray[i].getDataType()) {

                    case 'C':

                        byte[] bArray = new byte[this.header.fieldArray[i].getFieldLength()];
                        dataInputStream.read(bArray);
                        recordObjects[i] = new String(bArray, characterSetName);
                        break;

                    case 'D':

                        byte[] tByteYear = new byte[4];
                        dataInputStream.read(tByteYear);

                        byte[] tByteMonth = new byte[2];
                        dataInputStream.read(tByteMonth);

                        byte[] tByteDay = new byte[2];
                        dataInputStream.read(tByteDay);

                        try {

                            GregorianCalendar calendar = new GregorianCalendar(
                                    Integer.parseInt(new String(tByteYear)),
                                    Integer.parseInt(new String(tByteMonth)) - 1,
                                    Integer.parseInt(new String(tByteDay))
                            );

                            recordObjects[i] = calendar.getTime();
                        } catch (NumberFormatException e) {
                            /* this field may be empty or may have improper value set */
                            recordObjects[i] = null;
                        }

                        break;

                    case 'F':

                        try {

                            byte[] bytes = new byte[this.header.fieldArray[i].getFieldLength()];
                            dataInputStream.read(bytes);
                            bytes = Utils.trimLeftSpaces(bytes);
                            if (bytes.length > 0 && !Utils.contains(bytes, (byte) '?')) {

                                recordObjects[i] = new Float(new String(bytes));
                            } else {

                                recordObjects[i] = null;
                            }
                        } catch (NumberFormatException e) {

                            throw new DbfException("Failed to parse Float: " + e.getMessage());
                        }

                        break;

                    case 'N':

                        try {

                            byte[] tNumeric = new byte[this.header.fieldArray[i].getFieldLength()];
                            dataInputStream.read(tNumeric);
                            tNumeric = Utils.trimLeftSpaces(tNumeric);

                            if (tNumeric.length > 0 && !Utils.contains(tNumeric, (byte) '?')) {

                                recordObjects[i] = new Double(new String(tNumeric));
                            } else {

                                recordObjects[i] = null;
                            }
                        } catch (NumberFormatException e) {

                            throw new DbfException("Failed to parse Number: " + e.getMessage());
                        }

                        break;

                    case 'L':

                        byte readByte = dataInputStream.readByte();
                        if (readByte == 'Y' || readByte == 't' || readByte == 'T' || readByte == 't') {

                            recordObjects[i] = Boolean.TRUE;
                        } else {

                            recordObjects[i] = Boolean.FALSE;
                        }
                        break;

                    case 'M':
                        // TODO Later
                        recordObjects[i] = new String("null");
                        break;

                    default:
                        recordObjects[i] = new String("null");
                }
            }
        } catch (EOFException e) {

            return null;
        } catch (IOException e) {

            throw new DbfException(e.getMessage());
        }

        return recordObjects;
    }

    @Override
    public void close() throws Exception {
        IoUtils.closeQuietly(dataInputStream);
    }
}
