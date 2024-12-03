package com.petalaura.library.Service;

import com.petalaura.library.dto.AddressDto;
import com.petalaura.library.model.Address;

import java.util.List;
import java.util.Optional;

public interface AddressService {
   Address save(AddressDto addressDto, String email);
   void deleteById(Long addressId);
   Optional<Address> findByid(Long id);
   List<Address> findAddressByCustomer(String email);
   Address update(AddressDto addressDto);
}
