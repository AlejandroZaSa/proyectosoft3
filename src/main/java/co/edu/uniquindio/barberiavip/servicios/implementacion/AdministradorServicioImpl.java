package co.edu.uniquindio.barberiavip.servicios.implementacion;

import co.edu.uniquindio.barberiavip.dto.administrador.*;
import co.edu.uniquindio.barberiavip.modelo.entidades.*;
import co.edu.uniquindio.barberiavip.modelo.enums.Estado;
import co.edu.uniquindio.barberiavip.repositorios.*;
import co.edu.uniquindio.barberiavip.servicios.interfaces.AdministradorServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdministradorServicioImpl implements AdministradorServicio {

    private final AgendaRepository agendaRepository;
    private final CursoRepository cursoRepository;
    private final ServicioRepository servicioRepository;
    private final InscripcionRepository inscripcionRepository;
    private final SolicitudCitaRepository solicitudCitaRepository;
    private final PagoRepository pagoRepository;

    @Override
    public int crearCurso(CursoDTO cursoDTO) throws Exception {

        Curso cursoNuevo = new Curso();

        cursoNuevo.setActivo(true);
        cursoNuevo.setNombre(cursoDTO.nombre());
        cursoNuevo.setCosto(cursoDTO.costo());
        cursoNuevo.setDescripcion(cursoDTO.descripcion());
        cursoNuevo.setFechaFin(cursoDTO.fechaFin());
        cursoNuevo.setFechaInicio(cursoDTO.fechaInicio());

        Curso cursoRegistrado = cursoRepository.save(cursoNuevo);

        return cursoRegistrado.getId();
    }

    @Override
    public int crearServicio(ServicioDTO servicioDTO) throws Exception {

        Servicio servicioNuevo = new Servicio();

        servicioNuevo.setCosto(servicioDTO.costo());
        servicioNuevo.setDescripcion(servicioDTO.descripcion());
        servicioNuevo.setActivo(servicioDTO.activo());
        servicioNuevo.setNombre(servicioDTO.nombre());

        Servicio servicioRegistrado = servicioRepository.save(servicioNuevo);

        return servicioRegistrado.getId();
    }

    @Override
    public int actualizarCurso(int codigoCurso, CursoDTO curso) throws Exception {

        Optional<Curso> opcional = cursoRepository.findById(codigoCurso);

        if (opcional.isEmpty()) {
            throw new Exception("No existe un curso con el codigo: " + codigoCurso);
        }

        Curso buscado = opcional.get();

        buscado.setActivo(curso.activo());
        buscado.setNombre(curso.nombre());
        buscado.setCosto(curso.costo());
        buscado.setDescripcion(curso.descripcion());
        buscado.setFechaFin(curso.fechaFin());
        buscado.setFechaInicio(curso.fechaInicio());

        cursoRepository.save(buscado);

        return buscado.getId();
    }

    @Override
    public int actualizarServicio(int codigoServicio, ServicioDTO servicio) throws Exception {

        Optional<Servicio> opcional = servicioRepository.findById(codigoServicio);

        if (opcional.isEmpty()) {
            throw new Exception("No existe un servicio con el codigo: " + codigoServicio);
        }

        Servicio buscado = opcional.get();

        buscado.setActivo(servicio.activo());
        buscado.setCosto(servicio.costo());
        buscado.setDescripcion(servicio.descripcion());
        buscado.setNombre(servicio.nombre());

        servicioRepository.save(buscado);

        return buscado.getId();
    }

    @Override
    public void eliminarCurso(int codigoCurso) throws Exception {

        Optional<Curso> opcional = cursoRepository.findById(codigoCurso);

        if (opcional.isEmpty()) {
            throw new Exception("No existe un curso con el codigo: " + codigoCurso);
        }

        Curso buscado = opcional.get();

        buscado.setActivo(false);

        cursoRepository.save(buscado);
    }

    @Override
    public void eliminarServicio(int codigoServicio) throws Exception {

        Optional<Servicio> opcional = servicioRepository.findById(codigoServicio);

        if (opcional.isEmpty()) {
            throw new Exception("No existe un servicio con el codigo: " + codigoServicio);
        }

        Servicio buscado = opcional.get();

        buscado.setActivo(false);

        servicioRepository.save(buscado);
    }

    @Override
    public CursoDTO obtenerCurso(int codigoCurso) throws Exception {
        Optional<Curso> opcional = cursoRepository.findById(codigoCurso);

        if (opcional.isEmpty()) {
            throw new Exception("No existe un curso con el codigo: " + codigoCurso);
        }

        Curso buscado = opcional.get();

        return new CursoDTO(buscado.getCosto(),
                buscado.getNombre(),
                buscado.getDescripcion(),
                buscado.getFechaInicio(),
                buscado.getFechaFin(),
                buscado.isActivo());
    }

    @Override
    public ServicioDTO obtenerServicio(int codigoServicio) throws Exception {

        Optional<Servicio> opcional = servicioRepository.findById(codigoServicio);

        if (opcional.isEmpty()) {
            throw new Exception("No existe un servicio con el codigo: " + codigoServicio);
        }

        Servicio buscado = opcional.get();

        return new ServicioDTO(
                buscado.getNombre(),
                buscado.getDescripcion(),
                buscado.getCosto(),
                buscado.isActivo()
        );
    }

    @Override
    public void cambiarEstadoInscripcion(EstadoInscripcionDTO estadoInscripcionDTO) throws Exception {

        Optional<Inscripcion> opcionalInscripcion = inscripcionRepository.findById(estadoInscripcionDTO.idInscripcion());

        if (opcionalInscripcion.isEmpty()) {
            throw new Exception("No existe la inscripcion con el codigo " + estadoInscripcionDTO.idInscripcion());
        }
        Inscripcion buscado = opcionalInscripcion.get();

        buscado.setEstado(estadoInscripcionDTO.estado());

        if(estadoInscripcionDTO.estado()==Estado.PAGADO){
            Optional<Pago> pago = pagoRepository.findById(buscado.getPago().getId());

            if (pago.isEmpty()) {
                throw new Exception("No existe un pago con el código" + buscado.getPago().getId());
            }

            Pago pagoEncontrado = pago.get();
            pagoEncontrado.setEstado(estadoInscripcionDTO.estado());
            pagoEncontrado.setFechaPago(LocalDate.now());
            pagoRepository.save(pagoEncontrado);
        }

        inscripcionRepository.save(buscado);
    }

    @Override
    public void cambiarEstadoCita(EstadoCitaDTO estadoCitaDTO) throws Exception {

        Optional<SolicitudCita> opcionalSolicitud = solicitudCitaRepository.findById(estadoCitaDTO.idCita());

        if (opcionalSolicitud.isEmpty()) {
            throw new Exception("No existe la inscripcion con el codigo " + estadoCitaDTO.idCita());
        }
        SolicitudCita buscado = opcionalSolicitud.get();

        buscado.setEstado(estadoCitaDTO.estado());

        if(estadoCitaDTO.estado()==Estado.PAGADO){
            Optional<Pago> pago = pagoRepository.findById(buscado.getPago().getId());

            if (pago.isEmpty()) {
                throw new Exception("No existe un pago con el código" + buscado.getPago().getId());
            }

            Pago pagoEncontrado = pago.get();
            pagoEncontrado.setEstado(estadoCitaDTO.estado());
            pagoEncontrado.setFechaPago(LocalDate.now());
            pagoRepository.save(pagoEncontrado);
        }

        solicitudCitaRepository.save(buscado);
    }

    @Override
    public void actualizarAgenda(List<ItemAgendaDTO> agendaDTO) throws Exception {

    }

    @Override
    public List<ItemAgendaDTO> cargarAgenda(int idBarbero) throws Exception{

        List<Agenda> agenda = agendaRepository.buscarAgendaBarbero(idBarbero);

        if(agenda.isEmpty()){

            throw new Exception("No existe agenda para el barbero con codigo" + idBarbero);
        }
        List<ItemAgendaDTO> listaAgenda = new ArrayList<>();

        for(Agenda a: agenda){
            listaAgenda.add(new ItemAgendaDTO(

                    a.getId(),
                    a.getDia(),
                    a.getHoraEntrada(),
                    a.getHoraSalida()
            ));
        }

        return listaAgenda;
    }
}
