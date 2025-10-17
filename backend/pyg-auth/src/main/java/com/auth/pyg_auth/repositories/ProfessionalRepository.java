package com.auth.pyg_auth.repositories;
import com.auth.pyg_auth.models.Professional;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, Long> {
    Professional findByEmail(String email);
}