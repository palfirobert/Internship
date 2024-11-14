package com.example.java.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "users_tenants")
public class UserTenants {
    /**
     * Id and primary key of the table.
     */
    @EmbeddedId
    private UserTenantsPrimaryKey primaryKey;

    /**
     * Id of the user.
     */
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "battery_user_id", nullable = false, insertable = false, updatable = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private BatteryUser userBattery;

    /**
     * Id of the tenant.
     */
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id", nullable = false, insertable = false, updatable = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Tenant tenant;

}
