package com.example.bordjroject.Service;

import com.example.bordjroject.Entity.login_Entity;
import com.example.bordjroject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void registerUser(String username ,String password) {
        String encPassword = passwordEncoder.encode(password);
        login_Entity user = new login_Entity();
        user.setUsername(username);
        user.setPassword(encPassword);
        userRepository.save(user);
    }


    public boolean login(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }
}