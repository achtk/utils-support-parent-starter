/*	
 DBFHeader
 Class for reading the metadata assuming that the given
 InputStream carries DBF data.

 This file is part of JavaDBF packege.

 Author: anil@linuxense.com
 License: LGPL (http://www.gnu.org/copyleft/lesser.html)

 $Id$
*/

package com.chua.common.support.file.javadbf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

/**
 * head
 *
 * @author Administrator
 */
public class DbfHeader {

    static final byte SIG_DBASE_III = (byte) 0x03;
    /* DBF structure start here */

    byte signature;
    /**
     * 0
     */
    byte year;
    /**
     * 1
     */
    byte month;
    /**
     * 2
     */
    byte day;
    /**
     * 3
     */
    int numberOfRecords;
    /**
     * 4-7
     */
    short headerLength;
    /**
     * 8-9
     */
    short recordLength;
    /**
     * 10-11
     */
    short reserv1;
    /**
     * 12-13
     */
    byte incompleteTransaction;
    /**
     * 14
     */
    byte encryptionFlag;
    /**
     * 15
     */
    int freeRecordThread;
    /**
     * 16-19
     */
    int reserv2;
    /**
     * 20-23
     */
    int reserv3;
    /**
     * 24-27
     */
    byte mdxFlag;
    /**
     * 28
     */
    byte languageDriver;
    /**
     * 29
     */
    short reserv4;
    /**
     * 30-31
     */
    DbfField[] fieldArray;
    /**
     * each 32 bytes
     */
    byte terminator1;

    /**
     * n+1
     */

    //byte[] databaseContainer;
    /* DBF structure ends here */

    DbfHeader() {

        this.signature = SIG_DBASE_III;
        this.terminator1 = 0x0D;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    void read(DataInput dataInput) throws IOException {

        signature = dataInput.readByte();
        year = dataInput.readByte();
        month = dataInput.readByte();
        day = dataInput.readByte();
        numberOfRecords = Utils.readLittleEndianInt(dataInput);

        headerLength = Utils.readLittleEndianShort(dataInput);
        recordLength = Utils.readLittleEndianShort(dataInput);

        reserv1 = Utils.readLittleEndianShort(dataInput);
        incompleteTransaction = dataInput.readByte();
        encryptionFlag = dataInput.readByte();
        freeRecordThread = Utils.readLittleEndianInt(dataInput);
        reserv2 = dataInput.readInt();
        reserv3 = dataInput.readInt();
        mdxFlag = dataInput.readByte();
        languageDriver = dataInput.readByte();
        reserv4 = Utils.readLittleEndianShort(dataInput);

        Vector vector = new Vector();

        DbfField field = DbfField.createField(dataInput);
        while (field != null) {

            vector.addElement(field);
            field = DbfField.createField(dataInput);
        }

        fieldArray = new DbfField[vector.size()];

        for (int i = 0; i < fieldArray.length; i++) {

            fieldArray[i] = (DbfField) vector.elementAt(i);
        }

    }

    void write(DataOutput dataOutput) throws IOException {

        dataOutput.writeByte(signature);

        GregorianCalendar calendar = new GregorianCalendar();
        year = (byte) (calendar.get(Calendar.YEAR) - 1900);
        month = (byte) (calendar.get(Calendar.MONTH) + 1);
        day = (byte) (calendar.get(Calendar.DAY_OF_MONTH));

        dataOutput.writeByte(year);
        dataOutput.writeByte(month);
        dataOutput.writeByte(day);

        numberOfRecords = Utils.littleEndian(numberOfRecords);
        dataOutput.writeInt(numberOfRecords);

        headerLength = findHeaderLength();
        dataOutput.writeShort(Utils.littleEndian(headerLength));

        recordLength = findRecordLength();
        dataOutput.writeShort(Utils.littleEndian(recordLength));

        dataOutput.writeShort(Utils.littleEndian(reserv1));
        dataOutput.writeByte(incompleteTransaction);
        dataOutput.writeByte(encryptionFlag);
        dataOutput.writeInt(Utils.littleEndian(freeRecordThread));
        dataOutput.writeInt(Utils.littleEndian(reserv2));
        dataOutput.writeInt(Utils.littleEndian(reserv3));

        dataOutput.writeByte(mdxFlag);
        dataOutput.writeByte(languageDriver);
        dataOutput.writeShort(Utils.littleEndian(reserv4));

        for (int i = 0; i < fieldArray.length; i++) {
            fieldArray[i].write(dataOutput);
        }

        dataOutput.writeByte(terminator1);
    }

    private short findHeaderLength() {

        return (short) (
                1 +
                        3 +
                        4 +
                        2 +
                        2 +
                        2 +
                        1 +
                        1 +
                        4 +
                        4 +
                        4 +
                        1 +
                        1 +
                        2 +
                        (32 * fieldArray.length) +
                        1
        );
    }

    private short findRecordLength() {

        int recordLength = 0;
        for (int i = 0; i < fieldArray.length; i++) {
            recordLength += fieldArray[i].getFieldLength();
        }

        return (short) (recordLength + 1);
    }

    /**
     * 获取索引
     *
     * @param label 名称
     * @return 索引
     */
    public int indexOf(String label) {
        for (int i = 0; i < fieldArray.length; i++) {
            DbfField dbfField = fieldArray[i];
            if (dbfField.getName().equals(label)) {
                return i;
            }
        }
        return -1;
    }
}
