package com.pm.employeeservice.ConfigTests;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyController {
    @GetMapping("/api/v1/family")
    public String dummyGet() { return "OK"; }

    @PostMapping("/api/v1/login")
    public String dummyLogin() { return "OK"; }
}
