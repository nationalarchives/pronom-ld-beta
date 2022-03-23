package com.wallscope.pronombackend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ServiceController {
    @GetMapping("/health")
    public Map<String, String> health(Model model) {
        Map<String, String> res = new HashMap<>();
        res.put("status", "available");
        return res;
    }

}
