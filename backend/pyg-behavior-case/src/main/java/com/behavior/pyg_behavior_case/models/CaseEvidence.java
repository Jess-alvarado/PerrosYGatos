package com.behavior.pyg_behavior_case.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "case_evidences")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseEvidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url; // URL del archivo subido

    @Enumerated(EnumType.STRING)
    private EvidenceType type;      // "VIDEO", "IMAGE"

    private String description; // guía contextual del dueño

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    private BehaviorCase behaviorCase;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}