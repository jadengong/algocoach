package com.algocoach.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class SimpleController {

    @GetMapping("/simple")
    public String simple() {
        return "Simple endpoint working!";
    }
}
