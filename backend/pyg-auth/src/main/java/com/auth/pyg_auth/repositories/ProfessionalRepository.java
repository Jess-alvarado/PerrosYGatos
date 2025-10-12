package com.auth.pyg_auth.repositories;
import com.auth.pyg_auth.models.Professional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessionalRepository extends CrudRepository<Professional, Long> {
    Professional findByEmail(String email);
}