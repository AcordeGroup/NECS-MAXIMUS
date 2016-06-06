/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.security.services;

import com.necs.maximus.db.facade.AgentFacade;
import com.necs.maximus.db.entity.Agent;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 *
 * @author Carlos Moh <carlos.moh at necs.com>
 */
public class UserService implements UserDetailsService {

    private AgentFacade agentFacadeEjb;

    public UserService() {
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Agent agent = agentFacadeEjb.find(username);

        if (agent != null) {
            
            //Obtiene la colección de Authorities a partir de la lista de profiles
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            if (agent.getType() != null) {
                authorities.add(new SimpleGrantedAuthority(agent.getType()));
            }
            User userDetails = new User(username, (agent.getPasswordVal() != null) ? agent.getPasswordVal() : "", true, true, true, true, authorities);
            //
            return userDetails;
        } else {
            throw new UsernameNotFoundException("USERNAME " + username + " NOT FOUND");
        }
    }

    /**
     * @param agentFacadeEjb the agentFacadeEjb to set
     */
    public void setAgentFacadeEjb(AgentFacade agentFacadeEjb) {
        this.agentFacadeEjb = agentFacadeEjb;
    }

}
