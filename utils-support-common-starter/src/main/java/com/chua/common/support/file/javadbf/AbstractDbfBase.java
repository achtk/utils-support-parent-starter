/*
	$Id: DBFBase.java,v 1.3 2004/03/31 15:59:40 anil Exp $
	Serves as the base class of DBFReader adn DBFWriter.
	
	@author: anil@linuxense.com

	Support for choosing implemented character Sets as 
	suggested by Nick Voznesensky <darkers@mail.ru>
*/
/**
 * Base class for DBFReader and DBFWriter.
 */
package com.chua.common.support.file.javadbf;

import lombok.Data;

/**
 * dbf base
 * @author Administrator
 */
@Data
public abstract class AbstractDbfBase {

    protected String characterSetName = "8859_1";
    protected final int END_OF_DATA = 0x1A;


}
