package co.edu.uniquindio.barberiavip.modelo.entidades;

import co.edu.uniquindio.barberiavip.modelo.enums.Estado;
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
public class SolicitudCita {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private float costo;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private Estado estado;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Cliente cliente;

    @ManyToMany(mappedBy = "citas")
    private List<Servicio> servicios;

    @OneToOne(mappedBy = "solicitudCita")
    private Pago pago;

}
