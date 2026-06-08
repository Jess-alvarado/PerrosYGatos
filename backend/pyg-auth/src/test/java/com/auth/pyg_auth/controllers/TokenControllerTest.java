package com.auth.pyg_auth.controllers;

import com.auth.pyg_auth.services.AccessTokenBlacklistService;
import com.auth.pyg_auth.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import java.util.Date;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TokenController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AccessTokenBlacklistService accessTokenBlacklistService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("Should return 200 when token is valid")
    void validateToken_whenTokenIsValid_shouldReturn200() throws Exception {

        Claims claims = new DefaultClaims();
        claims.setSubject("jess@perrosygatos.cl");
        claims.put("uid", 1L);
        claims.put("role", "ROLE_OWNER");
        claims.setExpiration(new Date(System.currentTimeMillis() + 60000));

        when(accessTokenBlacklistService.isBlacklisted(anyString()))
                .thenReturn(false);

        when(jwtService.getAllClaims(anyString()))
                .thenReturn(claims);

        mockMvc.perform(
                        post("/api/auth/validate")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer fake-token")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }
}