package com.eqlplus.config;

import lombok.Builder;
import lombok.Data;

/**
 * 需求配置
 */
@Data @Builder
public class RequireConfig {
    // 是否创建dao层
    private boolean needDao;
    // 是否创建bean
    private boolean needBean;
    // 是否创建注释
    private boolean needComment;
    // 是否创建service
    private boolean needService;
    // 是否创建controller
    private boolean needController;

    // 是否创建dto
    private boolean needDto;

}
