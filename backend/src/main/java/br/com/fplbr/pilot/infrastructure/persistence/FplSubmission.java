package br.com.fplbr.pilot.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "fpl_submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}


