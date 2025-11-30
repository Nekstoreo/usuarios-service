package com.pragma.usuarios.infrastructure.output.jpa.adapter;

import com.pragma.usuarios.domain.model.Role;
import com.pragma.usuarios.infrastructure.output.jpa.entity.RoleEntity;
import com.pragma.usuarios.infrastructure.output.jpa.mapper.RoleEntityMapper;
import com.pragma.usuarios.infrastructure.output.jpa.repository.IRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleJpaAdapterTest {

    private static final Long ROLE_ID = 1L;
    private static final String ROLE_NAME = "OWNER";
    private static final String ROLE_DESCRIPTION = "Restaurant owner";

    @Mock
    private IRoleRepository roleRepository;

    @Mock
    private RoleEntityMapper roleEntityMapper;

    @InjectMocks
    private RoleJpaAdapter roleJpaAdapter;

    private Role role;
    private RoleEntity roleEntity;

    @BeforeEach
    void setUp() {
        role = new Role(ROLE_ID, ROLE_NAME, ROLE_DESCRIPTION);
        roleEntity = new RoleEntity(ROLE_ID, ROLE_NAME, ROLE_DESCRIPTION);
    }

    @Nested
    @DisplayName("Find By Id Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should find role by id when exists")
        void shouldFindRoleByIdWhenExists() {
            // Arrange
            when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(roleEntity));
            when(roleEntityMapper.toModel(roleEntity)).thenReturn(role);

            // Act
            Optional<Role> result = roleJpaAdapter.findById(ROLE_ID);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(ROLE_ID, result.get().getId());
            assertEquals(ROLE_NAME, result.get().getName());
            verify(roleRepository).findById(ROLE_ID);
            verify(roleEntityMapper).toModel(roleEntity);
        }

        @Test
        @DisplayName("Should return empty when role not found by id")
        void shouldReturnEmptyWhenRoleNotFoundById() {
            // Arrange
            when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.empty());

            // Act
            Optional<Role> result = roleJpaAdapter.findById(ROLE_ID);

            // Assert
            assertTrue(result.isEmpty());
            verify(roleRepository).findById(ROLE_ID);
            verify(roleEntityMapper, never()).toModel(any());
        }
    }

    @Nested
    @DisplayName("Find By Name Tests")
    class FindByNameTests {

        @Test
        @DisplayName("Should find role by name when exists")
        void shouldFindRoleByNameWhenExists() {
            // Arrange
            when(roleRepository.findByName(ROLE_NAME)).thenReturn(Optional.of(roleEntity));
            when(roleEntityMapper.toModel(roleEntity)).thenReturn(role);

            // Act
            Optional<Role> result = roleJpaAdapter.findByName(ROLE_NAME);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(ROLE_NAME, result.get().getName());
            assertEquals(ROLE_DESCRIPTION, result.get().getDescription());
            verify(roleRepository).findByName(ROLE_NAME);
            verify(roleEntityMapper).toModel(roleEntity);
        }

        @Test
        @DisplayName("Should return empty when role not found by name")
        void shouldReturnEmptyWhenRoleNotFoundByName() {
            // Arrange
            when(roleRepository.findByName(ROLE_NAME)).thenReturn(Optional.empty());

            // Act
            Optional<Role> result = roleJpaAdapter.findByName(ROLE_NAME);

            // Assert
            assertTrue(result.isEmpty());
            verify(roleRepository).findByName(ROLE_NAME);
            verify(roleEntityMapper, never()).toModel(any());
        }

        @Test
        @DisplayName("Should find different roles by name")
        void shouldFindDifferentRolesByName() {
            // Arrange
            String adminRoleName = "ADMIN";
            RoleEntity adminRoleEntity = new RoleEntity(2L, adminRoleName, "System administrator");
            Role adminRole = new Role(2L, adminRoleName, "System administrator");
            
            when(roleRepository.findByName(adminRoleName)).thenReturn(Optional.of(adminRoleEntity));
            when(roleEntityMapper.toModel(adminRoleEntity)).thenReturn(adminRole);

            // Act
            Optional<Role> result = roleJpaAdapter.findByName(adminRoleName);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(adminRoleName, result.get().getName());
            verify(roleRepository).findByName(adminRoleName);
        }
    }
}
