package com.auth.pyg_auth.controllers;

import com.auth.pyg_auth.dto.requests.LoginRequest;
import com.auth.pyg_auth.dto.requests.RefreshTokenRequest;
import com.auth.pyg_auth.dto.requests.UserRegisterRequest;
import com.auth.pyg_auth.dto.responses.AuthResponse;
import com.auth.pyg_auth.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private AuthResponse authResponsePrueba;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();

        objectMapper = new ObjectMapper();

        authResponsePrueba = AuthResponse.builder()
                .accessToken("access-token-prueba")
                .refreshToken("refresh-token-prueba")
                .tokenType("Bearer")
                .build();
    }

    @Test
    @DisplayName("Successful login returns 200 with tokens")
    void login_conCredencialesValidas_debeRetornar200ConTokens() throws Exception {

        LoginRequest request = LoginRequest.builder()
                .username("ana@perrosgatos.cl")
                .password("password123")
                .build();

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(authResponsePrueba);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token-prueba"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-prueba"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("Successful registration returns 200")
    void register_conDatosValidos_debeRetornar200() throws Exception {

        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("nuevo@perrosgatos.cl")
                .password("password123")
                .firstname("Pedro")
                .lastname("Soto")
                .rolename("ROLE_OWNER")
                .build();

        when(authService.register(any(UserRegisterRequest.class)))
                .thenReturn(authResponsePrueba);

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    @DisplayName("Valid refresh token returns 200")
    void refresh_conTokenValido_debeRetornar200() throws Exception {

        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("refresh-token")
                .build();

        when(authService.refresh(any(RefreshTokenRequest.class)))
                .thenReturn(authResponsePrueba);

        mockMvc.perform(
                        post("/api/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    @DisplayName("Logout returns 204")
    void logout_conTokenValido_debeRetornar204() throws Exception {

        doNothing().when(authService).logout(anyString());

        mockMvc.perform(
                        post("/api/auth/logout")
                                .header("Authorization", "Bearer access-token-prueba"))
                .andExpect(status().isNoContent());

        verify(authService, times(1))
                .logout("access-token-prueba");
    }
}