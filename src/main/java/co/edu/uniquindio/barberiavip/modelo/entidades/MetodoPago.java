package co.edu.uniquindio.barberiavip.modelo.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MetodoPago {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String numeroTarjeta;

    @Column(nullable = false)
    private String codigoSeguridad;

    @Column(nullable = false)
    private LocalDate fechaExpiracion;

    @Column(nullable = false)
    private String primerNombre;

    @Column(nullable = false)
    private String apellido;

    @OneToMany(mappedBy = "metodoPago")
    private List<Pago> pagos;

}
