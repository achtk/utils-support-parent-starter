package com.chua.common.support.protocol.ftp.client.extrecognizers;


import com.chua.common.support.protocol.ftp.client.FTPClient;
import com.chua.common.support.protocol.ftp.client.FTPTextualExtensionRecognizer;

import java.util.ArrayList;

/**
 * A textual extension recognizer with parametric extensions, which can be added
 * or removed at runtime.
 *
 * @author Carlo Pelliccia
 * @see FTPClient#setTextualExtensionRecognizer(FTPTextualExtensionRecognizer)
 */
public class ParametricTextualExtensionRecognizer implements
        FTPTextualExtensionRecognizer {

    /**
     * Extension list.
     */
    private ArrayList exts = new ArrayList();

    /**
     * It builds the recognizer with an empty extension list.
     */
    public ParametricTextualExtensionRecognizer() {
        ;
    }

    /**
     * It builds the recognizer with an initial extension list.
     *
     * @param exts The initial extension list.
     */
    public ParametricTextualExtensionRecognizer(String[] exts) {
        for (int i = 0; i < exts.length; i++) {
            addExtension(exts[i]);
        }
    }

    /**
     * It builds the recognizer with an initial extension list.
     *
     * @param exts The initial extension list.
     */
    public ParametricTextualExtensionRecognizer(ArrayList exts) {
        int size = exts.size();
        for (int i = 0; i < size; i++) {
            Object aux = exts.get(i);
            if (aux instanceof String) {
                String ext = (String) aux;
                addExtension(ext);
            }
        }
    }

    /**
     * This method adds an extension to the recognizer.
     *
     * @param ext The extension.
     */
    public void addExtension(String ext) {
        synchronized (exts) {
            ext = ext.toLowerCase();
            exts.add(ext);
        }
    }

    /**
     * This method removes an extension to the recognizer.
     *
     * @param ext The extension to be removed.
     */
    public void removeExtension(String ext) {
        synchronized (exts) {
            ext = ext.toLowerCase();
            exts.remove(ext);
        }
    }

    /**
     * This method returns the recognized extension list.
     *
     * @return The list with all the extensions recognized to be for textual
     * files.
     */
    public String[] getExtensions() {
        synchronized (exts) {
            int size = exts.size();
            String[] ret = new String[size];
            for (int i = 0; i < size; i++) {
                ret[i] = (String) exts.get(i);
            }
            return ret;
        }
    }

    public boolean isTextualExt(String ext) {
        synchronized (exts) {
            return exts.contains(ext);
        }
    }

}
