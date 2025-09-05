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
 * ServiÃ§o de aplicaÃ§Ã£o para operaÃ§Ãµes relacionadas a aerÃ³dromos.
 * Coordena as operaÃ§Ãµes de negÃ³cio e delega a persistÃªncia para o repositÃ³rio.
 */
@ApplicationScoped
public class AerodromoService {

    @Inject
    AerodromoRepository aerodromoRepository;

    /**
     * Busca um aerÃ³dromo pelo seu cÃ³digo ICAO.
     *
     * @param icao CÃ³digo ICAO do aerÃ³dromo (4 letras)
     * @return Um Optional contendo o aerÃ³dromo, se encontrado
     */
    public Optional<Aerodromo> buscarPorIcao(String icao) {
        return aerodromoRepository.buscarPorIcao(icao.toUpperCase());
    }

    /**
     * Busca aerÃ³dromos por termo de busca, que pode ser parte do nome, cÃ³digo ICAO, cidade ou estado.
     *
     * @param termo Termo de busca
     * @param uf Filtro opcional por UF
     * @param pagina NÃºmero da pÃ¡gina (comeÃ§ando em 0)
     * @param tamanhoPagina Quantidade de itens por pÃ¡gina
     * @return Lista de aerÃ³dromos que correspondem aos critÃ©rios de busca
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
     * Conta quantos aerÃ³dromos correspondem aos critÃ©rios de busca.
     *
     * @param termo Termo de busca
     * @param uf Filtro opcional por UF
     * @return Total de aerÃ³dromos encontrados
     */
    public long contarAerodromos(String termo, String uf) {
        return aerodromoRepository.contar(
            termo != null ? termo.trim() : null,
            uf != null ? uf.toUpperCase() : null
        );
    }

    /**
     * Busca aerÃ³dromos para sugestÃµes no formulÃ¡rio de plano de voo.
     * @param termo Termo de busca (pode ser cÃ³digo ICAO, IATA, nome da cidade ou UF)
     * @param limite NÃºmero mÃ¡ximo de resultados a retornar
     * @return Lista de DTOs de aerÃ³dromos que correspondem ao critÃ©rio de busca
     */
    public List<AerodromoDTO> buscarSugestoesAerodromos(String termo, int limite) {
        // Se o termo tem 3 ou 4 letras, pode ser um cÃ³digo ICAO ou IATA
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
        
        // Busca genÃ©rica por nome, cidade ou UF
        return buscarAerodromos(termo, null, 0, limite);
    }

    /**
     * Salva um novo aerÃ³dromo ou atualiza um existente.
     *
     * @param aerodromo AerÃ³dromo a ser salvo/atualizado
     * @return O aerÃ³dromo salvo/atualizado
     * @throws IllegalArgumentException Se o aerÃ³dromo for nulo ou o cÃ³digo ICAO for invÃ¡lido
     */
    @Transactional
    public Aerodromo salvarAerodromo(Aerodromo aerodromo) {
        if (aerodromo == null) {
            throw new IllegalArgumentException("AerÃ³dromo nÃ£o pode ser nulo");
        }
        
        if (aerodromo.getIcao() == null || aerodromo.getIcao().trim().length() != 4) {
            throw new IllegalArgumentException("CÃ³digo ICAO invÃ¡lido. Deve conter 4 caracteres");
        }
        
        // Garante que o cÃ³digo ICAO esteja em maiÃºsculas
        // Ajuste: domÃ­nio usa campos imutÃ¡veis; recria com builder
        aerodromo = aerodromo.toBuilder()
                .icao(aerodromo.getIcao().toUpperCase().trim())
                .build();
        
        return aerodromoRepository.salvar(aerodromo);
    }

    /**
     * Remove um aerÃ³dromo pelo seu cÃ³digo ICAO.
     *
     * @param icao CÃ³digo ICAO do aerÃ³dromo a ser removido
     * @return true se o aerÃ³dromo foi removido, false se nÃ£o foi encontrado
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
     * Verifica se um aerÃ³dromo Ã© terminal com base no cÃ³digo ICAO.
     *
     * @param icao CÃ³digo ICAO do aerÃ³dromo
     * @return true se for um aerÃ³dromo terminal, false caso contrÃ¡rio
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
     * Busca aerÃ³dromos que atendam a critÃ©rios especÃ­ficos de pista.
     *
     * @param comprimentoMinimo Comprimento mÃ­nimo da pista em metros
     * @param superficie Tipo de superfÃ­cie da pista (opcional)
     * @param possuiIls Se a pista deve ter ILS
     * @return Lista de aerÃ³dromos que atendem aos critÃ©rios
     */
    public List<Aerodromo> buscarPorCaracteristicasPista(Double comprimentoMinimo, String superficie, Boolean possuiIls) {
        return aerodromoRepository.buscarPorCaracteristicasPista(comprimentoMinimo, superficie, possuiIls);
    }
}
