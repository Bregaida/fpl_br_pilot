package br.com.fplbr.pilot.aerodromos.infrastructure.rest;

import br.com.fplbr.pilot.aerodromos.application.dto.AerodromoBuscaDTO;
import br.com.fplbr.pilot.aerodromos.application.dto.AerodromoDTO;
import br.com.fplbr.pilot.aerodromos.application.dto.FrequenciaDTO;
import br.com.fplbr.pilot.aerodromos.application.service.AerodromoService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Recurso REST para operações relacionadas a aeródromos.
 */
@Path("/api/v1/aerodromos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Aeródromos", description = "Operações relacionadas a aeródromos")
public class AerodromoResource {

    @Inject
    AerodromoService aerodromoService;

    @GET
    @Path("/{icao}")
    @Operation(
        summary = "Busca um aeródromo pelo código ICAO",
        description = "Retorna os detalhes de um aeródromo com base no código ICAO fornecido"
    )
    @APIResponse(
        responseCode = "200",
        description = "Aeródromo encontrado",
        content = @Content(schema = @Schema(implementation = AerodromoDTO.class))
    )
    @APIResponse(responseCode = "404", description = "Aeródromo não encontrado")
    @APIResponse(responseCode = "400", description = "Código ICAO inválido")
    public Response buscarPorIcao(
        @PathParam("icao") 
        @NotBlank(message = "Código ICAO é obrigatório")
        @Pattern(regexp = "[A-Za-z]{4}", message = "Código ICAO deve conter exatamente 4 letras")
        String icao) {
        
        return aerodromoService.buscarPorIcao(icao.toUpperCase())
                .map(aerodromo -> Response.ok(AerodromoDTO.fromDomain(aerodromo)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Operation(
        summary = "Busca/Listagem de aeródromos",
        description = "Retorna uma lista paginada filtrando por query (icao/iata/nome/UF)"
    )
    @APIResponse(
        responseCode = "200",
        description = "Lista de aeródromos",
        content = @Content(schema = @Schema(implementation = AerodromoDTO[].class))
    )
    public List<AerodromoDTO> listar(
        @Parameter(description = "Termo de busca (icao/iata/nome/UF)") @QueryParam("query") String query,
        @Parameter(description = "Filtro por UF") @QueryParam("uf") String uf,
        @Parameter(description = "Número da página (0-based)") @DefaultValue("0") @QueryParam("pagina") int pagina,
        @Parameter(description = "Tamanho da página") @DefaultValue("20") @QueryParam("tamanho") int tamanho) {
        
        return aerodromoService.buscarAerodromos(query, uf, pagina, tamanho);
    }

    @GET
    @Path("/contar")
    @Operation(
        summary = "Conta o total de aeródromos que correspondem aos critérios de busca",
        description = "Retorna o total de aeródromos que correspondem aos critérios fornecidos"
    )
    @APIResponse(
        responseCode = "200",
        description = "Total de aeródromos encontrados",
        content = @Content(schema = @Schema(implementation = Long.class))
    )
    public long contar(
        @Parameter(description = "Termo para busca (nome, código, cidade, etc.)") @QueryParam("busca") String termoBusca,
        @Parameter(description = "Filtro por UF") @QueryParam("uf") String uf) {
        
        return aerodromoService.contarAerodromos(termoBusca, uf);
    }

    @POST
    @Operation(
        summary = "Cria um novo aeródromo",
        description = "Cria um novo aeródromo com os dados fornecidos"
    )
    @APIResponse(
        responseCode = "201",
        description = "Aeródromo criado com sucesso",
        content = @Content(schema = @Schema(implementation = AerodromoDTO.class))
    )
    @APIResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    @APIResponse(responseCode = "409", description = "Já existe um aeródromo com o mesmo código ICAO")
    public Response criar(@Valid AerodromoDTO aerodromoDTO) {
        var aerodromo = aerodromoService.salvarAerodromo(aerodromoDTO.toDomain());
        return Response
                .status(Response.Status.CREATED)
                .entity(AerodromoDTO.fromDomain(aerodromo))
                .build();
    }

    @PUT
    @Path("/{icao}")
    @Operation(
        summary = "Atualiza um aeródromo existente",
        description = "Atualiza os dados de um aeródromo existente com base no código ICAO"
    )
    @APIResponse(
        responseCode = "200",
        description = "Aeródromo atualizado com sucesso",
        content = @Content(schema = @Schema(implementation = AerodromoDTO.class))
    )
    @APIResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    @APIResponse(responseCode = "404", description = "Aeródromo não encontrado")
    public Response atualizar(
        @PathParam("icao") String icao,
        @Valid AerodromoDTO aerodromoDTO) {
        
        // Garante que o código ICAO do path corresponde ao do corpo
        if (!icao.equalsIgnoreCase(aerodromoDTO.getIcao())) {
            aerodromoDTO.setIcao(icao.toUpperCase());
        }
        
        var aerodromoAtualizado = aerodromoService.salvarAerodromo(aerodromoDTO.toDomain());
        return Response.ok(AerodromoDTO.fromDomain(aerodromoAtualizado)).build();
    }

    @DELETE
    @Path("/{icao}")
    @Operation(
        summary = "Remove um aeródromo",
        description = "Remove um aeródromo com base no código ICAO fornecido"
    )
    @APIResponse(responseCode = "204", description = "Aeródromo removido com sucesso")
    @APIResponse(responseCode = "404", description = "Aeródromo não encontrado")
    public Response remover(
        @PathParam("icao") 
        @NotBlank(message = "Código ICAO é obrigatório")
        @Pattern(regexp = "[A-Za-z]{4}", message = "Código ICAO deve conter exatamente 4 letras")
        String icao) {
        
        if (aerodromoService.removerAerodromo(icao.toUpperCase())) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{icao}/terminal")
    @Operation(
        summary = "Verifica se um aeródromo é terminal",
        description = "Verifica se o aeródromo com o código ICAO fornecido é um aeródromo terminal"
    )
    @APIResponse(
        responseCode = "200",
        description = "Indica se o aeródromo é terminal",
        content = @Content(schema = @Schema(implementation = Boolean.class))
    )
    @APIResponse(responseCode = "400", description = "Código ICAO inválido")
    public Response isTerminal(
        @PathParam("icao") 
        @NotBlank(message = "Código ICAO é obrigatório")
        @Pattern(regexp = "[A-Za-z]{4}", message = "Código ICAO deve conter exatamente 4 letras")
        String icao) {
        
        boolean isTerminal = aerodromoService.isAerodromoTerminal(icao.toUpperCase());
        return Response.ok(isTerminal).build();
    }

    @GET
    @Path("/{icao}/frequencias")
    @Operation(
        summary = "Lista as frequências de um aeródromo",
        description = "Retorna a lista de frequências de rádio disponíveis para o aeródromo"
    )
    @APIResponse(
        responseCode = "200",
        description = "Lista de frequências do aeródromo",
        content = @Content(schema = @Schema(implementation = FrequenciaDTO[].class))
    )
    @APIResponse(responseCode = "404", description = "Aeródromo não encontrado")
    public Response listarFrequencias(
        @PathParam("icao") 
        @NotBlank(message = "Código ICAO é obrigatório")
        @Pattern(regexp = "[A-Za-z]{4}", message = "Código ICAO deve conter exatamente 4 letras")
        String icao) {
        
        return aerodromoService.buscarPorIcao(icao.toUpperCase())
                .map(aerodromo -> {
                    var frequencias = aerodromo.getFrequencias() != null ? 
                            aerodromo.getFrequencias().stream()
                                    .map(FrequenciaDTO::fromDomain)
                                    .collect(java.util.stream.Collectors.toList()) :
                            java.util.Collections.<FrequenciaDTO>emptyList();
                    return Response.ok(frequencias).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
    
    @GET
    @Path("/existe/{codigo}")
    @Operation(
        summary = "Verifica se um aeródromo existe",
        description = "Verifica se um aeródromo existe com base no código ICAO (4 letras) ou IATA (3 letras)"
    )
    @APIResponse(
        responseCode = "200",
        description = "Indica se o aeródromo existe",
        content = @Content(schema = @Schema(implementation = Boolean.class))
    )
    public Response aerodromoExiste(
        @PathParam("codigo")
        @NotBlank(message = "Código do aeródromo é obrigatório")
        @Pattern(regexp = "[A-Za-z]{3,4}", message = "Código deve ter 3 (IATA) ou 4 (ICAO) letras")
        String codigo) {
        
        final String codigoBusca = codigo.trim().toUpperCase();
        boolean existe;
        
        if (codigoBusca.length() == 4) {
            // Busca por ICAO
            existe = aerodromoService.buscarPorIcao(codigoBusca).isPresent();
        } else {
            // Busca por IATA
            existe = !aerodromoService.buscarAerodromos(codigoBusca, null, 0, 1)
                    .stream()
                    .filter(a -> codigoBusca.equals(a.getIata()))
                    .findFirst()
                    .isPresent();
        }
        
        return Response.ok(existe).build();
    }
    
    @GET
    @Path("/detalhes/{icao}")
    @Operation(
        summary = "Obtém os detalhes completos de um aeródromo",
        description = "Retorna todas as informações detalhadas de um aeródromo, incluindo pistas e frequências"
    )
    @APIResponse(
        responseCode = "200",
        description = "Detalhes do aeródromo",
        content = @Content(schema = @Schema(implementation = AerodromoDTO.class))
    )
    @APIResponse(responseCode = "404", description = "Aeródromo não encontrado")
    public Response obterDetalhesAerodromo(
        @PathParam("icao")
        @NotBlank(message = "Código ICAO é obrigatório")
        @Pattern(regexp = "[A-Za-z]{4}", message = "Código ICAO deve conter exatamente 4 letras")
        String icao) {
        
        return aerodromoService.buscarPorIcao(icao.toUpperCase())
                .map(aerodromo -> Response.ok(AerodromoDTO.fromDomain(aerodromo)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
    
    @GET
    @Path("/buscar")
    @Operation(
        summary = "Busca aeródromos por termo",
        description = "Busca aeródromos por código ICAO, IATA, nome da cidade ou UF"
    )
    @APIResponse(
        responseCode = "200",
        description = "Lista de aeródromos que correspondem ao critério de busca",
        content = @Content(schema = @Schema(implementation = AerodromoBuscaDTO[].class))
    )
    public Response buscarAerodromos(
        @Parameter(description = "Termo de busca (código ICAO, IATA, nome da cidade ou UF)")
        @QueryParam("q") @NotBlank(message = "Termo de busca é obrigatório") String termo,
        @Parameter(description = "Limitar o número de resultados")
        @QueryParam("limite") @DefaultValue("10") int limite) {
        
        // Converte o termo para maiúsculas para busca case-insensitive
        String termoBusca = termo.trim().toUpperCase();
        
        // Se o termo tiver 3 ou 4 caracteres, pode ser um código IATA ou ICAO
        if (termoBusca.matches("[A-Z]{3,4}")) {
            // Tenta buscar por ICAO exato (4 letras)
            if (termoBusca.length() == 4) {
                var aerodromo = aerodromoService.buscarPorIcao(termoBusca)
                        .map(a -> List.of(AerodromoBuscaDTO.fromAerodromoDTO(AerodromoDTO.fromDomain(a))))
                        .orElseGet(List::of);
                
                if (!aerodromo.isEmpty()) {
                    return Response.ok(aerodromo).build();
                }
            }
            
            // Tenta buscar por IATA exato (3 letras)
            if (termoBusca.length() == 3) {
                var aerodromos = aerodromoService.buscarAerodromos(termoBusca, null, 0, limite)
                        .stream()
                        .filter(a -> termoBusca.equals(a.getIata()))
                        .map(AerodromoBuscaDTO::fromAerodromoDTO)
                        .collect(Collectors.toList());
                
                if (!aerodromos.isEmpty()) {
                    return Response.ok(aerodromos).build();
                }
            }
        }
        
        // Busca genérica por nome, cidade ou UF
        List<AerodromoBuscaDTO> resultados = aerodromoService.buscarAerodromos(termoBusca, null, 0, limite)
                .stream()
                .map(AerodromoBuscaDTO::fromAerodromoDTO)
                .collect(Collectors.toList());
        
        return Response.ok(resultados).build();
    }
}
