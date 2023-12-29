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

    public void creaSesion(AuthenticationRequest authRequest) {
        Session session = new Session();
        User user = userRepository.findByUsername(authRequest.getUsername()).get();
        session.setUser(user);
        session.setFechaInicioSesion(new Date());
        sessionRepository.save(session);
    }

    public void finSesion(){

    }
}
