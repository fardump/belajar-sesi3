package com.coa.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Data untuk membuat akun COA")
public record CreateAccountRequest(

        @Schema(description = "Kode akun 4 digit", example = "1301") @NotBlank @Pattern(regexp = "\\d{4}", message = "harus 4 digit angka") String name,

        @Schema(description = "Tipe akun", example = "ASSET") @NotNull AccountType type,

        @Schema(description = "Kode akun induk (opsional)", example = "1000", requiredMode = Schema.requiredMode.NOT_REQUIRED) @Pattern(regexp = "\\d{4}", message = "harus 4 digit angka") String ParentCode) {
}
