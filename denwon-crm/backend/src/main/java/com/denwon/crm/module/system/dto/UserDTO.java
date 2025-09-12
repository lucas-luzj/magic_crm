package com.denwon.crm.module.system.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 用户DTO
 * 
 * @author Denwon Team
 * @since 1.0.0
 */
@Data
public class UserDTO {
    
    private Long id;
    
    private String username;
    
    private String name;
    
    private String email;
    
    private String mobile;
    
    private String employeeNo;
    
    private String title;
    
    private String avatar;
    
    private Integer status;
    
    private OrgUnitDTO orgUnit;
    
    private Set<RoleDTO> roles;
    
    private LocalDateTime lastLoginAt;
    
    private String lastLoginIp;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @Data
    public static class OrgUnitDTO {
        private Long id;
        private String code;
        private String name;
        private String type;
    }
    
    @Data
    public static class RoleDTO {
        private Long id;
        private String code;
        private String name;
    }
}