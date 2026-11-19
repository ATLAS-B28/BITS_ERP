package com.example.bitserp.modules.finance.service;

import com.example.bitserp.modules.finance.entity.ExpenseCategory;
import com.example.bitserp.modules.finance.repository.ExpenseCategoryRepository;
import com.example.bitserp.shared.exception.ResourceNotException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseCategoryService {

    private final ExpenseCategoryRepository expenseCategoryRepository;

    @Transactional(readOnly = true)
    public List<ExpenseCategory> getCategories(String module) {
        if(module == null || module.isBlank()) {
            return expenseCategoryRepository.findByActiveTrue();
        }
        return expenseCategoryRepository.findByModuleAndActiveTrue(module.toUpperCase());
    }

    @Transactional
    public ExpenseCategory create(String name, String module) {
        if(expenseCategoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("ExpenseCategory already exists:" + name);
        }
        ExpenseCategory expenseCategory = new ExpenseCategory();
        expenseCategory.setName(name.trim());
        expenseCategory.setModule(module.toUpperCase());

        return expenseCategoryRepository.save(expenseCategory);
    }

    @Transactional
    public ExpenseCategory deactivate(Integer id) {
        ExpenseCategory expenseCategory = expenseCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotException("ExpenseCategory Not Found: " + id));
        expenseCategory.setActive(false);

        return expenseCategoryRepository.save(expenseCategory);
    }
}
