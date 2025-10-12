package com.auth.pyg_auth.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PetProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;        // DOG, CAT
    private String breed;       // o "Mestizo"
    private String name;
    private Integer age;
    private Boolean sterilized;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}
