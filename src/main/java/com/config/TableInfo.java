package com.config;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class TableInfo {
    private String fieldName;
    private String fileType;
    private String comment;

    private String sqlType;
    private String sqlTypeName;
}
