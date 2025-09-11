package com.magic.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 线索跟进记录实体
 */
@Entity
@Table(name = "lead_follow_ups")
@Where(clause = "deleted = false")
public class LeadFollowUp {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid")
    private UUID id;
    
    @Column(name = "lead_id", columnDefinition = "uuid", nullable = false)
    private UUID leadId; // 线索ID
    
    @Column(name = "follow_type", length = 50, nullable = false)
    private String followType; // 跟进类型: CALL, EMAIL, VISIT, MEETING, OTHER
    
    @Column(name = "subject", length = 200, nullable = false)
    private String subject; // 主题
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content; // 内容
    
    @Column(name = "follow_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime followTime; // 跟进时间
    
    @Column(name = "next_follow_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextFollowTime; // 下次跟进时间
    
    @Column(name = "result", length = 50)
    private String result; // 跟进结果: POSITIVE, NEGATIVE, NEUTRAL, NO_ANSWER
    
    @Column(name = "score_change")
    private Integer scoreChange; // 评分变化
    
    @Column(name = "status_change", length = 20)
    private String statusChange; // 状态变化
    
    @Column(name = "created_by", columnDefinition = "uuid")
    private UUID createdBy;
    
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_by", columnDefinition = "uuid")
    private UUID updatedBy;
    
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "deleted")
    @JsonIgnore
    private Boolean deleted = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", insertable = false, updatable = false)
    private Lead lead;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (deleted == null) {
            deleted = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getLeadId() {
        return leadId;
    }
    
    public void setLeadId(UUID leadId) {
        this.leadId = leadId;
    }
    
    public String getFollowType() {
        return followType;
    }
    
    public void setFollowType(String followType) {
        this.followType = followType;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public LocalDateTime getFollowTime() {
        return followTime;
    }
    
    public void setFollowTime(LocalDateTime followTime) {
        this.followTime = followTime;
    }
    
    public LocalDateTime getNextFollowTime() {
        return nextFollowTime;
    }
    
    public void setNextFollowTime(LocalDateTime nextFollowTime) {
        this.nextFollowTime = nextFollowTime;
    }
    
    public String getResult() {
        return result;
    }
    
    public void setResult(String result) {
        this.result = result;
    }
    
    public Integer getScoreChange() {
        return scoreChange;
    }
    
    public void setScoreChange(Integer scoreChange) {
        this.scoreChange = scoreChange;
    }
    
    public String getStatusChange() {
        return statusChange;
    }
    
    public void setStatusChange(String statusChange) {
        this.statusChange = statusChange;
    }
    
    public UUID getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public UUID getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(UUID updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Boolean getDeleted() {
        return deleted;
    }
    
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    
    public Lead getLead() {
        return lead;
    }
    
    public void setLead(Lead lead) {
        this.lead = lead;
    }
}
