package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * Controller method to handle PUT request to update customer details
     *
     * @param saveAddressRequest
     * @param customerUuid
     * @param authorization
     * @return UpdateCustomerResponse
     * @throws AuthorizationFailedException
     * @throws SaveAddressException
     */
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/address",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(
            SaveAddressRequest saveAddressRequest,
            @PathVariable("customerId") final String customerUuid,
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException {

        // Set the user typed content as the new content of the Question entity
        String flatBuildingName = saveAddressRequest.getFlatBuildingName();
        String locality = saveAddressRequest.getLocality();
        String city = saveAddressRequest.getCity();
        String pinCode = saveAddressRequest.getPincode();
        String stateUuid = saveAddressRequest.getStateUuid();

        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setFlatNumber(flatBuildingName);
        addressEntity.setLocality(locality);
        addressEntity.setCity(city);
        addressEntity.setPincode(pinCode);
        addressEntity.setUuid(stateUuid);

        // Authorize user and edit the question with Uuid passed
        AddressEntity savedAddress =
                addressService.saveAddress(addressEntity, flatBuildingName, locality, city, pinCode, stateUuid, authorization);

        SaveAddressResponse saveAddressResponse =
                new SaveAddressResponse()
                        .id(savedAddress.getUuid())
                        .status("ADDRESS SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SaveAddressResponse>(saveAddressResponse, HttpStatus.CREATED);
    }

}
