package com.jlib.mmsis.models;

public class PaginationModel {
    private String pageNumber;
    private PaginationPostModel postModel;

    public PaginationModel(String pageNumber, PaginationPostModel postModel) {
        this.pageNumber = pageNumber;
        this.postModel = postModel;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public PaginationPostModel getPostModel() {
        return postModel;
    }
}
