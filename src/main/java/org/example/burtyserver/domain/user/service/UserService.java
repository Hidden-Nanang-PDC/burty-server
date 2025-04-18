package org.example.burtyserver.domain.user.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.user.model.entity.User;
import org.example.burtyserver.domain.user.model.repository.UserRepository;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User updateUserProfile(Long userId, String nickname, String region, Integer age){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        user.updateProfile(nickname, region, age);
        return userRepository.save(user);
    }
}
