package com.coa.service;

// Akun induk tidak valid (tidak ada, beda tipe, nonaktif, atau membentuk siklus) -> 400
public class InvalidParentAccountException extends RuntimeException {
    public InvalidParentAccountException(String message) {
        super(message);
    }
}
