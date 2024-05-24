package co.edu.uniquindio.barberiavip.servicios.implementacion;

import co.edu.uniquindio.barberiavip.dto.barberia.EmailDTO;
import co.edu.uniquindio.barberiavip.dto.cliente.*;
import co.edu.uniquindio.barberiavip.modelo.entidades.*;
import co.edu.uniquindio.barberiavip.modelo.enums.Dia;
import co.edu.uniquindio.barberiavip.modelo.enums.Estado;
import co.edu.uniquindio.barberiavip.repositorios.*;
import co.edu.uniquindio.barberiavip.servicios.interfaces.ClienteServicio;
import co.edu.uniquindio.barberiavip.servicios.interfaces.EmailServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteServicioImpl implements ClienteServicio {

    private final ClienteRepository clienteRepository;
    private final AdminRepository adminRepository;
    private final CursoRepository cursoRepository;
    private final SolicitudCitaRepository solicitudCitaRepository;
    private final InscripcionRepository inscripcionRepository;
    private final PagoRepository pagoRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final EmailServicio emailServicio;
    private BarberoRepository barberoRepository;
    private AgendaRepository agendaRepository;
    private ServicioRepository servicioRepository;

    @Override
    public int registrarse(RegistroClienteDTO clienteDTO) throws Exception {

        Cliente cliente = null;

        if (!estaRepetidoCorreo(clienteDTO.email())) {
            throw new Exception("El  correo " + clienteDTO.email() + " ya está en uso");
        }

        cliente = new Cliente();
        cliente.setNombre(clienteDTO.nombre());
        cliente.setApellido(clienteDTO.apellido());
        cliente.setEmail(clienteDTO.email());
        cliente.setTelefono(clienteDTO.telefono());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        cliente.setPassword(passwordEncoder.encode(clienteDTO.password()));

        cliente.setActivo(true);

        Cliente clienteRegistrado = clienteRepository.save(cliente);

        return clienteRegistrado.getId();

    }

    public boolean estaRepetidoCorreo(String email) {
        Cliente cliente = clienteRepository.findByEmail(email);
        Administrador admin = adminRepository.findByCorreo(email);
        return cliente == null && admin == null;
    }

    @Override
    public List<ItemBarberoCitaDTO> filtrarBarberoCita(LocalDate fecha) throws Exception {

        if (fecha.isBefore(LocalDate.now())) {
            throw new Exception("La fecha es anterior a hoy");
        }

        //____________________________Vemos para qué día se quiere la cita____________________________
        //Obtenemos el día de la semana
        DayOfWeek diaDeLaSemana = fecha.getDayOfWeek();

        int numeroDia = diaDeLaSemana.getValue() - 1;
        //Obtenemos el enumerable
        Dia dia = Dia.values()[numeroDia];

        //________Obtenemos todos los horarios posibles en los que se pueda agendar una cita______
        List<ItemBarberoCitaDTO> listaItemBarberoCitaDTOS = new ArrayList<>();

        List<Barbero> barberoList = barberoRepository.findAll();

        for (Barbero barbero : barberoList) {
            Agenda horarioBarbero = agendaRepository.obtenerAgendaFecha(barbero.getId(), dia);

            if (horarioBarbero != null) {

                List<SolicitudCita> citasPendientes = solicitudCitaRepository.obtenerCitasFecha(barbero.getId(), fecha);

                //Empezamos con la primera hora de trabajo del medico
                LocalTime horaInicioCita = horarioBarbero.getHoraEntrada();
                LocalTime finJornada = horarioBarbero.getHoraSalida();

                /*
                 * Mientras que el posible inicio de la cita sea diferente al fin de la jornada
                 * Se evalua como posible inicio de una nueva cita
                 * */

                while (!horaInicioCita.equals(finJornada) && horaInicioCita.isBefore(finJornada) && verificarHoras(horaInicioCita, finJornada)) {

                    boolean sePuedeAgendar = true;
                    //Validamos que ninguna cita cumpla con esa hora
                    for (SolicitudCita cita : citasPendientes) {
                        if (horaInicioCita.equals(cita.getHora())) {
                            sePuedeAgendar = false;
                            break;
                        }
                    }
                    if (sePuedeAgendar) {
                        listaItemBarberoCitaDTOS.add(new ItemBarberoCitaDTO(barbero.getId(), barbero.getNombreCompleto(), horaInicioCita));
                    }
                    //Sumamos 60 minutos que es la duración de una cita
                    horaInicioCita = horaInicioCita.plusMinutes(60);
                }

            }

        }
        //________________________________________________________________________________________
        if (listaItemBarberoCitaDTOS.isEmpty()) {
            throw new Exception("No hay disponibilidad de barberos, inténtalo más tarde");
        }

        return listaItemBarberoCitaDTOS;
    }

    private boolean verificarHoras(LocalTime horaInicioCita, LocalTime finJornada) {

        Duration diferencia = Duration.between(finJornada, horaInicioCita);

        // Obtener la diferencia en minutos
        long minutosDiferencia = Math.abs(diferencia.toMinutes());

        if (minutosDiferencia >= 30) {
            return true;
        }

        return false;
    }


    @Override
    public int solicitarCita(SolicitudCitaDTO solicitudCitaDTO) throws Exception {

        Optional<Barbero> barbero = barberoRepository.findById(solicitudCitaDTO.idBarbero());

        if (barbero.isEmpty()) {
            throw new Exception("No existe el barbero con el código " + solicitudCitaDTO.idBarbero());
        }

        Optional<Cliente> cliente = clienteRepository.findById(solicitudCitaDTO.idCliente());

        if (cliente.isEmpty()) {
            throw new Exception("No existe el  cliente con código " + solicitudCitaDTO.idCliente());
        }

        SolicitudCita citaNueva = new SolicitudCita();

        citaNueva.setCliente(cliente.get());
        citaNueva.setBarbero(barbero.get());
        citaNueva.setFecha(solicitudCitaDTO.fecha());
        citaNueva.setHora(solicitudCitaDTO.hora());
        citaNueva.setEstado(Estado.PENDIENTE);

        float costoTotal = 0;

        for (Integer s : solicitudCitaDTO.servicios()) {
            Optional<Servicio> servicio = servicioRepository.findById(s);
            if (servicio.isEmpty()) {
                throw new Exception("No existe un servicio con el código" + s);

            }
            costoTotal = costoTotal + servicio.get().getCosto();
            citaNueva.getServicios().add(servicio.get());

        }
        citaNueva.setCosto(costoTotal);
        SolicitudCita citaRegistrada = solicitudCitaRepository.save(citaNueva);

        emailServicio.enviarEmail(new EmailDTO("Agendamiento de Cita", cliente.get().getEmail(), "Haz agendado una cita con fecha " +
                citaRegistrada.getFecha() + " y hora " + citaRegistrada.getHora() + " con el barbero " + citaRegistrada.getBarbero().getNombreCompleto()));

        return citaRegistrada.getId();
    }

    @Override
    public int inscribirCurso(InscripcionCursoDTO inscripcionCursoDTO) throws Exception {

        Optional<Cliente> cliente = clienteRepository.findById(inscripcionCursoDTO.idCliente());

        if (cliente.isEmpty()) {
            throw new Exception("No existe un cliente con el código" + inscripcionCursoDTO.idCliente());
        }

        Optional<Curso> curso = cursoRepository.findById(inscripcionCursoDTO.idCurso());

        if (curso.isEmpty()) {
            throw new Exception("No existe un curso con el código" + inscripcionCursoDTO.idCurso());
        }

        Cliente clienteEncontrado = cliente.get();
        Curso cursoEncontrado = curso.get();

        Inscripcion inscripcion = new Inscripcion();

        inscripcion.setFechaInscripcion(LocalDate.now());
        inscripcion.setCosto(cursoEncontrado.getCosto());
        inscripcion.setEstado(Estado.PENDIENTE);
        inscripcion.setCurso(cursoEncontrado);
        inscripcion.setCliente(clienteEncontrado);

        Inscripcion inscripcionRegistrada = inscripcionRepository.save(inscripcion);

        Pago pago = new Pago();

        pago.setEstado(Estado.PENDIENTE);
        pago.setMonto(cursoEncontrado.getCosto());
        pago.setInscripcion(inscripcionRegistrada);
        pagoRepository.save(pago);

        return inscripcionRegistrada.getId();
    }

    @Override
    public List<ItemInscripcionCursoDTO> cargarInscripciones(int codigoCliente) throws Exception {

        List<Inscripcion> inscripciones = inscripcionRepository.buscarInscripcionesCliente(codigoCliente);

        if (inscripciones.isEmpty()) {
            throw new Exception("No hay inscripciones registrados");
        }

        List<ItemInscripcionCursoDTO> listaInscripcionCursoDTOS = new ArrayList<>();

        for (Inscripcion i : inscripciones) {

            listaInscripcionCursoDTOS.add(new ItemInscripcionCursoDTO(

                    i.getFechaInscripcion(),
                    i.getCosto(),
                    i.getEstado(),
                    i.getCurso().getNombre(),
                    i.getPago() == null ? 0 : i.getPago().getId()
            ));

        }

        return listaInscripcionCursoDTOS;
    }

    @Override
    public List<ItemSolicitudCitaDTO> cargarCitas(int codigoCliente) throws Exception {

        List<SolicitudCita> solicitudes = solicitudCitaRepository.buscarCitasCliente(codigoCliente);

        if (solicitudes.isEmpty()) {
            throw new Exception("No hay solicitudes de citas registrados");
        }

        List<ItemSolicitudCitaDTO> itemSolicitudCitaDTOS = new ArrayList<>();

        for (SolicitudCita s : solicitudes) {

            itemSolicitudCitaDTOS.add(new ItemSolicitudCitaDTO(

                    obtenerServicios(s),
                    s.getCosto(),
                    s.getFecha(),
                    s.getEstado(),
                    s.getPago() == null ? 0 : s.getPago().getId()
            ));

        }

        return itemSolicitudCitaDTOS;
    }

    private String obtenerServicios(SolicitudCita s) {

        StringBuilder servicios = new StringBuilder();

        for (Servicio servicio : s.getServicios()) {
            servicios.append(servicio.getNombre()).append("\r\n");
        }

        return servicios.toString();
    }

    @Override
    public int pagar(MetodoPagoDTO metodoPagoDTO) throws Exception {

        Optional<Pago> pago = pagoRepository.findById(metodoPagoDTO.idPago());

        if (pago.isEmpty()) {
            throw new Exception("No existe un pago con el código" + metodoPagoDTO.idPago());
        }

        MetodoPago metodoPago = new MetodoPago();

        metodoPago.setApellido(metodoPagoDTO.apellido());
        metodoPago.setNumeroTarjeta(metodoPagoDTO.numeroTarjeta());
        metodoPago.setCodigoSeguridad(metodoPagoDTO.codigoSeguridad());
        metodoPago.setFechaExpiracion(metodoPagoDTO.fechaExpiracion());
        metodoPago.setPrimerNombre(metodoPagoDTO.primerNombre());

        MetodoPago metodoPagoRegistrado = metodoPagoRepository.save(metodoPago);

        Pago pagoEncontrado = pago.get();

        pagoEncontrado.setMetodoPago(metodoPagoRegistrado);
        pagoEncontrado.setEstado(Estado.PAGADO);
        pagoRepository.save(pagoEncontrado);

        return metodoPagoRegistrado.getId();
    }
}
