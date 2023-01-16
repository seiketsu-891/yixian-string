package com.star.string.req;

import lombok.Data;

@Data
public class TimeEntryReq {
    private String id;
    private String description;
    private String CategoryId;
    private Long start;
    private Long end;
    private Boolean isManual;
}


