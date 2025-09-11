package com.magic.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 跟进记录实体
 */
@Entity
@Table(name = "activities")
@Where(clause = "deleted = false")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Activity {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid")
    private UUID id;
    
    @Column(name = "customer_id", columnDefinition = "uuid", nullable = false)
    private UUID customerId; // 客户
    
    @Column(name = "contact_id", columnDefinition = "uuid")
    private UUID contactId; // 联系人
    
    @Column(name = "project_id", columnDefinition = "uuid")
    private UUID projectId; // 项目
    
    @Column(name = "opportunity_id", columnDefinition = "uuid")
    private UUID opportunityId; // 商机
    
    @Column(name = "activity_type", length = 50, nullable = false)
    private String activityType; // 类型: VISIT, CALL, MEETING, EMAIL, OTHER
    
    @Column(name = "subject", length = 200, nullable = false)
    private String subject; // 主题
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content; // 内容
    
    @Column(name = "start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime; // 开始时间
    
    @Column(name = "end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime; // 结束时间
    
    @Column(name = "location", length = 200)
    private String location; // 地点
    
    @Type(type = "jsonb")
    @Column(name = "participants", columnDefinition = "jsonb")
    private List<UUID> participants; // 参与人
    
    @Column(name = "next_action", length = 500)
    private String nextAction; // 下一步行动
    
    @Column(name = "next_follow_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextFollowTime; // 下次跟进时间
    
    @Column(name = "voice_note_id", columnDefinition = "uuid")
    private UUID voiceNoteId; // 语音记录ID
    
    @Column(name = "status", length = 20)
    private String status = "COMPLETED"; // 状态
    
    @Type(type = "jsonb")
    @Column(name = "attachments", columnDefinition = "jsonb")
    private List<Map<String, Object>> attachments; // 附件
    
    @Type(type = "jsonb")
    @Column(name = "custom_fields", columnDefinition = "jsonb")
    private Map<String, Object> customFields; // 自定义字段
    
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
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", insertable = false, updatable = false)
    private Contact contact;
    
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
        if (status == null) {
            status = "COMPLETED";
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
    
    public UUID getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }
    
    public UUID getContactId() {
        return contactId;
    }
    
    public void setContactId(UUID contactId) {
        this.contactId = contactId;
    }
    
    public UUID getProjectId() {
        return projectId;
    }
    
    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }
    
    public UUID getOpportunityId() {
        return opportunityId;
    }
    
    public void setOpportunityId(UUID opportunityId) {
        this.opportunityId = opportunityId;
    }
    
    public String getActivityType() {
        return activityType;
    }
    
    public void setActivityType(String activityType) {
        this.activityType = activityType;
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
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public List<UUID> getParticipants() {
        return participants;
    }
    
    public void setParticipants(List<UUID> participants) {
        this.participants = participants;
    }
    
    public String getNextAction() {
        return nextAction;
    }
    
    public void setNextAction(String nextAction) {
        this.nextAction = nextAction;
    }
    
    public LocalDateTime getNextFollowTime() {
        return nextFollowTime;
    }
    
    public void setNextFollowTime(LocalDateTime nextFollowTime) {
        this.nextFollowTime = nextFollowTime;
    }
    
    public UUID getVoiceNoteId() {
        return voiceNoteId;
    }
    
    public void setVoiceNoteId(UUID voiceNoteId) {
        this.voiceNoteId = voiceNoteId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<Map<String, Object>> getAttachments() {
        return attachments;
    }
    
    public void setAttachments(List<Map<String, Object>> attachments) {
        this.attachments = attachments;
    }
    
    public Map<String, Object> getCustomFields() {
        return customFields;
    }
    
    public void setCustomFields(Map<String, Object> customFields) {
        this.customFields = customFields;
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
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public Contact getContact() {
        return contact;
    }
    
    public void setContact(Contact contact) {
        this.contact = contact;
    }
}