package com.e_commerce.project.service;

import com.e_commerce.project.exception.ResourceNotFoundException;
import com.e_commerce.project.model.Address;
import com.e_commerce.project.model.User;
import com.e_commerce.project.payload.AddressDTO;
import com.e_commerce.project.repository.AddressRepository;
import com.e_commerce.project.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {

        Address address = modelMapper.map(addressDTO, Address.class);
        List<Address> userAddresses = user.getAddresses();
        userAddresses.add(address);
        user.setAddresses(userAddresses);

        address.setUser(user);


        return modelMapper.map(addressRepository.save(address),AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {

        List<Address> addressList = addressRepository.findAll();
        return addressList.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO getAddress(Long addressId) {

        Address address = addressRepository.findById(addressId).orElseThrow(
                () -> new ResourceNotFoundException("Address", "addressId", addressId)
        );
        return modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUsersAddresses(User user) {

        List<Address> userAddresses = user.getAddresses();
        return userAddresses.stream().map(address -> modelMapper.map(
                address,AddressDTO.class
        )).toList();
    }

    @Override
    public AddressDTO updateAddress(AddressDTO addressDTO, Long addressId) {
        Address address = modelMapper.map(addressDTO, Address.class);

        Address addressDB = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        addressDB.setCity(address.getCity());
        addressDB.setState(address.getState());
        addressDB.setCountry(address.getCountry());
        addressDB.setStreet(address.getStreet());
        addressDB.setPinCode(address.getPinCode());
        addressDB.setBuildingName(address.getBuildingName());

        Address savedAddress = addressRepository.save(addressDB);

        User user = addressDB.getUser();
        user.getAddresses().removeIf(userAddress->userAddress.getAddressId().equals(addressId));
      user.getAddresses().add(address);
userRepository.save(user);

        return modelMapper.map(savedAddress,AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {

        Address address= addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        User user = address.getUser();
        user.getAddresses().removeIf(userAddress->userAddress.getAddressId().equals(addressId));
        userRepository.save(user);
        addressRepository.delete(address);
        return "Address successfully deleted with addressId " + addressId;
    }
}
