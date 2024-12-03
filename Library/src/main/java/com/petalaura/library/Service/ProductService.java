package com.petalaura.library.Service;

import com.petalaura.library.dto.ProductDto;
import com.petalaura.library.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public interface ProductService {
    List<ProductDto> findAll();

    Product save(List<MultipartFile> imageProducts, ProductDto productDto);

    Page<ProductDto> searchProducts(int pageNo, String keyword);

    Page<Product> pageProducts(int pageNo);

    void enableById(Long id);

    void deleteById(Long id);

    Product update(List<MultipartFile> imageProduct, ProductDto productDto);

    ProductDto getById(Long id);

    List<Product> findAllByCategory(long id);

    Product getProductById(long id);

    List<ProductDto> listViewProducts();

    Product findById(Long productId);

    List<Product> findAllProduct();

    long countTotalProducts();

    List<Object[]> getProductStats();

    List<Object[]> getProductsStatsBetweenDates(Date startDate, Date endDate);

    List<Product> filterHighPrice();

    List<Product> filterLowPrice();

    Page<ProductDto> filterHighProducts(int pageNo, int pageSize);

    Page<ProductDto> filterLowProducts(int pageNo, int pageSize);

    List<Product> filterByPopularity();

    Page<ProductDto> filterByNameAscending(int pageNo, int pageSize);

    Page<ProductDto> filterByNameDescending(int pageNo, int pageSize);

    List<Product> findAllByCategoryName(String categoryName);

    List<Product> findByNameStartingWithIgnoreCase(String key);

    List<Object[]> findTopSellingProductsDashBoard(Pageable pageable);
}