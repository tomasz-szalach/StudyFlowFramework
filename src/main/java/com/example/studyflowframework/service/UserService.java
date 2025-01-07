package com.example.studyflowframework.service;

import com.example.studyflowframework.model.User;
import com.example.studyflowframework.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByEmail(String email) throws Exception {
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) {
            throw new Exception("NoMatchingRecordException: user not found for email " + email);
        }
        return opt.get();
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(Long userId, String newPassword) {
        userRepository.updatePassword(userId, newPassword);
    }

    public boolean ifContainsEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
