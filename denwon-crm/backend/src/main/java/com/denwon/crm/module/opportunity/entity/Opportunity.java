package com.denwon.crm.module.opportunity.entity;

import com.denwon.crm.common.entity.BaseEntity;
import com.denwon.crm.module.customer.entity.Customer;
import com.denwon.crm.module.customer.entity.Contact;
import com.denwon.crm.module.system.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商机实体
 * 
 * @author Denwon Team
 * @since 1.0.0
 */
@Entity
@Table(name = "opportunities")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Opportunity extends BaseEntity {
    
    @Column(unique = true, nullable = false, length = 50)
    private String code;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;
    
    @Column(name = "stage_template_id")
    private Long stageTemplateId;
    
    @Column(name = "current_stage", length = 50)
    private String currentStage;
    
    @Column(name = "stage_entered_at")
    private LocalDateTime stageEnteredAt;
    
    @Column(name = "expected_amount_ex_tax", precision = 18, scale = 2)
    private BigDecimal expectedAmountExTax;
    
    @Column(name = "expected_amount_inc_tax", precision = 18, scale = 2)
    private BigDecimal expectedAmountIncTax;
    
    @Column
    private Integer probability = 0;
    
    @Column(name = "expected_sign_at")
    private LocalDate expectedSignAt;
    
    @Column(name = "actual_sign_at")
    private LocalDate actualSignAt;
    
    @Column(length = 200)
    private String competitor;
    
    @Column(name = "purchase_method", length = 50)
    private String purchaseMethod;
    
    @Column(name = "decision_chain", columnDefinition = "jsonb")
    private String decisionChain;
    
    @Column(name = "win_loss_reason", length = 200)
    private String winLossReason;
    
    @Column(columnDefinition = "jsonb")
    private String tags;
    
    @Column(columnDefinition = "jsonb")
    private String collaborators;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", insertable = false, updatable = false)
    private User owner;
}