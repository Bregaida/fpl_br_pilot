package br.com.fplbr.pilot.aisweb.application.mapper;

import br.com.fplbr.pilot.aisweb.application.dto.*;
import br.com.fplbr.pilot.aisweb.application.dto.InfotempDto.InfotempItemDto;

/**
 * Mapper class to convert between old and new DTO package structures.
 * This is a temporary class to help with the migration to the new package structure.
 */
public class DtoMapper {
    
    private DtoMapper() {
        // Utility class - prevent instantiation
    }

    // NOTAM Mappings
    public static NotamDto toNotamDto(br.com.fplbr.pilot.aisweb.application.dto.NotamDto oldDto) {
        if (oldDto == null) return null;
        return new NotamDto(
            toNotamMetaDto(oldDto.meta()),
            oldDto.items().stream()
                .map(DtoMapper::toNotamItemDto)
                .toList()
        );
    }

    public static NotamItemDto toNotamItemDto(br.com.fplbr.pilot.aisweb.application.dto.NotamItemDto oldDto) {
        if (oldDto == null) return null;
        return new NotamItemDto(
            oldDto.id(),
            oldDto.icaoCode(),
            oldDto.message(),
            oldDto.type(),
            oldDto.validFrom(),
            oldDto.validUntil(),
            oldDto.fir(),
            oldDto.trafficType(),
            oldDto.purpose(),
            oldDto.scope(),
            oldDto.lowerLimit(),
            oldDto.upperLimit(),
            oldDto.location(),
            oldDto.status(),
            oldDto.rawData()
        );
    }

    public static NotamMetaDto toNotamMetaDto(br.com.fplbr.pilot.aisweb.application.dto.NotamMetaDto oldDto) {
        if (oldDto == null) return null;
        return new NotamMetaDto(
            oldDto.total(),
            oldDto.page(),
            oldDto.perPage(),
            oldDto.sortBy(),
            oldDto.sortOrder()
        );
    }

    // AIP Mappings
    public static PubAipDto toPubAipDto(br.com.fplbr.pilot.aisweb.application.dto.PubAipDto oldDto) {
        if (oldDto == null) return null;
        return new PubAipDto(
            oldDto.items().stream()
                .map(DtoMapper::toPubAipItemDto)
                .toList()
        );
    }

    public static PubAipItemDto toPubAipItemDto(br.com.fplbr.pilot.aisweb.application.dto.PubAipItemDto oldDto) {
        if (oldDto == null) return null;
        return new PubAipItemDto(
            oldDto.groupId(),
            oldDto.packageName(),
            oldDto.amdtEffective(),
            oldDto.publicId(),
            oldDto.ext(),
            oldDto.p(),
            oldDto.amdtNumber(),
            oldDto.amdtSequence(),
            oldDto.amdtYear(),
            oldDto.variantIndex()
        );
    }

    // AIXM Mappings
    public static PubAixmDto toPubAixmDto(br.com.fplbr.pilot.aisweb.application.dto.PubAixmDto oldDto) {
        if (oldDto == null) return null;
        return new PubAixmDto(
            oldDto.items().stream()
                .map(DtoMapper::toPubAixmItemDto)
                .toList()
        );
    }

    public static PubAixmItemDto toPubAixmItemDto(br.com.fplbr.pilot.aisweb.application.dto.PubAixmItemDto oldDto) {
        if (oldDto == null) return null;
        return new PubAixmItemDto(
            oldDto.groupId(),
            oldDto.packageName(),
            oldDto.amdtEffective(),
            oldDto.publicId(),
            oldDto.ext(),
            oldDto.p(),
            oldDto.amdtNumber(),
            oldDto.amdtSequence(),
            oldDto.amdtYear(),
            oldDto.variantIndex(),
            oldDto.typeNormalized()
        );
    }

    // Cartas Mappings
    public static CartasDto toCartasDto(br.com.fplbr.pilot.aisweb.application.dto.CartasDto oldDto) {
        if (oldDto == null) return null;
        return new CartasDto(
            toCartasHeaderDto(oldDto.header()),
            oldDto.items().stream()
                .map(DtoMapper::toCartasItemDto)
                .toList()
        );
    }

    public static CartasHeaderDto toCartasHeaderDto(br.com.fplbr.pilot.aisweb.application.dto.CartasHeaderDto oldDto) {
        if (oldDto == null) return null;
        return new CartasHeaderDto(
            oldDto.emenda(),
            oldDto.lastupdate(),
            oldDto.total()
        );
    }

    public static CartasItemDto toCartasItemDto(br.com.fplbr.pilot.aisweb.application.dto.CartasItemDto oldDto) {
        if (oldDto == null) return null;
        return new CartasItemDto(
            oldDto.id(),
            oldDto.especie(),
            oldDto.tipo(),
            oldDto.tipoDescr(),
            oldDto.nome(),
            oldDto.icaoCode(),
            oldDto.dt(),
            oldDto.dtPublic(),
            oldDto.amdt(),
            oldDto.use(),
            oldDto.hasSpecialProcedures(),
            oldDto.suplementosCount(),
            oldDto.downloadUrl(),
            oldDto.fileSlug(),
            oldDto.format()
        );
    }

    // INFOTEMP Mappings
    public static InfotempDto toInfotempDto(br.com.fplbr.pilot.aisweb.application.dto.InfotempDto oldDto) {
        if (oldDto == null) return null;
        return new InfotempDto(
            oldDto.totalRegistros(),
            oldDto.itens(),
            oldDto.agregadoPorIcao().stream()
                .map(DtoMapper::toInfotempAgregadoPorIcaoDto)
                .toList(),
            oldDto.meta()
        );
    }

    public static InfotempItemDto toInfotempItemDto(br.com.fplbr.pilot.aisweb.application.dto.InfotempItemDto oldDto) {
        if (oldDto == null) return null;
        return new InfotempItemDto(
            oldDto.id(),
            oldDto.icao(),
            oldDto.observacao(),
            oldDto.inicioVigencia(),
            oldDto.fimVigencia(),
            oldDto.dataPublicacao(),
            oldDto.severidade(),
            oldDto.ativoAgora(),
            oldDto.impactoOperacional(),
            oldDto.periodoInvalido()
        );
    }

    public static InfotempAgregadoPorIcaoDto toInfotempAgregadoPorIcaoDto(
            br.com.fplbr.pilot.aisweb.application.dto.InfotempAgregadoPorIcaoDto oldDto) {
        if (oldDto == null) return null;
        return new InfotempAgregadoPorIcaoDto(
            oldDto.icao(),
            oldDto.vigentes(),
            oldDto.vigenteMaisSevero(),
            oldDto.proximos(),
            oldDto.historico(),
            oldDto.estadoAggregado()
        );
    }

    // Meteo Mappings
    public static MeteoDto toMeteoDto(br.com.fplbr.pilot.aisweb.application.dto.MeteoDto oldDto) {
        if (oldDto == null) return null;
        return new MeteoDto(
            oldDto.icao(),
            toSunDto(oldDto.sol()),
            toMetarDto(oldDto.metar()),
            toTafDto(oldDto.taf())
        );
    }

    public static SunDto toSunDto(br.com.fplbr.pilot.aisweb.application.dto.SunDto oldDto) {
        if (oldDto == null) return null;
        return new SunDto(
            oldDto.icao(),
            oldDto.date(),
            oldDto.sunrise(),
            oldDto.sunset(),
            oldDto.diaSemanaNumero(),
            oldDto.diaSemanaNome(),
            oldDto.weekdayOk(),
            oldDto.latitude(),
            oldDto.longitude(),
            oldDto.geoOk(),
            oldDto.intervaloOk()
        );
    }

    public static MetarDto toMetarDto(br.com.fplbr.pilot.aisweb.application.dto.MetarDto oldDto) {
        if (oldDto == null) return null;
        return new MetarDto(
            oldDto.raw(),
            oldDto.tipo(),
            oldDto.horario(),
            toVentoDto(oldDto.vento()),
            oldDto.prevailing_m(),
            oldDto.clouds().stream()
                .map(DtoMapper::toNuvemDto)
                .toList(),
            oldDto.weather(),
            oldDto.temperatura(),
            oldDto.dewpoint(),
            oldDto.qnh(),
            oldDto.remarks()
        );
    }

    public static TafDto toTafDto(br.com.fplbr.pilot.aisweb.application.dto.TafDto oldDto) {
        if (oldDto == null) return null;
        return new TafDto(
            oldDto.raw(),
            oldDto.issued(),
            oldDto.validity(),
            toCondicaoInicialDto(oldDto.condicoesIniciais()),
            oldDto.tn(),
            oldDto.tx(),
            oldDto.mudancas().stream()
                .map(DtoMapper::toMudancaDto)
                .toList()
        );
    }

    public static VentoDto toVentoDto(br.com.fplbr.pilot.aisweb.application.dto.VentoDto oldDto) {
        if (oldDto == null) return null;
        return new VentoDto(
            oldDto.direcao(),
            oldDto.velocidade(),
            oldDto.variacaoMin(),
            oldDto.variacaoMax()
        );
    }

    public static NuvemDto toNuvemDto(br.com.fplbr.pilot.aisweb.application.dto.NuvemDto oldDto) {
        if (oldDto == null) return null;
        return new NuvemDto(
            oldDto.tipo(),
            oldDto.base()
        );
    }

    public static CondicaoInicialDto toCondicaoInicialDto(
            br.com.fplbr.pilot.aisweb.application.dto.CondicaoInicialDto oldDto) {
        if (oldDto == null) return null;
        return new CondicaoInicialDto(
            toVentoDto(oldDto.vento()),
            oldDto.visibilidade(),
            oldDto.nuvens().stream()
                .map(DtoMapper::toNuvemDto)
                .toList()
        );
    }

    public static MudancaDto toMudancaDto(br.com.fplbr.pilot.aisweb.application.dto.MudancaDto oldDto) {
        if (oldDto == null) return null;
        return new MudancaDto(
            oldDto.tipo(),
            oldDto.janela(),
            toCondicaoInicialDto(oldDto.condicoes())
        );
    }

    // Suplementos Mappings
    public static SuplementosDto toSuplementosDto(br.com.fplbr.pilot.aisweb.application.dto.SuplementosDto oldDto) {
        if (oldDto == null) return null;
        return new SuplementosDto(
            toSuplementosMetaDto(oldDto.meta()),
            oldDto.items().stream()
                .map(DtoMapper::toSuplementosItemDto)
                .toList()
        );
    }

    public static SuplementosItemDto toSuplementosItemDto(
            br.com.fplbr.pilot.aisweb.application.dto.SuplementosItemDto oldDto) {
        if (oldDto == null) return null;
        return new SuplementosItemDto(
            oldDto.id(),
            oldDto.icaoCode(),
            oldDto.type(),
            oldDto.category(),
            oldDto.name(),
            oldDto.description(),
            oldDto.effectiveDate(),
            oldDto.expirationDate(),
            oldDto.status(),
            oldDto.url(),
            oldDto.fileExtension(),
            oldDto.fileSize(),
            oldDto.createdBy(),
            oldDto.createdAt(),
            oldDto.updatedBy(),
            oldDto.updatedAt(),
            // Novos campos importantes
            oldDto.titulo(),
            oldDto.texto(),
            oldDto.duracao(),
            oldDto.ref(),
            oldDto.anexo(),
            oldDto.n(),
            oldDto.serie(),
            oldDto.local(),
            oldDto.dt(),
            oldDto.dataInicio(),
            oldDto.dataFim()
        );
    }

    public static SuplementosMetaDto toSuplementosMetaDto(
            br.com.fplbr.pilot.aisweb.application.dto.SuplementosMetaDto oldDto) {
        if (oldDto == null) return null;
        return new SuplementosMetaDto(
            oldDto.total(),
            oldDto.page(),
            oldDto.perPage(),
            oldDto.sortBy(),
            oldDto.sortOrder()
        );
    }
}
