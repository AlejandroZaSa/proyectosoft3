package co.edu.uniquindio.barberiavip.dto.cliente;

import co.edu.uniquindio.barberiavip.modelo.enums.Estado;

import java.time.LocalDate;

public record ItemSolicitudCitaDTO(

        String servicios,

        float precio,

        LocalDate fecha,

        Estado estado,

        int idPago
) {
}
