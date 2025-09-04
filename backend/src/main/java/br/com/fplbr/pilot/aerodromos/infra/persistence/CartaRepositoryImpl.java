package br.com.fplbr.pilot.aerodromos.infra.persistence;

import br.com.fplbr.pilot.aerodromos.dto.CartaAerodromoDTO;
import br.com.fplbr.pilot.aerodromos.ports.out.CartaRepositoryPort;
import io.quarkus.hibernate.orm.panache.Panache;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CartaRepositoryImpl implements CartaRepositoryPort {

    @Override
    @Transactional
    public void upsertAll(List<CartaAerodromoDTO> cartas) {
        for (CartaAerodromoDTO dto : cartas) {
            // Try to find existing chart by ICAO and title
            CartaEntity entity = CartaEntity.find("icao = ?1 and titulo = ?2", 
                    dto.getIcao(), dto.getTitulo()).firstResult();
            
            if (entity == null) {
                entity = new CartaEntity();
            }
            
            // Update entity with DTO data
            entity.icao = dto.getIcao();
            entity.titulo = dto.getTitulo();
            entity.tipo = dto.getTipo();
            entity.caminho = dto.getCaminho();
            entity.hash = dto.getHash();
            entity.ciclo = dto.getCiclo();
            
            // Save or update
            Panache.getEntityManager().merge(entity);
        }
    }

    @Override
    public List<CartaAerodromoDTO> porIcao(String icao) {
        return CartaEntity.<CartaEntity>list("icao", icao)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    private CartaAerodromoDTO toDTO(CartaEntity entity) {
        return CartaAerodromoDTO.builder()
                .icao(entity.icao)
                .titulo(entity.titulo)
                .tipo(entity.tipo)
                .caminho(entity.caminho)
                .hash(entity.hash)
                .ciclo(entity.ciclo)
                .build();
    }
}
