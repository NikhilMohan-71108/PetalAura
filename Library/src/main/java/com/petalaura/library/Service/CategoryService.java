package com.petalaura.library.Service;

import com.petalaura.library.dto.CategoryDto;
import com.petalaura.library.model.Category;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface  CategoryService {
    List<Category> findAll();
    Category save(Category category);
    Category update(Category category);
    void deleteById(Long id);
    void enableById(Long id);
    Optional<Category> findById(Long id);
    List<Category> findAllByActivatedTrue();
    List<CategoryDto> getCategoriesAndSize();
    long countTotalProducts();
    List<Object[]> findTopSellingCategories(Pageable pageable);

    long countTotalCategories();
}
