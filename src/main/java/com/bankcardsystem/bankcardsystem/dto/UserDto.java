package com.bankcardsystem.bankcardsystem.dto;

import com.bankcardsystem.bankcardsystem.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private Role role;
}
