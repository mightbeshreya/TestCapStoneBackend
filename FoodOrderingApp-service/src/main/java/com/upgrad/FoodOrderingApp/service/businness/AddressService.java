package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AddressService {

    @Autowired
    AddressDao addressDao; //Handle all data of Address

    @Autowired
    CustomerDao customerDao; //Handle all data customer

    @Autowired
    CustomerAddressDao customerAddressDao;

    @Autowired
    StateDao stateDao;

    /* This method is to saveAddress.Takes the Address and state entity and saves the Address to the DB.
    If error throws exception with error code and error message.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(AddressEntity addressEntity, StateEntity stateEntity) throws SaveAddressException, AuthorizationFailedException {

        if (addressEntity.getCity().isEmpty() || addressEntity.getFlat_buil_number().isEmpty() || addressEntity.getPincode().isEmpty() || addressEntity.getLocality().isEmpty()) {
            throw new SaveAddressException("SAR-001", "No field can be empty");
        }
        if (!addressDao.IsPinCodeValid(addressEntity.getPincode())) {
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }
        addressEntity.setState(stateEntity);
        //CustomerAuthEntity customerAuthTokenEntity = customerDao.checkAuthToken(authorizationToken);

        /*if (customerAuthTokenEntity.equals(null)) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        final ZonedDateTime customerSignOut = customerAuthTokenEntity.getLogoutAt();

        if (customerSignOut != null && customerAuthTokenEntity != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }
        final ZonedDateTime customerSessionExpireTime = customerAuthTokenEntity.getExpiresAt();
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.systemDefault());
        if (customerSessionExpireTime.compareTo(currentTime) < 0) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }*/
        return addressDao.saveAddress(addressEntity);
    }

    //To get state by UUID
    public StateEntity getStateByUUID(String uuid) throws AddressNotFoundException{
        StateEntity stateEntity = stateDao.getStateByUuid(uuid);
        if(stateEntity == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        }
        return stateEntity;
    }

    //To save the customer address
    public CustomerAddressEntity saveCustomerAddress(CustomerAddressEntity customerAddressEntity) {

        return addressDao.save(customerAddressEntity);
    }

    //To get Address By Customer
    @Transactional(propagation = Propagation.REQUIRED)
    public List<AddressEntity> getAllAddress(CustomerEntity customerEntity) throws AuthorizationFailedException {

        //CustomerAuthEntity customerAuth = customerDao.checkAuthToken(authorizationToken);

        /*if (customerAuth.equals(null)) {
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
        } */

        List<CustomerAddressEntity> customerAddressEntity = customerAddressDao.getAddressByCustomer(customerEntity);
        List<AddressEntity> addressEntityList = new ArrayList<>();
        for (CustomerAddressEntity cae : customerAddressEntity) {
            AddressEntity addressEntity = cae.getAddress();
            addressEntityList.add(addressEntity);
        }
        //addressEntityList

        return addressEntityList;

    }

    public AddressEntity deleteAddress(AddressEntity addressEntity)  {
            return addressDao.deleteAddress(addressEntity);
    }

    public AddressEntity getAddressByUUID(String addressUuid, CustomerEntity customerEntity)
    throws AddressNotFoundException, AuthorizationFailedException{
        if(addressUuid==null || addressUuid.isEmpty()){
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
        }
        AddressEntity addressEntity = addressDao.getAddressByUUID(addressUuid);
        if(addressEntity == null ){
            throw new AddressNotFoundException("ANF-003", "No address by this id");
        }
        CustomerAddressEntity customerAddressEntity = customerAddressDao.getSingleAddress(addressEntity);
        if(!customerEntity.getId().equals(customerAddressEntity.getCustomer().getId())) {
            throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
        }
        return addressEntity;
    }

    public List<StateEntity> getAllStates(){
        List<StateEntity> allStates = stateDao.getAllStates();
        return allStates;
    }
}
