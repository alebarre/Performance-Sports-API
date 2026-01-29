package br.com.performancesports.domain.evento;

import br.com.performancesports.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "evento_inscricoes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_evento_aluno", columnNames = {"evento_id", "aluno_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class EventoInscricao extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "evento_id", nullable = false)
    private Long eventoId;

    @Column(name = "aluno_id", nullable = false)
    private Long alunoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InscricaoStatus status;

    @Column(name = "invited_at", nullable = false)
    private LocalDateTime invitedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;
}
