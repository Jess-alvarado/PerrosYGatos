package com.behavior.pyg_behavior_case.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "case_proposals")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseProposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Viene del header X-User-Id cuando el profesional propone
    private Long professionalId;

    private String approach;        // enfoque del especialista
    private Integer estimatedPrice;  // "45.000 por sesión"
    private Integer estimatedSessions; // sesiones estimadas

    @Enumerated(EnumType.STRING)
    private ProposalStatus status; // PENDING, ACCEPTED, REJECTED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    private BehaviorCase behaviorCase;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}