package br.com.fplbr.pilot.aerodromos.domain.repository;

import br.com.fplbr.pilot.aerodromos.domain.model.Aerodromo;

import java.util.List;
import java.util.Optional;

/**
 * Interface para o repositÃƒÂ³rio de aerÃƒÂ³dromos.
 * Define as operaÃƒÂ§ÃƒÂµes de persistÃƒÂªncia para a entidade Aerodromo.
 */
public interface AerodromoRepository {
    
    /**
     * Busca um aerÃƒÂ³dromo pelo seu cÃƒÂ³digo ICAO.
     *
     * @param icao CÃƒÂ³digo ICAO do aerÃƒÂ³dromo (4 letras)
     * @return Um Optional contendo o aerÃƒÂ³dromo, ou vazio se nÃƒÂ£o encontrado
     * @throws IllegalArgumentException se o cÃƒÂ³digo ICAO for nulo ou vazio
     */
    Optional<Aerodromo> buscarPorIcao(String icao);
    
    /**
     * Busca um aerÃƒÂ³dromo pelo seu cÃƒÂ³digo IATA.
     *
     * @param iata CÃƒÂ³digo IATA do aerÃƒÂ³dromo (3 letras)
     * @return Um Optional contendo o aerÃƒÂ³dromo, ou vazio se nÃƒÂ£o encontrado
     * @throws IllegalArgumentException se o cÃƒÂ³digo IATA for nulo ou vazio
     */
    Optional<Aerodromo> buscarPorIata(String iata);

    /**
     * Busca aerÃƒÂ³dromos por termo de busca, que pode ser parte do nome, cÃƒÂ³digo ICAO, IATA, cidade ou estado.
     *
     * @param termo Termo de busca
     * @param uf Filtro opcional por UF
     * @param pagina NÃƒÂºmero da pÃƒÂ¡gina (comeÃƒÂ§ando em 0)
     * @param tamanhoPagina Quantidade de itens por pÃƒÂ¡gina
     * @return Lista de aerÃƒÂ³dromos que correspondem aos critÃƒÂ©rios de busca
     */
    List<Aerodromo> buscar(String termo, String uf, int pagina, int tamanhoPagina);
    
    /**
     * Busca aerÃƒÂ³dromos por cidade.
     *
     * @param cidade Nome da cidade para busca
     * @param limite NÃƒÂºmero mÃƒÂ¡ximo de resultados a retornar
     * @return Lista de aerÃƒÂ³dromos na cidade especificada
     */
    List<Aerodromo> buscarPorCidade(String cidade, int limite);
    
    /**
     * Busca aerÃƒÂ³dromos por UF.
     *
     * @param uf Sigla da UF para busca (ex: SP, RJ)
     * @param limite NÃƒÂºmero mÃƒÂ¡ximo de resultados a retornar
     * @return Lista de aerÃƒÂ³dromos no estado especificado
     */
    List<Aerodromo> buscarPorUf(String uf, int limite);

    /**
     * Conta quantos aerÃƒÂ³dromos correspondem aos critÃƒÂ©rios de busca.
     *
     * @param termo Termo de busca
     * @param uf Filtro opcional por UF
     * @return Total de aerÃƒÂ³dromos encontrados
     */
    long contar(String termo, String uf);

    /**
     * Salva um novo aerÃƒÂ³dromo ou atualiza um existente.
     *
     * @param aerodromo O aerÃƒÂ³dromo a ser salvo/atualizado
     * @return O aerÃƒÂ³dromo salvo/atualizado
     * @throws IllegalArgumentException se o aerÃƒÂ³dromo for nulo
     */
    Aerodromo salvar(Aerodromo aerodromo);

    /**
     * Remove um aerÃƒÂ³dromo pelo seu cÃƒÂ³digo ICAO.
     *
     * @param icao CÃƒÂ³digo ICAO do aerÃƒÂ³dromo a ser removido
     * @return true se o aerÃƒÂ³dromo foi removido, false se nÃƒÂ£o foi encontrado
     * @throws IllegalArgumentException se o cÃƒÂ³digo ICAO for nulo ou vazio
     */
    boolean removerPorIcao(String icao);

    /**
     * Verifica se um aerÃƒÂ³dromo com o cÃƒÂ³digo ICAO especificado existe.
     *
     * @param icao CÃƒÂ³digo ICAO do aerÃƒÂ³dromo
     * @return true se o aerÃƒÂ³dromo existe, false caso contrÃƒÂ¡rio
     * @throws IllegalArgumentException se o cÃƒÂ³digo ICAO for nulo ou vazio
     */
    boolean existePorIcao(String icao);
    
    /**
     * Verifica se um aerÃƒÂ³dromo com o cÃƒÂ³digo IATA especificado existe.
     *
     * @param iata CÃƒÂ³digo IATA do aerÃƒÂ³dromo
     * @return true se o aerÃƒÂ³dromo existe, false caso contrÃƒÂ¡rio
     * @throws IllegalArgumentException se o cÃƒÂ³digo IATA for nulo ou vazio
     */
    boolean existePorIata(String iata);
    
    /**
     * Busca todos os aerÃƒÂ³dromos que possuem pistas com as caracterÃƒÂ­sticas especificadas.
     *
     * @param comprimentoMinimo Comprimento mÃƒÂ­nimo da pista em metros
     * @param superficie Tipo de superfÃƒÂ­cie da pista (opcional)
     * @param possuiIls Se a pista deve ter ILS
     * @return Lista de aerÃƒÂ³dromos que atendem aos critÃƒÂ©rios
     */
    List<Aerodromo> buscarPorCaracteristicasPista(Double comprimentoMinimo, String superficie, Boolean possuiIls);
}
