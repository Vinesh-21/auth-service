package com.energyplatform.auth_service.auth.controller;

import com.energyplatform.auth_service.auth.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.energyplatform.auth_service.auth.entity.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/auth/role")
@AllArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public Flux<Role> getAllRoles(){
        return roleService.findAllRoles();
    }

    @GetMapping("/{Id}")
    public Mono<Role> getAllRoles(@PathVariable String Id){
        return roleService.findRole(Id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Role> createRole(@RequestBody Role role){

        return roleService.createRole(role);

    }


    @PutMapping("/{id}")
    public Mono<Role> updateRole(
            @PathVariable String id,
            @RequestBody Role role
    ) {
        return roleService.updateRole(id, role);
    }


    @DeleteMapping("/{id}")
    public Mono<Void> deleteRole(
            @PathVariable String id
    ) {
        return roleService.deleteRole(id);
    }




}
