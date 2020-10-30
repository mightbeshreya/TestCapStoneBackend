package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomerService {

    private static final String PASSWORD_PATTERN = "(((?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%]).{3,10}))";
    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Autowired
    CustomerAddressDao customerAddressDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity signup(CustomerEntity customerEntity) throws SignUpRestrictedException {
        CustomerEntity isContactNumberExist = customerDao.IsContactNumberExists(customerEntity.getContactNumber());
        if (isContactNumberExist != null) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
        }
        if (customerEntity.getFirstname() == null
                && customerEntity.getEmail() == null
                && customerEntity.getPassword() == null
                && customerEntity.getContactNumber() == null) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }
        validateCustomerData(customerEntity);
        String password = customerEntity.getPassword();
        String[] encryptedText = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedText[0]);
        customerEntity.setPassword(encryptedText[1]);
        return customerDao.createCustomer(customerEntity);
    }

    private CustomerEntity validateCustomerData(CustomerEntity customerEntity) throws SignUpRestrictedException {
        if (customerEntity.getFirstname() == null
                || customerEntity.getEmail() == null
                || customerEntity.getContactNumber() == null
                || customerEntity.getPassword() == null) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        } else {
            validateEmail(customerEntity.getEmail());
            validateContactNo(customerEntity.getContactNumber());
            validatePassword(customerEntity.getPassword());
        }
        return customerEntity;
    }

    private void validatePassword(String password) throws SignUpRestrictedException {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        if (matcher.matches() == false) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        }
    }


    private void validateEmail(String email) throws SignUpRestrictedException {
        Pattern VALID_EMAIL_REGEX =
                Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_REGEX.matcher(email);
        if (matcher.find() == false) {
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }
    }

    private void validateContactNo(String contactNumber) throws SignUpRestrictedException {
        if (Pattern.matches("[0-9]{10}", contactNumber) == false) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthTokenEntity getCustomer(final String authorizationToken){

        CustomerAuthTokenEntity customerAuth = customerDao.checkAuthToken(authorizationToken);
        if(customerAuth == null){
            return null;
        }
        else {
            return customerAuth;
        }
    }
    //To get Address By Customer
    @Transactional(propagation = Propagation.REQUIRED)
    public List<CustomerAddressEntity> getAddressByCustomer(final String authorizationToken) throws AuthorizationFailedException {

        CustomerAuthTokenEntity customerAuth = customerDao.checkAuthToken(authorizationToken);

        if (customerAuth.equals(null)) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        final ZonedDateTime customerSignOutTime = customerAuth.getLogoutAt();

        if (customerSignOutTime != null && customerAuth != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime customerSessionExpireTime = customerAuth.getExpiresAt();
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.systemDefault());
        if (customerSessionExpireTime.compareTo(currentTime) < 0) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        List<CustomerAddressEntity> customerAddressEntity = customerAddressDao.getAddressByCustomer(customerAuth.getCustomer());


        return customerAddressEntity;

    }
}