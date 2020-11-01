package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class LogoutBusinessService {
    @Autowired
    CustomerDao customerDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity logOut(final String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthTokenEntity = customerDao.checkAuthToken(accessToken);
        final ZonedDateTime current = ZonedDateTime.now();
        if (customerAuthTokenEntity != null && customerAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }
        if (customerAuthTokenEntity != null && (customerAuthTokenEntity.getExpiresAt().isBefore(current) || customerAuthTokenEntity.getExpiresAt().isEqual(current))) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }
        if (customerAuthTokenEntity != null && customerAuthTokenEntity.getAccessToken().equals(accessToken)) {
            final ZonedDateTime now = ZonedDateTime.now();
            customerAuthTokenEntity.setLogoutAt(now);
            CustomerEntity logOutCustomer = customerAuthTokenEntity.getCustomer();
            customerDao.updateCustomerAuthToken(customerAuthTokenEntity);
            return logOutCustomer;
        } else {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
    }
}
