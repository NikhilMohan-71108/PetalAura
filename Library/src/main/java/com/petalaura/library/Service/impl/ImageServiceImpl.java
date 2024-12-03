package com.petalaura.library.Service.impl;

import com.petalaura.library.Repository.ImageRepository;
import com.petalaura.library.Service.ImageService;
import com.petalaura.library.model.Image;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ImageServiceImpl implements ImageService {
    private ImageRepository imageRepository;

    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public List<Image> findProductImages(long id) {
        return imageRepository.findImageBy(id);
    }

    @Override
    public List<Image> findAll() {
        return imageRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteImage(Long imageId) {
        imageRepository.deleteById(imageId);
    }
}
