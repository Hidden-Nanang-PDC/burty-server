package org.example.burtyserver.domain.user.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserProfileUpdateRequest {
    private String nickname;
    private String region;
    private LocalDate birthDate;
}
