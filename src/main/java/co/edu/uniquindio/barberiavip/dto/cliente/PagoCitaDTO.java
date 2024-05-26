package co.edu.uniquindio.barberiavip.dto.cliente;

import java.time.LocalDate;

public record PagoCitaDTO (

        int idCita,

        String numeroTarjeta,

        String codigoSeguridad,

        LocalDate fechaExpiracion,

        String primerNombre,

        String apellido
) {

}