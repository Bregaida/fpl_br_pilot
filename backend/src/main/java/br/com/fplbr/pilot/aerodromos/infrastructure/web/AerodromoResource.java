package br.com.fplbr.pilot.aerodromos.infrastructure.web;

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

@Path("/api/v1/aerodromos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "AerÃ³dromos", description = "OperaÃ§Ãµes relacionadas a aerÃ³dromos")
public class AerodromoResource {

    @Inject
    AerodromoService aerodromoService;

    @GET
    @Path("/{icao}")
    @Operation(
        summary = "Busca um aerÃ³dromo pelo cÃ³digo ICAO",
        description = "Retorna os detalhes de um aerÃ³dromo com base no cÃ³digo ICAO fornecido"
    )
    @APIResponse(
        responseCode = "200",
        description = "AerÃ³dromo encontrado",
        content = @Content(schema = @Schema(implementation = AerodromoDTO.class))
    )
    @APIResponse(responseCode = "404", description = "AerÃ³dromo nÃ£o encontrado")
    @APIResponse(responseCode = "400", description = "CÃ³digo ICAO invÃ¡lido")
    public Response buscarPorIcao(
        @PathParam("icao")
        @NotBlank(message = "CÃ³digo ICAO Ã© obrigatÃ³rio")
        @Pattern(regexp = "[A-Za-z]{4}", message = "CÃ³digo ICAO deve conter exatamente 4 letras")
        String icao) {

        return aerodromoService.buscarPorIcao(icao.toUpperCase())
                .map(aerodromo -> Response.ok(AerodromoDTO.fromDomain(aerodromo)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Operation(
        summary = "Busca/Listagem de aerÃ³dromos",
        description = "Retorna uma lista paginada filtrando por query (icao/iata/nome/UF)"
    )
    @APIResponse(
        responseCode = "200",
        description = "Lista de aerÃ³dromos",
        content = @Content(schema = @Schema(implementation = AerodromoDTO[].class))
    )
    public List<AerodromoDTO> listar(
        @Parameter(description = "Termo de busca (icao/iata/nome/UF)") @QueryParam("query") String query,
        @Parameter(description = "Filtro por UF") @QueryParam("uf") String uf,
        @Parameter(description = "NÃºmero da pÃ¡gina (0-based)") @DefaultValue("0") @QueryParam("pagina") int pagina,
        @Parameter(description = "Tamanho da pÃ¡gina") @DefaultValue("20") @QueryParam("tamanho") int tamanho) {

        return aerodromoService.buscarAerodromos(query, uf, pagina, tamanho);
    }

    @GET
    @Path("/contar")
    @Operation(
        summary = "Conta o total de aerÃ³dromos que correspondem aos critÃ©rios de busca",
        description = "Retorna o total de aerÃ³dromos que correspondem aos critÃ©rios fornecidos"
    )
    @APIResponse(
        responseCode = "200",
        description = "Total de aerÃ³dromos encontrados",
        content = @Content(schema = @Schema(implementation = Long.class))
    )
    public long contar(
        @Parameter(description = "Termo para busca (nome, cÃ³digo, cidade, etc.)") @QueryParam("busca") String termoBusca,
        @Parameter(description = "Filtro por UF") @QueryParam("uf") String uf) {

        return aerodromoService.contarAerodromos(termoBusca, uf);
    }

    @POST
    @Operation(
        summary = "Cria um novo aerÃ³dromo",
        description = "Cria um novo aerÃ³dromo com os dados fornecidos"
    )
    @APIResponse(
        responseCode = "201",
        description = "AerÃ³dromo criado com sucesso",
        content = @Content(schema = @Schema(implementation = AerodromoDTO.class))
    )
    @APIResponse(responseCode = "400", description = "Dados invÃ¡lidos fornecidos")
    @APIResponse(responseCode = "409", description = "JÃ¡ existe um aerÃ³dromo com o mesmo cÃ³digo ICAO")
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
        summary = "Atualiza um aerÃ³dromo existente",
        description = "Atualiza os dados de um aerÃ³dromo existente com base no cÃ³digo ICAO"
    )
    @APIResponse(
        responseCode = "200",
        description = "AerÃ³dromo atualizado com sucesso",
        content = @Content(schema = @Schema(implementation = AerodromoDTO.class))
    )
    @APIResponse(responseCode = "400", description = "Dados invÃ¡lidos fornecidos")
    @APIResponse(responseCode = "404", description = "AerÃ³dromo nÃ£o encontrado")
    public Response atualizar(
        @PathParam("icao") String icao,
        @Valid AerodromoDTO aerodromoDTO) {

        if (!icao.equalsIgnoreCase(aerodromoDTO.getIcao())) {
            aerodromoDTO.setIcao(icao.toUpperCase());
        }

        var aerodromoAtualizado = aerodromoService.salvarAerodromo(aerodromoDTO.toDomain());
        return Response.ok(AerodromoDTO.fromDomain(aerodromoAtualizado)).build();
    }

    @DELETE
    @Path("/{icao}")
    @Operation(
        summary = "Remove um aerÃ³dromo",
        description = "Remove um aerÃ³dromo com base no cÃ³digo ICAO fornecido"
    )
    @APIResponse(responseCode = "204", description = "AerÃ³dromo removido com sucesso")
    @APIResponse(responseCode = "404", description = "AerÃ³dromo nÃ£o encontrado")
    public Response remover(
        @PathParam("icao")
        @NotBlank(message = "CÃ³digo ICAO Ã© obrigatÃ³rio")
        @Pattern(regexp = "[A-Za-z]{4}", message = "CÃ³digo ICAO deve conter exatamente 4 letras")
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
        summary = "Verifica se um aerÃ³dromo Ã© terminal",
        description = "Verifica se o aerÃ³dromo com o cÃ³digo ICAO fornecido Ã© um aerÃ³dromo terminal"
    )
    @APIResponse(
        responseCode = "200",
        description = "Indica se o aerÃ³dromo Ã© terminal",
        content = @Content(schema = @Schema(implementation = Boolean.class))
    )
    @APIResponse(responseCode = "400", description = "CÃ³digo ICAO invÃ¡lido")
    public Response isTerminal(
        @PathParam("icao")
        @NotBlank(message = "CÃ³digo ICAO Ã© obrigatÃ³rio")
        @Pattern(regexp = "[A-Za-z]{4}", message = "CÃ³digo ICAO deve conter exatamente 4 letras")
        String icao) {

        boolean isTerminal = aerodromoService.isAerodromoTerminal(icao.toUpperCase());
        return Response.ok(isTerminal).build();
    }

    @GET
    @Path("/{icao}/frequencias")
    @Operation(
        summary = "Lista as frequÃªncias de um aerÃ³dromo",
        description = "Retorna a lista de frequÃªncias de rÃ¡dio disponÃ­veis para o aerÃ³dromo"
    )
    @APIResponse(
        responseCode = "200",
        description = "Lista de frequÃªncias do aerÃ³dromo",
        content = @Content(schema = @Schema(implementation = FrequenciaDTO[].class))
    )
    @APIResponse(responseCode = "404", description = "AerÃ³dromo nÃ£o encontrado")
    public Response listarFrequencias(
        @PathParam("icao")
        @NotBlank(message = "CÃ³digo ICAO Ã© obrigatÃ³rio")
        @Pattern(regexp = "[A-Za-z]{4}", message = "CÃ³digo ICAO deve conter exatamente 4 letras")
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
        summary = "Verifica se um aerÃ³dromo existe",
        description = "Verifica se um aerÃ³dromo existe com base no cÃ³digo ICAO (4 letras) ou IATA (3 letras)"
    )
    @APIResponse(
        responseCode = "200",
        description = "Indica se o aerÃ³dromo existe",
        content = @Content(schema = @Schema(implementation = Boolean.class))
    )
    public Response aerodromoExiste(
        @PathParam("codigo")
        @NotBlank(message = "CÃ³digo do aerÃ³dromo Ã© obrigatÃ³rio")
        @Pattern(regexp = "[A-Za-z]{3,4}", message = "CÃ³digo deve ter 3 (IATA) ou 4 (ICAO) letras")
        String codigo) {

        final String codigoBusca = codigo.trim().toUpperCase();
        boolean existe;

        if (codigoBusca.length() == 4) {
            existe = aerodromoService.buscarPorIcao(codigoBusca).isPresent();
        } else {
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
        summary = "ObtÃ©m os detalhes completos de um aerÃ³dromo",
        description = "Retorna todas as informaÃ§Ãµes detalhadas de um aerÃ³dromo, incluindo pistas e frequÃªncias"
    )
    @APIResponse(
        responseCode = "200",
        description = "Detalhes do aerÃ³dromo",
        content = @Content(schema = @Schema(implementation = AerodromoDTO.class))
    )
    @APIResponse(responseCode = "404", description = "AerÃ³dromo nÃ£o encontrado")
    public Response obterDetalhesAerodromo(
        @PathParam("icao")
        @NotBlank(message = "CÃ³digo ICAO Ã© obrigatÃ³rio")
        @Pattern(regexp = "[A-Za-z]{4}", message = "CÃ³digo ICAO deve conter exatamente 4 letras")
        String icao) {

        return aerodromoService.buscarPorIcao(icao.toUpperCase())
                .map(aerodromo -> Response.ok(AerodromoDTO.fromDomain(aerodromo)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/buscar")
    @Operation(
        summary = "Busca aerÃ³dromos por termo",
        description = "Busca aerÃ³dromos por cÃ³digo ICAO, IATA, nome da cidade ou UF"
    )
    @APIResponse(
        responseCode = "200",
        description = "Lista de aerÃ³dromos que correspondem ao critÃ©rio de busca",
        content = @Content(schema = @Schema(implementation = AerodromoBuscaDTO[].class))
    )
    public Response buscarAerodromos(
        @Parameter(description = "Termo de busca (cÃ³digo ICAO, IATA, nome da cidade ou UF)")
        @QueryParam("q") @NotBlank(message = "Termo de busca Ã© obrigatÃ³rio") String termo,
        @Parameter(description = "Limitar o nÃºmero de resultados")
        @QueryParam("limite") @DefaultValue("10") int limite) {

        String termoBusca = termo.trim().toUpperCase();

        if (termoBusca.matches("[A-Z]{3,4}")) {
            if (termoBusca.length() == 4) {
                var aerodromo = aerodromoService.buscarPorIcao(termoBusca)
                        .map(a -> List.of(AerodromoBuscaDTO.fromAerodromoDTO(AerodromoDTO.fromDomain(a))))
                        .orElseGet(List::of);

                if (!aerodromo.isEmpty()) {
                    return Response.ok(aerodromo).build();
                }
            }

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

        List<AerodromoBuscaDTO> resultados = aerodromoService.buscarAerodromos(termoBusca, null, 0, limite)
                .stream()
                .map(AerodromoBuscaDTO::fromAerodromoDTO)
                .collect(Collectors.toList());

        return Response.ok(resultados).build();
    }
}
