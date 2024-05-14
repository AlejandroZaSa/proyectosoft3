package co.edu.uniquindio.barberiavip.servicios.implementacion;

import co.edu.uniquindio.barberiavip.dto.cliente.*;
import co.edu.uniquindio.barberiavip.modelo.entidades.*;
import co.edu.uniquindio.barberiavip.modelo.enums.Dia;
import co.edu.uniquindio.barberiavip.modelo.enums.Estado;
import co.edu.uniquindio.barberiavip.repositorios.*;
import co.edu.uniquindio.barberiavip.servicios.interfaces.ClienteServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

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
    private final ClienteMongoRepository clienteMongoRepository;

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public int registrarse(RegistroClienteDTO clienteDTO) throws Exception {

        Cliente cliente = null;
        ClienteMongo clienteMongo = null;

        try {
            if (!estaRepetidoCorreo(clienteDTO.email())) {
                throw new Exception("El correo " + clienteDTO.email() + " ya está en uso");
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
        }catch(Exception e){

            if (!estaRepetidoCorreoMongo(clienteDTO.email())) {
                throw new Exception("El correo " + clienteDTO.email() + " ya está en uso");
            }

            clienteMongo = new ClienteMongo();
            clienteMongo.setId(generateSequence(ClienteMongo.SEQUENCE_NAME));
            clienteMongo.setNombre(clienteDTO.nombre());
            clienteMongo.setApellido(clienteDTO.apellido());
            clienteMongo.setEmail(clienteDTO.email());
            clienteMongo.setTelefono(clienteDTO.telefono());

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            clienteMongo.setPassword(passwordEncoder.encode(clienteDTO.password()));

            clienteMongo.setActivo(true);
            ClienteMongo clienteRegistrado = clienteMongoRepository.save(clienteMongo);

            return (int) clienteRegistrado.getId();
        }

    }
    private boolean estaRepetidoCorreoMongo(String email) {
        ClienteMongo cliente = clienteMongoRepository.findByEmail(email);
        return cliente == null;
    }
    public boolean estaRepetidoCorreo(String email) {
        Cliente cliente = clienteRepository.findByEmail(email);
        Administrador admin = adminRepository.findByCorreo(email);
        return cliente == null && admin == null;
    }

    @Override
    public List<ItemBarberoCitaDTO> filtrarBarberoCita(LocalDate fecha) throws Exception {

        if(fecha.isBefore(LocalDate.now())){
            throw new Exception("La fecha es anterior a hoy");
        }

        //____________________________Vemos para qué día se quiere la cita____________________________
        //Obtenemos el día de la semana
        DayOfWeek diaDeLaSemana = fecha.getDayOfWeek();

        int numeroDia = diaDeLaSemana.getValue() - 1;
        //Obtenemos el enumerable
        Dia dia = Dia.values()[numeroDia];

        //________Obtenemos todos los horarios posibles en los que se pueda agendar una cita______
        List<ItemBarberoCitaDTO> listaItemMedicoCitaDTOS = new ArrayList<>();

        for (Medico medico : medicosDisponibles) {
            Horario horarioMedico = horarioRepository.obtenerHorarioFecha(medico.getId(), dia);

            if (horarioMedico != null) {

                List<Cita> citasPendientes = citaRepository.obtenerCitasFecha(medico.getId(), citaDTO.fecha());

                //Empezamos con la primera hora de trabajo del medico
                LocalTime horaInicioCita = horarioMedico.getHoraInicio();
                LocalTime finJornada = horarioMedico.getHoraFin();

                /*
                 * Mientras que el posible inicio de la cita sea diferente al fin de la jornada
                 * Se evalua como posible inicio de una nueva cita
                 * */

                while (!horaInicioCita.equals(finJornada) && horaInicioCita.isBefore(finJornada) && verificarHoras(horaInicioCita,finJornada)) {

                    boolean sePuedeAgendar = true;
                    //Validamos que ninguna cita cumpla con esa hora
                    for (Cita cita : citasPendientes) {
                        if (horaInicioCita.equals(cita.getHora())) {
                            sePuedeAgendar = false;
                            break;
                        }
                    }
                    if (sePuedeAgendar) {
                        listaItemMedicoCitaDTOS.add(new ItemMedicoCitaDTO(medico.getId(), medico.getNombreCompleto(), horaInicioCita));
                    }
                    //Sumamos 60 minutos que es la duración de una cita
                    horaInicioCita = horaInicioCita.plusMinutes(60);
                }

            }

        }
        //________________________________________________________________________________________
        if(listaItemMedicoCitaDTOS.isEmpty()){
            throw new Exception("No hay disponibilidad de médicos, inténtalo más tarde");
        }

        return listaItemMedicoCitaDTOS;
    }

    private boolean verificarHoras(LocalTime horaInicioCita, LocalTime finJornada) {

        Duration diferencia = Duration.between(finJornada, horaInicioCita);

        // Obtener la diferencia en minutos
        long minutosDiferencia = Math.abs(diferencia.toMinutes());

        if(minutosDiferencia>=30){
            return true;
        }

        return false;
    }



    @Override
    public int solicitarCita(SolicitudCitaDTO solicitudCitaDTO) throws Exception {





        return 0;
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
                    i.getPago().getId()
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
                  s.getPago().getId()
            ));

        }

        return itemSolicitudCitaDTOS;
    }

    private String obtenerServicios(SolicitudCita s) {

        StringBuilder servicios = new StringBuilder();

        for(Servicio servicio:s.getServicios()){
            servicios.append(servicio.getNombre()).append("\r\n");
        }

        return servicios.toString();
    }

    @Override
    public int pagar(MetodoPagoDTO metodoPagoDTO) throws Exception{

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
        pagoRepository.save(pagoEncontrado);

        return metodoPagoRegistrado.getId();
    }

    public long generateSequence(String seqName) {
        DatabaseSequence counter = mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq",1), options().returnNew(true).upsert(true),
                DatabaseSequence.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }

}
