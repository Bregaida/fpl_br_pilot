package br.com.fplbr.pilot.aerodromos.domain.repository;

import br.com.fplbr.pilot.aerodromos.domain.model.Aerodromo;

import java.util.List;
import java.util.Optional;

/**
 * Interface para o repositÃ³rio de aerÃ³dromos.
 * Define as operaÃ§Ãµes de persistÃªncia para a entidade Aerodromo.
 */
public interface AerodromoRepository {
    
    /**
     * Busca um aerÃ³dromo pelo seu cÃ³digo ICAO.
     *
     * @param icao CÃ³digo ICAO do aerÃ³dromo (4 letras)
     * @return Um Optional contendo o aerÃ³dromo, ou vazio se nÃ£o encontrado
     * @throws IllegalArgumentException se o cÃ³digo ICAO for nulo ou vazio
     */
    Optional<Aerodromo> buscarPorIcao(String icao);
    
    /**
     * Busca um aerÃ³dromo pelo seu cÃ³digo IATA.
     *
     * @param iata CÃ³digo IATA do aerÃ³dromo (3 letras)
     * @return Um Optional contendo o aerÃ³dromo, ou vazio se nÃ£o encontrado
     * @throws IllegalArgumentException se o cÃ³digo IATA for nulo ou vazio
     */
    Optional<Aerodromo> buscarPorIata(String iata);

    /**
     * Busca aerÃ³dromos por termo de busca, que pode ser parte do nome, cÃ³digo ICAO, IATA, cidade ou estado.
     *
     * @param termo Termo de busca
     * @param uf Filtro opcional por UF
     * @param pagina NÃºmero da pÃ¡gina (comeÃ§ando em 0)
     * @param tamanhoPagina Quantidade de itens por pÃ¡gina
     * @return Lista de aerÃ³dromos que correspondem aos critÃ©rios de busca
     */
    List<Aerodromo> buscar(String termo, String uf, int pagina, int tamanhoPagina);
    
    /**
     * Busca aerÃ³dromos por cidade.
     *
     * @param cidade Nome da cidade para busca
     * @param limite NÃºmero mÃ¡ximo de resultados a retornar
     * @return Lista de aerÃ³dromos na cidade especificada
     */
    List<Aerodromo> buscarPorCidade(String cidade, int limite);
    
    /**
     * Busca aerÃ³dromos por UF.
     *
     * @param uf Sigla da UF para busca (ex: SP, RJ)
     * @param limite NÃºmero mÃ¡ximo de resultados a retornar
     * @return Lista de aerÃ³dromos no estado especificado
     */
    List<Aerodromo> buscarPorUf(String uf, int limite);

    /**
     * Conta quantos aerÃ³dromos correspondem aos critÃ©rios de busca.
     *
     * @param termo Termo de busca
     * @param uf Filtro opcional por UF
     * @return Total de aerÃ³dromos encontrados
     */
    long contar(String termo, String uf);

    /**
     * Salva um novo aerÃ³dromo ou atualiza um existente.
     *
     * @param aerodromo O aerÃ³dromo a ser salvo/atualizado
     * @return O aerÃ³dromo salvo/atualizado
     * @throws IllegalArgumentException se o aerÃ³dromo for nulo
     */
    Aerodromo salvar(Aerodromo aerodromo);

    /**
     * Remove um aerÃ³dromo pelo seu cÃ³digo ICAO.
     *
     * @param icao CÃ³digo ICAO do aerÃ³dromo a ser removido
     * @return true se o aerÃ³dromo foi removido, false se nÃ£o foi encontrado
     * @throws IllegalArgumentException se o cÃ³digo ICAO for nulo ou vazio
     */
    boolean removerPorIcao(String icao);

    /**
     * Verifica se um aerÃ³dromo com o cÃ³digo ICAO especificado existe.
     *
     * @param icao CÃ³digo ICAO do aerÃ³dromo
     * @return true se o aerÃ³dromo existe, false caso contrÃ¡rio
     * @throws IllegalArgumentException se o cÃ³digo ICAO for nulo ou vazio
     */
    boolean existePorIcao(String icao);
    
    /**
     * Verifica se um aerÃ³dromo com o cÃ³digo IATA especificado existe.
     *
     * @param iata CÃ³digo IATA do aerÃ³dromo
     * @return true se o aerÃ³dromo existe, false caso contrÃ¡rio
     * @throws IllegalArgumentException se o cÃ³digo IATA for nulo ou vazio
     */
    boolean existePorIata(String iata);
    
    /**
     * Busca todos os aerÃ³dromos que possuem pistas com as caracterÃ­sticas especificadas.
     *
     * @param comprimentoMinimo Comprimento mÃ­nimo da pista em metros
     * @param superficie Tipo de superfÃ­cie da pista (opcional)
     * @param possuiIls Se a pista deve ter ILS
     * @return Lista de aerÃ³dromos que atendem aos critÃ©rios
     */
    List<Aerodromo> buscarPorCaracteristicasPista(Double comprimentoMinimo, String superficie, Boolean possuiIls);
}
