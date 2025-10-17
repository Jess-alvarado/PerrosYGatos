package com.auth.pyg_auth.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.auth.pyg_auth.models.Role;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
