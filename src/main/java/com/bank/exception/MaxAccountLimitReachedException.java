package com.bank.exception;

public class MaxAccountLimitReachedException extends RuntimeException {
    public MaxAccountLimitReachedException(String message) {
        super(message);
    }
}