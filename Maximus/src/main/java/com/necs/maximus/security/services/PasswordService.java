/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.security.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 *
 * @author Carlos Moh <carlos.moh at necs.com>
 */
public class PasswordService implements PasswordEncoder {

    public PasswordService() {
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {     
        System.out.println(encode(rawPassword));
        return new BCryptPasswordEncoder().matches(rawPassword, encodedPassword);
    }
}
