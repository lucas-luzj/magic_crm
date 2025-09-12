package com.denwon.crm.module.customer.entity;

import com.denwon.crm.common.entity.BaseEntity;
import com.denwon.crm.module.system.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 客户实体
 * 
 * @author Denwon Team
 * @since 1.0.0
 */
@Entity
@Table(name = "customers")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends BaseEntity {
    
    @Column(unique = true, nullable = false, length = 50)
    private String code;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(name = "short_name", length = 100)
    private String shortName;
    
    @Column(length = 50)
    private String uscc; // 统一社会信用代码
    
    @Column(length = 50)
    private String industry;
    
    @Column(length = 100)
    private String region;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    @Column(length = 200)
    private String website;
    
    @Column(length = 50)
    private String scale;
    
    @Column(length = 20)
    private String level; // VIP, KEY, NORMAL
    
    @Column(length = 50)
    private String source;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", insertable = false, updatable = false)
    private User owner;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_customer_id")
    private Customer parentCustomer;
    
    @Column(name = "is_key")
    private Boolean isKey = false;
    
    @Column(name = "is_blacklist")
    private Boolean isBlacklist = false;
    
    @Column(name = "pool_status", length = 20)
    private String poolStatus = "PRIVATE"; // PUBLIC, PRIVATE
    
    @Column(name = "pool_entered_at")
    private LocalDateTime poolEnteredAt;
    
    @Column(columnDefinition = "jsonb")
    private String collaborators;
    
    @Column(columnDefinition = "jsonb")
    private String tags;
    
    @Transient
    private Integer contactCount;
    
    @Transient
    private Integer opportunityCount;
    
    @Transient
    private Integer contractCount;
}