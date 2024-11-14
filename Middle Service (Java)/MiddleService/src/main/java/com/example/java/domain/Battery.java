package com.example.java.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;

@Entity
@Data
@Table(name = "battery")
@RequiredArgsConstructor
@AllArgsConstructor
public class Battery {

    /**
     * Id field for battery table from DB.
     */

    @Id
    @Column(name = "battery_id")
    private String batteryId;

    /**
     * Field for the battery type.
     */
    @Column(name = "battery_type")
    private String batteryType;

    /**
     * Field for the tenant that uses the battery.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;
}
