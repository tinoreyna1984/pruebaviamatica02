package com.viamatica.backend.service;

import com.viamatica.backend.model.dto.request.AuthenticationRequest;
import com.viamatica.backend.model.entity.Session;
import com.viamatica.backend.model.entity.User;
import com.viamatica.backend.repository.SessionRepository;
import com.viamatica.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private UserRepository userRepository;

    public void creaSesion(String username, String jwt) {
        Session session = new Session();
        User user = userRepository.findByUsername(username).get();
        session.setUser(user);
        session.setJwt(jwt); // paso JWT
        session.setFechaInicioSesion(new Date());
        sessionRepository.save(session);
    }

    public void finSesion(String jwt){
        sessionRepository.agregarFechaFinSesion(jwt);
    }
}
