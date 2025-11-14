package com.owner.pyg_owner.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.owner.pyg_owner.models.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    Optional<Pet> findByIdAndOwnerUserId(Long petId, Long userId);
}
