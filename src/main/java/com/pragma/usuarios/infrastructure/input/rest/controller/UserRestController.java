package com.pragma.usuarios.infrastructure.input.rest.controller;

import com.pragma.usuarios.application.dto.request.CreateEmployeeRequest;
import com.pragma.usuarios.application.dto.request.CreateOwnerRequest;
import com.pragma.usuarios.application.dto.response.UserResponse;
import com.pragma.usuarios.application.handler.IUserHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User management API")
public class UserRestController {

    private final IUserHandler userHandler;

    public UserRestController(IUserHandler userHandler) {
        this.userHandler = userHandler;
    }

    @Operation(summary = "Create owner",
            description = "Creates a user account with OWNER role. Only ADMIN can perform this action.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Owner created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden - Only ADMIN role can create owners",
                    content = @Content),
            @ApiResponse(responseCode = "409",
                    description = "User already exists with the provided email or document",
                    content = @Content)
    })
    @PostMapping("/owners")
    public ResponseEntity<UserResponse> createOwner(@Valid @RequestBody CreateOwnerRequest request) {
        UserResponse response = userHandler.createOwner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Create employee",
            description = "Creates a user account with EMPLOYEE role. Only OWNER can perform this action.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Employee created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden - Only OWNER role can create employees",
                    content = @Content),
            @ApiResponse(responseCode = "409",
                    description = "User already exists with the provided email or document",
                    content = @Content)
    })
    @PostMapping("/employees")
    public ResponseEntity<UserResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        UserResponse response = userHandler.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get user by ID",
            description = "Retrieves a user by their ID",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return userHandler.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
