package co.edu.uniquindio.barberiavip.dto.administrador;

import java.time.LocalDate;

public record CursoDTO(

        float costo,

        String nombre,

        String descripcion,

        LocalDate fechaInicio,

        LocalDate fechaFin,

        boolean activo
) {
}
