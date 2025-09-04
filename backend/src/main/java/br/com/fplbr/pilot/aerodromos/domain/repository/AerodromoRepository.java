package br.com.fplbr.pilot.aerodromos.domain.repository;

import br.com.fplbr.pilot.aerodromos.domain.model.Aerodromo;

import java.util.List;
import java.util.Optional;

/**
 * Interface para o repositório de aeródromos.
 * Define as operações de persistência para a entidade Aerodromo.
 */
public interface AerodromoRepository {
    
    /**
     * Busca um aeródromo pelo seu código ICAO.
     *
     * @param icao Código ICAO do aeródromo (4 letras)
     * @return Um Optional contendo o aeródromo, ou vazio se não encontrado
     * @throws IllegalArgumentException se o código ICAO for nulo ou vazio
     */
    Optional<Aerodromo> buscarPorIcao(String icao);
    
    /**
     * Busca um aeródromo pelo seu código IATA.
     *
     * @param iata Código IATA do aeródromo (3 letras)
     * @return Um Optional contendo o aeródromo, ou vazio se não encontrado
     * @throws IllegalArgumentException se o código IATA for nulo ou vazio
     */
    Optional<Aerodromo> buscarPorIata(String iata);

    /**
     * Busca aeródromos por termo de busca, que pode ser parte do nome, código ICAO, IATA, cidade ou estado.
     *
     * @param termo Termo de busca
     * @param uf Filtro opcional por UF
     * @param pagina Número da página (começando em 0)
     * @param tamanhoPagina Quantidade de itens por página
     * @return Lista de aeródromos que correspondem aos critérios de busca
     */
    List<Aerodromo> buscar(String termo, String uf, int pagina, int tamanhoPagina);
    
    /**
     * Busca aeródromos por cidade.
     *
     * @param cidade Nome da cidade para busca
     * @param limite Número máximo de resultados a retornar
     * @return Lista de aeródromos na cidade especificada
     */
    List<Aerodromo> buscarPorCidade(String cidade, int limite);
    
    /**
     * Busca aeródromos por UF.
     *
     * @param uf Sigla da UF para busca (ex: SP, RJ)
     * @param limite Número máximo de resultados a retornar
     * @return Lista de aeródromos no estado especificado
     */
    List<Aerodromo> buscarPorUf(String uf, int limite);

    /**
     * Conta quantos aeródromos correspondem aos critérios de busca.
     *
     * @param termo Termo de busca
     * @param uf Filtro opcional por UF
     * @return Total de aeródromos encontrados
     */
    long contar(String termo, String uf);

    /**
     * Salva um novo aeródromo ou atualiza um existente.
     *
     * @param aerodromo O aeródromo a ser salvo/atualizado
     * @return O aeródromo salvo/atualizado
     * @throws IllegalArgumentException se o aeródromo for nulo
     */
    Aerodromo salvar(Aerodromo aerodromo);

    /**
     * Remove um aeródromo pelo seu código ICAO.
     *
     * @param icao Código ICAO do aeródromo a ser removido
     * @return true se o aeródromo foi removido, false se não foi encontrado
     * @throws IllegalArgumentException se o código ICAO for nulo ou vazio
     */
    boolean removerPorIcao(String icao);

    /**
     * Verifica se um aeródromo com o código ICAO especificado existe.
     *
     * @param icao Código ICAO do aeródromo
     * @return true se o aeródromo existe, false caso contrário
     * @throws IllegalArgumentException se o código ICAO for nulo ou vazio
     */
    boolean existePorIcao(String icao);
    
    /**
     * Verifica se um aeródromo com o código IATA especificado existe.
     *
     * @param iata Código IATA do aeródromo
     * @return true se o aeródromo existe, false caso contrário
     * @throws IllegalArgumentException se o código IATA for nulo ou vazio
     */
    boolean existePorIata(String iata);
    
    /**
     * Busca todos os aeródromos que possuem pistas com as características especificadas.
     *
     * @param comprimentoMinimo Comprimento mínimo da pista em metros
     * @param superficie Tipo de superfície da pista (opcional)
     * @param possuiIls Se a pista deve ter ILS
     * @return Lista de aeródromos que atendem aos critérios
     */
    List<Aerodromo> buscarPorCaracteristicasPista(Double comprimentoMinimo, String superficie, Boolean possuiIls);
}
