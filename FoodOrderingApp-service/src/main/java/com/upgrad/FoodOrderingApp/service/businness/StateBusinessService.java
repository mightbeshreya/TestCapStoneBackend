package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateBusinessService {

    @Autowired
    StateDao stateDao; //Handles all data related to the State.

     /* This method is to get All State data.*/
    public List<StateEntity> getAllStates(){
        List<StateEntity> allStates = stateDao.getAllStates();
        return allStates;
    }

    /* This method is to get State by ID*/
    public StateEntity getStateById(String id) {
        return stateDao.getStateByUuid(id);
    }
}
