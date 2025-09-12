package com.denwon.crm.module.system.entity;

import com.denwon.crm.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 组织单位实体
 * 
 * @author Denwon Team
 * @since 1.0.0
 */
@Entity
@Table(name = "org_units")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgUnit extends BaseEntity {
    
    @Column(unique = true, nullable = false, length = 50)
    private String code;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private OrgUnit parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<OrgUnit> children = new ArrayList<>();
    
    @Column(nullable = false)
    private Integer level = 1;
    
    @Column(length = 500)
    private String path;
    
    @Column(nullable = false, length = 20)
    private String type; // COMPANY, DEPARTMENT, TEAM
    
    @OneToMany(mappedBy = "orgUnit")
    private List<User> users = new ArrayList<>();
}