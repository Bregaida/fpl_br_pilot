package br.com.fplbr.pilot.common.application.service;

import br.com.fplbr.pilot.common.infrastructure.web.dto.CepResponseDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.Map;

@ApplicationScoped
public class CepService {
    
    private static final Logger LOG = Logger.getLogger(CepService.class);
    
    @ConfigProperty(name = "cep.viacep.url", defaultValue = "https://viacep.com.br/ws/%s/json/")
    String viaCepUrl;
    
    @ConfigProperty(name = "cep.republicavirtual.url", defaultValue = "https://republicavirtual.com.br/web_cep.php?cep=%s&formato=json")
    String republicaVirtualUrl;
    
    private final Client client = ClientBuilder.newClient();
    
    public CepResponseDto buscarCep(String cep) {
        // Remove máscara do CEP
        String cepLimpo = cep.replaceAll("\\D", "");
        
        if (cepLimpo.length() != 8) {
            return CepResponseDto.builder()
                .erro(true)
                .mensagem("CEP deve ter 8 dígitos")
                .build();
        }
        
        // Tenta primeiro a ViaCEP
        try {
            CepResponseDto viaCepResult = buscarViaViaCep(cepLimpo);
            if (viaCepResult != null && !viaCepResult.isErro()) {
                LOG.info("CEP encontrado via ViaCEP: " + cep);
                return viaCepResult;
            }
        } catch (Exception e) {
            LOG.warn("Erro ao buscar CEP via ViaCEP: " + e.getMessage());
        }
        
        // Se falhou, tenta a República Virtual
        try {
            CepResponseDto republicaResult = buscarViaRepublicaVirtual(cepLimpo);
            if (republicaResult != null && !republicaResult.isErro()) {
                LOG.info("CEP encontrado via República Virtual: " + cep);
                return republicaResult;
            }
        } catch (Exception e) {
            LOG.warn("Erro ao buscar CEP via República Virtual: " + e.getMessage());
        }
        
        return CepResponseDto.builder()
            .erro(true)
            .mensagem("CEP não encontrado")
            .build();
    }
    
    private CepResponseDto buscarViaViaCep(String cep) {
        String url = viaCepUrl.replace("%s", cep);
        
        try (Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .get()) {
            
            if (response.getStatus() == 200) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = response.readEntity(Map.class);
                
                if (data.containsKey("erro")) {
                    return CepResponseDto.builder()
                        .erro(true)
                        .mensagem("CEP não encontrado na ViaCEP")
                        .build();
                }
                
                return CepResponseDto.builder()
                    .logradouro((String) data.get("logradouro"))
                    .bairro((String) data.get("bairro"))
                    .cidade((String) data.get("localidade"))
                    .uf((String) data.get("uf"))
                    .cep((String) data.get("cep"))
                    .erro(false)
                    .build();
            }
        } catch (Exception e) {
            LOG.error("Erro ao consultar ViaCEP: " + e.getMessage());
        }
        
        return null;
    }
    
    private CepResponseDto buscarViaRepublicaVirtual(String cep) {
        String url = republicaVirtualUrl.replace("%s", cep);
        
        try (Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .get()) {
            
            if (response.getStatus() == 200) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = response.readEntity(Map.class);
                
                String resultado = (String) data.get("resultado");
                if ("0".equals(resultado)) {
                    return CepResponseDto.builder()
                        .erro(true)
                        .mensagem("CEP não encontrado na República Virtual")
                        .build();
                }
                
                String tipoLogradouro = (String) data.get("tipo_logradouro");
                String logradouro = (String) data.get("logradouro");
                String logradouroCompleto = tipoLogradouro != null && logradouro != null 
                    ? tipoLogradouro + " " + logradouro 
                    : logradouro;
                
                return CepResponseDto.builder()
                    .logradouro(logradouroCompleto)
                    .bairro((String) data.get("bairro"))
                    .cidade((String) data.get("cidade"))
                    .uf((String) data.get("uf"))
                    .cep(cep)
                    .erro(false)
                    .build();
            }
        } catch (Exception e) {
            LOG.error("Erro ao consultar República Virtual: " + e.getMessage());
        }
        
        return null;
    }
}
