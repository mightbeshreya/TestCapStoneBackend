package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomerBusinessService {

    private static final String PASSWORD_PATTERN = "(((?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%]).{3,10}))";
    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

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
        }
        else {
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
                Pattern.compile("^[A-Z0-9._%+-]+[A-Z0-9.-]+\\.[A-Z]{2,6}}$", Pattern.CASE_INSENSITIVE);
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

}
