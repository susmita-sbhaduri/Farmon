/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.bhaduri.farmon.controller;

import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.application.NavigationHandler;
import java.io.Serializable;
import javax.naming.NamingException;

import org.farmon.farmonclient.FarmonClient;
import org.farmon.farmondto.FarmonDTO;
import org.farmon.farmondto.UserDTO;

/**
 *
 * @author sb
 */
@Named(value = "loginController")
@SessionScoped
public class LoginController implements Serializable {
    private String username;
    private String password;
    private FarmonDTO farmonDTO;
    /**
     * Creates a new instance of LoginController
     */
    public LoginController() {
    }  
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String login () throws NamingException {
        farmonDTO = new FarmonDTO();
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setPassword(password);
        
        
        FarmonClient clientService = new FarmonClient();
        farmonDTO.setUserDto(userDTO);
        farmonDTO = clientService.callLoginAuthService(farmonDTO);
        

        if(farmonDTO.getUserDto().getID().equals("null")){
            return "landing?faces-redirect=true";
        } else return "/secured/userhome?faces-redirect=true";
//        if (username.equals("susmita")) {
//            if (password.equals("bumbu123")) {
//                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", username);
//                return "/secured/empty?faces-redirect=true";
//            }
//        }
//        return "landing?faces-redirect=true";
    }
    public void logout () {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        
        // Reset username and password
        this.username = null;
        this.password = null;
        // Redirect to login page
        NavigationHandler nav = FacesContext.getCurrentInstance().getApplication().getNavigationHandler();
        nav.handleNavigation(FacesContext.getCurrentInstance(), null, "/login.xhtml?faces-redirect=true");
    }
}
