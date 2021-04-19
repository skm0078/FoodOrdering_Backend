package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AddressService {
    @Autowired
    private AddressDao addressDao;

    @Autowired
    private StateDao stateDao;

    @Autowired
    private CustomerAddressDao customerAddressDao;

    @Autowired
    private CustomerAuthDao customerAuthDao;

    /**
     * Update customer endpoint
     *
     * @param stateUuid   : stateId of which you want to update and all other parameters
     * @param accessToken : access-token for authorization
     * @throws AuthorizationFailedException : If token is invalid you get authorization failed
     *                                      response
     * @throws SaveAddressException         : If userid is invalid or not found
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(AddressEntity addressEntity, final String flatBuildingName, final String locality, final String city,
                                     final String pinCode, final String stateUuid, final String accessToken)
            throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException {
        CustomerAuthEntity customerAuthEntity = this.customerAuthDao.getCustomerAuthByToken(accessToken);

        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if (customerAuthEntity.getLogoutAt() != null && (customerAuthEntity.getLogoutAt().isBefore(ZonedDateTime.now()))) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        if (customerAuthEntity != null && customerAuthEntity.getExpiresAt() != null && (customerAuthEntity.getExpiresAt().isBefore(ZonedDateTime.now()))) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        if ((flatBuildingName.length() == 0) || (locality.length() == 0) ||
                (city.length() == 0) || (pinCode.length() == 0) || (stateUuid.length() == 0)) {
            throw new SaveAddressException("SAR-001", "No field can be empty");
        }

        if (!(isValidPinCode(pinCode))) {
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }

        if (!isStateUuidInDB(stateUuid)) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        }
        // Assign a UUID to the user that is being created.
        addressEntity.setUuid(UUID.randomUUID().toString());

        return addressDao.createAddress(addressEntity);
    }

    /**
     * Business logic to authorize user who wants to get a list of all address and return list of
     * addresses
     *
     * @param authorization
     * @return list of all address
     * @throws AuthorizationFailedException
     */
    public List<AddressEntity> getAllAddresses(final String authorization)
            throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerAuthByToken(authorization);
        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Retrieve logout_at attribute value of UserAuthEntity to check if user has already signed
        // out
        ZonedDateTime logoutAt = customerAuthEntity.getLogoutAt();
        if (logoutAt != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }

        if (customerAuthEntity != null && customerAuthEntity.getExpiresAt() != null && (customerAuthEntity.getExpiresAt().isBefore(ZonedDateTime.now()))) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        return addressDao.getAllAddress();
    }

    /**
     * Business logic to authorize user and delete question
     *
     * @param accessToken
     * @param addressId
     * @return deleted address
     * @throws AuthorizationFailedException
     * @throws AddressNotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity deleteAddress(final String accessToken, final String addressId)
            throws AuthorizationFailedException, AddressNotFoundException {
        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerAuthByToken(accessToken);

        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (customerAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException(
                    "ATHR-002", "User is signed out.Sign in first to get all questions");
        } else if (customerAuthEntity.getExpiresAt() != null && (customerAuthEntity.getExpiresAt().isBefore(ZonedDateTime.now()))) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        AddressEntity addressEntity = addressDao.getAddressByUuid(addressId);
        if (addressEntity == null) {
            throw new AddressNotFoundException("ANF-003", "No address by this id");
        }

        if (!customerAddressDao.getCustomerByAddress(addressEntity).getUuid().equals(customerAuthEntity.getCustomerEntity().getUuid())) {
            throw new AuthorizationFailedException(
                    "ATHR-004", "You are not authorized to view/update/delete any one else's address ");
        }
        if (addressId.length() == 0) {
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
        }

        addressDao.deleteAddress(addressEntity);
        return addressEntity;
    }

    /**
     * Business logic to authorize user who wants to get a list of all address and return list of
     * addresses
     *
     * @param
     * @return list of all states
     * @throws
     */
    public List<StateEntity> getAllStates() {

        return stateDao.getAllStates();
    }

    // Checks whether pinCode is in correct format
    private boolean isValidPinCode(final String pinCode) {
        String contactNumberRegex = "^\\d{6}$";

        Pattern pat = Pattern.compile(contactNumberRegex);
        if (pinCode == null)
            return false;
        return pat.matcher(pinCode).matches();
    }

    // checks whether the state uuid entered exist in the database
    private boolean isStateUuidInDB(final String stateUuid) {
        return stateDao.getStateById(stateUuid) != null;
    }
}
