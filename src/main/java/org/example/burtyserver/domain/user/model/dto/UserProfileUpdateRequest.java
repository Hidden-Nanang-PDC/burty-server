package org.example.burtyserver.domain.user.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateRequest {
    private String nickname;
    private String region;
    private Integer age;
}
