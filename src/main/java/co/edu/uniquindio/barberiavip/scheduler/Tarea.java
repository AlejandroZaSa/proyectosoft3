package co.edu.uniquindio.barberiavip.scheduler;

import co.edu.uniquindio.barberiavip.modelo.entidades.Cliente;
import co.edu.uniquindio.barberiavip.modelo.entidades.ClienteMongo;
import co.edu.uniquindio.barberiavip.repositorios.ClienteMongoRepository;
import co.edu.uniquindio.barberiavip.repositorios.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class Tarea {

    private final ClienteRepository clienteRepository;
    private final ClienteMongoRepository clienteMongoRepository;

    @Scheduled(cron = "0 0 * * * ?")
    public void ejecutarTarea() throws Exception {

        try{
            List<Cliente> clientesSql = clienteRepository.findAll();

            if(clientesSql.isEmpty()){
                throw new Exception("No hay clientes registrados en la base de datos de mysql");
            }

            List<ClienteMongo> clientesMongo = clienteMongoRepository.findAll();

            if(clientesMongo.isEmpty()){
                throw new Exception("No hay clientes registrados en la base de datos de mongodb");
            }

            compararYRegistrarClientes(clientesSql, clientesMongo);

        }catch(Exception e) {
            System.out.println("Se seguirá intentando la conexión con mysql");
        }
    }

    public void compararYRegistrarClientes(List<Cliente> clientesSql, List<ClienteMongo> clientesMongo) {
        for (ClienteMongo clienteMongo : clientesMongo) {
            boolean encontrado = false;
            for (Cliente clienteSql : clientesSql) {
                if (clienteMongo.getEmail().equals(clienteSql.getEmail())) {
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                // El clienteMongo no está registrado en MySQL, así que lo registraremos
                registrarClienteEnMysql(clienteMongo);
            }
        }
    }

    private void registrarClienteEnMysql(ClienteMongo clienteMongo) {
        Cliente cliente = new Cliente();
        cliente.setNombre(clienteMongo.getNombre());
        cliente.setApellido(clienteMongo.getApellido());
        cliente.setEmail(clienteMongo.getEmail());
        cliente.setTelefono(clienteMongo.getTelefono());
        cliente.setPassword(clienteMongo.getPassword());

        cliente.setActivo(true);

        Cliente clienteRegistrado = clienteRepository.save(cliente);
    }
}