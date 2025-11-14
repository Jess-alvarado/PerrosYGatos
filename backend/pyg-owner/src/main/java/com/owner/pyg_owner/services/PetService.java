package com.owner.pyg_owner.services;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.owner.pyg_owner.clients.AuthServiceClient;
import com.owner.pyg_owner.dto.requests.PetRequest;
import com.owner.pyg_owner.dto.responses.PetResponse;
import com.owner.pyg_owner.models.OwnerProfile;
import com.owner.pyg_owner.models.Pet;
import com.owner.pyg_owner.models.PetType;
import com.owner.pyg_owner.repositories.OwnerRepository;
import com.owner.pyg_owner.repositories.PetRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PetService {

        private final PetRepository petRepo;
        private final OwnerRepository ownerRepo;
        private final AuthServiceClient authServiceClient;

        public PetService(PetRepository petRepo, OwnerRepository ownerRepo, AuthServiceClient authServiceClient) {
                this.petRepo = petRepo;
                this.ownerRepo = ownerRepo;
                this.authServiceClient = authServiceClient;
        }

        /**
         * Add a new pet for the authenticated owner
         */
        @Transactional
        public PetResponse addPet(String bearerToken, PetRequest req) {
                // 1) Validate token and get userId
                var authRes = authServiceClient.validateToken(bearerToken);
                Long userId = authRes.getUserId();

                // 2) Find owner profile
                OwnerProfile owner = ownerRepo.findByUserId(userId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Owner profile not found. Please complete your profile first."));

                // 3) Create pet and associate with owner
                Pet pet = Pet.builder()
                                .name(req.name())
                                .type(PetType.valueOf(req.type().toUpperCase(Locale.ROOT)))
                                .breed(req.breed())
                                .age(req.age())
                                .sterilized(req.sterilized())
                                .sex(req.sex())
                                .behaviorDescription(req.behaviorDescription())
                                .owner(owner)
                                .build();

                @SuppressWarnings("null")
                Pet saved = petRepo.save(pet);

                return new PetResponse(
                                saved.getId(),
                                saved.getName(),
                                saved.getType().name(),
                                saved.getBreed(),
                                saved.getAge(),
                                saved.getSterilized(),
                                saved.getSex(),
                                saved.getBehaviorDescription());
        }

        /**
         * Get all pets for the authenticated owner
         */
        @Transactional(readOnly = true)
        public List<PetResponse> getPetsByOwner(String bearerToken) {
                var authRes = authServiceClient.validateToken(bearerToken);
                Long userId = authRes.getUserId();

                OwnerProfile owner = ownerRepo.findByUserId(userId)
                                .orElseThrow(() -> new EntityNotFoundException("Owner profile not found."));

                return owner.getPets().stream()
                                .map(p -> new PetResponse(
                                                p.getId(),
                                                p.getName(),
                                                p.getType().name(),
                                                p.getBreed(),
                                                p.getAge(),
                                                p.getSterilized(),
                                                p.getSex(),
                                                p.getBehaviorDescription()))
                                .collect(Collectors.toList());
        }

        /**
         * Get a specific pet by its ID for the authenticated owner
         */
        @Transactional(readOnly = true)
        public PetResponse getPetById(String bearerToken, Long petId) {
                var authRes = authServiceClient.validateToken(bearerToken);
                Long userId = authRes.getUserId();

                Pet pet = petRepo.findByIdAndOwnerUserId(petId, userId)
                                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));

                return new PetResponse(
                                pet.getId(),
                                pet.getName(),
                                pet.getType().name(),
                                pet.getBreed(),
                                pet.getAge(),
                                pet.getSterilized(),
                                pet.getSex(),
                                pet.getBehaviorDescription());
        }
}