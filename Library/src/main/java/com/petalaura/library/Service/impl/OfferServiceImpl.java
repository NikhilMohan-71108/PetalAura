package com.petalaura.library.Service.impl;

import com.petalaura.library.Repository.CategoryRepository;
import com.petalaura.library.Repository.OfferRepository;
import com.petalaura.library.Repository.ProductRepository;
import com.petalaura.library.Service.OfferService;
import com.petalaura.library.dto.OfferDto;
import com.petalaura.library.model.Category;
import com.petalaura.library.model.Offer;
import com.petalaura.library.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfferServiceImpl  implements OfferService {

    private OfferRepository offerRepository;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;

    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository,
                            ProductRepository productRepository,
                            CategoryRepository categoryRepository) {
        this.offerRepository = offerRepository;
        this.productRepository=productRepository;
        this.categoryRepository=categoryRepository;
    }

    @Override
    public void saveOffer(OfferDto offerDto) {
        Offer offer=new Offer();
        offer.setProduct(offerDto.getProduct());
        offer.setName(offerDto.getName());
        offer.setDescription(offerDto.getDescription());
        offer.setDiscount(offerDto.getDiscount());
        offer.setCategory(offerDto.getCategory());
        offer.setOfferType(offerDto.getOfferType());
        offer.setActivated(false);
        offerRepository.save(offer);

    }

    public OfferServiceImpl() {
        super();
    }

    @Override
    public Offer findById(Long id) {
        return offerRepository.getReferenceById(id);

    }

    @Override
    public void updateOffer(OfferDto offerDto) {
        Offer offer=offerRepository.getReferenceById(offerDto.getId());

        offer.setName(offerDto.getName());
        offer.setDescription(offerDto.getDescription());
       offer.setDiscount(offerDto.getDiscount());
        offerRepository.save(offer);

    }

    @Override
    public List<Offer> findAllOffer() {
        return offerRepository.findAll();
    }

    @Override
    public void enableOffer(Long id) {
        Offer offer=offerRepository.getReferenceById(id);
        offer.setActivated(true);
        offerRepository.save(offer);
        if(offer.getOfferType().equals("Product Offer")) {
            Product product = productRepository.getReferenceById(offer.getProduct().getId());
            double costprice = product.getCostPrice();
            double offerPercentage = offer.getDiscount();
            double offers = costprice * offerPercentage / 100;
            double newBalance=Math.round((costprice - offers) * 100.0) / 100.0;
            product.setSalePrice(newBalance);
            productRepository.save(product);
        }
        else{
            Category category=categoryRepository.getReferenceById(offer.getCategory().getId());
            String categories=category.getName();
            List<Product> products=productRepository.findAllByCategory(categories);
            for(Product product:products){
                double costprice = product.getCostPrice();
                double offerPercentage = offer.getDiscount();
                double offers = costprice * offerPercentage / 100;
                double newBalance=Math.round((costprice - offers) * 100.0) / 100.0;
                product.setSalePrice(newBalance);
                productRepository.save(product);
            }
        }
    }

    @Override
    public void disableOffer(Long id) {
        Offer offer=offerRepository.getReferenceById(id);
        offer.setActivated(false);
        offerRepository.save(offer);
        if(offer.getOfferType().equals("Product Offer")) {
            Product product = productRepository.getReferenceById(offer.getProduct().getId());
            product.setSalePrice(product.getCostPrice());
            productRepository.save(product);
        }
        else{
            Category category=categoryRepository.getReferenceById(offer.getCategory().getId());
            String categories=category.getName();
            List<Product> products=productRepository.findAllByCategory(categories);
            for(Product product:products){
                product.setSalePrice(product.getCostPrice());
                productRepository.save(product);
            }

        }
    }
}