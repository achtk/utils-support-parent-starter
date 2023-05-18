package com.chua.common.support.extra.el.baseutil.encrypt;

public class Base64Tool
{
    static private char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    public static String encode(byte[] src, int length)
    {
        StringBuilder cache = new StringBuilder();
        for (int i = 0; i < length; )
        {
            int hign   = i;
            int middle = hign + 1;
            int low    = middle + 1;
            if (low < length)
            {
                int base = ((src[hign] & 0xff) << 16) | ((src[middle] & 0xff) << 8) | (src[low] & 0xff);
                cache.append(alphabet[(base >> 18) & 0x3f]);
                cache.append(alphabet[(base >> 12) & 0x3f]);
                cache.append(alphabet[(base >> 6) & 0x3f]);
                cache.append(alphabet[(base) & 0x3f]);
                i = low + 1;
            }
            else if (middle < length)
            {
                int base = ((src[hign] & 0xff) << 8) | ((src[middle] & 0xff));
                cache.append(alphabet[(base >> 10) & 0x3f]);
                cache.append(alphabet[(base >> 4) & 0x3f]);
                cache.append(alphabet[((base) & 0x0f) << 2]);
                cache.append('=');
                return cache.toString();
            }
            else
            {
                int base = (src[hign] & 0xff);
                cache.append(alphabet[(base >> 2) & 0x3f]);
                cache.append(alphabet[((base) & 0x03) << 4]);
                cache.append('=');
                cache.append('=');
                return cache.toString();
            }
        }
        return cache.toString();
    }

    public static String encode(byte[] src)
    {
        return encode(src, src.length);
    }

    public static byte[] decode(String ciphertext)
    {
        char[] charArray = null;
        if ((ciphertext.length() & 0x03) == 0)
        {
            charArray = ciphertext.toCharArray();
        }
        else if ((ciphertext.length() & 0x03) == 3)
        {
            charArray = new char[ciphertext.length() + 1];
            System.arraycopy(ciphertext.toCharArray(), 0, charArray, 0, ciphertext.length());
            charArray[charArray.length - 1] = '=';
        }
        else if ((ciphertext.length() & 0x03) == 2)
        {
            charArray = new char[ciphertext.length() + 2];
            System.arraycopy(ciphertext.toCharArray(), 0, charArray, 0, ciphertext.length());
            charArray[charArray.length - 2] = '=';
            charArray[charArray.length - 1] = '=';
        }
        else
        {
            throw new UnsupportedOperationException();
        }
        byte[] result = charArray[charArray.length - 2] == '=' ? //
                new byte[charArray.length / 4 * 3 - 2] : charArray[charArray.length - 1] == '=' ? //
                new byte[charArray.length / 4 * 3 - 1] : new byte[charArray.length / 4 * 3];
        int j = 0;
        for (int i = 0; i < charArray.length; )
        {
            int h1 = i;
            int h2 = h1 + 1;
            int h3 = h2 + 1;
            int h4 = h3 + 1;
            if (charArray[h3] == '=')
            {
                byte a = (byte) (((toByte(charArray[h1]) & 0x3f) << 2) | (toByte(charArray[h2]) & 0x3f) >> 4);
                result[j] = a;
                return result;
            }
            else if (charArray[h4] == '=')
            {
                byte a = (byte) (((toByte(charArray[h1]) & 0xff) << 2) | ((toByte(charArray[h2]) & 0x3f) >> 4));
                byte b = (byte) (((toByte(charArray[h2]) & 0x0f) << 4) | ((toByte(charArray[h3]) & 0x3f) >> 2));
                result[j++] = a;
                result[j++] = b;
                return result;
            }
            else
            {
                byte a = (byte) (((toByte(charArray[h1]) & 0x3f) << 2) | ((toByte(charArray[h2]) & 0x3f) >> 4));
                byte b = (byte) (((toByte(charArray[h2]) & 0x0f) << 4) | ((toByte(charArray[h3]) & 0x3f)) >> 2);
                byte c = (byte) (((toByte(charArray[h3]) & 0x03) << 6) | ((toByte(charArray[h4]) & 0x3f)));
                result[j++] = a;
                result[j++] = b;
                result[j++] = c;
                i = h4 + 1;
            }
        }
        return result;
    }

    private static byte toByte(char c)
    {
        switch (c)
        {
            case 'A':
                return 0;
            case 'B':
                return 1;
            case 'C':
                return 2;
            case 'D':
                return 3;
            case 'E':
                return 4;
            case 'F':
                return 5;
            case 'G':
                return 6;
            case 'H':
                return 7;
            case 'I':
                return 8;
            case 'J':
                return 9;
            case 'K':
                return 10;
            case 'L':
                return 11;
            case 'M':
                return 12;
            case 'N':
                return 13;
            case 'O':
                return 14;
            case 'P':
                return 15;
            case 'Q':
                return 16;
            case 'R':
                return 17;
            case 'S':
                return 18;
            case 'T':
                return 19;
            case 'U':
                return 20;
            case 'V':
                return 21;
            case 'W':
                return 22;
            case 'X':
                return 23;
            case 'Y':
                return 24;
            case 'Z':
                return 25;
            case 'a':
                return 26;
            case 'b':
                return 27;
            case 'c':
                return 28;
            case 'd':
                return 29;
            case 'e':
                return 30;
            case 'f':
                return 31;
            case 'g':
                return 32;
            case 'h':
                return 33;
            case 'i':
                return 34;
            case 'j':
                return 35;
            case 'k':
                return 36;
            case 'l':
                return 37;
            case 'm':
                return 38;
            case 'n':
                return 39;
            case 'o':
                return 40;
            case 'p':
                return 41;
            case 'q':
                return 42;
            case 'r':
                return 43;
            case 's':
                return 44;
            case 't':
                return 45;
            case 'u':
                return 46;
            case 'v':
                return 47;
            case 'w':
                return 48;
            case 'x':
                return 49;
            case 'y':
                return 50;
            case 'z':
                return 51;
            case '0':
                return 52;
            case '1':
                return 53;
            case '2':
                return 54;
            case '3':
                return 55;
            case '4':
                return 56;
            case '5':
                return 57;
            case '6':
                return 58;
            case '7':
                return 59;
            case '8':
                return 60;
            case '9':
                return 61;
            case '+':
                return 62;
            case '/':
                return 63;
            default:
                throw new UnsupportedOperationException("非标准base64字符:" + c);
        }
    }
}
