package org.example.burtyserver.domain.user.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.auth.service.AuthService;
import org.example.burtyserver.domain.user.model.entity.User;
import org.example.burtyserver.domain.user.model.repository.UserRepository;
import org.example.burtyserver.global.security.UserPrincipal;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthService authService;

    @Transactional
    public User updateUserProfile(Long userId, String nickname, String region, LocalDate birthDate, String job){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        user.updateProfile(nickname, region, birthDate, job);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(Long userId, HttpServletRequest request, HttpServletResponse response) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        System.out.println("탈퇴 전: " + user.getName() + ", " + user.getProfileImageUrl());

        // 사용자 비활성화
        user = user.deactivateAccount();
        System.out.println("탈퇴 후: " + user.getName() + ", " + user.getProfileImageUrl());

        userRepository.save(user);

        authService.logout(UserPrincipal.create(user), request, response);
    }
}
