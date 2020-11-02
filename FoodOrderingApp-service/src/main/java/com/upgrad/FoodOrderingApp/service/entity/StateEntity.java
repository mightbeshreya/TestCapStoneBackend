package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "state")
@NamedQueries({
        @NamedQuery(name = "getStateByUUID", query = "SELECT  s from StateEntity s where s.uuid = :uuid"),
        @NamedQuery(name = "getStateById", query = "SELECT s from StateEntity s where s.id = :id"),
        @NamedQuery(name = "getAllStates", query = "SELECT s from StateEntity s")
})
public class StateEntity {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 200)
    private String uuid;

    @Column(name = "state_name")
    @Size(max = 30)
    private String state_name;

    public StateEntity() {}

    public StateEntity(@NotNull @Size(max = 200) String uuid, @Size(max = 30) String stateName) {
        this.uuid = uuid;
        this.state_name = stateName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStateName() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }
}
