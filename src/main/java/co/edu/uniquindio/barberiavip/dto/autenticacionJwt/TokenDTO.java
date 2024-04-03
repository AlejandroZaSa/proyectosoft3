package co.edu.uniquindio.barberiavip.dto.autenticacionJwt;

import jakarta.validation.constraints.NotBlank;

public record TokenDTO(
        @NotBlank
        String token) {
}
