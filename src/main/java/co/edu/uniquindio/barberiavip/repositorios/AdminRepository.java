package co.edu.uniquindio.barberiavip.repositorios;

import co.edu.uniquindio.barberiavip.modelo.entidades.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Administrador, Integer> {

    @Query("select a from Administrador a where a.correo = :email")
    Administrador findByCorreo(String email);
}
