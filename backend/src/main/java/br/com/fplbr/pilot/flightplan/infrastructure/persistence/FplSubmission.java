package br.com.fplbr.pilot.flightplan.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "fpl_submissions")
public class FplSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "modo", length = 3, nullable = false)
    private String modo; // PVC | PVS

    @Column(name = "identificacao", length = 16)
    private String identificacao;

    @Column(name = "payload_json", nullable = false, columnDefinition = "TEXT")
    private String payloadJson;
    
    // Construtor padrão
    public FplSubmission() {}
    
    // Construtor com parâmetros
    public FplSubmission(Long id, String identificacao, String payloadJson) {
        this.id = id;
        this.identificacao = identificacao;
        this.payloadJson = payloadJson;
    }
    
    // Getters e Setters básicos
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIdentificacao() { return identificacao; }
    public void setIdentificacao(String identificacao) { this.identificacao = identificacao; }
    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }
    
    // Métodos faltantes para compilar
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getModo() { return modo; }
    public void setModo(String modo) { this.modo = modo; }
    
    // Builder
    public static FplSubmissionBuilder builder() {
        return new FplSubmissionBuilder();
    }
    
    public static class FplSubmissionBuilder {
        private Long id;
        private String identificacao;
        private String payloadJson;
        private OffsetDateTime createdAt;
        private String modo;
        
        public FplSubmissionBuilder id(Long id) { this.id = id; return this; }
        public FplSubmissionBuilder identificacao(String identificacao) { this.identificacao = identificacao; return this; }
        public FplSubmissionBuilder payloadJson(String payloadJson) { this.payloadJson = payloadJson; return this; }
        public FplSubmissionBuilder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }
        public FplSubmissionBuilder modo(String modo) { this.modo = modo; return this; }
        
        public FplSubmission build() {
            FplSubmission entity = new FplSubmission();
            entity.setId(id);
            entity.setIdentificacao(identificacao);
            entity.setPayloadJson(payloadJson);
            entity.setCreatedAt(createdAt);
            entity.setModo(modo);
            return entity;
        }
    }
}


