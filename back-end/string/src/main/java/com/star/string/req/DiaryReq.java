package com.star.string.req;

import lombok.Data;

import javax.naming.ldap.PagedResultsControl;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Data
public class DiaryReq {
    private String dialogs;
    private List<DiaryContentReq> content;
}
