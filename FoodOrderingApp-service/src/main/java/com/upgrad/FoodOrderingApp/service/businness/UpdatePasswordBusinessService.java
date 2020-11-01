package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class UpdatePasswordBusinessService {

    @Autowired
    CustomerDao customerDao;

    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updatedPassword(String accessToken, String updatePassword, String oldPassword) throws AuthorizationFailedException, UpdateCustomerException {

        CustomerAuthEntity customerAuthToken = customerDao.checkAuthToken(accessToken);
        CustomerEntity updatedCustomerPassword = customerAuthToken.getCustomer();
        ZonedDateTime current = ZonedDateTime.now();

        if (updatePassword == null || oldPassword == null){
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }
        if (customerAuthToken == null){
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        if (customerAuthToken.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }
        if (customerAuthToken.getExpiresAt().isBefore(current)){
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }
        oldPassword = passwordCryptographyProvider.encrypt(oldPassword, customerAuthToken.getCustomer().getSalt());

        if (!oldPassword.equals(customerAuthToken.getCustomer().getPassword())){
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        }
        String[] encryptedText = passwordCryptographyProvider.encrypt(updatePassword);
        updatedCustomerPassword.setSalt(encryptedText[0]);
        updatedCustomerPassword.setPassword(encryptedText[1]);
        CustomerEntity customerPasswordChange = customerDao.updatePassword(updatedCustomerPassword);
        return customerPasswordChange;
    }
}
