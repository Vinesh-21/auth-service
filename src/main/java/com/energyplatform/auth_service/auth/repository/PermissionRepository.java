package com.energyplatform.auth_service.auth.repository;

import com.energyplatform.auth_service.auth.entity.Permission;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PermissionRepository extends ReactiveMongoRepository<Permission, String> {


    Mono<Boolean> existsByName(String name);

    Flux<Permission> findByNameIn(List<String> names);
}