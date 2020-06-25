package com.jlib.mmsis.models;

public class PdfModel {
    private String sortNumber, name, size, date, download_count;
    private PdfDownloadModel dlModel;

    public PdfModel(String sortNumber, String name, String size, String date, String download_count, PdfDownloadModel dlModel) {
        this.sortNumber = sortNumber;
        this.name = name;
        this.size = size;
        this.date = date;
        this.download_count = download_count;
        this.dlModel = dlModel;
    }

    public String getSortNumber() {
        return sortNumber;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getDate() {
        return date;
    }

    public String getDownload_count() {
        return download_count;
    }

    public PdfDownloadModel getDlModel() {
        return dlModel;
    }
}
