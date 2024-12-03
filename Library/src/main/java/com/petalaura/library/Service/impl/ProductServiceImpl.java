package com.petalaura.library.Service.impl;

import com.petalaura.library.Repository.ImageRepository;
import com.petalaura.library.Repository.ProductRepository;
import com.petalaura.library.Service.ProductService;
import com.petalaura.library.dto.ProductDto;
import com.petalaura.library.model.Image;
import com.petalaura.library.model.Product;
import com.petalaura.library.utils.ImageUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository, ImageUpload imageUpload) {
        this.productRepository = productRepository;
        this.imageUpload = imageUpload;
    }
    @Autowired
    private ImageRepository imageRepository;

    private final ImageUpload imageUpload;

    @Override
    public List<ProductDto> findAll() {
        List<Product> products=productRepository.findAll();
        List<ProductDto> productDtoList=transferData(products);
        return productDtoList;
    }

    @Override
    public Product save(List<MultipartFile> imageProducts, ProductDto productDto) {
        Product product = new Product();
        try {
            product.setId(productDto.getId());
            product.setName(productDto.getName());
            product.setDescription(productDto.getDescription());
            product.setLong_description(productDto.getLong_description());
            product.setCurrentQuantity(productDto.getCurrentQuantity());
            product.setCostPrice(productDto.getCostPrice());
            product.setSalePrice(productDto.getSalePrice());
            product.setCategory(productDto.getCategory());
            product.set_activated(true);

            // Save the product first to get its ID
            Product savedProduct = productRepository.save(product);

            if (imageProducts != null && !imageProducts.isEmpty()) {
                List<Image> imagesList = new ArrayList<>();
                for (MultipartFile imageProduct : imageProducts) {
                    String imageName = imageUpload.storeFile(imageProduct);

                    // Check if an image with the same name exists for the product
                    if (!isImageExistsForProduct(savedProduct.getId(), imageName)) {
                        Image image = new Image();
                        image.setName(imageName);
                        image.setProduct(savedProduct);
                        imageRepository.save(image);
                        imagesList.add(image);
                    }
                }
                savedProduct.setImages(imagesList);

            }

            return productRepository.save(savedProduct);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Page<ProductDto> searchProducts(int pageNo, String keyword) {
        Pageable pageable = PageRequest.of(pageNo, 5);
        List<ProductDto> productDtoList = transferData(productRepository.searchProductsList(keyword));
       Page<ProductDto> dtoPage = toPage(productDtoList, pageable);
        return dtoPage;
    }

    @Override
    public Page<Product> pageProducts(int pageNo) {
        Pageable pageable=PageRequest.of(pageNo,8);
        List<ProductDto> products= transferData(productRepository.findAll());
        Page<Product> productPage=toPage(products,pageable);
        return productPage;
    }

    @Override
    public void enableById(Long id) {
        Product product = productRepository.getById(id);
        product.set_activated(true);
        product.set_deleted(false);
        productRepository.save(product);
    }

    @Override
    public void deleteById(Long id) {
        Product product = productRepository.getById(id);
        product.set_deleted(true);
        product.set_activated(false);
        productRepository.save(product);
    }

    @Override
    public Product update(List<MultipartFile> imageProducts, ProductDto productDto) {
        try {
            long id= productDto.getId();
            Product productUpdate = productRepository.getById(productDto.getId());
            if (imageProducts != null && !imageProducts.isEmpty() && imageProducts.size()!=1) {
                List<Image> imagesList = new ArrayList<>();
                List<Image> image = imageRepository.findImageBy(id);
                int minSize = Math.min(imageProducts.size(), image.size());
                //int i=0;
                for (int i = 0; i < minSize; i++) {
                    MultipartFile imageProduct = imageProducts.get(i);
                    String imageName = imageUpload.storeFile(imageProduct);
                    image.get(i).setName(imageName);
                    image.get(i).setProduct(productUpdate);
                    imageRepository.save(image.get(i));
                    imagesList.add(image.get(i));
                    i++;
                }
                productUpdate.setImages(imagesList);
            }
            productUpdate.setCategory(productDto.getCategory());
            productUpdate.setName(productDto.getName());
            productUpdate.setDescription(productDto.getDescription());
            productUpdate.setLong_description(productDto.getLong_description());
            productUpdate.setCostPrice(productDto.getCostPrice());
            productUpdate.setSalePrice(productDto.getSalePrice());
            productUpdate.setCurrentQuantity(productDto.getCurrentQuantity());
            return productRepository.save(productUpdate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ProductDto getById(Long id) {
        ProductDto productDto = new ProductDto();
        Product product = productRepository.getById(id);
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setLong_description(product.getLong_description());
        productDto.setCostPrice(product.getCostPrice());
        productDto.setSalePrice(product.getSalePrice());
        productDto.setCurrentQuantity(product.getCurrentQuantity());
        productDto.setCategory(product.getCategory());
        productDto.setImage(product.getImages());
        //productDto.setDeleted(product.is_deleted());
        productDto.setActivated(productDto.isActivated());
        return productDto;
    }

    @Override
    public List<Product> findAllByCategory(long id) {
        return productRepository.findAllByCategoryId(id);
    }

    @Override
    public Product getProductById(long id) {
        return productRepository.getById(id);
    }

//    @Override
//    public List<ProductDto> filterHighProducts() {
//        return transferData(productRepository.filterHighProducts());
//    }
//
//
//    @Override
//    public List<ProductDto> filterLowProducts(){
//        return transferData(productRepository.filterLowerProducts());
//    }

    @Override
    public List<ProductDto> listViewProducts() {
        return transferData(productRepository.listViewProduct());
    }

    @Override
    public Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public List<Product> findAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public long countTotalProducts() {
         return productRepository.count();
    }



    @Override
    public List<Object[]> getProductStats() {
        return productRepository.getProductStatsForConfirmedOrders();
    }

    @Override
    public List<Object[]> getProductsStatsBetweenDates(Date startDate, Date endDate) {
        return productRepository.getProductsStatsForConfirmedOrdersBetweenDates(startDate,endDate);
    }

    @Override
    public List<Product>  filterHighPrice() {
        return  productRepository.filterHighPrice();
    }

    @Override
    public List<Product> filterLowPrice() {
        return productRepository.filterLowPrice();
    }

    @Override
    public Page<ProductDto> filterHighProducts(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Product> products = productRepository.filterHighProducts(pageable);
       return products.map(this::mapToDto);

    }

    @Override
    public Page<ProductDto> filterLowProducts(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Product> products = productRepository.filterLowerProducts(pageable);
        return products.map(this::mapToDto);
    }

    @Override
    public List<Product> filterByPopularity() {
        return productRepository.findTopSellingProducts();
    }

    @Override
    public Page<ProductDto> filterByNameAscending(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Product> products = productRepository.filterByNameAscending(pageable);
        return products.map(this::mapToDto);
    }

    @Override
    public Page<ProductDto> filterByNameDescending(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Product> products = productRepository.filterByNameDescending(pageable);
        return products.map(this::mapToDto);
    }

    @Override
    public List<Product> findAllByCategoryName(String categoryName) {
        return productRepository.findAllByCategoryName(categoryName);
    }

    @Override
    public List<Product> findByNameStartingWithIgnoreCase(String key) {
        return productRepository.findByNameStartingWithIgnoreCase(key);
    }

    @Override
    public List<Object[]> findTopSellingProductsDashBoard(Pageable pageable) {
        return productRepository.findTopSellingProducts(pageable);
    }

    private ProductDto mapToDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setLong_description(product.getLong_description());
        productDto.setCostPrice(product.getCostPrice());
        productDto.setSalePrice(product.getSalePrice());
        productDto.setCurrentQuantity(product.getCurrentQuantity());
        productDto.setCategory(product.getCategory());
        productDto.setImage(product.getImages());
        productDto.setActivated(product.is_activated());
        productDto.setDeleted(product.is_deleted());
        return productDto;
    }

    private List<ProductDto> transferData(List<Product> products) {
        List<ProductDto> productDtoList = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = new ProductDto();
            productDto.setId(product.getId());
            productDto.setName(product.getName());
            productDto.setCurrentQuantity(product.getCurrentQuantity());
            productDto.setCostPrice(product.getCostPrice());
            productDto.setSalePrice(product.getSalePrice());
            productDto.setDescription(product.getDescription());
            productDto.setLong_description(product.getLong_description());
            productDto.setImage(product.getImages());
            productDto.setCategory(product.getCategory());
            productDto.setActivated(product.is_activated());
            productDto.setDeleted(product.is_deleted());
            productDtoList.add(productDto);
        }
        return productDtoList;
    }
    private boolean isImageExistsForProduct(Long productId, String imageName) {
        // Check if an image with the same name exists for the given product ID
        return imageRepository.existsByNameAndProductId(imageName, productId);
    }
    private Page toPage(List<ProductDto> list, Pageable pageable) {
        if (pageable.getOffset() >= list.size()) {
            return Page.empty();
        }
        int startIndex = (int) pageable.getOffset();
        int endIndex = ((pageable.getOffset() + pageable.getPageSize()) > list.size())
                ? list.size()
                : (int) (pageable.getOffset() + pageable.getPageSize());
        List subList = list.subList(startIndex, endIndex);
        return new PageImpl(subList, pageable, list.size());
    }
}
