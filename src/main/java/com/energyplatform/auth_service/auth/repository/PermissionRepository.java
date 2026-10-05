package com.energyplatform.auth_service.auth.repository;

import com.energyplatform.auth_service.auth.entity.Permission;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PermissionRepository extends ReactiveMongoRepository<Permission, String> {
}