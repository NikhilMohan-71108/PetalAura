package com.petalaura.library.Service;

import com.petalaura.library.model.Image;
import jakarta.transaction.Transactional;

import java.util.List;

public interface ImageService {
    List<Image> findProductImages(long id);

    List<Image> findAll();

    @Transactional
    void deleteImage(Long imageId);
}
