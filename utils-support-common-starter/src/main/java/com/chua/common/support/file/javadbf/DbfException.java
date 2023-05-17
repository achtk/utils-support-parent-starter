/*
  DBFException
	Represents exceptions happen in the JAvaDBF classes.

  This file is part of JavaDBF packege.

  author: anil@linuxense.com
  license: LGPL (http://www.gnu.org/copyleft/lesser.html)

  $Id: DBFException.java,v 1.2 2004/03/31 10:40:18 anil Exp $
*/
package com.chua.common.support.file.javadbf;

import java.io.IOException;

/**
 * 异常
 *
 * @author Administrator
 */
public class DbfException extends IOException {

    private static final long serialVersionUID = -1493978629479908234L;

    public DbfException() {

        super();
    }

    public DbfException(String msg) {

        super(msg);
    }
}
