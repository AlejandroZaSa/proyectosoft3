package co.edu.uniquindio.barberiavip.dto.cliente;

import co.edu.uniquindio.barberiavip.modelo.entidades.Servicio;

import java.util.List;

public record SolicitudCitaDTO(

        int idCliente,

        int idPago,

        List<Servicio> servicios
) {
}
