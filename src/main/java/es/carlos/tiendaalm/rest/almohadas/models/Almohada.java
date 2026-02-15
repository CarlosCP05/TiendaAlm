package es.carlos.tiendaalm.rest.almohadas.models;

import es.carlos.tiendaalm.rest.tiendas.models.Tienda;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // JPA necesita un constructor vacío
@Entity
@Table(name = "ALMOHADAS")
public class Almohada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer peso;
    @Column(nullable = false)
    private Integer altura;
    @Column(nullable = false)
    private Integer ancho;
    @Column(nullable = false)
    private Integer grosor;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Tacto tacto;
    @Column(nullable = false)
    private String firmeza;

    @Builder.Default
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    @Builder.Default
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;

    /*Cambiar por stock despues de implementarlo
    @ManyToOne
    @JoinColumn(name = "tienda_id")
    private Tienda tienda;
     */
}