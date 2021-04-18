package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AddressService {
    @Autowired
    private AddressDao addressDao;

    @Autowired
    private StateDao stateDao;

    @Autowired
    private CustomerAddressDao customerAddressDao;

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

        if ((customerEntity.getFirstName()==null)||(customerEntity.getEmail()==null)||
                (customerEntity.getContactNumber()==null)||(customerEntity.getPassword()==null)) {
            throw new SignUpRestrictedException(
                    "SGR-005", "Except last name all fields should be filled");
        }

        if(!(isValidEmail(customerEntity.getEmail()))){
            throw new SignUpRestrictedException(
                    "SGR-002", "Invalid email-id format!");
        }

        if(!(isValidContactNumber(customerEntity.getContactNumber()))){
            throw new SignUpRestrictedException(
                    "SGR-003", "Invalid contact number!");
        }

        if(!(isStrongPassword(customerEntity.getPassword()))){
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
}
