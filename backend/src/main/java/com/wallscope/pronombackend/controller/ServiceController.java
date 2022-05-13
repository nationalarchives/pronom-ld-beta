package com.wallscope.pronombackend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ServiceController {
    Logger logger = LoggerFactory.getLogger(ServiceController.class);

    @GetMapping("/health")
    public Map<String, String> health(Model model) {
        Map<String, String> res = new HashMap<>();
        res.put("status", "available");
        return res;
    }
}
