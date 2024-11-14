package com.example.java.domain;


import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Data;


import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@ToString
@Getter
@Setter
@Table(name = "tenant")
public class Tenant {
    /**
     * The id of the tenant.
     */
    @Id
    @Column(name = "tenant_id")
    private String tenantId;


    /**
     * Relation between the tenant and the user table.
     */
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "tenant_id")
    private Set<Battery> tenantBatteries = new HashSet<>();

    /**
     * Relation between the tenant and user table.
     */
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<UserTenants> usersAssigned = new HashSet<>();

    /**
     * @param tenantIdInstance string instance of the tenant id that needs to be set.
     */
    public Tenant(final String tenantIdInstance) {
        this.tenantId = tenantIdInstance;
    }
}
