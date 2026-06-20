package com.owner.pyg_owner.services;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.owner.pyg_owner.dto.requests.PetRequest;
import com.owner.pyg_owner.dto.responses.PetResponse;
import com.owner.pyg_owner.exceptions.NotFoundException;
import com.owner.pyg_owner.exceptions.ValidationException;
import com.owner.pyg_owner.models.OwnerProfile;
import com.owner.pyg_owner.models.Pet;
import com.owner.pyg_owner.models.PetType;
import com.owner.pyg_owner.repositories.OwnerRepository;
import com.owner.pyg_owner.repositories.PetRepository;

@Service
public class PetService {

        private final PetRepository petRepo;
        private final OwnerRepository ownerRepo;

        public PetService(PetRepository petRepo, OwnerRepository ownerRepo) {
                this.petRepo = petRepo;
                this.ownerRepo = ownerRepo;
        }

        @Transactional
        public PetResponse addPet(Long userId, PetRequest req) {
                OwnerProfile owner = ownerRepo.findByUserId(userId)
                        .orElseThrow(() -> new NotFoundException(
                                "Owner profile not found. Please complete your profile first."));

                Pet pet = Pet.builder()
                        .name(req.name())
                        .type(parsePetType(req.type()))
                        .breed(req.breed())
                        .age(req.age())
                        .sterilized(req.sterilized())
                        .sex(req.sex())
                        .behaviorDescription(req.behaviorDescription())
                        .owner(owner)
                        .build();

                try {
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
                } catch (Exception ex) {
                        throw new ValidationException("Error saving pet: Invalid data provided");
                }
        }

        @Transactional(readOnly = true)
        public List<PetResponse> getPetsByOwner(Long userId) {
                if (!ownerRepo.findByUserId(userId).isPresent()) {
                        throw new NotFoundException("Owner profile not found.");
                }

                return petRepo.findByOwnerUserId(userId).stream()
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

        @Transactional(readOnly = true)
        public PetResponse getPetById(Long userId, Long petId) {
                Pet pet = petRepo.findByIdAndOwnerUserId(petId, userId)
                        .orElseThrow(() -> new NotFoundException("Pet not found"));

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

        private PetType parsePetType(String type) {
                try {
                        return PetType.valueOf(type.toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException ex) {
                        throw new ValidationException(
                                "Invalid pet type: " + type + ". Allowed values: DOG, CAT");
                }
        }
}