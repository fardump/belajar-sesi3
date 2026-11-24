package com.coa.api;

import io.swagger.v3.oas.annotations.media.Schema;

// Record: DTO immutable, equals/hashCode/toString otomatis. Entity tidak pernah keluar ke API.
@Schema(description = "Detail akun COA")
public record AccountResponse() {
    @Schema(description = "ID Akun", example = "6") Long id,
    @Schema(description = "Kode Akun", example = "1301") String code,
    @Schema(description = "Nama Akun", example = "Persediaan") String name,
    @Schema(description = "Tipe Akun", example = "ASSET") AccountType type,
    @Schema(description = "Kode Akun Induk", example = "1000", nullable = true) String parentCode,
    @Schema(description = "Status aktif", example = true) boolean active,
}
