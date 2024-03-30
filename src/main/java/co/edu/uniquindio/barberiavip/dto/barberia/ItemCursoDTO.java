package co.edu.uniquindio.barberiavip.dto.barberia;

import java.time.LocalDate;

public record ItemCursoDTO(


        int idCurso,
        float costo,

        String nombre,

        String descripcion,

        LocalDate fechaInicio,

        LocalDate fechaFin

) {
}
