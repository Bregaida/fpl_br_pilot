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
 * ServiÃƒÂ§o de aplicaÃƒÂ§ÃƒÂ£o para operaÃƒÂ§ÃƒÂµes relacionadas a aerÃƒÂ³dromos.
 * Coordena as operaÃƒÂ§ÃƒÂµes de negÃƒÂ³cio e delega a persistÃƒÂªncia para o repositÃƒÂ³rio.
 */
@ApplicationScoped
public class AerodromoService {

    @Inject
    AerodromoRepository aerodromoRepository;

    /**
     * Busca um aerÃƒÂ³dromo pelo seu cÃƒÂ³digo ICAO.
     *
     * @param icao CÃƒÂ³digo ICAO do aerÃƒÂ³dromo (4 letras)
     * @return Um Optional contendo o aerÃƒÂ³dromo, se encontrado
     */
    public Optional<Aerodromo> buscarPorIcao(String icao) {
        return aerodromoRepository.buscarPorIcao(icao.toUpperCase());
    }

    /**
     * Busca aerÃƒÂ³dromos por termo de busca, que pode ser parte do nome, cÃƒÂ³digo ICAO, cidade ou estado.
     *
     * @param termo Termo de busca
     * @param uf Filtro opcional por UF
     * @param pagina NÃƒÂºmero da pÃƒÂ¡gina (comeÃƒÂ§ando em 0)
     * @param tamanhoPagina Quantidade de itens por pÃƒÂ¡gina
     * @return Lista de aerÃƒÂ³dromos que correspondem aos critÃƒÂ©rios de busca
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
     * Conta quantos aerÃƒÂ³dromos correspondem aos critÃƒÂ©rios de busca.
     *
     * @param termo Termo de busca
     * @param uf Filtro opcional por UF
     * @return Total de aerÃƒÂ³dromos encontrados
     */
    public long contarAerodromos(String termo, String uf) {
        return aerodromoRepository.contar(
            termo != null ? termo.trim() : null,
            uf != null ? uf.toUpperCase() : null
        );
    }

    /**
     * Busca aerÃƒÂ³dromos para sugestÃƒÂµes no formulÃƒÂ¡rio de plano de voo.
     * @param termo Termo de busca (pode ser cÃƒÂ³digo ICAO, IATA, nome da cidade ou UF)
     * @param limite NÃƒÂºmero mÃƒÂ¡ximo de resultados a retornar
     * @return Lista de DTOs de aerÃƒÂ³dromos que correspondem ao critÃƒÂ©rio de busca
     */
    public List<AerodromoDTO> buscarSugestoesAerodromos(String termo, int limite) {
        // Se o termo tem 3 ou 4 letras, pode ser um cÃƒÂ³digo ICAO ou IATA
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
        
        // Busca genÃƒÂ©rica por nome, cidade ou UF
        return buscarAerodromos(termo, null, 0, limite);
    }

    /**
     * Salva um novo aerÃƒÂ³dromo ou atualiza um existente.
     *
     * @param aerodromo AerÃƒÂ³dromo a ser salvo/atualizado
     * @return O aerÃƒÂ³dromo salvo/atualizado
     * @throws IllegalArgumentException Se o aerÃƒÂ³dromo for nulo ou o cÃƒÂ³digo ICAO for invÃƒÂ¡lido
     */
    @Transactional
    public Aerodromo salvarAerodromo(Aerodromo aerodromo) {
        if (aerodromo == null) {
            throw new IllegalArgumentException("AerÃƒÂ³dromo nÃƒÂ£o pode ser nulo");
        }
        
        if (aerodromo.getIcao() == null || aerodromo.getIcao().trim().length() != 4) {
            throw new IllegalArgumentException("CÃƒÂ³digo ICAO invÃƒÂ¡lido. Deve conter 4 caracteres");
        }
        
        // Garante que o cÃƒÂ³digo ICAO esteja em maiÃƒÂºsculas
        // Ajuste: domÃƒÂ­nio usa campos imutÃƒÂ¡veis; recria com builder
        aerodromo = aerodromo.toBuilder()
                .icao(aerodromo.getIcao().toUpperCase().trim())
                .build();
        
        return aerodromoRepository.salvar(aerodromo);
    }

    /**
     * Remove um aerÃƒÂ³dromo pelo seu cÃƒÂ³digo ICAO.
     *
     * @param icao CÃƒÂ³digo ICAO do aerÃƒÂ³dromo a ser removido
     * @return true se o aerÃƒÂ³dromo foi removido, false se nÃƒÂ£o foi encontrado
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
     * Verifica se um aerÃƒÂ³dromo ÃƒÂ© terminal com base no cÃƒÂ³digo ICAO.
     *
     * @param icao CÃƒÂ³digo ICAO do aerÃƒÂ³dromo
     * @return true se for um aerÃƒÂ³dromo terminal, false caso contrÃƒÂ¡rio
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
     * Busca aerÃƒÂ³dromos que atendam a critÃƒÂ©rios especÃƒÂ­ficos de pista.
     *
     * @param comprimentoMinimo Comprimento mÃƒÂ­nimo da pista em metros
     * @param superficie Tipo de superfÃƒÂ­cie da pista (opcional)
     * @param possuiIls Se a pista deve ter ILS
     * @return Lista de aerÃƒÂ³dromos que atendem aos critÃƒÂ©rios
     */
    public List<Aerodromo> buscarPorCaracteristicasPista(Double comprimentoMinimo, String superficie, Boolean possuiIls) {
        return aerodromoRepository.buscarPorCaracteristicasPista(comprimentoMinimo, superficie, possuiIls);
    }
}
