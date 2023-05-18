package com.chua.common.support.extra.el.baseutil;

import java.io.*;
import java.nio.charset.Charset;

public class IoUtil
{
    /**
     * 这段代码拷贝自Hutool
     *
     * @param in
     * @param charset
     * @return
     */
    public static BufferedReader getReader(InputStream in, Charset charset)
    {
        if (null == in)
        {
            return null;
        }
        else
        {
            InputStreamReader reader;
            if (null == charset)
            {
                reader = new InputStreamReader(in);
            }
            else
            {
                reader = new InputStreamReader(in, charset);
            }
            return new BufferedReader(reader);
        }
    }

    public static byte[] readAllBytes(InputStream inputStream) throws IOException
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[]                buffer = new byte[1024];
        int                   length;
        while ((length = inputStream.read(buffer)) != -1)
        {
            output.write(buffer, 0, length);
        }
        return output.toByteArray();
    }
}
