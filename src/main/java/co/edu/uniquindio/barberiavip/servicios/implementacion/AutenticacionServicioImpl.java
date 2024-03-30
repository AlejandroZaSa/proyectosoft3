package co.edu.uniquindio.barberiavip.servicios.implementacion;

import co.edu.uniquindio.barberiavip.dto.autenticacionJwt.TokenDTO;
import co.edu.uniquindio.barberiavip.dto.barberia.LoginDTO;
import co.edu.uniquindio.barberiavip.modelo.entidades.Administrador;
import co.edu.uniquindio.barberiavip.modelo.entidades.Cliente;
import co.edu.uniquindio.barberiavip.repositorios.AdminRepository;
import co.edu.uniquindio.barberiavip.repositorios.ClienteRepository;
import co.edu.uniquindio.barberiavip.servicios.interfaces.AutenticacionServicio;
import co.edu.uniquindio.barberiavip.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AutenticacionServicioImpl implements AutenticacionServicio {

    private final ClienteRepository clienteRepository;
    private final AdminRepository adminRepository;

    private final JWTUtils jwtUtils;

    @Override
    public TokenDTO login(LoginDTO loginDTO) throws Exception {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Object[] datos = buscarCorreo(loginDTO);

        if (!passwordEncoder.matches(loginDTO.password(), datos[3].toString())) {
            throw new Exception("La contraseña ingresada es incorrecta");
        }

        return new TokenDTO(crearToken(datos));
    }

    private String crearToken(Object[] datos) {

        Map<String, Object> map = new HashMap<>();
        map.put("rol", datos[4]);
        map.put("nombre", datos[1]);
        map.put("id", datos[2]);

        return jwtUtils.generarToken(datos[0].toString(), map);
    }

    public Object[] buscarCorreo(LoginDTO loginDTO) throws Exception {

        String correo = "";
        int codigo = 0;
        String nombre = "";
        String password = "";
        String rol = "";

        Administrador admin = adminRepository.findByCorreo(loginDTO.email());

        if (admin == null) {

            Cliente cliente = clienteRepository.findByEmail(loginDTO.email());

            if (cliente == null) {
                throw new Exception("El usuario no existe");

            }else if(cliente.isActivo()){

                correo = cliente.getEmail();
                nombre = cliente.getNombre();
                codigo = cliente.getId();
                password = cliente.getPassword();
                rol = "cliente";
            }else{
                throw new Exception("Estás inactivo");
            }

        }else {

            correo = admin.getCorreo();
            nombre = "admin";
            codigo = 1;
            password = admin.getPassword();
            rol = "admin";
        }

        return new Object[]{correo, nombre, codigo, password, rol };
    }
}
