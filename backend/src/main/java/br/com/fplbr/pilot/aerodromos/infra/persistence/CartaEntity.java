package br.com.fplbr.pilot.aerodromos.infra.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "carta_aerodromo")
public class CartaEntity extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "icao", length = 4, nullable = false)
    public String icao;

    @Column(name = "titulo", nullable = false)
    public String titulo;
    
    @Column(name = "tipo", length = 20)
    public String tipo;
    
    @Column(name = "caminho", length = 1024)
    public String caminho;
    
    @Column(name = "hash", length = 128)
    public String hash;
    
    @Column(name = "ciclo")
    public LocalDate ciclo;
}
