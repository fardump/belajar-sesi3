package com.coa.service;

import org.springframework.data.domain.Pageable;

import com.coa.api.AccountResponse;
import com.coa.api.CreateAccountRequest;
import com.coa.api.UpdateAccountRequest;
import com.coa.domain.Account;
import com.coa.domain.AccountRepository;
import com.coa.domain.AccountSummary;
import com.coa.domain.AccountType;
import org.springframework.data.domain.Page;

import jakarta.transaction.Transactional;

public class AccountService {
    private final AccountRepository repo;
    private final AccountMapper mapper;

    @Transactional
    public AccountResponse create(CreateAccountRequest req) {
        if (repo.existsByCode(req.code())) { // aturan domain
            throw new DuplicateAccountException(req.code()); // -> 409
        }
        Account account = Account.of(req.code(), req.name(), req.type());
        account.assignParent(resolveParent(req.parentCode(), req.type(), null));
        // Unique constraint di DB tetap ada sebagai pengaman race condition
        return mapper.toResponse(repo.save(account));
    }

    public Page<AccountSummary> listActive(Pageable pageable) {
        return repo.findByActiveTrue(pageable);
    }

    public AccountResponse findById(Long id) {
        return mapper.toResponse(getAccount(id));
    }

    @Transactional
    public AccountResponse update(Long id, UpdateAccountRequest req) {
        Account account = getAccount(id);
        account.update(req.name(), req.type());
        account.assignParent(resolveParent(req.parentCode(), req.type(), account));
        return mapper.toResponse(account); // dirty checking: UPDATE saat commit
    }

    @Transactional
    public void deactivate(Long id) {
        getAccount(id).deactivate();
    }

    private Account getAccount(Long id) {
        return repo.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    private Account resolveParent(String parentCode, AccountType type, Account self) {
        if (parentCode == null || parentCode.isBlank()) {
            return null;
        }
        Account parent = repo.findByCode(parentCode).orElseThrow(
                () -> new InvalidParentAccountException("Akun induk %s tidak ditemukan".formatted(parentCode)));
        if (!parent.isActive()) {
            throw new InvalidParentAccountException("Akun induk %s sudah nonaktif".formatted(parentCode));
        }
        if (parent.getType() != type) {
            throw new InvalidParentAccountException(
                    "Tipe akun induk (%s) harus sama dengan tipe akun (%s)".formatted(parent.getType(), type));
        }
        if (self != null) {
            for (Account p = parent; p != null; p = p.getParent()) { // cegah siklus hierarki
                if (p.getId().equals(self.getId())) {
                    throw new InvalidParentAccountException(
                            "Akun %s tidak boleh menjadi induk dari dirinya sendiri atau turunannya"
                                    .formatted(parentCode));
                }
            }
        }
        return parent;
    }
}
