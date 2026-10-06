package com.energyplatform.auth_service.auth.service;

import com.energyplatform.auth_service.auth.entity.Permission;
import com.energyplatform.auth_service.auth.entity.Role;
import com.energyplatform.auth_service.auth.repository.PermissionRepository;
import com.energyplatform.auth_service.auth.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;


    // =========================
    // FIND ALL ROLES
    // =========================

    public Flux<Role> findAllRoles() {
        return roleRepository.findAll();
    }


    // =========================
    // FIND ROLE
    // =========================

    public Mono<Role> findRole(String id) {

        return roleRepository.findById(id)
                .switchIfEmpty(
                        Mono.error(
                                new IllegalArgumentException(
                                        "Role not found: " + id
                                )
                        )
                );
    }


    // =========================
    // CREATE ROLE
    // =========================

    public Mono<Role> createRole(Role role) {

        return validatePermissions(role.getPermissions())

                .then(
                        roleRepository.existsByName(role.getName())
                )

                .flatMap(exists -> {

                    if (exists) {
                        return Mono.error(
                                new IllegalArgumentException(
                                        "Role already exists: "
                                                + role.getName()
                                )
                        );
                    }

                    // Roles created through the API
                    // are always non-system roles.
                    role.setSystemRole(false);

                    return roleRepository.save(role);
                });
    }


    // =========================
    // UPDATE ROLE
    // =========================

    public Mono<Role> updateRole(
            String id,
            Role updatedRole
    ) {

        return roleRepository.findById(id)

                .switchIfEmpty(
                        Mono.error(
                                new IllegalArgumentException(
                                        "Role not found: " + id
                                )
                        )
                )

                .flatMap(existingRole -> {

                    // System roles cannot be modified
                    if (Boolean.TRUE.equals(
                            existingRole.getSystemRole()
                    )) {

                        return Mono.error(
                                new IllegalArgumentException(
                                        "System role cannot be modified: "
                                                + existingRole.getName()
                                )
                        );
                    }

                    // Validate permissions first
                    return validatePermissions(
                            updatedRole.getPermissions()
                    )

                            // Check duplicate role name
                            .then(
                                    roleRepository.existsByName(
                                            updatedRole.getName()
                                    )
                            )

                            .flatMap(exists -> {

                                /*
                                 * If the name exists and belongs
                                 * to another role, reject it.
                                 */
                                if (exists &&
                                        !existingRole.getName()
                                                .equals(updatedRole.getName())) {

                                    return Mono.error(
                                            new IllegalArgumentException(
                                                    "Role name already exists: "
                                                            + updatedRole.getName()
                                            )
                                    );
                                }

                                // Update editable fields
                                existingRole.setName(
                                        updatedRole.getName()
                                );

                                existingRole.setPermissions(
                                        updatedRole.getPermissions()
                                );

                                /*
                                 * Do NOT update systemRole.
                                 *
                                 * Existing database value remains unchanged.
                                 */

                                return roleRepository.save(existingRole);
                            });
                });
    }


    // =========================
    // VALIDATE PERMISSIONS
    // =========================

    private Mono<Void> validatePermissions(List<String> permissions) {

        if (permissions == null || permissions.isEmpty()) {
            return Mono.empty();
        }

        return permissionRepository
                .findByNameIn(permissions)
                .map(Permission::getName)
                .collectList()
                .flatMap(existingPermissions -> {

                    Set<String> missingPermissions =
                            new HashSet<>(permissions);

                    missingPermissions.removeAll(
                            new HashSet<>(existingPermissions)
                    );

                    if (!missingPermissions.isEmpty()) {
                        return Mono.error(
                                new IllegalArgumentException(
                                        "Permission(s) not found: "
                                                + missingPermissions
                                )
                        );
                    }

                    return Mono.empty();
                });
    }

    // =========================
// DELETE ROLE
// =========================

    public Mono<Void> deleteRole(String id) {

        return roleRepository.findById(id)
                .switchIfEmpty(
                        Mono.error(
                                new IllegalArgumentException(
                                        "Role not found: " + id
                                )
                        )
                )
                .flatMap(role -> {

                    if (Boolean.TRUE.equals(role.getSystemRole())) {
                        return Mono.error(
                                new IllegalArgumentException(
                                        "System role cannot be deleted: "
                                                + role.getName()
                                )
                        );
                    }

                    return roleRepository.delete(role);
                });
    }
}