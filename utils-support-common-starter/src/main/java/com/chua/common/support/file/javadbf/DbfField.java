/*
  DBFField
	Class represents a "field" (or column) definition of a DBF data structure.

  This file is part of JavaDBF packege.

  author: anil@linuxense.com
  license: LGPL (http://www.gnu.org/copyleft/lesser.html)

  $Id: DBFField.java,v 1.7 2004/03/31 10:50:11 anil Exp $
*/

package com.chua.common.support.file.javadbf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static com.chua.common.support.constant.CommonConstant.*;
import static com.chua.common.support.constant.NumberConstant.NUM_100;

/**
 * dbf field
 *
 * @author Administrator
 */
public class DbfField {

    public static final byte FIELD_TYPE_C = (byte) 'C';
    public static final byte FIELD_TYPE_L = (byte) 'L';
    public static final byte FIELD_TYPE_N = (byte) 'N';
    public static final byte FIELD_TYPE_F = (byte) 'F';
    public static final byte FIELD_TYPE_D = (byte) 'D';
    public static final byte FIELD_TYPE_M = (byte) 'M';

    /**
     * Field struct variables start here
     */
    byte[] fieldName = new byte[11];
    /**
     * 0-10
     */
    byte dataType;
    /**
     * 11
     */
    int reserv1;
    /**
     * 12-15
     */
    int fieldLength;
    /**
     * 16
     */
    byte decimalCount;
    /**
     * 17
     */
    short reserv2;
    /**
     * 18-19
     */
    byte workAreaId;
    /**
     * 20
     */
    short reserv3;
    /**
     * 21-22
     */
    byte setFieldsFlag;
    /**
     * 23
     */
    byte[] reserv4 = new byte[7];
    /**
     * 24-30
     */
    byte indexFieldFlag;
    /**
     * other class variables
     */
    int nameNullIndex = 0;

    /**
     * Creates a DBFField object from the data read from the given DataInputStream.
     * <p>
     * The data in the DataInputStream object is supposed to be organised correctly
     * and the stream "pointer" is supposed to be positioned properly.
     *
     * @param in DataInputStream
     * @return Returns the created DBFField object.
     * @throws IOException If any stream reading problems occures.
     */
    protected static DbfField createField(DataInput in)
            throws IOException {

        DbfField field = new DbfField();

        byte readByte = in.readByte();
        int x0D = 0x0d;
        if (readByte == (byte) x0D) {

            return null;
        }

        in.readFully(field.fieldName, 1, 10);
        field.fieldName[0] = readByte;

        for (int i = 0; i < field.fieldName.length; i++) {

            if (field.fieldName[i] == (byte) 0) {

                field.nameNullIndex = i;
                break;
            }
        }

        field.dataType = in.readByte();
        field.reserv1 = Utils.readLittleEndianInt(in);
        field.fieldLength = in.readUnsignedByte();
        field.decimalCount = in.readByte();
        field.reserv2 = Utils.readLittleEndianShort(in);
        field.workAreaId = in.readByte();
        field.reserv2 = Utils.readLittleEndianShort(in);
        field.setFieldsFlag = in.readByte();
        in.readFully(field.reserv4);
        field.indexFieldFlag = in.readByte();

        return field;
    }

    /**
     * Writes the content of DBFField object into the stream as per
     * DBF format specifications.
     *
     * @param out OutputStream
     * @throws IOException if any stream related issues occur.
     */
    protected void write(DataOutput out)
            throws IOException {

        //DataOutputStream out = new DataOutputStream( os);

        // Field Name
        out.write(fieldName);
        out.write(new byte[11 - fieldName.length]);

        // data type
        out.writeByte(dataType);
        out.writeInt(0x00);
        out.writeByte(fieldLength);
        out.writeByte(decimalCount);
        out.writeShort((short) 0x00);
        out.writeByte((byte) 0x00);
        out.writeShort((short) 0x00);
        out.writeByte((byte) 0x00);
        out.write(new byte[7]);
        out.writeByte((byte) 0x00);
    }

    /**
     * Returns the name of the field.
     *
     * @return Name of the field as String.
     */
    public String getName() {

        return new String(this.fieldName, 0, nameNullIndex);
    }

    /**
     * Returns the data type of the field.
     *
     * @return Data type as byte.
     */
    public byte getDataType() {

        return dataType;
    }

    /**
     * Returns field length.
     *
     * @return field length as int.
     */
    public int getFieldLength() {

        return fieldLength;
    }

    /**
     * Returns the decimal part. This is applicable
     * only if the field type if of numeric in nature.
     * <p>
     * If the field is specified to hold integral values
     * the value returned by this method will be zero.
     *
     * @return decimal field size as int.
     */
    public int getDecimalCount() {

        return decimalCount;
    }

    // Setter methods

    // byte[] fieldName = new byte[ 11]; /* 0-10*/
    // byte dataType
    // int reserv1
    // byte fieldLength
    // byte decimalCount
    // short reserv2
    // byte workAreaId
    // short reserv3
    // byte setFieldsFlag
    // byte[] reserv4 = new byte[ 7]
    // byte indexFieldFlag

    /**
     * @deprecated This method is depricated as of version 0.3.3.1 and is replaced by {@link #setName(String)}.
     */
    public void setFieldName(String value) {

        setName(value);
    }

    /**
     * Sets the name of the field.
     *
     * @param name of the field as String.
     * @since 0.3.3.1
     */
    public void setName(String name) {

        if (name == null) {

            throw new IllegalArgumentException("Field name cannot be null");
        }

        if (name.length() == 0 || name.length() > NUM_100) {

            throw new IllegalArgumentException("Field name should be of length 0-10");
        }

        this.fieldName = name.getBytes();
        this.nameNullIndex = this.fieldName.length;
    }

    /**
     * Sets the data type of the field.
     *
     * @param value of the field. One of the following:<br>
     *              C, L, N, F, D, M
     */
    public void setDataType(byte value) {
        switch (value) {
            case 'D':
                this.fieldLength = 8;
                break;
            case 'C':
            case 'L':
            case 'N':
            case 'F':
            case 'M':
                this.dataType = value;
                break;
            default:
                throw new IllegalArgumentException("Unknown data type");
        }
    }

    /**
     * Length of the field.
     * This method should be called before calling setDecimalCount().
     *
     * @param value of the field as int.
     */
    public void setFieldLength(int value) {

        if (value <= 0) {

            throw new IllegalArgumentException("Field length should be a positive number");
        }

        if (this.dataType == FIELD_TYPE_D) {

            throw new UnsupportedOperationException("Cannot do this on a Date field");
        }

        fieldLength = value;
    }

    /**
     * Sets the decimal place size of the field.
     * Before calling this method the size of the field
     * should be set by calling setFieldLength().
     *
     * @param value of the decimal field.
     */
    public void setDecimalCount(int value) {

        if (value < 0) {

            throw new IllegalArgumentException("Decimal length should be a positive number");
        }

        if (value > fieldLength) {

            throw new IllegalArgumentException("Decimal length should be less than field length");
        }

        decimalCount = (byte) value;
    }

    public String getType() {
        byte dataType1 = getDataType();
        if (LETTER_UPPERCASE_C == dataType1) {
            return "CHARACTER";
        }

        if (LETTER_UPPERCASE_V == dataType1) {
            return "VARCHAR";
        }

        if (LETTER_UPPERCASE_D == dataType1) {
            return "DATE";
        }

        if (LETTER_UPPERCASE_O == dataType1) {
            return "DOUBLE";
        }

        if (LETTER_UPPERCASE_N == dataType1) {
            return "NUMERIC";
        }

        if (LETTER_UPPERCASE_I == dataType1) {
            return "LONG";
        }


        if (LETTER_UPPERCASE_W == dataType1) {
            return "BLOB";
        }

        if (LETTER_UPPERCASE_Y == dataType1) {
            return "CURRENCY";
        }

        if (LETTER_UPPERCASE_T == dataType1) {
            return "TIMESTAMP";
        }

        if (SYMBOL_AT_CHAR == dataType1) {
            return "TIMESTAMP_DBASE7";
        }

        if (DIGITS_LOWER[0] == dataType1) {
            return "NULL_FLAGS";
        }

        if (SYMBOL_PLUS_CHAR == dataType1) {
            return "AUTOINCREMENT";
        }

        if (LETTER_UPPERCASE_G == dataType1) {
            return "GENERAL_OLE";
        }

        if (LETTER_UPPERCASE_P == dataType1) {
            return "PICTURE";
        }

        if (LETTER_UPPERCASE_F == dataType1) {
            return "FLOATING_POINT";
        }

        if (LETTER_UPPERCASE_M == dataType1) {
            return "MEMO";
        }

        if (LETTER_UPPERCASE_B == dataType1) {
            return "BINARY";
        }

        if (LETTER_UPPERCASE_L == dataType1) {
            return "LOGICAL";
        }

        if (LETTER_UPPERCASE_Q == dataType1) {
            return "VARBINARY";
        }

        return null;
    }
}
