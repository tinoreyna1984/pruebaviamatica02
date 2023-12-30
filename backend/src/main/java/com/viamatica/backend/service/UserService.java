package com.viamatica.backend.service;

import com.viamatica.backend.model.dto.request.UserRequest;
import com.viamatica.backend.model.entity.User;
import com.viamatica.backend.repository.UserRepository;
import com.viamatica.backend.util.JsonSchemaValidatorUtil;
import com.viamatica.backend.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // codificador de password
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JsonSchemaValidatorUtil jsonSchemaValidatorUtil;

    private void encriptarClaveUsuario(UserRequest userRequest) {
        String claveEncriptada = passwordEncoder.encode(userRequest.getPassword());
        userRequest.setPassword(claveEncriptada);
    }


    private String crearEmailDesdeNombreUsuario(UserRequest userRequest) {
        String[] fullLastName = userRequest.getLastName().toLowerCase().split(" ");
        String emailLastName = "";
        if(fullLastName.length > 1){
            emailLastName = fullLastName[0] + fullLastName[1].charAt(0);
        }
        else emailLastName = userRequest.getLastName().toLowerCase();
        String email = userRequest.getName().toLowerCase().charAt(0) + emailLastName + "@mail.com";
        if(userRepository.findByEmail(email).isPresent()){
            email = userRequest.getName().toLowerCase().charAt(0) + emailLastName + 1 + "@mail.com";
        }
        return email;
    }

    public ResponseEntity<Object> getUsers(Integer page, Integer size){
        if (page != null && size != null) {
            // Si se proporcionan los parámetros de paginación, devuelve una lista paginada
            Pageable pageable = PageRequest.of(page, size);
            Page<User> pageResult = userRepository.findAll(pageable);
            return ResponseEntity.ok(pageResult);
        } else {
            // Si no se proporcionan los parámetros de paginación, devuelve una lista completa
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(users);
        }
    }

    public ResponseEntity<?> getUser(Long id){
        User usuario = null;
        Map<String, Object> response = new HashMap<>();

        try {
            usuario = userRepository.findById(id).get();
        }catch(DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<User>(usuario, HttpStatus.OK);
    }

    public ResponseEntity<?> saveUser(UserRequest userRequest){
        User usuarioNuevo = new User();
        Map<String, Object> response = new HashMap<>();

        // si no viaja el ROL, por defecto debe ser el de USUARIO
        if(userRequest.getRole() == null)
            userRequest.setRole(Role.USER);

        // proceso de validación
        String jsonRequest = jsonSchemaValidatorUtil.convertObjectToJson(userRequest);
        if (!jsonSchemaValidatorUtil.validateJson(jsonRequest, "user-schema.json")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El JSON no cumple con el esquema de validación");
        }

        // encripta clave
        encriptarClaveUsuario(userRequest);

        usuarioNuevo.setName(userRequest.getName());
        usuarioNuevo.setLastName(userRequest.getLastName());
        usuarioNuevo.setEmail(userRequest.getEmail());
        usuarioNuevo.setUsername(userRequest.getUsername());
        usuarioNuevo.setPassword(userRequest.getPassword());
        usuarioNuevo.setRole(userRequest.getRole());

        try {
            String email = crearEmailDesdeNombreUsuario(userRequest);
            userRequest.setEmail(email);
            usuarioNuevo = userRepository.save(usuarioNuevo);
        } catch(DataAccessException e) {
            response.put("mensaje", "Error al realizar el insert en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El usuario ha sido creado con éxito");
        response.put("usuario", usuarioNuevo);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateUser(UserRequest userRequest, Long id){
        User usuarioActual = userRepository.findById(id).get();
        User usuarioEditado = null;
        Map<String, Object> response = new HashMap<>();

        // proceso de validación
        String jsonRequest = jsonSchemaValidatorUtil.convertObjectToJson(userRequest);
        if (!jsonSchemaValidatorUtil.validateJson(jsonRequest, "user-schema.json")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El JSON no cumple con el esquema de validación");
        }

        try {
            usuarioActual.setName(userRequest.getName());
            usuarioActual.setLastName(userRequest.getLastName());
            usuarioActual.setEmail(userRequest.getEmail());
            usuarioActual.setUsername(userRequest.getUsername());
            // encripta clave
            encriptarClaveUsuario(userRequest);
            usuarioActual.setPassword(userRequest.getPassword());
            // si no viaja el ROL, por defecto debe ser el de USUARIO
            if(userRequest.getRole() == null)
                usuarioActual.setRole(Role.USER);
            else
                usuarioActual.setRole(userRequest.getRole());
            usuarioEditado = userRepository.save(usuarioActual);
        } catch(DataAccessException e) {
            response.put("mensaje", "Error al realizar el update en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El usuario ha sido editado con éxito");
        response.put("usuario", usuarioEditado);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.ACCEPTED);
    }

    public ResponseEntity<?> deleteUser(Long id){
        Map<String, Object> response = new HashMap<>();

        try {
            userRepository.deleteById(id);
        }catch(DataAccessException e) {
            response.put("mensaje", "Error al realizar el delete en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El usuario ha sido eliminado con éxito");

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.ACCEPTED);
    }

    public ResponseEntity<?> dashboard(){
        Map<String, Object> response = new HashMap<>();
        long totalUsers = 0L;
        long activeUsers = 0L;
        long lockedUsers = 0L;
        try {
            totalUsers = userRepository.totalUsers();
            activeUsers = userRepository.totalActiveUsers();
            lockedUsers = userRepository.totalLockedUsers();
            response.put("total", totalUsers);
            response.put("activos", activeUsers);
            response.put("bloqueados", lockedUsers);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        }catch(DataAccessException e) {
            response.put("mensaje", "Error al realizar el delete en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
