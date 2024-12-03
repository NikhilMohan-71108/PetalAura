package com.petalaura.library.Service.impl;

import com.petalaura.library.Repository.AddressRepository;
import com.petalaura.library.Service.AddressService;
import com.petalaura.library.Service.CustomerService;

import com.petalaura.library.dto.AddressDto;

import com.petalaura.library.model.Address;
import com.petalaura.library.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    AddressRepository addressRepository;

     @Autowired
    CustomerService customerService;

    @Override
    public Address save(AddressDto addressDto, String email) {
       Customer customer = customerService.findByEmail(email);
       Address address =new Address();
       address.setAddressLine1(addressDto.getAddressLine1());
       address.setAddressLine2(addressDto.getAddressLine2());
       address.setCity(addressDto.getCity());
       address.setCountry(addressDto.getCountry());
       address.setState(addressDto.getState());
       address.setPinCode(addressDto.getPinCode());
       address.setCustomer(customer);
       address.set_default(false);
       return addressRepository.save(address);
    }

    @Override
    public void deleteById(Long addressId) {
        addressRepository.deleteById(addressId);
    }

    @Override
    public Optional<Address> findByid(Long id) {
        return addressRepository.findById(id);
    }

    @Override
    public List<Address> findAddressByCustomer(String email) {
        return addressRepository.findAddressByCustomer(email);
    }

    @Override
    public Address update(AddressDto addressDto) {
        Address address=addressRepository.getReferenceById(addressDto.getId());
        address.setAddressLine1(addressDto.getAddressLine1());
        address.setAddressLine2(addressDto.getAddressLine2());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setDistrict(addressDto.getDistrict());
        address.setCountry(addressDto.getCountry());
        address.setPinCode(addressDto.getPinCode());
        address.set_default(false);
        return addressRepository.save(address);
    }
}
