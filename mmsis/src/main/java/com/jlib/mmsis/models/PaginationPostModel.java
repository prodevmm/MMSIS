package com.jlib.mmsis.models;

public class PaginationPostModel {
    private String code_code, page_list_scale, pg;

    public PaginationPostModel(String code_code, String page_list_scale, String pg) {
        this.code_code = code_code;
        this.page_list_scale = page_list_scale;
        this.pg = pg;
    }

    public String getCode_code() {
        return code_code;
    }

    public String getPage_list_scale() {
        return page_list_scale;
    }

    public String getPg() {
        return pg;
    }
}
