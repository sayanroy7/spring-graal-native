package com.example.jafu.controller;

import com.example.jafu.model.Message;
import com.example.jafu.model.Metadata;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author SAROY on 1/15/2020
 */
@RestController
public class JafuApplicationRestController {

    @RequestMapping(path = "/text", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String getTextPlain() {
        return "This is text/plain message from Jafu RestController";
    }

    @RequestMapping(path = "/json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Message getMessageJson() {
        Message message = new Message();
        message.setId("message1");
        message.setSerial(1234567);
        message.setCode(4L);
        message.setVersion(0.1258D);

        Metadata metadata = new Metadata();
        metadata.setId(2131354);
        metadata.setName("metadata1");
        metadata.setValues(Collections.singletonMap("type", "metadata"));
        message.setMetadata(metadata);

        Map<String, String> props = new HashMap<>();
        props.put("type", "message");
        props.put("status", "approved");
        message.setProperties(props);

        return message;
    }


}
