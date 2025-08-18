/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.farmon.controller;

import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author sb
 */
@Named(value = "userAuthentication")
@SessionScoped
public class UserAuthentication implements Serializable {
 private String name;
    /**
     * Creates a new instance of UserAuthentication
     */
    public UserAuthentication() {
        name = "susmita";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
