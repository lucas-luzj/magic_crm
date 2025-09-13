package com.denwon.crm.module.lead.entity;

import com.denwon.crm.common.entity.BaseEntity;
import com.denwon.crm.module.customer.entity.Customer;
import com.denwon.crm.module.system.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 线索实体
 * 
 * @author Denwon Team
 * @since 1.0.0
 */
@Entity
@Table(name = "leads")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead extends BaseEntity {
    
    @Column(unique = true, nullable = false, length = 50)
    private String code;
    
    @Column(nullable = false, length = 50)
    private String source; // WEBSITE, PHONE, REFERRAL, CAMPAIGN
    
    @Column(length = 50)
    private String channel;
    
    @Column(name = "customer_name", length = 200)
    private String customerName;
    
    @Column(name = "contact_name", length = 100)
    private String contactName;
    
    @Column(length = 20)
    private String mobile;
    
    @Column(length = 20)
    private String phone;
    
    @Column(length = 100)
    private String email;
    
    @Column(length = 50)
    private String wechat;
    
    @Column(length = 100)
    private String region;
    
    @Column(length = 50)
    private String industry;
    
    @Column(name = "product_intent", length = 200)
    private String productIntent;
    
    @Column(name = "budget_range", length = 50)
    private String budgetRange;
    
    @Column(name = "purchase_timeline", length = 50)
    private String purchaseTimeline;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @Column
    private Integer score = 0;
    
    @Column(length = 20)
    private String status = "NEW"; // NEW, ASSIGNED, FOLLOWING, CONVERTED, INVALID
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;
    
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
    
    @Column(name = "converted_at")
    private LocalDateTime convertedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "converted_to_customer_id")
    private Customer convertedToCustomer;
    
    @Column(name = "invalid_reason", length = 200)
    private String invalidReason;
    
    @Column(columnDefinition = "jsonb")
    private String tags;
}