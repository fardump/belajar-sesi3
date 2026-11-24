package com.coa.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Data untuk mengubah akun COA")
public record UpdateAccountRequest(
        @Schema(description = "Nama akun", example = "Persediaan barang dagang", maxLength = 150) @NotBlank @Size(max = 150) String name,

        @Schema(description = "Tipe akun", example = "ASSET") @NotBlank AccountType type,

        @Schema(description = "Kode akun induk (opsional)", example = "1000", requiredMode = Schema.RequiredMode.NOT_REQUIRED) @Pattern(regexp = "\\d{4}", message = "Harus 4 digit angka") String parentCode) {
}
