package com.coa.api;

import java.net.URI;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.boot.data.autoconfigure.web.DataWebProperties.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Chart of accounts", description = "Kelola akun COA")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class AccountController {
    private static final String PROBLEM_JSON = "application/problem+json";

    private static final String EXAMPLE_400 = """
            {"title": "Validasi gagal", "status": 400, "errors":["code: must not be blank"], "requestId": "a1b2c3"}""";
    private static final String EXAMPLE_404 = """
            {"title": "Not found", "status": 404, "errors":["code: Akun 99 tidak ditemukan"], "requestId": "d4e5f6"}""";
    private static final String EXAMPLE_409 = """
            {"title": "Conflict", "status": 409, "errors":["code: Kode akun 1101 sudah ditemukan"], "requestId": "g7h8i9"}""";

    private final AccountService service;

    @Operation(summary = "Buat akun baru", description = "Kode akun 4 digit dan harus naik. Akun induk (opsional) harus aktif dan bertipe sama.")
    @ApiResponse(responseCode = "201", description = "Akun dibuat; header Location berisi URL akun baru")
    @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class), examples = @ExampleObject(EXAMPLE_409)))

    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody CreateAccountRequest req) {
        AccountResponse created = service.create(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Daftar akun aktif (paginated)", description = "Query string: page (mulai 0), size (maks 100), sort (mis. code, asc atau name, desc).")
    @ApiResponse(responseCode = "200", description = "Halaman akun aktif")
    @ApiResponse(responseCode = "400", description = "Parameter tidak dikenal", content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))

    @GetMapping
    public PagedModel<AccountSummary> list(
            @ParameterObject @PageableDefault(size = 20, sort = "code") Pageable pageable) {
        return new PagedModel<>(service.listActive(pageable));
    }

    @Operation(summary = "Detail akun", description = "Mengembalikan akun aktif maupun nonaktif.")
    @ApiResponse(responseCode = "200", description = "Akun ditemukan")
    @ApiResponse(responseCode = "404", description = "Akun tidak ditemukan", content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class), examples = @ExampleObject(EXAMPLE_404)))
    @GetMapping("/{id}")
    public AccountResponse get(@RequestParam(description = "ID Akun", example = "1"), @PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Ubah akun", description = "Mengganti nama, tipe, dan akun induk. Kode akun tidak bisa diubah.")
    @ApiResponse(responseCode = "200", description = "Akun diperbarui")
    @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class), examples = @ExampleObject(EXAMPLE_400)))
    @ApiResponse(responseCode = "404", description = "Akun tidak ditemukan", content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class), examples = @ExampleObject(EXAMPLE_404)))
    @PutMapping("/{id}")
    public AccountResponse update(@Parameter(description = "ID Akun", example = "1"), @PathVariable Long id, @Valid @RequestBody UpdateAccountRequest req) {
        return service.update(id, req);
    }

    @Operation(summary = "Nonaktifkan akun", description = "Idempotent: akun nonaktif tidak muncul di daftar, tetapi tetap bisa dilihat detailnya.")
    @ApiResponse(responseCode = "204", description = "Akun dinonaktifkan")
    @ApiResponse(responseCode = "404", description = "Akun tidak ditemukan", content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class), examples = @ExampleObject(EXAMPLE_404)))
    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@Parameter(description = "ID Akun", example = "1") @PathVariable Long id) {
        service.deactivate(id);
    }
}
