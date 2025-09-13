package com.denwon.crm.module.contract.entity;

import com.denwon.crm.common.entity.BaseEntity;
import com.denwon.crm.module.customer.entity.Customer;
import com.denwon.crm.module.opportunity.entity.Opportunity;
import com.denwon.crm.module.system.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销售合同实体
 * 
 * @author Denwon Team
 * @since 1.0.0
 */
@Entity
@Table(name = "sales_contracts")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesContract extends BaseEntity {
    
    @Column(unique = true, nullable = false, length = 50)
    private String code;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(nullable = false, length = 20)
    private String type; // DIRECT, FRAMEWORK, AGENCY, SERVICE
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id")
    private Opportunity opportunity;
    
    @Column(length = 10)
    private String currency = "CNY";
    
    @Column(name = "exchange_rate", precision = 10, scale = 4)
    private BigDecimal exchangeRate = BigDecimal.ONE;
    
    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate;
    
    @Column(name = "amount_ex_tax", nullable = false, precision = 18, scale = 2)
    private BigDecimal amountExTax;
    
    @Column(name = "amount_inc_tax", nullable = false, precision = 18, scale = 2)
    private BigDecimal amountIncTax;
    
    @Column(name = "tax_amount", precision = 18, scale = 2)
    private BigDecimal taxAmount;
    
    @Column(name = "discount_amount", precision = 18, scale = 2)
    private BigDecimal discountAmount;
    
    @Column(name = "signer_entity", length = 200)
    private String signerEntity;
    
    @Column(name = "signer_name", length = 100)
    private String signerName;
    
    @Column(name = "signer_contact", length = 100)
    private String signerContact;
    
    @Column(name = "signed_date")
    private LocalDate signedDate;
    
    @Column(name = "effective_date")
    private LocalDate effectiveDate;
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    @Column(name = "delivery_address", columnDefinition = "TEXT")
    private String deliveryAddress;
    
    @Column(name = "delivery_contact", length = 100)
    private String deliveryContact;
    
    @Column(name = "delivery_phone", length = 20)
    private String deliveryPhone;
    
    @Column(name = "payment_term_id")
    private Long paymentTermId;
    
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    
    @Column(name = "contract_terms", columnDefinition = "TEXT")
    private String contractTerms;
    
    @Column(name = "special_terms", columnDefinition = "TEXT")
    private String specialTerms;
    
    @Column(name = "approval_status", length = 20)
    private String approvalStatus = "DRAFT"; // DRAFT, PENDING, APPROVED, REJECTED, SIGNED
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "contract_status", length = 20)
    private String contractStatus = "DRAFT"; // DRAFT, ACTIVE, COMPLETED, TERMINATED
    
    @Column(columnDefinition = "jsonb")
    private String collaborators;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", insertable = false, updatable = false)
    private User owner;
}