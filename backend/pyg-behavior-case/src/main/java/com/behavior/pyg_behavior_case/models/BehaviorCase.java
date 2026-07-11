package com.behavior.pyg_behavior_case.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "behavior_cases")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BehaviorCase {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // Viene del header X-User-Id — no guardamos datos del dueño,
        // solo la referencia
        private Long ownerId;

        // Descripción del problema
        private String title;           // "Mi perro ataca a mi gato"
        private String description;     // descripción general
        private String detailedDescription; // más detalle

        // Preguntas estructuradas (true/false)
        private Boolean hasChildren;        // hay niños en casa?
        private Boolean hasOtherPets;       // convive con otras mascotas?
        private Boolean hasAggression;      // ha habido mordidas/arañazos?
        private Boolean isAloneFrequently;  // pasa mucho tiempo solo? - se siente medio raro, hay que eliminarlo

        // Contexto temporal
        private String behaviorDuration; // "2 semanas", "6 meses", etc.

        // Estado del caso
        @Enumerated(EnumType.STRING)
        private CaseStatus status; // OPEN, IN_PROGRESS, CLOSED

        @Version
        private Long version;

        @CreatedDate
        @Column(updatable = false)
        private LocalDateTime createdAt;

        @LastModifiedDate
        private LocalDateTime updatedAt;

        // Relaciones
        @OneToMany(mappedBy = "behaviorCase", cascade = CascadeType.ALL)
        private List<CasePet> pets;

        @OneToMany(mappedBy = "behaviorCase", cascade = CascadeType.ALL)
        private List<CaseEvidence> evidences;

        @OneToMany(mappedBy = "behaviorCase", cascade = CascadeType.ALL)
        private List<CaseProposal> proposals;
    }