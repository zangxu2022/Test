package com.example.demo.module;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserInfo {
    @ApiModelProperty(value = "用户ID")
    private String userId;

}
