package br.com.fplbr.pilot.aerodromos.infrastructure.persistence;

import br.com.fplbr.pilot.aerodromos.domain.model.Aerodromo;
import br.com.fplbr.pilot.aerodromos.domain.model.Pista;
import br.com.fplbr.pilot.aerodromos.domain.repository.AerodromoRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação do repositório de aeródromos usando Panache.
 */
@ApplicationScoped
public class AerodromoPanacheRepository implements AerodromoRepository {

    @Override
    public Optional<Aerodromo> buscarPorIcao(String icao) {
        if (icao == null || icao.trim().isEmpty()) {
            throw new IllegalArgumentException("Código ICAO não pode ser nulo ou vazio");
        }
        return AerodromoEntity.findByIdOptional(icao.toUpperCase())
                .map(entity -> ((AerodromoEntity) entity).toDomain());
    }

    @Override
    public Optional<Aerodromo> buscarPorIata(String iata) {
        if (iata == null || iata.trim().isEmpty()) {
            throw new IllegalArgumentException("Código IATA não pode ser nulo ou vazio");
        }
        return AerodromoEntity.find("UPPER(iata) = ?1", iata.trim().toUpperCase())
                .project(AerodromoEntity.class)
                .firstResultOptional()
                .map(AerodromoEntity::toDomain);
    }

    @Override
    public List<Aerodromo> buscar(String termo, String uf, int pagina, int tamanhoPagina) {
        String termoBusca = termo != null ? termo.trim().toUpperCase() : "";
        String ufBusca = uf != null ? uf.trim().toUpperCase() : "";
        
        // Se o termo tem 3 ou 4 caracteres, pode ser um código ICAO ou IATA
        if (termoBusca.length() == 4) {
            // Tenta buscar por ICAO exato
            var aerodromo = buscarPorIcao(termoBusca);
            if (aerodromo.isPresent()) {
                return List.of(aerodromo.get());
            }
        } else if (termoBusca.length() == 3) {
            // Tenta buscar por IATA exato
            var aerodromos = buscarPorIata(termoBusca)
                    .map(List::of)
                    .orElseGet(List::of);
            if (!aerodromos.isEmpty()) {
                return aerodromos;
            }
        }
        
        // Se chegou aqui, faz a busca normal
        StringBuilder query = new StringBuilder();
        Parameters params = Parameters.with("page", pagina).and("pageSize", Math.min(tamanhoPagina, 50));
        
        if (!termoBusca.isEmpty()) {
            String termoLike = "%" + termoBusca + "%";
            query.append("where (upper(icao) like :termo ")
                .append("or upper(iata) like :termo ")
                .append("or upper(nome) like :termo ")
                .append("or upper(municipio) like :termo) ");
            params.and("termo", termoLike);
        }
        
        if (!ufBusca.isEmpty()) {
            if (query.length() > 0) query.append("and ");
            else query.append("where ");
            query.append("upper(uf) = :uf ");
            params.and("uf", ufBusca);
        }
        
        // Ordenação priorizando correspondências exatas
        query.append("order by ")
            .append("case ")
            .append("when upper(icao) = :exactTerm then 1 ")
            .append("when upper(iata) = :exactTerm then 2 ")
            .append("when upper(nome) like :startsWithTerm then 3 ")
            .append("when upper(municipio) like :startsWithTerm then 4 ")
            .append("else 5 ")
            .append("end, nome");
            
        params.and("exactTerm", termoBusca)
              .and("startsWithTerm", termoBusca + "%");
        
        return AerodromoEntity.find(query.toString(), params)
                .page(pagina, Math.min(tamanhoPagina, 50))
                .stream()
                .map(AerodromoEntity.class::cast)
                .map(AerodromoEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Aerodromo> buscarPorCidade(String cidade, int limite) {
        if (cidade == null || cidade.trim().isEmpty()) {
            throw new IllegalArgumentException("Cidade não pode ser nula ou vazia");
        }
        
        String cidadeBusca = "%" + cidade.trim().toUpperCase() + "%";
        
        return AerodromoEntity.find("upper(municipio) like :cidade order by nome", 
                Parameters.with("cidade", cidadeBusca))
            .range(0, Math.min(limite, 50))
            .stream()
            .map(AerodromoEntity.class::cast)
            .map(AerodromoEntity::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Aerodromo> buscarPorUf(String uf, int limite) {
        if (uf == null || uf.trim().isEmpty()) {
            throw new IllegalArgumentException("UF não pode ser nula ou vazia");
        }
        
        String ufBusca = uf.trim().toUpperCase();
        
        return AerodromoEntity.find("upper(uf) = :uf order by municipio, nome", 
                Parameters.with("uf", ufBusca))
            .range(0, Math.min(limite, 50))
            .stream()
            .map(AerodromoEntity.class::cast)
            .map(AerodromoEntity::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public long contar(String termo, String uf) {
        String termoBusca = termo != null ? termo.trim().toUpperCase() : "";
        String ufBusca = uf != null ? uf.trim().toUpperCase() : "";
        
        if (termoBusca.isEmpty() && ufBusca.isEmpty()) {
            return AerodromoEntity.count();
        }
        
        StringBuilder query = new StringBuilder();
        Parameters params = Parameters.with("dummy", 0); // Parâmetro dummy para inicializar
        
        if (!termoBusca.isEmpty()) {
            query.append("and (upper(icao) like :termo ")
                .append("or upper(iata) like :termo ")
                .append("or upper(nome) like :termo ")
                .append("or upper(municipio) like :termo) ");
            params.and("termo", "%" + termoBusca + "%");
        }
        
        if (!ufBusca.isEmpty()) {
            query.append("and upper(uf) = :uf ");
            params.and("uf", ufBusca);
        }
        
        return AerodromoEntity.count("where 1=1 " + query, params);
    }

    @Override
    @Transactional
    public Aerodromo salvar(Aerodromo aerodromo) {
        AerodromoEntity entity = AerodromoEntity.fromDomain(aerodromo);
        
        // Se for uma atualização, primeiro carregamos a entidade existente
        if (aerodromo.getIcao() != null && AerodromoEntity.count("icao", aerodromo.getIcao().toUpperCase()) > 0) {
            // Atualiza a entidade existente
            AerodromoEntity existingEntity = AerodromoEntity.findById(aerodromo.getIcao().toUpperCase());
            updateEntity(existingEntity, entity);
            existingEntity.persist();
            return existingEntity.toDomain();
        } else {
            // Cria uma nova entidade
            entity.persist();
            return entity.toDomain();
        }
    }

    @Override
    @Transactional
    public boolean removerPorIcao(String icao) {
        if (icao == null || icao.trim().isEmpty()) {
            throw new IllegalArgumentException("Código ICAO não pode ser nulo ou vazio");
        }
        icao = icao.trim().toUpperCase();
        if (AerodromoEntity.count("upper(icao) = ?1", icao) > 0) {
            AerodromoEntity.deleteById(icao);
            return true;
        }
        return false;
    }

    @Override
    public boolean existePorIcao(String icao) {
        if (icao == null || icao.trim().isEmpty()) {
            throw new IllegalArgumentException("Código ICAO não pode ser nulo ou vazio");
        }
        return AerodromoEntity.count("upper(icao) = ?1", icao.trim().toUpperCase()) > 0;
    }
    
    @Override
    public boolean existePorIata(String iata) {
        if (iata == null || iata.trim().isEmpty()) {
            throw new IllegalArgumentException("Código IATA não pode ser nulo ou vazio");
        }
        return AerodromoEntity.count("upper(iata) = ?1", iata.trim().toUpperCase()) > 0;
    }

    @Override
    public List<Aerodromo> buscarPorCaracteristicasPista(Double comprimentoMinimo, String superficie, Boolean possuiIls) {
        StringBuilder query = new StringBuilder("select distinct a from AerodromoEntity a join a.pistas p where 1=1");
        
        if (comprimentoMinimo != null) {
            query.append(" and p.comprimentoMetros >= :comprimentoMinimo");
        }
        
        if (superficie != null && !superficie.trim().isEmpty()) {
            query.append(" and upper(p.superficie) = upper(:superficie)");
        }
        
        if (possuiIls != null) {
            query.append(" and p.ils = :possuiIls");
        }
        
        query.append(" order by a.nome");
        
        var queryObj = AerodromoEntity.getEntityManager().createQuery(query.toString(), AerodromoEntity.class);
        
        if (comprimentoMinimo != null) {
            queryObj.setParameter("comprimentoMinimo", comprimentoMinimo);
        }
        
        if (superficie != null && !superficie.trim().isEmpty()) {
            queryObj.setParameter("superficie", superficie);
        }
        
        if (possuiIls != null) {
            queryObj.setParameter("possuiIls", possuiIls);
        }
        
        return queryObj.getResultList().stream()
                .map(AerodromoEntity::toDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Atualiza os campos de uma entidade existente com base em uma entidade atualizada.
     */
    private void updateEntity(AerodromoEntity existing, AerodromoEntity updated) {
        // Atualiza os campos básicos
        existing.setIata(updated.getIata());
        existing.setNome(updated.getNome());
        existing.setMunicipio(updated.getMunicipio());
        existing.setUf(updated.getUf());
        existing.setRegiao(updated.getRegiao());
        existing.setLatitude(updated.getLatitude());
        existing.setLongitude(updated.getLongitude());
        existing.setAltitudePes(updated.getAltitudePes());
        existing.setTipo(updated.getTipo());
        existing.setUso(updated.getUso());
        existing.setCindacta(updated.getCindacta());
        existing.setInternacional(updated.isInternacional());
        existing.setTerminal(updated.isTerminal());
        existing.setHorarioFuncionamento(updated.getHorarioFuncionamento());
        existing.setTelefone(updated.getTelefone());
        existing.setEmail(updated.getEmail());
        existing.setResponsavel(updated.getResponsavel());
        existing.setObservacoes(updated.getObservacoes());
        existing.setAtivo(updated.isAtivo());
        
        // Atualiza as pistas
        if (updated.getPistas() != null) {
            // Remove as pistas existentes que não estão mais na lista atualizada
            existing.getPistas().removeIf(pistaExistente -> 
                updated.getPistas().stream()
                    .noneMatch(pistaAtualizada -> 
                        pistaAtualizada.getDesignacao() != null && 
                        pistaAtualizada.getDesignacao().equals(pistaExistente.getDesignacao()))
            );
            
            // Atualiza as pistas existentes ou adiciona novas
            for (var pistaAtualizada : updated.getPistas()) {
                boolean encontrada = false;
                
                for (var pistaExistente : existing.getPistas()) {
                    if (pistaAtualizada.getDesignacao() != null && 
                        pistaAtualizada.getDesignacao().equals(pistaExistente.getDesignacao())) {
                        // Atualiza a pista existente
                        updatePistaEntity(pistaExistente, pistaAtualizada);
                        encontrada = true;
                        break;
                    }
                }
                
                if (!encontrada && pistaAtualizada.getDesignacao() != null) {
                    // Adiciona uma nova pista
                    var novaPista = PistaEntity.fromDomain(pistaAtualizada.toDomain(), existing);
                    existing.getPistas().add(novaPista);
                }
            }
        }
        
        // Atualiza as frequências (mesma lógica das pistas)
        if (updated.getFrequencias() != null) {
            existing.getFrequencias().removeIf(freqExistente -> 
                updated.getFrequencias().stream()
                    .noneMatch(freqAtualizada -> 
                        freqAtualizada.getTipo() != null && 
                        freqAtualizada.getTipo().equals(freqExistente.getTipo()) &&
                        freqAtualizada.getValor() != null &&
                        freqAtualizada.getValor().equals(freqExistente.getValor()))
            );
            
            for (var freqAtualizada : updated.getFrequencias()) {
                boolean encontrada = false;
                
                for (var freqExistente : existing.getFrequencias()) {
                    if (freqAtualizada.getTipo() != null && 
                        freqAtualizada.getValor() != null &&
                        freqAtualizada.getTipo().equals(freqExistente.getTipo()) &&
                        freqAtualizada.getValor().equals(freqExistente.getValor())) {
                        // Atualiza a frequência existente
                        updateFrequenciaEntity(freqExistente, freqAtualizada);
                        encontrada = true;
                        break;
                    }
                }
                
                if (!encontrada && freqAtualizada.getTipo() != null && freqAtualizada.getValor() != null) {
                    // Adiciona uma nova frequência
                    var novaFreq = FrequenciaEntity.fromDomain(freqAtualizada.toDomain(), existing);
                    existing.getFrequencias().add(novaFreq);
                }
            }
        }
    }
    
    /**
     * Atualiza os campos de uma entidade de pista existente.
     */
    private void updatePistaEntity(PistaEntity existing, PistaEntity updated) {
        existing.setDesignacao(updated.getDesignacao());
        existing.setComprimentoMetros(updated.getComprimentoMetros());
        existing.setLarguraMetros(updated.getLarguraMetros());
        existing.setSuperficie(updated.getSuperficie());
        existing.setResistenciaPcn(updated.getResistenciaPcn());
        existing.setClassificacaoPcn(updated.getClassificacaoPcn());
        existing.setTora(updated.getTora());
        existing.setToda(updated.getToda());
        existing.setAsda(updated.getAsda());
        existing.setLda(updated.getLda());
        existing.setIls(updated.isIls());
        existing.setCategoriaIls(updated.getCategoriaIls());
        existing.setPapi(updated.getPapi());
        existing.setLuzesBorda(updated.getLuzesBorda());
        existing.setLuzesCentro(updated.getLuzesCentro());
        existing.setObservacoes(updated.getObservacoes());
    }
    
    /**
     * Atualiza os campos de uma entidade de frequência existente.
     */
    private void updateFrequenciaEntity(FrequenciaEntity existing, FrequenciaEntity updated) {
        existing.setTipo(updated.getTipo());
        existing.setDescricao(updated.getDescricao());
        existing.setValor(updated.getValor());
        existing.setHorarioFuncionamento(updated.getHorarioFuncionamento());
        existing.setObservacoes(updated.getObservacoes());
    }
}
