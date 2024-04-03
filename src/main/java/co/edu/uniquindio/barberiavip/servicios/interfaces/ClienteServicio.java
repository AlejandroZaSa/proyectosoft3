package co.edu.uniquindio.barberiavip.servicios.interfaces;

import co.edu.uniquindio.barberiavip.dto.cliente.*;

import java.util.List;

public interface ClienteServicio {

    int registrarse(RegistroClienteDTO registroClienteDTO) throws Exception;

    int solicitarCita(SolicitudCitaDTO solicitudCitaDTO) throws Exception;

    int inscribirCurso(InscripcionCursoDTO inscripcionCursoDTO) throws Exception;

    List<ItemInscripcionCursoDTO> cargarInscripciones(int codigoCliente) throws Exception;

    List<ItemSolicitudCitaDTO> cargarCitas(int codigoCliente) throws Exception;

    int pagar(MetodoPagoDTO metodoPagoDTO) throws Exception;

}
