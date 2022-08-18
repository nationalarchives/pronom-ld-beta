package com.wallscope.pronombackend.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

@Controller
public class CustomErrorController implements ErrorController {
    Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletResponse response) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");

        if (response.getStatus() == HttpStatus.NOT_FOUND.value()) {
            modelAndView.addObject("code", 404);
            modelAndView.addObject("name", "Page not found");
            modelAndView.addObject("message", "The page you are looking for might have been removed.");
        } else if (response.getStatus() == HttpStatus.FORBIDDEN.value()) {
            modelAndView.addObject("code", 403);
            modelAndView.addObject("name", "Forbidden");
            modelAndView.addObject("message", "Your level of access does not allow you to see this page. Contact an administrator.");
        } else if (response.getStatus() == HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()) {
            modelAndView.addObject("code", 415);
            modelAndView.addObject("name", "Unsupported media");
            modelAndView.addObject("message", "Unsupported media. This usually happens when trying to export an unsupported RDF format. Try one of: .rdf .ttl .nt");
        } else if (response.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            modelAndView.addObject("code", 500);
            modelAndView.addObject("name", "Something went wrong");
            modelAndView.addObject("message", "An error has occurred and we're working on it.");
        }

        return modelAndView;
    }

}
