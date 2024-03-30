package co.edu.uniquindio.barberiavip.repositorios;

import co.edu.uniquindio.barberiavip.modelo.entidades.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Integer> {
}
