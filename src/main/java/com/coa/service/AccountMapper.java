package com.coa.service;

import com.coa.api.AccountResponse;
import com.coa.domain.Account;

public class AccountMapper {
    public AccountResponse toResponse(Account a) {
        String parentCode = a.getParent() == null ? null : a.getParent().getCode();
        return new AccountResponse(a.getId(), a.getCode(), a.getName(),
                a.getType(), parentCode, a.isActive());
    }
}
