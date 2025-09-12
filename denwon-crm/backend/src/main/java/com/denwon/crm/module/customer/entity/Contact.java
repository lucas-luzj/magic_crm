package com.denwon.crm.module.customer.entity;

import com.denwon.crm.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 联系人实体
 * 
 * @author Denwon Team
 * @since 1.0.0
 */
@Entity
@Table(name = "contacts")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 100)
    private String title;
    
    @Column(length = 100)
    private String department;
    
    @Column(length = 20)
    private String mobile;
    
    @Column(length = 20)
    private String phone;
    
    @Column(length = 100)
    private String email;
    
    @Column(length = 50)
    private String wechat;
    
    @Column(length = 20)
    private String qq;
    
    @Column(name = "role_tag", length = 50)
    private String roleTag; // DECISION_MAKER, INFLUENCER, USER
    
    @Column
    private LocalDate birthday;
    
    @Column(columnDefinition = "TEXT")
    private String preference;
    
    @Column
    private Boolean consent = true;
    
    @Column(name = "is_primary")
    private Boolean isPrimary = false;
}