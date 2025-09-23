package br.com.fplbr.pilot.piloto.application.service;

import br.com.fplbr.pilot.piloto.application.dto.PilotoDTO;
import br.com.fplbr.pilot.piloto.domain.entity.Piloto;
import br.com.fplbr.pilot.piloto.infrastructure.persistence.PilotoRepository;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Slf4j
public class PilotoService {

    @Inject
    PilotoRepository pilotoRepository;

    @Transactional
    public PilotoDTO criarPiloto(PilotoDTO dto) {
        log.info("Criando piloto: {}", dto.getNome());
        
        // Verificar se já existe piloto cadastrado
        if (pilotoRepository.count() > 0) {
            throw new IllegalArgumentException("Já existe um piloto cadastrado. Apenas um piloto pode ser cadastrado por vez.");
        }
        
        // Verificar se código ANAC já existe
        if (pilotoRepository.existsByCodigoAnac(dto.getCodigoAnac())) {
            throw new IllegalArgumentException("Código ANAC já cadastrado: " + dto.getCodigoAnac());
        }
        
        Piloto piloto = Piloto.builder()
                .nome(dto.getNome())
                .codigoAnac(dto.getCodigoAnac())
                .nomePista(dto.getNomePista())
                .build();
        
        pilotoRepository.persist(piloto);
        log.info("Piloto criado com sucesso: ID={}, Nome={}", piloto.getId(), piloto.getNome());
        
        return toDTO(piloto);
    }

    @Transactional
    public PilotoDTO atualizarPiloto(Long id, PilotoDTO dto) {
        log.info("Atualizando piloto ID: {}", id);
        
        Piloto piloto = pilotoRepository.findById(id);
        if (piloto == null) {
            throw new IllegalArgumentException("Piloto não encontrado com ID: " + id);
        }
        
        // Verificar se código ANAC já existe em outro piloto
        Piloto pilotoComMesmoCodigo = pilotoRepository.findByCodigoAnac(dto.getCodigoAnac());
        if (pilotoComMesmoCodigo != null && !pilotoComMesmoCodigo.getId().equals(id)) {
            throw new IllegalArgumentException("Código ANAC já cadastrado: " + dto.getCodigoAnac());
        }
        
        piloto.setNome(dto.getNome());
        piloto.setCodigoAnac(dto.getCodigoAnac());
        piloto.setNomePista(dto.getNomePista());
        
        pilotoRepository.persist(piloto);
        log.info("Piloto atualizado com sucesso: ID={}, Nome={}", piloto.getId(), piloto.getNome());
        
        return toDTO(piloto);
    }

    public Optional<PilotoDTO> buscarPilotoPorId(Long id) {
        Piloto piloto = pilotoRepository.findById(id);
        return piloto != null ? Optional.of(toDTO(piloto)) : Optional.empty();
    }

    public Optional<PilotoDTO> buscarPilotoPorCodigoAnac(String codigoAnac) {
        Piloto piloto = pilotoRepository.findByCodigoAnac(codigoAnac);
        return piloto != null ? Optional.of(toDTO(piloto)) : Optional.empty();
    }

    public List<PilotoDTO> listarPilotos() {
        return pilotoRepository.listAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public boolean existePiloto() {
        return pilotoRepository.count() > 0;
    }

    private PilotoDTO toDTO(Piloto piloto) {
        return PilotoDTO.builder()
                .id(piloto.getId())
                .nome(piloto.getNome())
                .codigoAnac(piloto.getCodigoAnac())
                .nomePista(piloto.getNomePista())
                .build();
    }
}
