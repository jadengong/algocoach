package com.algocoach.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "Test endpoint working!";
    }

    @PostMapping("/echo")
    public String echo(@RequestBody String body) {
        return "Echo: " + body;
    }
}
