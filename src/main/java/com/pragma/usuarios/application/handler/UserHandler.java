package com.pragma.usuarios.application.handler;

import com.pragma.usuarios.application.dto.request.CreateOwnerRequest;
import com.pragma.usuarios.application.dto.response.UserResponse;
import com.pragma.usuarios.application.mapper.UserRequestMapper;
import com.pragma.usuarios.application.mapper.UserResponseMapper;
import com.pragma.usuarios.domain.api.IUserServicePort;
import com.pragma.usuarios.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserHandler implements IUserHandler {

    private final IUserServicePort userServicePort;
    private final UserRequestMapper userRequestMapper;
    private final UserResponseMapper userResponseMapper;

    public UserHandler(IUserServicePort userServicePort,
                       UserRequestMapper userRequestMapper,
                       UserResponseMapper userResponseMapper) {
        this.userServicePort = userServicePort;
        this.userRequestMapper = userRequestMapper;
        this.userResponseMapper = userResponseMapper;
    }

    @Override
    public UserResponse createOwner(CreateOwnerRequest createOwnerRequest) {
        User user = userRequestMapper.toUser(createOwnerRequest);
        User savedUser = userServicePort.createOwner(user);
        return userResponseMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserById(Long id) {
        return userServicePort.getUserById(id)
                .map(userResponseMapper::toResponse);
    }
}
