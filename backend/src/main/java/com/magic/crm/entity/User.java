package com.magic.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.magic.crm.config.DateTimeDeserializer;
import com.magic.crm.config.LocalDateTimeDeserializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Size(min = 6)
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;

    /**
     * 头像URL
     */
    @Column(name = "avatar", length = 500)
    private String avatar;

    /**
     * 照片URL
     */
    @Column(name = "photo", length = 500)
    private String photo;

    /**
     * 生日
     */
    @Column(name = "birthday")
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private LocalDate birthday;

    /**
     * 最后登录IP
     */
    @Column(name = "last_login_ip", length = 45)
    private String lastLoginIp;

    /**
     * 最后登录设备的UserAgent
     */
    @Column(name = "last_login_user_agent", length = 1000)
    private String lastLoginUserAgent;

    /**
     * 职位
     */
    @Column(name = "position", length = 100)
    private String position;

    /**
     * 微信UnionID
     */
    @Column(name = "wechat_union_id", length = 100)
    private String wechatUnionId;

    /**
     * 微信OpenID
     */
    @Column(name = "wechat_open_id", length = 100)
    private String wechatOpenId;

    /**
     * 钉钉ID
     */
    @Column(name = "dingtalk_id", length = 100)
    private String dingtalkId;

    /**
     * QQ号码
     */
    @Column(name = "qq_number", length = 20)
    private String qqNumber;

    /**
     * 入职日期
     */
    @Column(name = "hire_date")
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private LocalDate hireDate;

    /**
     * 离职日期
     */
    @Column(name = "resignation_date")
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private LocalDate resignationDate;

    /**
     * 性别 (0:未知, 1:男, 2:女)
     */
    @Column(name = "gender")
    private Integer gender;

    /**
     * 籍贯
     */
    @Column(name = "hometown", length = 200)
    private String hometown;

    /**
     * 毕业院校
     */
    @Column(name = "graduate_school", length = 200)
    private String graduateSchool;

    /**
     * 地址
     */
    @Column(name = "address", length = 500)
    private String address;

    /**
     * 紧急联系人
     */
    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;

    /**
     * 紧急联系人电话
     */
    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    /**
     * 备注
     */
    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "last_login_at")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastLoginAt;

    /**
     * 部门ID
     */
    @Column(name = "department_id")
    private Long departmentId;

    /**
     * 所属部门（非数据库字段）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Department department;

    /**
     * 部门名称（非数据库字段）
     */
    @Transient
    private String departmentName;

    /**
     * 用户姓名拼音首字母
     */
    @Column(name = "name_pinyin")
    private String namePinyin;

    public enum Role {
        ADMIN, MANAGER, USER
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    @PostLoad
    public void postLoad() {
        if (department != null) {
            departmentName = department.getName();
        }
    }
}