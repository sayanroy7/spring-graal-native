package com.example.jafu.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @author SAROY on 1/15/2020
 */
@Controller
public class JafuApplicationController {

    @RequestMapping(path = "/welcome", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String welcome() {
        return "Welcome to Jafu MVC Controller. The time is: "+ new Date();
    }

}
