package com.viamatica.backend.config.implementation;

import com.viamatica.backend.model.entity.User;
import com.viamatica.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// implementación con clases Custom
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    //!! No usar @Autowired, generar constructor con userRepository
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).get();
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomUserDetails(user); // implementación con clases Custom
    }
}
