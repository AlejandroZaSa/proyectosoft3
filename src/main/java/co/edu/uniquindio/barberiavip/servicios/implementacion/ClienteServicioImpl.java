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
import java.util.*;

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
    private final BarberoRepository barberoRepository;
    private final AgendaRepository agendaRepository;
    private final ServicioRepository servicioRepository;

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

        Set<Servicio> lista = new HashSet<>();

        for (Integer s : solicitudCitaDTO.servicios()) {
            Optional<Servicio> servicio = servicioRepository.findById(s);
            if (servicio.isEmpty()) {
                throw new Exception("No existe un servicio con el código" + s);

            }
            costoTotal = costoTotal + servicio.get().getCosto();
            lista.add(servicio.get());
        }

        citaNueva.setServicios(lista);

        System.out.println("costo:" + costoTotal);

        citaNueva.setCosto(costoTotal);
        SolicitudCita citaRegistrada = solicitudCitaRepository.save(citaNueva);

        emailServicio.enviarEmail(new EmailDTO("Agendamiento de Cita", cliente.get().getEmail(), "Haz agendado una cita con fecha " +
                citaRegistrada.getFecha() + " y hora " + citaRegistrada.getHora() + " con el barbero " + citaRegistrada.getBarbero().getNombreCompleto()+ " codigo " + citaRegistrada.getId()));

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

        emailServicio.enviarEmail(new EmailDTO("Inscripcion curso", cliente.get().getEmail(), "Se ha creado una inscripcion con codigo " +
                inscripcionRegistrada.getId() + ", curso: "+inscripcionRegistrada.getCurso().getNombre()));

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
                    i.getPago() == null ? 0 : i.getPago().getId(),
                    i.getId()
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
                    s.getPago() == null ? 0 : s.getPago().getId(),
                    s.getId()
            ));

        }

        return itemSolicitudCitaDTOS;
    }

    @Override
    public int pagoInscripcion(PagoInscripcionDTO pagoInscripcionDTO) throws Exception {
        Optional<Inscripcion> inscripcion = inscripcionRepository.findById(pagoInscripcionDTO.idInscripcion());

        if (inscripcion.isEmpty()) {
            throw new Exception("No existe una inscripción con el código" + pagoInscripcionDTO.idInscripcion());
        }

        Optional<Cliente> cliente = clienteRepository.findById(pagoInscripcionDTO.idCliente());

        if(cliente.isEmpty()){
            throw new Exception("No existe un cliente con el codigo "+ pagoInscripcionDTO.idCliente());
        }

        MetodoPago metodoPago = obtenerMetodoPagoInscripcion(pagoInscripcionDTO,cliente.get());

        Pago pago = crearPagoInscripcion(inscripcion.get(), metodoPago);
        Pago pagoRegistrado = pagoRepository.save(pago);

        inscripcion.get().setPago(pagoRegistrado);
        inscripcion.get().setEstado(Estado.PAGADO);
        Inscripcion inscripcionRegistrada = inscripcionRepository.save(inscripcion.get());

        emailServicio.enviarEmail(new EmailDTO("Pago inscripcion", inscripcion.get().getCliente().getEmail(), "Haz pagado tu inscripcion con código " +
                inscripcionRegistrada.getId()));

        return metodoPago.getId();
    }

    private MetodoPago obtenerMetodoPagoInscripcion(PagoInscripcionDTO pagoInscripcionDTO, Cliente cliente) throws Exception {
        MetodoPago metodoPago;

        if (pagoInscripcionDTO.idMetodo() == 0) {
            metodoPago = crearNuevoMetodoPagoInscripcion(pagoInscripcionDTO,cliente);
            metodoPago = metodoPagoRepository.save(metodoPago);
        } else {
            Optional<MetodoPago> metodo = metodoPagoRepository.findById(pagoInscripcionDTO.idMetodo());
            if (metodo.isEmpty()) {
                throw new Exception("No hay un metodo de pago con el código " + pagoInscripcionDTO.idMetodo());
            }
            metodo.get().setCliente(cliente);
            MetodoPago registrado = metodoPagoRepository.save(metodo.get());
            metodoPago = registrado;
        }

        return metodoPago;
    }

    private MetodoPago obtenerMetodoPagoCita(PagoCitaDTO pagoCitaDTO, Cliente cliente) throws Exception {
        MetodoPago metodoPago;

        if (pagoCitaDTO.idMetodo() == 0) {
            metodoPago = crearNuevoMetodoPagoCita(pagoCitaDTO,cliente);
            metodoPago = metodoPagoRepository.save(metodoPago);
        } else {
            Optional<MetodoPago> metodo = metodoPagoRepository.findById(pagoCitaDTO.idMetodo());
            if (metodo.isEmpty()) {
                throw new Exception("No hay un metodo de pago con el código " + pagoCitaDTO.idMetodo());
            }
            metodo.get().setCliente(cliente);
            MetodoPago registrado = metodoPagoRepository.save(metodo.get());
            metodoPago = registrado;
        }

        return metodoPago;
    }

    private MetodoPago crearNuevoMetodoPagoInscripcion(PagoInscripcionDTO pagoInscripcionDTO, Cliente cliente) {
        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setApellido(pagoInscripcionDTO.apellido());
        metodoPago.setNumeroTarjeta(pagoInscripcionDTO.numeroTarjeta());
        metodoPago.setCliente(cliente);
        metodoPago.setCodigoSeguridad(pagoInscripcionDTO.codigoSeguridad());
        metodoPago.setFechaExpiracion(pagoInscripcionDTO.fechaExpiracion());
        metodoPago.setPrimerNombre(pagoInscripcionDTO.primerNombre());
        return metodoPago;
    }

    private MetodoPago crearNuevoMetodoPagoCita(PagoCitaDTO pagoCitaDTO, Cliente cliente) {
        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setApellido(pagoCitaDTO.apellido());
        metodoPago.setNumeroTarjeta(pagoCitaDTO.numeroTarjeta());
        metodoPago.setCodigoSeguridad(pagoCitaDTO.codigoSeguridad());
        metodoPago.setCliente(cliente);
        metodoPago.setFechaExpiracion(pagoCitaDTO.fechaExpiracion());
        metodoPago.setPrimerNombre(pagoCitaDTO.primerNombre());
        return metodoPago;
    }

    private Pago crearPagoInscripcion(Inscripcion inscripcion, MetodoPago metodoPago) {
        Pago pago = new Pago();
        pago.setEstado(Estado.PAGADO);
        pago.setMetodoPago(metodoPago);
        pago.setMonto(inscripcion.getCosto());
        pago.setFechaPago(LocalDate.now());
        return pago;
    }

    private Pago crearPagoCita(SolicitudCita cita, MetodoPago metodoPago) {
        Pago pago = new Pago();
        pago.setEstado(Estado.PAGADO);
        pago.setMetodoPago(metodoPago);
        pago.setMonto(cita.getCosto());
        pago.setFechaPago(LocalDate.now());
        return pago;
    }

    @Override
    public int pagoCita(PagoCitaDTO pagoCitaDTO) throws Exception {

        Optional<SolicitudCita> solicitudCita = solicitudCitaRepository.findById(pagoCitaDTO.idCita());

        if (solicitudCita.isEmpty()) {
            throw new Exception("No existe una cita con el código" + pagoCitaDTO.idCita());
        }

        Optional<Cliente> cliente = clienteRepository.findById(pagoCitaDTO.idCliente());

        if(cliente.isEmpty()){
            throw new Exception("No existe un cliente con el codigo "+ pagoCitaDTO.idCliente());
        }

        MetodoPago metodoPago = obtenerMetodoPagoCita(pagoCitaDTO, cliente.get());

        Pago pago = crearPagoCita(solicitudCita.get(), metodoPago);
        Pago pagoRegistrado = pagoRepository.save(pago);

        solicitudCita.get().setPago(pagoRegistrado);
        solicitudCita.get().setEstado(Estado.PAGADO);
        SolicitudCita solicitudCitaRegistrada = solicitudCitaRepository.save(solicitudCita.get());

        emailServicio.enviarEmail(new EmailDTO("Pago cita", solicitudCita.get().getCliente().getEmail(), "Haz pagado tu cita con código " +
                solicitudCitaRegistrada.getId()));


        return metodoPago.getId();
    }

    @Override
    public List<MetodoPagoDTO> cargarMetodosPay(int codigoCliente) throws Exception {

        List<MetodoPago> metodosPagos = metodoPagoRepository.findMetodosPagoCliente(codigoCliente);

        if(metodosPagos.isEmpty()){

            throw new Exception("No tiene métodos de pago guardados");
        }

        List<MetodoPagoDTO> metodosPagoDTOs = new ArrayList<>();

        for (MetodoPago metodoPago : metodosPagos) {
            metodosPagoDTOs.add(new MetodoPagoDTO(metodoPago.getId(),
                    metodoPago.getNumeroTarjeta()));
        }

        return metodosPagoDTOs;
    }

    private String obtenerServicios(SolicitudCita s) {

        StringBuilder servicios = new StringBuilder();

        for (Servicio servicio : s.getServicios()) {
            servicios.append(servicio.getNombre()).append("\r\n");
        }

        return servicios.toString();
    }


}
