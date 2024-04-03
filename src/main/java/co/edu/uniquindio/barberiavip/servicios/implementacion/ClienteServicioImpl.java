package co.edu.uniquindio.barberiavip.servicios.implementacion;

import co.edu.uniquindio.barberiavip.dto.cliente.*;
import co.edu.uniquindio.barberiavip.modelo.entidades.*;
import co.edu.uniquindio.barberiavip.modelo.enums.Estado;
import co.edu.uniquindio.barberiavip.repositorios.*;
import co.edu.uniquindio.barberiavip.servicios.interfaces.ClienteServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Override
    public int registrarse(RegistroClienteDTO clienteDTO) throws Exception {

        if (!estaRepetidoCorreo(clienteDTO.email())) {
            throw new Exception("El correo " + clienteDTO.email() + "ya está en uso");
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(clienteDTO.nombre());
        cliente.setApellido(clienteDTO.apellido());
        cliente.setEmail(clienteDTO.email());
        cliente.setTelefono(clienteDTO.telefono());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        cliente.setPassword(passwordEncoder.encode(clienteDTO.password()));

        cliente.setActivo(true);

        return clienteRepository.save(cliente).getId();
    }

    public boolean estaRepetidoCorreo(String email) {
        Cliente cliente = clienteRepository.findByEmail(email);
        Administrador admin = adminRepository.findByCorreo(email);
        return cliente == null && admin == null;
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

}
