package com.bank.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

@Embeddable
public record Address(String doorNumber, String streetName, String city, String pinCode) {}
