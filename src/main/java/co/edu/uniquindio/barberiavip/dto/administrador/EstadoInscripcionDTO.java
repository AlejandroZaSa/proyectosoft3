package co.edu.uniquindio.barberiavip.dto.administrador;

import co.edu.uniquindio.barberiavip.modelo.enums.Estado;

public record EstadoInscripcionDTO(

        int idInscripcion,

        Estado estado
) {
}
