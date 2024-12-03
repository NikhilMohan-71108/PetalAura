package com.petalaura.library.Service.impl;

import com.petalaura.library.Repository.CategoryRepository;
import com.petalaura.library.Service.CategoryService;
import com.petalaura.library.dto.CategoryDto;
import com.petalaura.library.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

@Autowired
private final CategoryRepository categoryRepository;
    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category save(Category category) {
            String categoryName = category.getName();
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new DataIntegrityViolationException("Please add a valid category name");
        }
            Category existingCategory = categoryRepository.findByNameIgnoreCase(categoryName);
            if (existingCategory != null) {
                throw new DataIntegrityViolationException("Category already exists");
            }

        return categoryRepository.save(category);

    }

    @Override
    public Category update(Category category) {
        Category categoryUpdate = categoryRepository.findById(category.getId()).get();
        categoryUpdate.setName(category.getName());
        categoryUpdate.setActivated(category.isActivated());
        categoryUpdate.setDeleted(category.isDeleted());
        return categoryRepository.save(categoryUpdate);
    }

    @Override
    public void deleteById(Long id) {
        Category category = categoryRepository.getById(id);
        category.setActivated(false);
        category.setDeleted(true);
        categoryRepository.save(category);
    }

    @Override
    public void enableById(Long id) {
        Category category = categoryRepository.getById(id);
        category.setActivated(true);
        category.setDeleted(false);
        categoryRepository.save(category);
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List<Category> findAllByActivatedTrue() {
        return categoryRepository.findAllByActivatedTrue();
    }

    @Override
    public List<CategoryDto> getCategoriesAndSize() {
        List<CategoryDto> category =categoryRepository.getCategoriesAndSize();
        return category;
    }

    @Override
    public long countTotalProducts() {
        return categoryRepository.count();
    }

    @Override
    public List<Object[]> findTopSellingCategories(Pageable pageable) {
        return categoryRepository.findTopSellingCategories(pageable);
    }

    @Override
    public long countTotalCategories() {
        return categoryRepository.count();
    }


}
