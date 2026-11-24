package com.coa.service;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long id) {
        super("Akun %d tidak ditemukan".formatted(id));
    }
}
