package com.example.bitserp.modules.finance.controller;

import com.example.bitserp.modules.finance.entity.ExpenseCategory;
import com.example.bitserp.modules.finance.service.ExpenseCategoryService;
import com.example.bitserp.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance/categories")
@RequiredArgsConstructor
public class ExpenseCategoryController {

    private final ExpenseCategoryService expenseCategoryService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER','FIN_EMPLOYEE','PROC_MANAGER','PROC_EMPLOYEE','ACCOUNTS')")
    public ResponseEntity<ApiResponse<List<ExpenseCategory>>> getCategories(
            @RequestParam(required = false) String module
    ) {
        return ResponseEntity.ok(ApiResponse.ok(expenseCategoryService.getCategories(module)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER')")
    public ResponseEntity<ApiResponse<ExpenseCategory>> createCategory(
            @RequestBody Map<String, String> body
            ) {
        return ResponseEntity.ok(ApiResponse.ok(expenseCategoryService.create(body.get("name"), body.get("module"))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER')")
    public ResponseEntity<ApiResponse<ExpenseCategory>> deactivateCategory(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(expenseCategoryService.deactivate(id)));
    }
}
