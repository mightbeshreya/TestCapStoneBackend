package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CustomerBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.LoginBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.LogoutBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.UpdatedCustomerBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("")
@CrossOrigin
public class CustomerController {
    @Autowired
    CustomerBusinessService customerBusinessService;

    @Autowired
    LoginBusinessService loginBusinessService;

    @Autowired
    LogoutBusinessService logoutBusinessService;

    @Autowired
    UpdatedCustomerBusinessService updatedCustomerBusinessService;

    /* The method handles Customer SignUp Related request.It takes the details as per in the SignupCustomerRequest
    & produces response in SignupCustomerResponse and returns UUID of newly Created Customer and Success message else Return error code and error Message.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setUuid(UUID.randomUUID().toString());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setFirstname(signupCustomerRequest.getFirstName());
        customerEntity.setLastname(signupCustomerRequest.getLastName());
        customerEntity.setPassword(signupCustomerRequest.getPassword());
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        CustomerEntity createdCustomer = customerBusinessService.signup(customerEntity);

        SignupCustomerResponse signupCustomerResponse = new SignupCustomerResponse().id(createdCustomer.getUuid()).status("CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupCustomerResponse>(signupCustomerResponse, HttpStatus.CREATED);
    }

    /*This Method handles the Login request and takes authorization parameter in Base64 coded and produces a LoginResponse containing info customer
    and response header containing bearer accessToken. If error returns the error code with corresponding Message.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/login", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");

        CustomerAuthTokenEntity customerAuthTokenEntity = loginBusinessService.authentication(decodedArray[0], decodedArray[1]);
        CustomerEntity customerEntity = customerAuthTokenEntity.getCustomer();
        LoginResponse loginResponse = new LoginResponse().id(customerEntity.getUuid()).message("LOGGED IN SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("accessToken", customerAuthTokenEntity.getAccessToken());
        return new ResponseEntity<LoginResponse>(loginResponse, headers, HttpStatus.OK);
    }

    /* This method handles the customer logout ,It takes the Bearer accessToken from authorization in the header and logs out the customer
    and returns a LogoutResponse conatining UUID of customer and the successful message.If error returns the error code with corresponding Message.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/logout", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logOut(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException, AuthorizationFailedException {
        CustomerEntity logOutUser = logoutBusinessService.logOut(authorization);
        LogoutResponse logoutResponse = new LogoutResponse().id(logOutUser.getUuid()).message("LOGGED OUT SUCCESSFULLY");
        return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);
    }

    /* This method handles the customer details update request. Takes the request as UpdateCustomerRequest and produces UpdateCustomerResponse containing the details of the updated Customer.
    If error returns the error code with corresponding Message.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> updateCustomer(@RequestHeader("authorization") final String authorization, @RequestBody(required = false) UpdateCustomerRequest updateCustomerRequest) throws AuthenticationFailedException, UpdateCustomerException {

        String[] authorizationData = authorization.split(" ");
        String userAccessToken = authorizationData[0];
        String firstName = updateCustomerRequest.getFirstName();
        String lastName = updateCustomerRequest.getLastName();
        CustomerEntity updateCustomer = updatedCustomerBusinessService.customerToBeUpdated(userAccessToken, firstName, lastName);
        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse().id(updateCustomer.getUuid()).firstName(updateCustomer.getFirstname()).lastName(updateCustomer.getLastname())
                .status("CUSTOMER DETAILS UPDATED SUCCESSFULLY");
        return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse, HttpStatus.OK);
    }
}