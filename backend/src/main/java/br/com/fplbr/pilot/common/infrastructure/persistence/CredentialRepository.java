package br.com.fplbr.pilot.common.infrastructure.persistence;

import br.com.fplbr.pilot.common.domain.entity.EncryptedCredential;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class CredentialRepository implements PanacheRepository<EncryptedCredential> {
    
    public Optional<EncryptedCredential> findByService(String service) {
        return find("service", service).firstResultOptional();
    }
    
    public void saveOrUpdate(String service, byte[] iv, byte[] ciphertext) {
        Optional<EncryptedCredential> existing = findByService(service);
        
        if (existing.isPresent()) {
            EncryptedCredential credential = existing.get();
            credential.setIv(iv);
            credential.setCiphertext(ciphertext);
            persist(credential);
        } else {
            EncryptedCredential credential = new EncryptedCredential(service, iv, ciphertext);
            persist(credential);
        }
    }
}
