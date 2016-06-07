/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.ui.beans;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import com.necs.maximus.db.entity.Agent;
import com.necs.maximus.enums.AgentType;
import com.necs.maximus.ui.beans.util.MobilePageController;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

/**
 *
 * @author Carlos Moh
 */
@Named(value = "agentLoginController")
@ViewScoped
public class AgentLoginController extends AbstractController<Agent> {

    @Inject
    private MobilePageController mobilePageController;

    private AuthenticationManager authenticationManager;

    private String username;

    private String password;

    public AgentLoginController() {
        // Inform the Abstract parent controller of the concrete Agent Entity
        super(Agent.class);
        WebApplicationContext ctx = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());
        authenticationManager = ctx.getBean("authenticationManager", AuthenticationManager.class);
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
    
    public String getUserLogged() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName(); //get logged in username
    }
    
    
    public String logout() {
        System.out.println("logout");
        return "/login/login.xhtml";
    }

    public String login() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage message = null;
        boolean loggedIn = false;
        String ret = "fail";

        try {
            if (username != null && password != null) {
                loggedIn = true;
                Authentication request = new UsernamePasswordAuthenticationToken(username, password);
                Authentication result = authenticationManager.authenticate(request);
                SecurityContextHolder.getContext().setAuthentication(result);
                if (result.isAuthenticated()) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", username);
                    if (result.getAuthorities() != null) {
                        String role = ((GrantedAuthority)result.getAuthorities().toArray()[0]).getAuthority();
                        switch (AgentType.valueOf(role)) {
                            case Administrator:
                                ret = "admin";
                                break;
                            case Sales:
                                ret = "sales";
                                break;
                            case Purchasing:
                                ret = "purchasing";
                                break;
                            default:                                
                        }
                    }
                } else {
                    loggedIn = false;
                    message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "Invalid credentials");
                }
            } else {
                loggedIn = false;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "Invalid credentials");
            }
        } catch (BadCredentialsException ex ) {
            loggedIn = false;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "Invalid credentials");
        }
        FacesContext.getCurrentInstance().addMessage(null, message);
        context.addCallbackParam("loggedIn", loggedIn);
        return ret;
    }

    /**
     * @return the authenticationManager
     */
    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    /**
     * @param authenticationManager the authenticationManager to set
     */
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

}
