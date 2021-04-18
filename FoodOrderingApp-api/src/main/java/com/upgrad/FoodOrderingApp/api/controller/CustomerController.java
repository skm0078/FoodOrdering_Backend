package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CustomerAuthenticationService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/")
public class CustomerController {

    @Autowired
    private CustomerAuthenticationService customerAuthenticationService;

    /**
     * This method is for customer signup. This method receives the object of SignupCustomerRequest type with
     * its attributes being set.
     *
     * @return SignupCustomerResponse - UUID of the user created.
     * @throws SignUpRestrictedException - if the contactNumber or email already exist in the database.
     */

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/customer/signup",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(SignupCustomerRequest signupCustomerRequest)
            throws SignUpRestrictedException {

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setPassword(signupCustomerRequest.getPassword());
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());

        CustomerEntity createdCustomerEntity = customerAuthenticationService.signup(customerEntity);
        SignupCustomerResponse userResponse =
                new SignupCustomerResponse()
                        .id(createdCustomerEntity.getUuid())
                        .status("CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupCustomerResponse>(userResponse, HttpStatus.CREATED);
    }

    /**
     * This method is for a customer to login.
     *
     * @param authorization for the basic authentication
     * @return Login response which has customerId and access-token in response header.
     * @throws AuthenticationFailedException : if username or password is invalid
     */
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/customer/login",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(
            @RequestHeader("authorization") final String authorization)
            throws AuthenticationFailedException {

        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        CustomerAuthEntity customerAuthEntity = customerAuthenticationService.login(authorization, decodedArray[0], decodedArray[1]);

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", customerAuthEntity.getAccessToken());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setId(customerAuthEntity.getCustomerEntity().getUuid());
        loginResponse.setMessage("LOGGED IN SUCCESSFULLY");

        return new ResponseEntity<LoginResponse>(loginResponse, headers, HttpStatus.OK);
    }

    /**
     * Request mapping to sign-out customer
     *
     * @param acessToken
     * @return LogoutResponse
     * @throws SignOutRestrictedException
     */
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/customer/logout",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(
            @RequestHeader("authorization") final String acessToken) throws SignOutRestrictedException {
        CustomerEntity customerEntity = customerAuthenticationService.logout(acessToken);
        LogoutResponse logoutResponse =
                new LogoutResponse().id(customerEntity.getUuid()).message("LOGGED OUT SUCCESSFULLY");
        return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);
    }

    /**
     * Controller method to handle PUT request to update customer details
     *
     * @param customerEditRequest
     * @param customerUuid
     * @param authorization
     * @return UpdateCustomerResponse
     * @throws AuthorizationFailedException
     * @throws UpdateCustomerException
     */
    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/customer",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> updateCustomerDetails(
            UpdateCustomerRequest customerEditRequest,
            @PathVariable("customerId") final String customerUuid,
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, UpdateCustomerException {
        // Set the user typed content as the new content of the Question entity
        String firstName = customerEditRequest.getFirstName();
        String lastName = customerEditRequest.getLastName();

        // Authorize user and edit the question with Uuid passed
        CustomerEntity customerEntity =
                customerAuthenticationService.updateCustomer(customerUuid, firstName, lastName, authorization);

        // Set the Uuid and status of edited question in response
        UpdateCustomerResponse customerEditResponse =
                new UpdateCustomerResponse().id(customerEntity.getUuid()).status("CUSTOMER DETAILS UPDATED SUCCESSFULLY");
        return new ResponseEntity<UpdateCustomerResponse>(customerEditResponse, HttpStatus.OK);
    }

    /**
     * Controller method to handle PUT request to update customer password
     *
     * @param updatePasswordRequest
     * @param customerUuid
     * @param authorization
     * @return UpdateCustomerResponse
     * @throws AuthorizationFailedException
     * @throws UpdateCustomerException
     */
    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/customer/password",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> updatePassword(
            UpdatePasswordRequest updatePasswordRequest,
            @PathVariable("customerId") final String customerUuid,
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, UpdateCustomerException {
        // Set the user typed content as the new content of the Question entity
        String newPassword = updatePasswordRequest.getNewPassword();
        String oldPassword = updatePasswordRequest.getOldPassword();

        // Authorize user and edit the question with Uuid passed
        CustomerEntity customerEntity =
                customerAuthenticationService.updateCustomerPassword(customerUuid, newPassword, authorization);

        // Set the Uuid and status of edited question in response
        UpdatePasswordResponse updatePasswordResponse =
                new UpdatePasswordResponse().id(customerEntity.getUuid()).status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");
        return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse, HttpStatus.OK);
    }

}
