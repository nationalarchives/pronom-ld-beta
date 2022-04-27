package com.wallscope.pronombackend.model;

public class PaginationHelper {
    private final String text;
    private final Integer offset;
    private final Boolean active;

    public PaginationHelper(String text, Integer offset, Boolean active) {
        this.text = text;
        this.offset = offset;
        this.active = active;
    }

    public Integer getOffset() {
        return offset;
    }

    public String getText() {
        return text;
    }

    public Boolean getActive() {
        return active;
    }
}
