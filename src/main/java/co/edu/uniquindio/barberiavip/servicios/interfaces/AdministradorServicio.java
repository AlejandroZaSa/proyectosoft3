package co.edu.uniquindio.barberiavip.servicios.interfaces;

import co.edu.uniquindio.barberiavip.dto.administrador.*;

import java.util.List;

public interface AdministradorServicio {

    int crearCurso(CursoDTO cursoDTO) throws Exception;

    int crearServicio(ServicioDTO servicioDTO) throws Exception;

    int actualizarCurso(int codigoCurso, CursoDTO curso) throws Exception;

    int actualizarServicio(int codigoServicio, ServicioDTO servicio) throws Exception;

    void eliminarCurso(int codigoCurso) throws Exception;

    void eliminarServicio(int codigoServicio) throws Exception;

    CursoDTO obtenerCurso(int codigoCurso) throws Exception;

    ServicioDTO obtenerServicio(int codigoServicio) throws Exception;

    void cambiarEstadoInscripcion(EstadoInscripcionDTO estadoInscripcionDTO) throws Exception;

    void cambiarEstadoCita(EstadoCitaDTO estadoCitaDTO) throws Exception;

    void actualizarAgenda(List<ItemAgendaDTO> agendaDTO) throws Exception;

    List<ItemAgendaDTO> cargarAgenda(int idBarbero) throws Exception;
}
