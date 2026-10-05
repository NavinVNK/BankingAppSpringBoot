package com.bank.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;


@Embeddable
public record Customer(String name, @Embedded Address address) {}