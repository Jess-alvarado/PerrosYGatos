package com.behavior.pyg_behavior_case.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "case_pets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CasePet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Referencia al ID original en pyg-owner
    private Long originalPetId;

    // Snapshot de los datos en el momento de crear el caso
    private String name;

    @Enumerated(EnumType.STRING)
    private PetType type;       // DOG, CAT

    private String breed;
    private Integer age;
    private Boolean sterilized;
    private String sex;
    private String personalityDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    private BehaviorCase behaviorCase;
}