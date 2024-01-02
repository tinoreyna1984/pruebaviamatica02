package com.viamatica.backend.service;

import com.networknt.schema.ValidationMessage;
import com.viamatica.backend.config.implementation.CustomUserDetails;
import com.viamatica.backend.model.dto.request.AuthenticationRequest;
import com.viamatica.backend.model.dto.request.RegistrationRequest;
import com.viamatica.backend.model.dto.response.AuthenticationResponse;
import com.viamatica.backend.model.entity.Session;
import com.viamatica.backend.model.entity.User;
import com.viamatica.backend.repository.UserRepository;
import com.viamatica.backend.util.JsonSchemaValidatorUtil;
import com.viamatica.backend.util.Role;
import com.viamatica.backend.util.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private JsonSchemaValidatorUtil jsonSchemaValidatorUtil;

    private String crearEmailDesdeNombreUsuario(User user) {
        String[] fullLastName = user.getLastName().toLowerCase().split(" ");
        String emailLastName = "";
        if(fullLastName.length > 1){
            emailLastName = fullLastName[0] + fullLastName[1].charAt(0);
        }
        else emailLastName = user.getLastName().toLowerCase();
        String email = user.getName().toLowerCase().charAt(0) + emailLastName + "@mail.com";
        if(userRepository.findByEmail(email).isPresent()){
            email = user.getName().toLowerCase().charAt(0) + emailLastName + 1 + "@mail.com";
        }
        return email;
    }

    public AuthenticationResponse login(AuthenticationRequest authRequest) {

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(), authRequest.getPassword()
        );

        authenticationManager.authenticate(authToken);

        User user = userRepository.findByUsername(authRequest.getUsername()).get();

        String jwt = jwtService.generateToken(user, generateExtraClaims(user));

        return new AuthenticationResponse(jwt);
    }

    public AuthenticationResponse register(RegistrationRequest registrationRequest){

        // proceso de validación
        String jsonRequest = jsonSchemaValidatorUtil.convertObjectToJson(registrationRequest);
        /*if (!jsonSchemaValidatorUtil.validateJson(jsonRequest, "user-schema.json")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El JSON no cumple con el esquema de validación");
        }*/
        Set<ValidationMessage> validationResult =
                jsonSchemaValidatorUtil.validateJson(jsonRequest, "user-schema.json");
        StringBuilder messages = new StringBuilder();
        if(!validationResult.isEmpty()){
            for(ValidationMessage vm: validationResult){
                messages.append(vm.getMessage()).append("\n");
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, messages.toString());
        }

        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(registrationRequest.getPassword());
        user.setAccessId(registrationRequest.getAccessId());
        user.setName(registrationRequest.getName());
        user.setLastName(registrationRequest.getLastName());
        String email = crearEmailDesdeNombreUsuario(user);
        //user.setEmail(registrationRequest.getEmail());
        user.setEmail(email);
        if(registrationRequest.getRole() == null)
            user.setRole(Role.USER);
        else
            user.setRole(registrationRequest.getRole());
        userRepository.save(user);
        String jwt = jwtService.generateToken(user, generateExtraClaims(user));
        return new AuthenticationResponse(jwt);
    }

    private Map<String, Object> generateExtraClaims(User user) {

        CustomUserDetails customUserDetails = new CustomUserDetails(user); // implementación con clases Custom

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId()); //
        extraClaims.put("name", user.getName());
        extraClaims.put("accessId", user.getAccessId()); //
        extraClaims.put("lastName", user.getLastName()); //
        extraClaims.put("email", user.getEmail()); //
        extraClaims.put("role", user.getRole().name());
        extraClaims.put("failedAttempts", user.getFailedAttempts());
        extraClaims.put("permissions", customUserDetails.getAuthorities());
        extraClaims.put("authorizedRoutes",
                user.getRole()
                        .getRoutes().stream()
                        .map(route -> Map.of("name", route.getName(), "path", route.getPath()))
                        .collect(Collectors.toList())); // rutas
        extraClaims.put("lastSessions", new ArrayList<>(
                user
                .getSessions()
                        .stream().filter(s -> s.getFechaFinSesion() != null)
                        .map(s -> {
                            Session session = new Session();
                            session.setJwt("");
                            session.setId(s.getId());
                            session.setUser(s.getUser());
                            session.setFechaInicioSesion(s.getFechaInicioSesion());
                            session.setFechaFinSesion(s.getFechaFinSesion());
                            return session;
                        })
                .collect(Collectors.toList())
                )
        );

        return extraClaims;
    }
}
