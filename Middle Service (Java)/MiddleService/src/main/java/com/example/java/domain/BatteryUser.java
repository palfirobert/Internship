package com.example.java.domain;

/*
 * BatteryUserClass
 * attributes :
 * battery_user_id : integer
 * useranme : string
 * password : string
 *
 * represents the batteryuser from the database and the user that will login from the site
 *
 * */

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import org.checkerframework.common.aliasing.qual.Unique;


import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "battery_users")
public class BatteryUser {
    /**
     * Id for user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "battery_user_id")
    private Integer id;
    /**
     * Email/username of the user.
     */
    @Unique
    private String email;
    /**
     * Password of the user.
     */
    private String password;
    /**
     * Family name of the user.
     */
    private String familyName;
    /**
     * Given name of the user.
     */
    private String givenName;
    /**
     * Account status.
     */
    private boolean verified;

    /**
     * Constructor.
     * @param emailInstance Email.
     * @param passwordInstance Password.
     * @param familyNameInstance Family name.
     * @param givenNameInstance Given name.
     */
    public BatteryUser(@Unique final String emailInstance,
                       final String passwordInstance,
                       final String familyNameInstance,
                       final String givenNameInstance) {
        this.email = emailInstance;
        this.password = passwordInstance;
        this.familyName = familyNameInstance;
        this.givenName = givenNameInstance;
    }

}
