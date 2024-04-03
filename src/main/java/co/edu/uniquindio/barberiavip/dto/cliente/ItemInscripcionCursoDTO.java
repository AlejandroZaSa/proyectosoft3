package co.edu.uniquindio.barberiavip.dto.cliente;

import co.edu.uniquindio.barberiavip.modelo.enums.Estado;

import java.time.LocalDate;

public record ItemInscripcionCursoDTO(

        LocalDate fechaInscripcion,

        float costo,

        Estado estado,

        String nombreCurso,

        int idPago
) {
}
