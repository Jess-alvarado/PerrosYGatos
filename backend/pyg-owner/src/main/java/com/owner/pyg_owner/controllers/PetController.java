package com.owner.pyg_owner.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pets")
@Tag(name = "Mascotas", description = "API para gestionar las mascotas de los dueños")
@SecurityRequirement(name = "bearerAuth")
public class PetController {

}