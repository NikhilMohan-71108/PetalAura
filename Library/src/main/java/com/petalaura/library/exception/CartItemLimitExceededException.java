package com.petalaura.library.exception;

public class CartItemLimitExceededException  extends RuntimeException {
    public CartItemLimitExceededException(String message) {
        super(message);
    }
}
