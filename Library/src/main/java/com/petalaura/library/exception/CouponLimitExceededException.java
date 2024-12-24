package com.petalaura.library.exception;

public class CouponLimitExceededException extends RuntimeException {
    public CouponLimitExceededException(String message) {
        super(message);
    }
}
