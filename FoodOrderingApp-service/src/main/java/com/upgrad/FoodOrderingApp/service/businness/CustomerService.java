package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class CustomerService {

    @Autowired private CustomerDao customerDao;

    @Autowired private CustomerAuthDao customerAuthDao;

    @Autowired private PasswordCryptographyProvider passwordCryptographyProvider;

    /**
     * This method receives CustomerEntity.
     * This method is to save customer in db.
     */
    /**
     * @param customerEntity - CustomerEntity
     * @return -  CustomerEntity
     * @exception - none.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(CustomerEntity customerEntity)
            throws SignUpRestrictedException {
        String validEmailFormat = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        String validContactNumber = "^(?=.*[0-9])D*\\d{10}\\D*$";
        if (customerEntity.getFirstName() == null
                || customerEntity.getEmail() == null
                || customerEntity.getContactNumber() == null
                || customerEntity.getPassword() == null
                || customerEntity.getEmail().equals("")
                || customerEntity.getFirstName().equals("")
                || customerEntity.getContactNumber().equals("")
                || customerEntity.getPassword().equals(""))
            throw new SignUpRestrictedException(
                    "SGR-005", "Except last name all fields should be filled");
        else if (customerDao.getContactByContactNumber(customerEntity.getContactNumber()) != null)
            throw new SignUpRestrictedException(
                    "SGR-001", "This contact number is already registered! Try other contact number.");
        else if (!customerEntity.getEmail().matches(validEmailFormat))
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        else if (!customerEntity.getContactNumber().matches(validContactNumber)
                || customerEntity.getContactNumber().length() != 10)
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        else if (this.verifyPasswordStrength(customerEntity.getPassword()))
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        else {
            String[] temp = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
            customerEntity.setSalt(temp[0]);
            customerEntity.setPassword(temp[1]);
            return customerDao.createUser(customerEntity);
        }
    }

    /**
     * This method receives username and password.
     * This method is to authenticate user based on username and password.
     */
    /**
     * @param username - username
     * @param password - password
     * @return -  CustomerAuthEntity
     * @exception - none.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(final String username, final String password)
            throws AuthenticationFailedException {
        CustomerEntity customerEntity = customerDao.getContactByContactNumber(username);
        if (customerEntity == null) {
            throw new AuthenticationFailedException(
                    "ATH-001", "This contact number has not been registered!");
        }
        String encryptedPassword =
                PasswordCryptographyProvider.encrypt(password, customerEntity.getSalt());
        CustomerAuthEntity preExistingCustomerAuth =
                customerAuthDao.getCustomerAuthByCustomerId(customerEntity.getId());
        if (preExistingCustomerAuth != null && encryptedPassword.equals(customerEntity.getPassword())) {
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(24);
            preExistingCustomerAuth.setLoginAt(now);
            preExistingCustomerAuth.setExpiresAt(expiresAt);
            preExistingCustomerAuth.setLogoutAt(null);

            customerAuthDao.updateCustomerAuth(preExistingCustomerAuth);

            return preExistingCustomerAuth;
        }
        if (encryptedPassword.equals(customerEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
            customerAuthEntity.setCustomerEntity(customerEntity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(24);
            customerAuthEntity.setAccessToken(
                    jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt));
            customerAuthEntity.setLoginAt(now);
            customerAuthEntity.setExpiresAt(expiresAt);
            customerAuthEntity.setUuid(customerEntity.getUuid());

            customerAuthDao.createAuthToken(customerAuthEntity);

            return customerAuthEntity;
        } else throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
    }

    /**
     * This method receives accesstoken.
     * This method is to logout the customer.
     */
    /**
     * @param accessToken - access token
     * @return -  CustomerAuthEntity
     * @exception - none.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity logout(String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerAuthByToken(accessToken);
        if (customerAuthEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        else if (ZonedDateTime.now().compareTo(customerAuthEntity.getExpiresAt()) > 0)
            throw new AuthorizationFailedException(
                    "ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        else if (customerAuthEntity.getLogoutAt() != null)
            throw new AuthorizationFailedException(
                    "ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        else {
            customerAuthEntity.setLogoutAt(ZonedDateTime.now());
            return customerAuthEntity;
        }
    }

    /**
     * This method receives CustomerEntity.
     * This method is to update customer details.
     */
    /**
     * @param customerEntity - CustomerEntity
     * @return -  CustomerEntity
     * @exception - none.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(CustomerEntity customerEntity)
            throws UpdateCustomerException {
        if (customerEntity.getFirstName() == null || customerEntity.getFirstName().equals(""))
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        customerDao.updateCustomerEntity(customerEntity);
        return customerEntity;
    }

    /**
     * This method receives oldpassword, new password and CustomerEntity.
     * This method is to update customer password.
     */
    /**
     * @param oldPassword - oldPassword
     * @param newPassword - newPassword
     * @param customerEntity - CustomerEntity
     * @return -  CustomerEntity
     * @exception - none.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomerPassword(
            String oldPassword, String newPassword, CustomerEntity customerEntity)
            throws UpdateCustomerException {

        if (oldPassword == null
                || newPassword == null
                || oldPassword.equals("")
                || newPassword.equals(""))
            throw new UpdateCustomerException("UCR-003", "No field should be empty");

        // check old password validity
        String encryptedPassword =
                PasswordCryptographyProvider.encrypt(oldPassword, customerEntity.getSalt());
        if (!encryptedPassword.equals(customerEntity.getPassword()))
            throw new UpdateCustomerException("UCR-004", "Incorrect old password!");

        // check new password strength
        if (this.verifyPasswordStrength(newPassword))
            throw new UpdateCustomerException("UCR-001", "Weak password!");

        String[] temp = passwordCryptographyProvider.encrypt(newPassword);
        customerEntity.setSalt(temp[0]);
        customerEntity.setPassword(temp[1]);
        customerDao.updateCustomerEntity(customerEntity);
        return customerEntity;
    }

    /**
     * This method receives user access token.
     * This method is to fetch customer details.
     */
    /**
     * @param accessToken - access token
     * @return -  CustomerEntity
     * @exception - AuthorizationFailedException.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity getCustomer(String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerAuthByToken(accessToken);
        if (customerAuthEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        else if (ZonedDateTime.now().compareTo(customerAuthEntity.getExpiresAt()) > 0)
            throw new AuthorizationFailedException(
                    "ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        else if (customerAuthEntity.getLogoutAt() != null)
            throw new AuthorizationFailedException(
                    "ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        return customerAuthEntity.getCustomerEntity();
    }

    public boolean verifyPasswordStrength(String password) {

        if (password.length() < 8) return true;
        boolean hasDigit = false;
        boolean hasUpperCaseLetter = false;
        boolean hasSpecialCharacter = false;
        String sc = "#@$%&*!^";
        for (int i = 0; i < password.length(); i++) {
            if (Character.isDigit(password.charAt(i))) hasDigit = true;
            if (Character.isUpperCase(password.charAt(i))) hasUpperCaseLetter = true;
            if (sc.indexOf(password.charAt(i)) >= 0) hasSpecialCharacter = true;
            if (hasDigit && hasUpperCaseLetter && hasSpecialCharacter) break;
        }
        return !hasDigit || !hasUpperCaseLetter || !hasSpecialCharacter;
    }
}
