package com.petalaura.library.Service;


import com.petalaura.library.dto.OfferDto;
import com.petalaura.library.model.Offer;

import java.util.List;

public interface OfferService {
    void saveOffer(OfferDto offerDto);
    List<Offer> findAllOffer();
    void enableOffer(Long id);
    void disableOffer(Long id);
    Offer findById(Long id);
    void updateOffer(OfferDto offerDto);
}
