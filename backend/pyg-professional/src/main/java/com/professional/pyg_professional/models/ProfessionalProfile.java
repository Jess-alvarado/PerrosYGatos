package com.professional.pyg_professional.models;

import java.time.LocalDate;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "professional_profiles")
@EntityListeners(AuditingEntityListener.class)
public class ProfessionalProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId; // Reference to the User entity

    // Basic contact information
    private String phone;
    private String address;
    private LocalDate birthDate;

    // Professional information
    private String profession; // Ej: "Entrenador", "Etólogo"
    private String bio;
    private Integer experienceYears;

    /**
     * species treated by this professional.
     * Examples of values: "DOG", "CAT", "BOTH"
     */
    private String petTypes;

    // Reputation metrics
    private Double rating;          // Average rating
    private Integer reviewCount;    // Number of reviews

    // Social media
    private String profilePictureUrl;
    private String instagram;
    private String website;

    /**
     * Basic availability. E.g.:
     * "Mon-Fri 10:00-18:00" or "Flexible, coordinate via chat".
     */
    private String availability;

    /**
     * Profile status:
     * - ACTIVE
     * - PENDING
     * - SUSPENDED
     */
    private String status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;
}
