package com.denwon.crm.module.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户更新DTO
 * 
 * @author Denwon Team
 * @since 1.0.0
 */
@Data
public class UserUpdateDTO {
    
    @Size(max = 100, message = "姓名长度不能超过100个字符")
    private String name;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Size(max = 20, message = "手机号长度不能超过20个字符")
    private String mobile;
    
    private String employeeNo;
    
    private Long orgUnitId;
    
    private String title;
    
    private String avatar;
    
    private Integer status;
}