package com.e_commerce.project.service;

import com.e_commerce.project.model.User;
import com.e_commerce.project.payload.AddressDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAddresses();

    AddressDTO getAddress(Long addressId);

    List<AddressDTO> getUsersAddresses(User user);

    AddressDTO updateAddress(@Valid AddressDTO addressDTO, Long addressId);

    String deleteAddress(Long addressId);
}
