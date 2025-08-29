package br.com.fplbr.pilot.fpl.aplicacao.casosdeuso;

import br.com.fplbr.pilot.fpl.aplicacao.dto.*;
import br.com.fplbr.pilot.fpl.aplicacao.servicos.FplMessageBuilder;
import br.com.fplbr.pilot.fpl.dominio.modelo.*;
import br.com.fplbr.pilot.fpl.dominio.validacao.ValidadorPlanoDeVoo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@ApplicationScoped
public class GerarPlanoDeVoo {
    
    @Inject
    FplMessageBuilder fplBuilder;
    
    /**
     * Executa o caso de uso de geração de plano de voo
     * @param req Requisição com os dados do plano de voo
     * @return Resposta com a mensagem FPL ou erros de validação
     */
    public RespostaPlanoDeVoo executar(RequisicaoPlanoDeVoo req) {
        try {
            // 1) Mapear para o DTO do builder
            FplDTO fplDto = mapearParaFplDto(req);
            
            // 2) Montar mensagem FPL
            String mensagemFpl = fplBuilder.montar(fplDto);
            
            // 3) Retornar sucesso
            return new RespostaPlanoDeVoo(mensagemFpl, List.of(), List.of(), List.of());
            
        } catch (IllegalArgumentException e) {
            // Erros de validação do builder
            return new RespostaPlanoDeVoo(
                null, 
                List.of("Erro ao gerar mensagem FPL: " + e.getMessage()),
                List.of("ERRO_GERACAO: " + e.getMessage()),
                List.of(new ErroValidacaoDTO("ERRO_GERACAO", e.getMessage()))
            );
        } catch (Exception e) {
            // Erro inesperado
            return new RespostaPlanoDeVoo(
                null,
                List.of("Erro inesperado: " + e.getMessage()),
                List.of("ERRO_INTERNO: " + e.getMessage()),
                List.of(new ErroValidacaoDTO("ERRO_INTERNO", e.getMessage()))
            );
        }
    }

    /**
     * Mapeia de RequisicaoPlanoDeVoo para FplDTO, aplicando valores padrão quando necessário
     */
    private FplDTO mapearParaFplDto(RequisicaoPlanoDeVoo req) {
        FplDTO dto = new FplDTO();
        
        // Item 7-8
        dto.identificacaoAeronave = FplDTO.nz(req.identificacaoAeronave, "TESTE").toUpperCase();
        dto.regrasDeVoo = FplDTO.nz(req.regrasDeVoo, "V"); // VFR por padrão
        dto.tipoDeVoo = FplDTO.nz(req.tipoDeVoo, "G"); // Geral por padrão
        
        // Item 9
        dto.tipoAeronave = FplDTO.nz(req.tipoAeronaveIcao, "ZZZZ").toUpperCase();
        dto.esteiraDeTurbulencia = FplDTO.nz(req.esteira, "M"); // Média por padrão
        
        // Item 10 - Equipamentos (simplificado)
        dto.equipamentoA = "S"; // Equipamento padrão
        dto.equipamentoB = "S"; // Equipamento padrão
        
        // Item 13
        dto.aerodromoPartida = FplDTO.nz(req.aerodromoPartida, "ZZZZ").toUpperCase();
        dto.horaPartidaUTC = FplDTO.nz(req.horaPartidaZulu, "1200");
        
        // Item 15
        dto.velocidadeCruzeiro = FplDTO.nz(req.velocidadeCruzeiro, "N0120").toUpperCase();
        dto.nivelCruzeiro = FplDTO.nz(req.nivelCruzeiro, "F090").toUpperCase();
        dto.rota = FplDTO.nz(req.rota, "DCT");
        
        // Item 16
        dto.aerodromoDestino = FplDTO.nz(req.aerodromoDestino, "ZZZZ").toUpperCase();
        dto.tempoTotalVoo = FplDTO.nz(req.eetTotal, "0100");
        
        // Adiciona aeródromos alternativos se existirem
        if (req.alternativo1 != null && !req.alternativo1.isBlank()) {
            dto.alternativos.add(req.alternativo1.trim().toUpperCase());
        }
        if (req.alternativo2 != null && !req.alternativo2.isBlank()) {
            dto.alternativos.add(req.alternativo2.trim().toUpperCase());
        }
        
        // Item 18 - Outras informações
        dto.dataDOF = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyMMdd"));
        dto.operadorOPR = FplDTO.nz(req.piloto, "OPERADOR");
        dto.pbn = ""; // PBN a ser preenchido conforme equipamento
        dto.nav = ""; // NAV a ser preenchido conforme equipamento
        dto.com = ""; // COM a ser preenchido conforme equipamento
        dto.dat = ""; // DAT a ser preenchido conforme equipamento
        dto.sur = ""; // SUR a ser preenchido conforme equipamento
        dto.rmk = FplDTO.nz(req.outrosDados, "");
        
        // Item 19 - Informações suplementares
        dto.autonomiaE = FplDTO.nz(req.autonomia, "0300");
        dto.pessoasP = req.pessoasABordo != null ? String.valueOf(req.pessoasABordo) : "1";
        dto.equipamentosEmergenciaR = ""; // Preencher conforme equipamentos de emergência
        dto.equipamentosSalvamentoS = ""; // Preencher conforme equipamentos de salvamento
        dto.coletesA = ""; // Preencher conforme coletes salva-vidas
        dto.jangadasD = ""; // Preencher conforme jangadas
        dto.corEMarcacoesC = ""; // Preencher conforme cores e marcações
        dto.contatoTelefoneN = FplDTO.nz(req.piloto, "").replaceAll("[^0-9]", "");
        if (dto.contatoTelefoneN.length() < 8) {
            dto.contatoTelefoneN = "00000000"; // Telefone padrão se inválido
        }
        
        return dto;
    }

    /**
     * Método de conveniência para compatibilidade com chamadas existentes
     */
    public RespostaPlanoDeVoo executar(
            String identificacaoAeronave,
            String regrasDeVoo,
            String tipoDeVoo,
            Integer quantidadeAeronaves,
            String tipoAeronaveIcao,
            String esteira,
            String equipamentos,
            String aerodromoPartida,
            String horaPartidaZulu,
            String velocidadeCruzeiro,
            String nivelCruzeiro,
            String rota,
            String aerodromoDestino,
            String eetTotal,
            String alternativo1,
            String alternativo2,
            String outrosDados,
            String autonomia,
            Integer pessoasABordo,
            String piloto
    ) {
        RequisicaoPlanoDeVoo req = new RequisicaoPlanoDeVoo();
        req.identificacaoAeronave = identificacaoAeronave;
        req.regrasDeVoo = regrasDeVoo;
        req.tipoDeVoo = tipoDeVoo;
        req.quantidadeAeronaves = quantidadeAeronaves;
        req.tipoAeronaveIcao = tipoAeronaveIcao;
        req.esteira = esteira;
        req.equipamentos = equipamentos;
        req.aerodromoPartida = aerodromoPartida;
        req.horaPartidaZulu = horaPartidaZulu;
        req.velocidadeCruzeiro = velocidadeCruzeiro;
        req.nivelCruzeiro = nivelCruzeiro;
        req.rota = rota;
        req.aerodromoDestino = aerodromoDestino;
        req.eetTotal = eetTotal;
        req.alternativo1 = alternativo1;
        req.alternativo2 = alternativo2;
        req.outrosDados = outrosDados;
        req.autonomia = autonomia;
        req.pessoasABordo = pessoasABordo;
        req.piloto = piloto;
        
        return executar(req);
    }
}




