package com.coa.domain;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByCode(String code);

    Optional<Account> findByCode(String code);

    /**
     * select a.id, a.code, a.name, a.type from account a where a.active order by
     * ... offset ? fetch first ? rows only
     * + select count(a.id) from account a where a.active
     */
    Page<AccountSummary> findByActiveTrue(Pageable pageable);
}
