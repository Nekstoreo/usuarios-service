package com.pragma.usuarios.infrastructure.output.jpa.adapter;

import com.pragma.usuarios.domain.model.Role;
import com.pragma.usuarios.domain.spi.IRolePersistencePort;
import com.pragma.usuarios.infrastructure.output.jpa.mapper.RoleEntityMapper;
import com.pragma.usuarios.infrastructure.output.jpa.repository.IRoleRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RoleJpaAdapter implements IRolePersistencePort {

    private final IRoleRepository roleRepository;
    private final RoleEntityMapper roleEntityMapper;

    public RoleJpaAdapter(IRoleRepository roleRepository, RoleEntityMapper roleEntityMapper) {
        this.roleRepository = roleRepository;
        this.roleEntityMapper = roleEntityMapper;
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id)
                .map(roleEntityMapper::toModel);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name)
                .map(roleEntityMapper::toModel);
    }
}
