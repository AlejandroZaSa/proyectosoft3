package co.edu.uniquindio.barberiavip.servicios.implementacion;

import co.edu.uniquindio.barberiavip.dto.administrador.CursoDTO;
import co.edu.uniquindio.barberiavip.dto.administrador.ServicioDTO;
import co.edu.uniquindio.barberiavip.dto.barberia.CambioPasswordDTO;
import co.edu.uniquindio.barberiavip.dto.barberia.EmailDTO;
import co.edu.uniquindio.barberiavip.dto.barberia.ItemCursoDTO;
import co.edu.uniquindio.barberiavip.dto.barberia.ItemServicioDTO;
import co.edu.uniquindio.barberiavip.modelo.entidades.Administrador;
import co.edu.uniquindio.barberiavip.modelo.entidades.Cliente;
import co.edu.uniquindio.barberiavip.modelo.entidades.Curso;
import co.edu.uniquindio.barberiavip.modelo.entidades.Servicio;
import co.edu.uniquindio.barberiavip.modelo.enums.Estado;
import co.edu.uniquindio.barberiavip.repositorios.AdminRepository;
import co.edu.uniquindio.barberiavip.repositorios.ClienteRepository;
import co.edu.uniquindio.barberiavip.repositorios.CursoRepository;
import co.edu.uniquindio.barberiavip.repositorios.ServicioRepository;
import co.edu.uniquindio.barberiavip.servicios.interfaces.BarberiaServicio;
import co.edu.uniquindio.barberiavip.servicios.interfaces.EmailServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BarberiaImpl implements BarberiaServicio {

    private final CursoRepository cursoRepository;
    private final ServicioRepository servicioRepository;
    private final ClienteRepository clienteRepository;
    private final AdminRepository adminRepository;
    private final EmailServicio emailServicio;

    @Override
    public List<Estado> cargarListaEstados() {
        return List.of(Estado.values());
    }

    @Override
    public List<ItemCursoDTO> listarCursos() throws Exception {

        List<Curso> cursos = cursoRepository.findAllActivos(true);

        if (cursos.isEmpty()) {
            throw new Exception("No hay cursos registrados");
        }

        List<ItemCursoDTO> listaCursos = new ArrayList<>();

        for (Curso c : cursos) {

            listaCursos.add(new ItemCursoDTO(

                    c.getId(),
                    c.getCosto(),
                    c.getNombre(),
                    c.getDescripcion(),
                    c.getFechaInicio(),
                    c.getFechaFin(),
                    c.isActivo()
            ));

        }

        return listaCursos;
    }

    @Override
    public List<ItemServicioDTO> listarServicios() throws Exception {

        List<Servicio> servicios = servicioRepository.findAllActivos(true);

        if (servicios.isEmpty()) {
            throw new Exception("No hay servicios registrados");
        }

        List<ItemServicioDTO> listaServicios = new ArrayList<>();

        for (Servicio s : servicios) {

            listaServicios.add(new ItemServicioDTO(

                    s.getId(),
                    s.getNombre(),
                    s.getDescripcion(),
                    s.getCosto(),
                    s.isActivo()
            ));

        }

        return listaServicios;
    }

    @Override
    public void cambiarPassword(CambioPasswordDTO cambioPasswordDTO) throws Exception {
        Cliente cliente = clienteRepository.findByEmail(cambioPasswordDTO.email());

        if (cliente == null) {
            Administrador admin = adminRepository.findByCorreo(cambioPasswordDTO.email());

            if (admin == null) {
                throw new Exception("El usuario con el email " + cambioPasswordDTO.email() + " no existe");
            } else {
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String passwordEncriptada = passwordEncoder.encode(cambioPasswordDTO.nuevaPassword());

                admin.setPassword(passwordEncriptada);
                adminRepository.save(admin);
            }
        } else {

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String passwordEncriptada = passwordEncoder.encode(cambioPasswordDTO.nuevaPassword());

            cliente.setPassword(passwordEncriptada);
            clienteRepository.save(cliente);
        }

    }

    @Override
    public void enviarLinkRecuperacion(String email) throws Exception {
        Cliente cliente = clienteRepository.findByEmail(email);

        if (cliente == null) {
            Administrador admin = adminRepository.findByCorreo(email);

            if (admin == null) {
                throw new Exception("El usuario con el email " + email + " no existe");
            } else {
                emailServicio.enviarEmail(new EmailDTO("Recupera tu cuenta", email, "https://software-5ncs.onrender.com/recuperar-password/"+email));

            }
        } else {

            emailServicio.enviarEmail(new EmailDTO("Recupera tu cuenta", email, "https://software-5ncs.onrender.com/recuperar-password/"+email));

        }
    }
}
