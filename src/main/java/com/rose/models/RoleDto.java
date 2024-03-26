package com.rose.models;

import com.rose.entities.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * A DTO for the {@link com.rose.entities.Role} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto implements Serializable {
    private Integer id;
    private ERole name;
}