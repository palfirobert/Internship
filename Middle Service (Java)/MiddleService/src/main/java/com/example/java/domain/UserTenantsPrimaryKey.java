package com.example.java.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;


@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UserTenantsPrimaryKey implements Serializable {
    /**
     * Id of the user.
     */
    @Column(name = "battery_user_id")
    private Integer batteryUserId;

    /**
     * Id of the tenant.
     */
    @Column(name = "tenant_id")
    private String tenantId;

}
