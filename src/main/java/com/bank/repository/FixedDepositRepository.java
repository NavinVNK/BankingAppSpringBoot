package com.bank.repository;

import com.bank.entity.FixedDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FixedDepositRepository extends JpaRepository<FixedDeposit, String> {

    // Derived Query Method to fetch all fixed deposits belonging to an account
    List<FixedDeposit> findByAccountAccountId(String accountId);
}