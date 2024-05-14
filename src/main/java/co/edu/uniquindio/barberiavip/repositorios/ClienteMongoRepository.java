package co.edu.uniquindio.barberiavip.repositorios;

import co.edu.uniquindio.barberiavip.modelo.entidades.Cliente;
import co.edu.uniquindio.barberiavip.modelo.entidades.ClienteMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteMongoRepository extends MongoRepository<ClienteMongo, Integer> {

    ClienteMongo findByEmail(String email);
}
