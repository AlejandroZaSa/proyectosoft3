package co.edu.uniquindio.barberiavip.dto.barberia;

import co.edu.uniquindio.barberiavip.modelo.enums.Estado;

public record ItemServicioDTO(


        int idServicio,

        String nombre,

        String descripcion,

        float costo,

        boolean estado
) {
}
