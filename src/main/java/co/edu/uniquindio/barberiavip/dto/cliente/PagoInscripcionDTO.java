package co.edu.uniquindio.barberiavip.dto.cliente;

import java.time.LocalDate;

public record PagoInscripcionDTO(

        int idInscripcion,

        String numeroTarjeta,

        String codigoSeguridad,

        LocalDate fechaExpiracion,

        String primerNombre,

        String apellido,

        int idMetodo,
        int idCliente

) {

}

