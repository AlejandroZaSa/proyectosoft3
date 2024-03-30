package co.edu.uniquindio.barberiavip.repositorios;

import co.edu.uniquindio.barberiavip.modelo.entidades.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
}
