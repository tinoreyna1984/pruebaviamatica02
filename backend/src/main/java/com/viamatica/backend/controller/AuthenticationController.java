package com.viamatica.backend.controller;

import com.viamatica.backend.model.dto.request.AuthenticationRequest;
import com.viamatica.backend.model.dto.request.RegistrationRequest;
import com.viamatica.backend.model.dto.request.SessionRequest;
import com.viamatica.backend.model.dto.response.AuthenticationResponse;
import com.viamatica.backend.repository.SessionRepository;
import com.viamatica.backend.repository.UserRepository;
import com.viamatica.backend.service.AuthenticationService;
import com.viamatica.backend.service.SessionService;
import com.viamatica.backend.util.JsonSchemaValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {


    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private JsonSchemaValidatorUtil jsonSchemaValidatorUtil;

    @PreAuthorize("permitAll")
    @PostMapping("/authenticate")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authRequest){
        Map<String, Object> response = new HashMap<>();

        try{
            AuthenticationResponse jwtDto = authenticationService.login(authRequest);
            sessionService.creaSesion(authRequest.getUsername(), jwtDto.getJwt()); // paso JWT
            response.put("mensaje", "Se ha dado acceso al usuario");
            response.put("jwt", jwtDto.getJwt());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (AuthenticationException e) {
            response.put("mensaje", "Error en autenticación: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("permitAll")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest registrationRequest){
        Map<String, Object> response = new HashMap<>();
        try{
            AuthenticationResponse jwtDto = authenticationService.register(registrationRequest);
            response.put("mensaje", "El usuario ha sido registrado con éxito");
            response.put("jwt", jwtDto.getJwt());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }catch (DataAccessException e) {
            response.put("mensaje",  "Error al registrar el usuario en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("permitAll")
    @PatchMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody SessionRequest sessionRequest){
        Map<String, Object> response = new HashMap<>();
        String jwt = sessionRequest.getJwt();
        sessionService.finSesion(jwt);
        response.put("mensaje", "Se cierra sesion");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("permitAll")
    @GetMapping("/public-access")
    public String publicAccessEndpoint(){
        return "Este endpoint es público";
    }

}
