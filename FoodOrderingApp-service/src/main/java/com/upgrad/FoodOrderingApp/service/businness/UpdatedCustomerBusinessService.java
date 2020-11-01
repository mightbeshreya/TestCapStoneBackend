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
public class UpdatedCustomerBusinessService {

    @Autowired
    CustomerDao customerDao;

    /* This method is to updateCustomer the customer using customerEntity and return the CustomerEntity .
      If error throws exception with error code and error message.
      */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(String accessToken, String firstName, String lastName) throws AuthorizationFailedException, UpdateCustomerException {

        CustomerAuthEntity customerauth = customerDao.checkAuthToken(accessToken);
        final ZonedDateTime current = ZonedDateTime.now();

        if (customerauth == null){
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        if (firstName == null){
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }
        if (customerauth.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }
        if (customerauth.getExpiresAt().isBefore(current)){
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        CustomerEntity customerEntity = customerauth.getCustomer();
        customerEntity.setFirstName(firstName);
        customerEntity.setLastName(lastName);
        CustomerEntity updatedCustomer = customerDao.updateCustomerDetails(customerEntity);
        return updatedCustomer;
    }
}
