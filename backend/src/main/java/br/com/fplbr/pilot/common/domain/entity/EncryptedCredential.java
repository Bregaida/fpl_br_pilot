package br.com.fplbr.pilot.common.domain.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "credentials")
public class EncryptedCredential {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "service", unique = true, nullable = false, length = 100)
    private String service;
    
    @Column(name = "iv", nullable = false, columnDefinition = "BYTEA")
    private byte[] iv;
    
    @Column(name = "ciphertext", nullable = false, columnDefinition = "BYTEA")
    private byte[] ciphertext;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Construtores
    public EncryptedCredential() {}
    
    public EncryptedCredential(String service, byte[] iv, byte[] ciphertext) {
        this.service = service;
        this.iv = iv;
        this.ciphertext = ciphertext;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters e Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getService() {
        return service;
    }
    
    public void setService(String service) {
        this.service = service;
    }
    
    public byte[] getIv() {
        return iv;
    }
    
    public void setIv(byte[] iv) {
        this.iv = iv;
    }
    
    public byte[] getCiphertext() {
        return ciphertext;
    }
    
    public void setCiphertext(byte[] ciphertext) {
        this.ciphertext = ciphertext;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
