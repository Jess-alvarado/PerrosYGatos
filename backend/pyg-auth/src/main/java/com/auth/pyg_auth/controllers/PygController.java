package com.auth.pyg_auth.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PygController {

    @Operation(summary = "Probar endpoint protegido", description = "Devuelve 200 OK si el token JWT es válido.")
    @PostMapping(value = "/test")
    public String test() {
        return "Pyg works!";
    }
}