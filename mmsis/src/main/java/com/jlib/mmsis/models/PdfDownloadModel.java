package com.jlib.mmsis.models;

public class PdfDownloadModel {
    private String file_mask, file_name;

    public PdfDownloadModel(String file_mask, String file_name) {
        this.file_mask = file_mask;
        this.file_name = file_name;
    }

    public String getFile_mask() {
        return file_mask;
    }

    public String getFile_name() {
        return file_name;
    }
}
