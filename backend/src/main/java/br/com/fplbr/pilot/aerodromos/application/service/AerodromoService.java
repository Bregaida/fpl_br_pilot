package br.com.fplbr.pilot.aerodromos.application.service;

import br.com.fplbr.pilot.aerodromos.application.dto.AerodromoBuscaDTO;
import br.com.fplbr.pilot.aerodromos.application.dto.AerodromoDTO;
import br.com.fplbr.pilot.aerodromos.domain.model.Aerodromo;
import br.com.fplbr.pilot.aerodromos.domain.repository.AerodromoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço de aplicação para operações relacionadas a aeródromos.
 * Coordena as operações de negócio e delega a persistência para o repositório.
 */
@ApplicationScoped
public class AerodromoService {

    @Inject
    AerodromoRepository aerodromoRepository;

    /**
     * Busca um aeródromo pelo seu código ICAO.
     *
     * @param icao Código ICAO do aeródromo (4 letras)
     * @return Um Optional contendo o aeródromo, se encontrado
     */
    public Optional<Aerodromo> buscarPorIcao(String icao) {
        return aerodromoRepository.buscarPorIcao(icao.toUpperCase());
    }

    /**
     * Busca aeródromos por termo de busca, que pode ser parte do nome, código ICAO, cidade ou estado.
     *
     * @param termo Termo de busca
     * @param uf Filtro opcional por UF
     * @param pagina Número da página (começando em 0)
     * @param tamanhoPagina Quantidade de itens por página
     * @return Lista de aeródromos que correspondem aos critérios de busca
     */
    public List<AerodromoDTO> buscarAerodromos(String termo, String uf, int pagina, int tamanhoPagina) {
        List<Aerodromo> aerodromos = aerodromoRepository.buscar(
            termo != null ? termo.trim() : null,
            uf != null ? uf.toUpperCase() : null,
            pagina,
            tamanhoPagina
        );
        return aerodromos.stream()
                .map(AerodromoDTO::fromDomain)
                .collect(Collectors.toList());
    }

    /**
     * Conta quantos aeródromos correspondem aos critérios de busca.
     *
     * @param termo Termo de busca
     * @param uf Filtro opcional por UF
     * @return Total de aeródromos encontrados
     */
    public long contarAerodromos(String termo, String uf) {
        return aerodromoRepository.contar(
            termo != null ? termo.trim() : null,
            uf != null ? uf.toUpperCase() : null
        );
    }

    /**
     * Busca aeródromos para sugestões no formulário de plano de voo.
     * @param termo Termo de busca (pode ser código ICAO, IATA, nome da cidade ou UF)
     * @param limite Número máximo de resultados a retornar
     * @return Lista de DTOs de aeródromos que correspondem ao critério de busca
     */
    public List<AerodromoDTO> buscarSugestoesAerodromos(String termo, int limite) {
        // Se o termo tem 3 ou 4 letras, pode ser um código ICAO ou IATA
        if (termo != null && (termo.length() == 3 || termo.length() == 4)) {
            // Tenta buscar por ICAO exato
            if (termo.length() == 4) {
                Optional<Aerodromo> aerodromo = buscarPorIcao(termo);
                if (aerodromo.isPresent()) {
                    return List.of(AerodromoDTO.fromDomain(aerodromo.get()));
                }
            }
            
            // Tenta buscar por IATA exato
            if (termo.length() == 3) {
                List<AerodromoDTO> porIata = buscarAerodromos(termo, null, 0, limite)
                        .stream()
                        .filter(a -> termo.equalsIgnoreCase(a.getIata()))
                        .collect(Collectors.toList());
                
                if (!porIata.isEmpty()) {
                    return porIata;
                }
            }
        }
        
        // Busca genérica por nome, cidade ou UF
        return buscarAerodromos(termo, null, 0, limite);
    }

    /**
     * Salva um novo aeródromo ou atualiza um existente.
     *
     * @param aerodromo Aeródromo a ser salvo/atualizado
     * @return O aeródromo salvo/atualizado
     * @throws IllegalArgumentException Se o aeródromo for nulo ou o código ICAO for inválido
     */
    @Transactional
    public Aerodromo salvarAerodromo(Aerodromo aerodromo) {
        if (aerodromo == null) {
            throw new IllegalArgumentException("Aeródromo não pode ser nulo");
        }
        
        if (aerodromo.getIcao() == null || aerodromo.getIcao().trim().length() != 4) {
            throw new IllegalArgumentException("Código ICAO inválido. Deve conter 4 caracteres");
        }
        
        // Garante que o código ICAO esteja em maiúsculas
        // Ajuste: domínio usa campos imutáveis; recria com builder
        aerodromo = aerodromo.toBuilder()
                .icao(aerodromo.getIcao().toUpperCase().trim())
                .build();
        
        return aerodromoRepository.salvar(aerodromo);
    }

    /**
     * Remove um aeródromo pelo seu código ICAO.
     *
     * @param icao Código ICAO do aeródromo a ser removido
     * @return true se o aeródromo foi removido, false se não foi encontrado
     */
    @Transactional
    public boolean removerAerodromo(String icao) {
        if (icao == null || icao.trim().length() != 4) {
            return false;
        }
        
        icao = icao.toUpperCase().trim();
        if (aerodromoRepository.existePorIcao(icao)) {
            return aerodromoRepository.removerPorIcao(icao);
        }
        return false;
    }

    /**
     * Verifica se um aeródromo é terminal com base no código ICAO.
     *
     * @param icao Código ICAO do aeródromo
     * @return true se for um aeródromo terminal, false caso contrário
     */
    public boolean isAerodromoTerminal(String icao) {
        if (icao == null || icao.trim().length() != 4) {
            return false;
        }
        
        return aerodromoRepository.buscarPorIcao(icao.toUpperCase())
                .map(Aerodromo::isTerminal)
                .orElse(false);
    }

    /**
     * Busca aeródromos que atendam a critérios específicos de pista.
     *
     * @param comprimentoMinimo Comprimento mínimo da pista em metros
     * @param superficie Tipo de superfície da pista (opcional)
     * @param possuiIls Se a pista deve ter ILS
     * @return Lista de aeródromos que atendem aos critérios
     */
    public List<Aerodromo> buscarPorCaracteristicasPista(Double comprimentoMinimo, String superficie, Boolean possuiIls) {
        return aerodromoRepository.buscarPorCaracteristicasPista(comprimentoMinimo, superficie, possuiIls);
    }
}
