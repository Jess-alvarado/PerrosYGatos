package com.auth.pyg_auth.repositories;
import com.auth.pyg_auth.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends CrudRepository<User, Long> {
    User findByUsername(String userName);
}