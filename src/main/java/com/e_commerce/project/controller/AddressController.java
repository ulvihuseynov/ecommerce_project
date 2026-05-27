package com.e_commerce.project.controller;

import com.e_commerce.project.model.User;
import com.e_commerce.project.payload.AddressDTO;
import com.e_commerce.project.service.AddressService;
import com.e_commerce.project.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    private final AddressService addressService;
    private final AuthUtil authUtil;

    public AddressController(AddressService addressService, AuthUtil authUtil) {
        this.addressService = addressService;
        this.authUtil = authUtil;
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO){

        User user = authUtil.loggedInUser();
        AddressDTO savedAddressDto=addressService.createAddress(addressDTO,user);

        return new ResponseEntity<>(savedAddressDto, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAddresses(){

        List<AddressDTO> addressDTOS=addressService.getAddresses();

        return new ResponseEntity<>(addressDTOS,HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable Long addressId){
        User user = authUtil.loggedInUser();
        AddressDTO addressDTO=addressService.getAddress(addressId);

        return new ResponseEntity<>(addressDTO,HttpStatus.OK);
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getUsersAddresses(){

        User user = authUtil.loggedInUser();
        List<AddressDTO> addressDTOS=addressService.getUsersAddresses(user);

        return new ResponseEntity<>(addressDTOS,HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@Valid @RequestBody AddressDTO addressDTO,
                                                    @PathVariable Long addressId){

        AddressDTO savedAddressDto=addressService.updateAddress(addressDTO,addressId);

        return new ResponseEntity<>(savedAddressDto, HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId){

        String status=addressService.deleteAddress(addressId);

        return new ResponseEntity<>(status, HttpStatus.OK);
    }

}
