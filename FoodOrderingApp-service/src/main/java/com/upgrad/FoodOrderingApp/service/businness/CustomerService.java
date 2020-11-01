package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
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
import java.util.UUID;
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
    public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {
        CustomerEntity isContactNumberExist = customerDao.IsContactNumberExists(customerEntity.getContactNumber());
        if (isContactNumberExist != null) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
        }
        if (customerEntity.getFirstname() == null
                || customerEntity.getEmail() == null
                || customerEntity.getPassword() == null
                || customerEntity.getContactNumber() == null
                || customerEntity.getFirstname().isEmpty()
                || customerEntity.getEmail().isEmpty()
                || customerEntity.getPassword().isEmpty()
                || customerEntity.getContactNumber().isEmpty()) {
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
    public CustomerEntity getCustomer(final String authorizationToken) throws AuthorizationFailedException{
        CustomerAuthEntity customerAuth = customerDao.checkAuthToken(authorizationToken);
        final ZonedDateTime current = ZonedDateTime.now();
        if (customerAuth == null){
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        if (customerAuth.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }
        if (customerAuth.getExpiresAt().isBefore(current)){
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }
        return customerAuth.getCustomer();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(final String contactNumber, final String password) throws AuthenticationFailedException {

        CustomerEntity customerEntity = customerDao.IsContactNumberExists(contactNumber);
        if (customerEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }
        String encryptedPassword = passwordCryptographyProvider.encrypt(password, customerEntity.getSalt());
        if (encryptedPassword.equals(customerEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            CustomerAuthEntity customerAuthTokenEntity = new CustomerAuthEntity();
            customerAuthTokenEntity.setCustomer(customerEntity);
            customerAuthTokenEntity.setUuid(UUID.randomUUID().toString());
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            customerAuthTokenEntity.setExpiresAt(expiresAt);
            customerAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(customerAuthTokenEntity.getUuid(), now, expiresAt));
            customerAuthTokenEntity.setLoginAt(now);
            //customerAuthTokenEntity.setLogoutAt(expiresAt);
            customerDao.createAuthToken(customerAuthTokenEntity);
            return customerAuthTokenEntity;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }
    }

    /*@Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity logout(final String accessToken) throws AuthorizationFailedException {
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
    } */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity logout(final String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthToken = customerDao.getUserAuthToken(accessToken);
        //if the access token doesnt exist in the database it will throw an error with below message
        //else if the access token exists in the database the logout time will be updated and persisted in the database
        if (customerAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        else {
            if(customerAuthToken.getLogoutAt()!=null) {
                throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
            }
            final ZonedDateTime now = ZonedDateTime.now();
            if(customerAuthToken.getExpiresAt().isBefore(now) || customerAuthToken.getExpiresAt().isEqual(now)){
                throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
            }
            else {
                customerAuthToken.setLogoutAt(now);
                customerDao.updateCustomerLogoutAt(customerAuthToken);
                return customerAuthToken;
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(CustomerEntity customerEntity)  {
        /*
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
        } */
        CustomerEntity updatedCustomer = customerDao.updateCustomerDetails(customerEntity);
        return updatedCustomer;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomerPassword(String oldPassword, String updatePassword, CustomerEntity customerEntity)
            throws AuthorizationFailedException, UpdateCustomerException {

        /*CustomerAuthEntity customerAuthToken = customerDao.checkAuthToken(accessToken);
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
        }*/
        if(!checkForPasswordStrength(updatePassword)) {
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        }
        final String encryptedPassword = passwordCryptographyProvider.encrypt(oldPassword, customerEntity.getSalt());
        if(!encryptedPassword.equals(customerEntity.getPassword())) {

            throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
        }
        /*
        oldPassword = passwordCryptographyProvider.encrypt(oldPassword, customerAuthToken.getCustomer().getSalt());

        if (!oldPassword.equals(customerAuthToken.getCustomer().getPassword())){
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        } */
        String[] encryptedText = passwordCryptographyProvider.encrypt(updatePassword);
        customerEntity.setSalt(encryptedText[0]);
        customerEntity.setPassword(encryptedText[1]);
        CustomerEntity updatedPaswordCustomer = customerDao.updatePassword(customerEntity);
        return updatedPaswordCustomer;
    }
    private boolean checkForPasswordStrength (String pass) {
        String regex = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        if (pass == null) {
            return false;
        }
        Matcher m = pattern.matcher(pass);
        return m.matches();
    }
}

