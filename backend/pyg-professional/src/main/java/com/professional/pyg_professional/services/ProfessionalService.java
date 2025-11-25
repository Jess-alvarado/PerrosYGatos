package com.professional.pyg_professional.services;

import org.springframework.stereotype.Service;

import com.professional.pyg_professional.dto.requests.ProfessionalRequest;
import com.professional.pyg_professional.dto.responses.ProfessionalResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfessionalService {

    @Transactional
    public ProfessionalResponse createProfile(String bearerToken , ProfessionalRequest req) {
        throw new UnsupportedOperationException("Unimplemented method 'createProfile'");
    }
}
