package com.capstone.user.service;

import com.capstone.user.db.UserRepository;
import com.capstone.user.model.UserDTO;
import com.capstone.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Getter
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        // SELLER인 경우 businessNumber와 businessName 필수 확인
        if ("SELLER".equalsIgnoreCase(userDTO.getRole())) {
            if (userDTO.getBusinessNumber() == null || userDTO.getBusinessNumber().isBlank() ||
                    userDTO.getBusinessName() == null || userDTO.getBusinessName().isBlank()) {
                throw new IllegalArgumentException("Business details must be provided for SELLER role");
            }
        }

        User user = UserConverter.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return UserConverter.toDTO(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserDtoById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        return UserConverter.toDTO(user);
    }

    public User getUser(String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        if(user.isPresent()) {
            return user.get();
        } else {
            throw new RuntimeException("user not found");
        }
    }
}
