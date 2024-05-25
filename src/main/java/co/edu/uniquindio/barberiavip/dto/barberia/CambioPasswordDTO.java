package co.edu.uniquindio.barberiavip.dto.barberia;

import jakarta.validation.constraints.NotBlank;

public record CambioPasswordDTO(
                    @NotBlank
                    String email,
                    @NotBlank
                    String nuevaPassword){
}
