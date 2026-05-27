package com.e_commerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;
    private String street;
    private String buildingName;
    private String city;
    private String state;
    private String country;
    private String pinCode;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Address(String street, String buildingName, String country, String state, String city, String pinCode) {
        this.street = street;
        this.buildingName = buildingName;
        this.country = country;
        this.state = state;
        this.city = city;
        this.pinCode = pinCode;
    }
}
