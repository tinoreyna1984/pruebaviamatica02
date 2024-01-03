package com.viamatica.backend.service;

import com.networknt.schema.ValidationMessage;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // codificador de password
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JsonSchemaValidatorUtil jsonSchemaValidatorUtil;

    private void encriptarClaveUserRequest(UserRequest userRequest) {
        String claveEncriptada = passwordEncoder.encode(userRequest.getPassword());
        userRequest.setPassword(claveEncriptada);
    }

    private void encriptarClaveUsuario(User user) {
        String claveEncriptada = passwordEncoder.encode(user.getPassword());
        user.setPassword(claveEncriptada);
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

    public ResponseEntity<?> getUsers(Integer page, Integer size){
        Map<String, Object> response = new HashMap<>();
        try{
            if (page != null && size != null) {
                // Si se proporcionan los parámetros de paginación, devuelve una lista paginada
                Pageable pageable = PageRequest.of(page, size);
                Page<User> pageResult = userRepository.findAll(pageable);
                //return ResponseEntity.ok(pageResult);
                response.put("mensaje", "La lista de usuarios se consultó con éxito");
                response.put("usuarios", pageResult);
            } else {
                // Si no se proporcionan los parámetros de paginación, devuelve una lista completa
                List<User> users = userRepository.findAll();
                //return ResponseEntity.ok(users);
                response.put("mensaje", "La lista de usuarios se consultó con éxito");
                response.put("usuarios", users);
            }
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        } catch (DataAccessException e){
            response.put("mensaje", "Error al realizar la consulta en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getUser(Long id){
        User usuario = null;
        Map<String, Object> response = new HashMap<>();

        try {
            usuario = userRepository.findById(id).get();
        }catch(DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Se encontró el usuario solicitado");
        response.put("usuario", usuario);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> saveUser(UserRequest userRequest){
        User usuarioNuevo = new User();
        Map<String, Object> response = new HashMap<>();

        // si no viaja el ROL, por defecto debe ser el de USUARIO
        if(userRequest.getRole() == null)
            userRequest.setRole(Role.USER);

        // proceso de validación
        String jsonRequest = jsonSchemaValidatorUtil.convertObjectToJson(userRequest);
        Set<ValidationMessage> validationResult =
                jsonSchemaValidatorUtil.validateJson(jsonRequest, "user-schema.json");
        StringBuilder messages = new StringBuilder();
        if(!validationResult.isEmpty()){
            for(ValidationMessage vm: validationResult){
                messages.append(vm.getMessage()).append("\n");
            }
            response.put("mensaje", messages.toString());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        // encripta clave
        encriptarClaveUserRequest(userRequest);
        // crea email a partir de datos del usuario
        userRequest.setEmail(crearEmailDesdeNombreUsuario(userRequest));

        usuarioNuevo.setName(userRequest.getName());
        usuarioNuevo.setLastName(userRequest.getLastName());
        usuarioNuevo.setEmail(userRequest.getEmail());
        usuarioNuevo.setAccessId(userRequest.getAccessId());
        usuarioNuevo.setUsername(userRequest.getUsername());
        usuarioNuevo.setPassword(userRequest.getPassword());
        usuarioNuevo.setRole(userRequest.getRole());

        try {
            usuarioNuevo = userRepository.save(usuarioNuevo);
        } catch(DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El usuario ha sido creado con éxito");
        response.put("usuario", usuarioNuevo);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateUser(UserRequest userRequest, Long id){

        Map<String, Object> response = new HashMap<>();

        // proceso de validación
        String jsonRequest = jsonSchemaValidatorUtil.convertObjectToJson(userRequest);
        Set<ValidationMessage> validationResult =
                jsonSchemaValidatorUtil.validateJson(jsonRequest, "user-schema.json");
        StringBuilder messages = new StringBuilder();
        if(!validationResult.isEmpty()){
            for(ValidationMessage vm: validationResult){
                messages.append(vm.getMessage()).append("\n");
            }
            response.put("mensaje", messages.toString());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        User usuarioActual = userRepository.findById(id).get();
        User usuarioEditado = null;
        try {
            usuarioActual.setName(userRequest.getName());
            usuarioActual.setLastName(userRequest.getLastName());
            usuarioActual.setEmail(userRequest.getEmail());
            usuarioActual.setUsername(userRequest.getUsername());
            usuarioActual.setAccessId(userRequest.getAccessId());
            usuarioActual.setPassword(userRequest.getPassword());
            // si no viaja el ROL, por defecto debe ser el de USUARIO
            if(userRequest.getRole() == null)
                usuarioActual.setRole(Role.USER);
            else
                usuarioActual.setRole(userRequest.getRole());
            // encripta clave
            encriptarClaveUsuario(usuarioActual);
            usuarioEditado = userRepository.save(usuarioActual);
        } catch(DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
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
            response.put("mensaje", "Error al realizar la consulta en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
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
            response.put("mensaje", "Error al realizar la consulta en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void cargarDesdeCSV(MultipartFile archivo) throws IOException {
        try (
             Reader reader = new InputStreamReader(archivo.getInputStream());
             CSVReader csvReader = new CSVReaderBuilder(reader).build()
        ) {
            List<String[]> filas = csvReader.readAll();

            for (String[] fila : filas) {
                UserRequest userRequest = pasarValores(fila);
                User user = new User();
                user.setUsername(userRequest.getUsername());
                encriptarClaveUserRequest(userRequest);
                user.setPassword(userRequest.getPassword());
                user.setAccessId(userRequest.getAccessId());
                user.setEmail(userRequest.getEmail());
                user.setName(userRequest.getName());
                user.setLastName(userRequest.getLastName());
                user.setRole(userRequest.getRole());
                userRepository.save(user);
            }
        } catch (CsvException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e){
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error de acceso a datos");
        }
    }

    private UserRequest pasarValores(String[] fila) {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername(fila[0]);
        userRequest.setPassword(fila[1]);
        userRequest.setAccessId(fila[2]);
        userRequest.setEmail(fila[3]);
        userRequest.setName(fila[4]);
        userRequest.setLastName(fila[5]);
        userRequest.setRole(Role.valueOf(fila[6]));
        return userRequest;
    }
}
