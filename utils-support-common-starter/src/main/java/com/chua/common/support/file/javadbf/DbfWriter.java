/*
	DBFWriter
	Class for defining a DBF structure and addin data to that structure and 
	finally writing it to an OutputStream.

	This file is part of JavaDBF packege.

	author: anil@linuxense.com
	license: LGPL (http://www.gnu.org/copyleft/lesser.html)

	$Id: DBFWriter.java,v 1.9 2004/03/31 10:57:16 anil Exp $
*/
package com.chua.common.support.file.javadbf;

import java.io.*;
import java.util.*;

/**
 * dbf write
 *
 * @author Administrator
 */
public class DbfWriter extends DbfBase implements AutoCloseable {

    /**
     * other class variables
     */
    DbfHeader header;
    @SuppressWarnings("rawtypes")
    Vector vRecords = new Vector();
    int recordCount = 0;
    RandomAccessFile raf = null;
    /**
     * Open and append records to an existing DBF
     */
    boolean appendMode = false;

    /**
     * Creates an empty Object.
     */
    public DbfWriter() {
        this.header = new DbfHeader();
    }

    /**
     * Creates a DBFWriter which can append to records to an existing DBF file.
     *
     * @param dbfFile The file passed in shouls be a valid DBF file.
     * @throws DbfException if the passed in file does exist but not a valid DBF file, or if an IO error occurs.
     */
    public DbfWriter(File dbfFile)
            throws DbfException {

        try {

            this.raf = new RandomAccessFile(dbfFile, "rw");

			/* before proceeding check whether the passed in File object
			 is an empty/non-existent file or not.
			 */
            if (!dbfFile.exists() || dbfFile.length() == 0) {

                this.header = new DbfHeader();
                return;
            }

            header = new DbfHeader();
            this.header.read(raf);

            /* position file pointer at the end of the raf */
            this.raf.seek(this.raf.length() - 1);
        } catch (FileNotFoundException e) {

            throw new DbfException("Specified file is not found. " + e.getMessage());
        } catch (IOException e) {

            throw new DbfException(e.getMessage() + " while reading header");
        }

        this.recordCount = this.header.numberOfRecords;
    }

    /**
     * Sets fields.
     */
    public void setFields(DbfField[] fields)
            throws DbfException {

        if (this.header.fieldArray != null) {

            throw new DbfException("Fields has already been set");
        }

        if (fields == null || fields.length == 0) {

            throw new DbfException("Should have at least one field");
        }

        for (int i = 0; i < fields.length; i++) {

            if (fields[i] == null) {

                throw new DbfException("Field " + (i + 1) + " is null");
            }
        }

        this.header.fieldArray = fields;

        try {

            if (this.raf != null && this.raf.length() == 0) {

				/* 
			  	this is a new/non-existent file. So write header before proceeding
		 		*/
                this.header.write(this.raf);
            }
        } catch (IOException e) {

            throw new DbfException("Error accesing file");
        }
    }

    /**
     * Add a record.
     */
    @SuppressWarnings("unchecked")
    public void addRecord(Object[] values)
            throws DbfException {

        if (this.header.fieldArray == null) {

            throw new DbfException("Fields should be set before adding records");
        }

        if (values == null) {

            throw new DbfException("Null cannot be added as row");
        }

        if (values.length != this.header.fieldArray.length) {

            throw new DbfException("Invalid record. Invalid number of fields in row");
        }

        for (int i = 0; i < this.header.fieldArray.length; i++) {

            if (values[i] == null) {

                continue;
            }

            switch (this.header.fieldArray[i].getDataType()) {

                case 'C':
                    if (!(values[i] instanceof String)) {
                        throw new DbfException("Invalid value for field " + i);
                    }
                    break;

                case 'L':
                    if (!(values[i] instanceof Boolean)) {
                        throw new DbfException("Invalid value for field " + i);
                    }
                    break;

                case 'N':
                    if (!(values[i] instanceof Double)) {
                        throw new DbfException("Invalid value for field " + i);
                    }
                    break;

                case 'D':
                    if (!(values[i] instanceof Date)) {
                        throw new DbfException("Invalid value for field " + i);
                    }
                    break;

                case 'F':
                    if (!(values[i] instanceof Double)) {
                        throw new DbfException("Invalid value for field " + i);
                    }
                    break;
                default:
            }
        }

        if (this.raf == null) {

            vRecords.addElement(values);
        } else {

            try {

                writeRecord(this.raf, values);
                this.recordCount++;
            } catch (IOException e) {

                throw new DbfException("Error occured while writing record. " + e.getMessage());
            }
        }
    }

    /**
     * Writes the set data to the OutputStream.
     */
    public void write(OutputStream out)
            throws DbfException {

        try {

            if (this.raf == null) {

                DataOutputStream outStream = new DataOutputStream(out);

                this.header.numberOfRecords = vRecords.size();
                this.header.write(outStream);

                /* Now write all the records */
                int tReccount = vRecords.size();
                for (int i = 0; i < tReccount; i++) {

                    Object[] elementAt = (Object[]) vRecords.elementAt(i);

                    writeRecord(outStream, elementAt);
                }

                outStream.write(END_OF_DATA);
                outStream.flush();
            } else {

                /* everything is written already. just update the header for record count and the END_OF_DATA mark */
                this.header.numberOfRecords = this.recordCount;
                this.raf.seek(0);
                this.header.write(this.raf);
                this.raf.seek(raf.length());
                this.raf.writeByte(END_OF_DATA);
                this.raf.close();
            }

        } catch (IOException e) {

            throw new DbfException(e.getMessage());
        }
    }

    public void write()
            throws DbfException {

        this.write(null);
    }

    private void writeRecord(DataOutput dataOutput, Object[] objectArray) throws IOException {
        dataOutput.write((byte) ' ');
        for (int j = 0; j < this.header.fieldArray.length; j++) {

            switch (this.header.fieldArray[j].getDataType()) {

                case 'C':
                    if (objectArray[j] != null) {
                        String strValue = objectArray[j].toString();
                        dataOutput.write(Utils.textPadding(strValue, characterSetName, this.header.fieldArray[j].getFieldLength()));
                    } else {
                        dataOutput.write(Utils.textPadding("", this.characterSetName, this.header.fieldArray[j].getFieldLength()));
                    }
                    break;
                case 'D':
                    if (objectArray[j] != null) {
                        GregorianCalendar calendar = new GregorianCalendar();
                        calendar.setTime((Date) objectArray[j]);
//						StringBuffer t_sb = new StringBuffer();
                        dataOutput.write(String.valueOf(calendar.get(Calendar.YEAR)).getBytes());
                        dataOutput.write(Utils.textPadding(String.valueOf(calendar.get(Calendar.MONTH) + 1), this.characterSetName, 2, Utils.ALIGN_RIGHT, (byte) '0'));
                        dataOutput.write(Utils.textPadding(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), this.characterSetName, 2, Utils.ALIGN_RIGHT, (byte) '0'));
                    } else {
                        dataOutput.write("        ".getBytes());
                    }
                    break;
                case 'F':
                    if (objectArray[j] != null) {
                        dataOutput.write(Utils.doubleFormating((Double) objectArray[j], this.characterSetName, this.header.fieldArray[j].getFieldLength(), this.header.fieldArray[j].getDecimalCount()));
                    } else {
                        dataOutput.write(Utils.textPadding("?", this.characterSetName, this.header.fieldArray[j].getFieldLength(), Utils.ALIGN_RIGHT));
                    }
                    break;
                case 'N':
                    if (objectArray[j] != null) {
                        dataOutput.write(
                                Utils.doubleFormating((Double) objectArray[j], this.characterSetName, this.header.fieldArray[j].getFieldLength(), this.header.fieldArray[j].getDecimalCount()));
                    } else {
                        dataOutput.write(
                                Utils.textPadding("?", this.characterSetName, this.header.fieldArray[j].getFieldLength(), Utils.ALIGN_RIGHT));
                    }
                    break;
                case 'L':
                    if (objectArray[j] != null) {
                        if (Objects.equals((Boolean) objectArray[j], Boolean.TRUE)) {
                            dataOutput.write((byte) 'T');
                        } else {
                            dataOutput.write((byte) 'F');
                        }
                    } else {
                        dataOutput.write((byte) '?');
                    }
                    break;
                case 'M':
                    break;
                default:
                    throw new DbfException("Unknown field type " + this.header.fieldArray[j].getDataType());
            }
        }
    }

    @Override
    public void close() throws Exception {

    }
}
