package com.chua.common.support.protocol.ftp.client;

/**
 * This interface describes how to implement a textual extension recognizer,
 * which can be plugged into a FTPClient object calling its
 * setTextualExtensionsRecognizer() method.
 *
 * @author Carlo Pelliccia
 * @see FTPClient#setTextualExtensionRecognizer(FTPTextualExtensionRecognizer)
 */
public interface FTPTextualExtensionRecognizer {

	/**
	 * This method returns true if the given file extension is recognized to be
	 * a textual one.
	 *
	 * @param ext The file extension, always in lower-case.
	 * @return true if the given file extension is recognized to be a textual
	 * one.
	 */
	public boolean isTextualExt(String ext);

}
