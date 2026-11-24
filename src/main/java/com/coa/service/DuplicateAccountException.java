package com.coa.service;

public class DuplicateAccountException extends RuntimeException {
    public DuplicateAccountException(String code) {
        super("Kode akun %s sudah digunakan".formatted(code));
    }
}
