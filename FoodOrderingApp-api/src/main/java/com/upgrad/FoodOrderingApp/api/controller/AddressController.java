package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * Controller method to handle PUT request to save address
     *
     * @param saveAddressRequest
     * @param authorization
     * @return saveAddressResponse
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

    /**
     * Controller method to handle GET request to fetch all addresses
     *
     * @param authorization
     * @return List of all address
     * @throws AuthorizationFailedException
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/address/customer",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAllQuestions(
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {
        // Authorize the user by passing in access-token of the user and Fetch list of all address
        List<AddressEntity> allAddresses = addressService.getAllAddresses(authorization);

        // create stateList arrayList to store all address in StateList form
        List<AddressList> addressListArrayList = new ArrayList<>();

        // Extract Uuid and content from each QuestionResponse entity
        for (AddressEntity address : allAddresses) {
            AddressList addressList = new AddressList();
            addressList.setId(UUID.fromString(address.getUuid()));
            addressList.setFlatBuildingName(address.getFlatNumber());
            addressList.setLocality(address.getLocality());
            addressList.setCity(address.getCity());
            addressList.setPincode(address.getPincode());
            AddressListState addressListState = new AddressListState();
            addressListState.stateName(address.getStateEntity().getStateName());
            addressList.setState(addressListState.stateName(address.getStateEntity().getStateName()));
            addressListArrayList.add(addressList);
        }

        // List to stateResponse entities
        final AddressListResponse addressListResponse = new AddressListResponse();

        for (AddressList addressList : addressListArrayList) {
            addressListResponse.addAddressesItem(addressList);
        }

        return new ResponseEntity<AddressListResponse>(addressListResponse, HttpStatus.OK);
    }

    /**
     * Controller method to handle DELETE request to delete address
     *
     * @param accessToken
     * @param addressId
     * @return
     * @throws AuthorizationFailedException
     * @throws AddressNotFoundException
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/address/{address_id}")
    public ResponseEntity<DeleteAddressResponse> deleteQuestion(
            @RequestHeader("authorization") final String accessToken,
            @PathVariable("addressId") final String addressId)
            throws AuthorizationFailedException, AddressNotFoundException {

        AddressEntity addressEntity = addressService.deleteAddress(accessToken, addressId);
        DeleteAddressResponse addressDeleteResponse = new DeleteAddressResponse();
        addressDeleteResponse.setId(UUID.fromString(addressEntity.getUuid()));
        addressDeleteResponse.setStatus("ADDRESS DELETED SUCCESSFULLY");
        return new ResponseEntity<DeleteAddressResponse>(addressDeleteResponse, HttpStatus.OK);
    }

    /**
     * Controller method to handle GET request to fetch all addresses
     *
     * @param
     * @return List of all states
     * @throws
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/states",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatesListResponse> getAllStatesC() {
        // Fetch list of all states
        List<StateEntity> allStates = addressService.getAllStates();
        // create stateList arrayList to store all states in StateList form
        List<StatesList> statesListArrayList = new ArrayList<>();

        // Extract Uuid and state from each StateEntity and store in stateList arrayList
        for (StateEntity state : allStates) {
            String uuid = state.getUuid();
            String stateName = state.getStateName();
            StatesList statesList = new StatesList();
            statesList.setId(UUID.fromString(uuid));
            statesList.stateName(stateName);
            statesListArrayList.add(statesList);
        }
        // List to stateResponse entities
        final StatesListResponse stateResponseList = new StatesListResponse();

        for (StatesList statesList : statesListArrayList) {
            stateResponseList.addStatesItem(statesList);
        }

        return new ResponseEntity<StatesListResponse>(stateResponseList, HttpStatus.OK);
    }

}
