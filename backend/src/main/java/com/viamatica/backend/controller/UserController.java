package com.viamatica.backend.controller;

import com.viamatica.backend.model.dto.request.UserRequest;
import com.viamatica.backend.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR','ROLE_USER')")
    @GetMapping("/users")
    // devuelve una lista completa o paginada si viajan parámetros de paginación
    public ResponseEntity<Object> listarUsuarios(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return userService.getUsers(page, size);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR','ROLE_USER')")
    @GetMapping("/users/{id}")
    public ResponseEntity<?> buscarUsuario(@PathVariable Long id){
        return userService.getUser(id);
    }


    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    @PostMapping("/users")
    public ResponseEntity<?> guardarUsuario(@RequestBody UserRequest userRequest){
        return userService.saveUser(userRequest);
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    @PutMapping("/users/{id}")
    public ResponseEntity<?> editarUsuario(@RequestBody UserRequest userRequest, @PathVariable Long id){
        return userService.updateUser(userRequest, id);
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> borrarUsuario(@PathVariable Long id){
        return userService.deleteUser(id);
    }
}
