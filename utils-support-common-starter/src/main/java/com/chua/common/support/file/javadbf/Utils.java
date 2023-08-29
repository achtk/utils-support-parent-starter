/*
 	Utils
  Class for contining utility functions.

  This file is part of JavaDBF packege.

  author: anil@linuxense.com
  license: LGPL (http://www.gnu.org/copyleft/lesser.html)

  $Id: Utils.java,v 1.7 2004/03/31 16:00:34 anil Exp $
*/
package com.chua.common.support.file.javadbf;

import java.io.DataInput;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Arrays;

import static com.chua.common.support.constant.NumberConstant.*;

/**
 * Miscelaneous functions required by the JavaDBF package.
 *
 * @author Administrator
 */
public final class Utils {

    public static final int ALIGN_LEFT = 10;
    public static final int ALIGN_RIGHT = 12;

    private Utils() {
    }

    public static int readLittleEndianInt(DataInput in)
            throws IOException {

        int bigEndian = 0;
        for (int shiftBy = 0; shiftBy < NUM_32; shiftBy += NUM_8) {
            bigEndian |= (in.readUnsignedByte() & 0xff) << shiftBy;
        }

        return bigEndian;
    }

    public static short readLittleEndianShort(DataInput in)
            throws IOException {

        int low = in.readUnsignedByte() & 0xff;
        int high = in.readUnsignedByte();

        return (short) (high << 8 | low);
    }

    public static byte[] trimLeftSpaces(byte[] arr) {

        StringBuffer buffer = new StringBuffer(arr.length);

        for (int i = 0; i < arr.length; i++) {

            if (arr[i] != ' ') {

                buffer.append((char) arr[i]);
            }
        }

        return buffer.toString().getBytes();
    }

    public static short littleEndian(short value) {

        short num1 = value;
        short mask = (short) 0xff;

        short num2 = (short) (num1 & mask);
        num2 <<= 8;
        mask <<= 8;

        num2 |= (num1 & mask) >> 8;

        return num2;
    }

    public static int littleEndian(int value) {

        int num1 = value;
        int mask = 0xff;
        int num2 = 0x00;

        num2 |= num1 & mask;

        for (int i = 1; i < NUM_4; i++) {

            num2 <<= 8;
            mask <<= 8;
            num2 |= (num1 & mask) >> (8 * i);
        }

        return num2;
    }

    public static byte[] textPadding(String text, String characterSetName, int length) throws UnsupportedEncodingException {

        return textPadding(text, characterSetName, length, Utils.ALIGN_LEFT);
    }

    public static byte[] textPadding(String text, String characterSetName, int length, int alignment) throws UnsupportedEncodingException {

        return textPadding(text, characterSetName, length, alignment, (byte) ' ');
    }

    public static byte[] textPadding(String text, String characterSetName, int length, int alignment,
                                     byte paddingByte) throws UnsupportedEncodingException {


        byte[] byteArray = new byte[length];
        if (text.getBytes(characterSetName).length >= length) {
            System.arraycopy(text.getBytes(characterSetName), 0, byteArray, 0, length);
        } else {

            Arrays.fill(byteArray, paddingByte);

            switch (alignment) {

                case ALIGN_LEFT:
                    System.arraycopy(text.getBytes(characterSetName), 0, byteArray, 0, text.getBytes(characterSetName).length);
                    break;

                case ALIGN_RIGHT:
                    int tOffset = length - text.length();
                    System.arraycopy(text.getBytes(characterSetName), 0, byteArray, tOffset, text.getBytes(characterSetName).length);
                    break;
                default:
            }
        }
        return byteArray;
    }

    public static byte[] doubleFormating(Double doubleNum, String characterSetName, int fieldLength, int sizeDecimalPart) throws UnsupportedEncodingException {

        int sizeWholePart = fieldLength - (sizeDecimalPart > 0 ? (sizeDecimalPart + 1) : 0);

        StringBuffer format = new StringBuffer(fieldLength);

        for (int i = 0; i < sizeWholePart; i++) {

            format.append("#");
        }

        if (sizeDecimalPart > 0) {

            format.append(".");

            for (int i = 0; i < sizeDecimalPart; i++) {

                format.append("0");
            }
        }

        DecimalFormat df = new DecimalFormat(format.toString());

        return textPadding(df.format(doubleNum.doubleValue()).toString(), characterSetName, fieldLength, ALIGN_RIGHT);
    }

    public static boolean contains(byte[] arr, byte value) {

        boolean found = false;
        for (int i = 0; i < arr.length; i++) {

            if (arr[i] == value) {

                found = true;
                break;
            }
        }

        return found;
    }
}
