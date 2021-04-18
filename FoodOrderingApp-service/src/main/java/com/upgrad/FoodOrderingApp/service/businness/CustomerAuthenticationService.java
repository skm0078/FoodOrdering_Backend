package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class CustomerAuthenticationService {
    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CustomerAuthDao customerAuthDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    /**
     * check if user is already created add salt and encryption to password
     *
     * @throws SignUpRestrictedException : throw exception if user already exists
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity signup(CustomerEntity customerEntity) throws SignUpRestrictedException {
        if (isContactNumberInUse(customerEntity.getContactNumber())) {
            throw new SignUpRestrictedException(
                    "SGR-001", "This contact number is already registered! Try other contact number.");
        }

        if (isEmailInUse(customerEntity.getEmail())) {
            throw new SignUpRestrictedException(
                    "SGR-002", "This user has already been registered, try with any other emailId");
        }
        // Assign a UUID to the user that is being created.
        customerEntity.setUuid(UUID.randomUUID().toString());
        // Assign encrypted password and salt to the user that is being created.
        String[] encryptedText = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedText[0]);
        customerEntity.setPassword(encryptedText[1]);
        return customerDao.createUser(customerEntity);
    }
    /**
     * the signin user method
     *
     * @param contactNumber : Contact Number that you want to signin
     * @param password : Password of user
     * @throws AuthenticationFailedException : If user not found or invalid password
     * @return CustomerAuthEntity access-token and singin response.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity login(final String contactNumber, final String password)
            throws AuthenticationFailedException {

        CustomerEntity customerEntity = customerDao.getContactByContactNumber(contactNumber);
        if (customerEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }
        final String encryptedPassword =
                passwordCryptographyProvider.encrypt(password, customerEntity.getSalt());
        if (!encryptedPassword.equals(customerEntity.getPassword())) {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }

        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
        CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
        customerAuthEntity.setUuid(UUID.randomUUID().toString());
        customerAuthEntity.setCustomerEntity(customerEntity);
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime expiresAt = now.plusHours(8);
        customerAuthEntity.setAccessToken(
                jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt));
        customerAuthEntity.setLoginAt(now);
        customerAuthEntity.setExpiresAt(expiresAt);

        customerAuthDao.createAuthToken(customerAuthEntity);
        customerDao.updateCustomerEntity(customerEntity);

        return customerAuthEntity;
    }

    /**
     * The signout method
     *
     * @param accessToken : required to signout the user
     * @throws SignOutRestrictedException : if the access-token is not found in the DB.
     * @return UserEntity : that user is signed out.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity logout(final String accessToken) throws SignOutRestrictedException {
        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerAuthByToken(accessToken);
        if (customerAuthEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
        customerAuthEntity.setLogoutAt(ZonedDateTime.now());
        customerAuthDao.updateCustomerAuth(customerAuthEntity);
        return customerAuthEntity.getCustomerEntity();
    }

    // checks whether the contactNumber exist in the database
    private boolean isContactNumberInUse(final String contactNumber) {
        return customerDao.getContactByContactNumber(contactNumber) != null;
    }

    // checks whether the email exist in the database
    private boolean isEmailInUse(final String email) {
        return customerDao.getUserByEmail(email) != null;
    }

    // checks whether the any field other than last name is empty
    private boolean isAnyFieldEmptyExceptLastName(final String email) {
        return customerDao.getUserByEmail(email) != null;
    }

    /**
     * Update customer endpoint
     *
     * @param customerId : customerId of which you want to update
     * @param accessToken : access-token for authorization
     * @throws AuthorizationFailedException : If token is invalid you get authorization failed
     *     response
     * @throws UpdateCustomerException : If userid is invalid or not found
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(final String customerId, final String firstName, final String lastName,
                                          final String accessToken)
            throws AuthorizationFailedException, UpdateCustomerException {
        CustomerAuthEntity customerAuthEntity = this.customerAuthDao.getCustomerAuthByToken(accessToken);

        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not logged in");
        }

        if (customerAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is loged out");
        }

        CustomerEntity existingUser = this.customerDao.getCustomerById(customerId);

        if (existingUser == null) {
            throw new UpdateCustomerException(
                    "USR-001", "User with entered uuid to be deleted does not exist");
        }

        existingUser.setFirstName(firstName);
        existingUser.setLastName(lastName);
        existingUser = this.customerDao.updateCustomerEntity(existingUser);
        return existingUser;
    }

    /**
     * Update customer endpoint
     *
     * @param customerId : customerId of which you want to update
     * @param accessToken : access-token for authorization
     * @throws AuthorizationFailedException : If token is invalid you get authorization failed
     *     response
     * @throws UpdateCustomerException : If userid is invalid or not found
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomerPassword(final String customerId, final String password, final String accessToken)
            throws AuthorizationFailedException, UpdateCustomerException {
        CustomerAuthEntity customerAuthEntity = this.customerAuthDao.getCustomerAuthByToken(accessToken);

        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not logged in");
        }

        if (customerAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is loged out");
        }

        CustomerEntity existingUser = this.customerDao.getCustomerById(customerId);

        if (existingUser == null) {
            throw new UpdateCustomerException(
                    "USR-001", "User with entered uuid to be deleted does not exist");
        }

        existingUser.setPassword(password);
        existingUser = this.customerDao.updateCustomerEntity(existingUser);
        return existingUser;
    }
}
