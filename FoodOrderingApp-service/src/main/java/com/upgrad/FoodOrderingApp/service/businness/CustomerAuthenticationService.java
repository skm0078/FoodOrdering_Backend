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
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
        // Validate if contact number is not used
        if (isContactNumberInUse(customerEntity.getContactNumber())) {
            throw new SignUpRestrictedException(
                    "SGR-001", "This contact number is already registered! Try other contact number.");
        }

        if ((customerEntity.getFirstName() == null) || (customerEntity.getEmail() == null) ||
                (customerEntity.getContactNumber() == null) || (customerEntity.getPassword() == null)) {
            throw new SignUpRestrictedException(
                    "SGR-005", "Except last name all fields should be filled");
        }

        if (!(isValidEmail(customerEntity.getEmail()))) {
            throw new SignUpRestrictedException(
                    "SGR-002", "Invalid email-id format!");
        }

        if (!(isValidContactNumber(customerEntity.getContactNumber()))) {
            throw new SignUpRestrictedException(
                    "SGR-003", "Invalid contact number!");
        }

        if (!(isStrongPassword(customerEntity.getPassword()))) {
            throw new SignUpRestrictedException(
                    "SGR-004", "Weak password!");
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
     * the login customer method
     *
     * @param contactNumber : Contact Number that you want to signin
     * @param password      : Password of user
     * @return CustomerAuthEntity access-token and login response.
     * @throws AuthenticationFailedException : If user not found or invalid password
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity login(final String authorization, final String contactNumber, final String password)
            throws AuthenticationFailedException {

        if (!(isAuthorizationInCorrectFormat(authorization))) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }

        CustomerEntity customerEntity = customerDao.getContactByContactNumber(contactNumber);
        if (customerEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }
        final String encryptedPassword =
                passwordCryptographyProvider.encrypt(password, customerEntity.getSalt());
        if (!encryptedPassword.equals(customerEntity.getPassword())) {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
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
     * @return UserEntity : that user is signed out.
     * @throws SignOutRestrictedException : if the access-token is not found in the DB.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity logout(final String accessToken) throws SignOutRestrictedException {
        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerAuthByToken(accessToken);
        if (customerAuthEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "Customer is not Logged in.");
        }
        if (customerAuthEntity != null && customerAuthEntity.getLogoutAt() != null && (customerAuthEntity.getLogoutAt().isBefore(ZonedDateTime.now()))) {
            throw new SignOutRestrictedException("SGR-002", "Customer is logged out. Log in again to access this endpoint.");
        }
        if (customerAuthEntity != null && customerAuthEntity.getExpiresAt() != null && (customerAuthEntity.getExpiresAt().isBefore(ZonedDateTime.now()))) {
            throw new SignOutRestrictedException("SGR-003", "Your session is expired. Log in again to access this endpoint.");
        }
        customerAuthEntity.setLogoutAt(ZonedDateTime.now());
        customerAuthDao.updateCustomerAuth(customerAuthEntity);
        return customerAuthEntity.getCustomerEntity();
    }

    /**
     * Update customer endpoint
     *
     * @param customerId  : customerId of which you want to update
     * @param accessToken : access-token for authorization
     * @throws AuthorizationFailedException : If token is invalid you get authorization failed
     *                                      response
     * @throws UpdateCustomerException      : If userid is invalid or not found
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(final String customerId, final String firstName, final String lastName,
                                         final String accessToken)
            throws AuthorizationFailedException, UpdateCustomerException {
        CustomerAuthEntity customerAuthEntity = this.customerAuthDao.getCustomerAuthByToken(accessToken);

        if (firstName.length() == 0) {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }

        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if (customerAuthEntity.getLogoutAt() != null && (customerAuthEntity.getLogoutAt().isBefore(ZonedDateTime.now()))) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        if (customerAuthEntity != null && customerAuthEntity.getExpiresAt() != null && (customerAuthEntity.getExpiresAt().isBefore(ZonedDateTime.now()))) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
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
     * @param customerId  : customerId of which you want to update
     * @param accessToken : access-token for authorization
     * @throws AuthorizationFailedException : If token is invalid you get authorization failed
     *                                      response
     * @throws UpdateCustomerException      : If userid is invalid or not found
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomerPassword(final String customerId, final String password, final String oldPassword, final String accessToken)
            throws AuthorizationFailedException, UpdateCustomerException {
        CustomerAuthEntity customerAuthEntity = this.customerAuthDao.getCustomerAuthByToken(accessToken);

        if (password.length() == 0 || oldPassword.length() == 0) {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }
        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if (customerAuthEntity.getLogoutAt() != null && (customerAuthEntity.getLogoutAt().isBefore(ZonedDateTime.now()))) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        if (customerAuthEntity != null && customerAuthEntity.getExpiresAt() != null && (customerAuthEntity.getExpiresAt().isBefore(ZonedDateTime.now()))) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        if (!(isStrongPassword(password))) {
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        }

        if (!oldPassword.equals(getCurrentPassword(accessToken))) {
            throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
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

    // checks whether the contactNumber exist in the database
    private boolean isContactNumberInUse(final String contactNumber) {
        return customerDao.getContactByContactNumber(contactNumber) != null;
    }

    // Checks whether email is in correct format
    private boolean isValidEmail(final String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    // Checks whether contact Number is in correct format
    private boolean isValidContactNumber(final String contactNumber) {
        String contactNumberRegex = "^\\d{10}$";

        Pattern pat = Pattern.compile(contactNumberRegex);
        if (contactNumber == null)
            return false;
        return pat.matcher(contactNumber).matches();
    }

    // Check if password is strong
    private boolean isStrongPassword(final String input) {
        boolean isPasswordStrong = false;

        // Checking lower alphabet in string
        int n = input.length();
        boolean hasLower = false, hasUpper = false,
                hasDigit = false, specialChar = false;
        Set<Character> set = new HashSet<Character>(
                Arrays.asList('!', '@', '#', '$', '%', '^', '&',
                        '*'));
        for (char i : input.toCharArray()) {
            if (Character.isLowerCase(i))
                hasLower = true;
            if (Character.isUpperCase(i))
                hasUpper = true;
            if (Character.isDigit(i))
                hasDigit = true;
            if (set.contains(i))
                specialChar = true;
        }

        // Strength of password
        System.out.print("Strength of password:- ");
        if (hasDigit && hasUpper && specialChar && (n >= 8)) {
            isPasswordStrong = true;
        } else {
            isPasswordStrong = false;
        }
        return isPasswordStrong;
    }

    // checks whether the authorization is in correct format in the database
    private boolean isAuthorizationInCorrectFormat(final String authorization) {
        boolean containsBasic = false;
        containsBasic = authorization.contains("Basic ");
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        //String contactNumber = decodedArray[0]; String password = decodedArray[1];
        boolean validContact = isValidContactNumber(decodedArray[0]);
        boolean validPassword = isStrongPassword(decodedArray[1]);
        if (containsBasic && validContact && validPassword) {
            return true;
        } else {
            return false;
        }
    }

    // checks whether the authorization is in correct format in the database
    private String getCurrentPassword(final String authorization) {
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        //String contactNumber = decodedArray[0];
        String password = decodedArray[1];
        return password;
    }
}
