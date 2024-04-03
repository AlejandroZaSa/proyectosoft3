package co.edu.uniquindio.barberiavip.dto.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record RegistroClienteDTO(

        @NotBlank
        @Length(max = 30, message="El nombre debe tener máximo 30 caracteres")
        String nombre,

        @NotBlank
        @Length(max = 30, message="El apellido debe tener máximo 30 caracteres")
        String apellido,

        @Length(max = 10, message="El telefono debe tener máximo 10 caracteres")
        String telefono,

        @NotBlank
        @Email(message = "Ingrese una dirección de correo electrónico válida")
        @Length(max = 50, message = "El correo debe tener máximo 50 caracteres")
        String email,

        @NotBlank
        String password,

        boolean activo

) {
}
