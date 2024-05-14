package co.edu.uniquindio.barberiavip.dto.cliente;

import co.edu.uniquindio.barberiavip.modelo.entidades.Servicio;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record SolicitudCitaDTO(

        int idCliente,

        int idPago,

        int idBarbero,

        LocalDate fecha,

        LocalTime hora,

        List<Servicio> servicios
) {
}
