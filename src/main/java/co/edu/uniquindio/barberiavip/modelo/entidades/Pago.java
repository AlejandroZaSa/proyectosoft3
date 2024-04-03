package co.edu.uniquindio.barberiavip.modelo.entidades;

import co.edu.uniquindio.barberiavip.modelo.enums.Estado;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pago {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private float monto;

    private LocalDate fechaPago;

    @Column(nullable = false)
    private Estado estado;

    @JoinColumn
    @ManyToOne
    private MetodoPago metodoPago;

    @OneToOne
    private Inscripcion inscripcion;

    @OneToOne
    private SolicitudCita solicitudCita;

}
