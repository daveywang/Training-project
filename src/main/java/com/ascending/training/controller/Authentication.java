/*
 *  Copyright 2019, Liwei Wang <daveywang@live.com>.
 *  All rights reserved.
 *  Author: Liwei Wang
 *  Date: 06/2019
 */

package com.ascending.training.controller;

import com.ascending.training.exception.AuthenticationException;
import com.ascending.training.exception.UserNotFoundException;
import com.ascending.training.model.User;
import com.ascending.training.service.UserService;
import com.ascending.training.util.JwtUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
//@Scope(value = WebApplicationContext.SCOPE_APPLICATION)
//@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequestMapping(value = {"/auth"})
public class Authentication {
    @Autowired private Logger logger;
    @Autowired private UserService userService;
    private String errorMsg = "The email or password is not correct.";
    private String tokenKeyWord = "Authorization";
    private String tokenType = "Bearer";

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity authenticate(@RequestBody User user) {
        String token = "";

        try {
            logger.debug(user.toString());
            User u = userService.getUserByCredentials(user.getEmail(), user.getPassword());
            if (u == null) throw new UserNotFoundException(user);
            token = JwtUtil.generateToken(u);
        }
        catch (UserNotFoundException userNotFoundexception) {
            throw userNotFoundexception;
        }
        catch (Exception e) {
            throw new AuthenticationException(e.getMessage(), user);
        }

        Map<String, String> map = new HashMap();
        map.put(tokenKeyWord, tokenType + " " + token);
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    @RequestMapping(value = "/token", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity authenticate(@RequestParam String email, @RequestParam String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        return authenticate(user);
    }
}
