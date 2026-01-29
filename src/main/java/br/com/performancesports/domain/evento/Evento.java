package br.com.performancesports.domain.evento;

import br.com.performancesports.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "eventos", indexes = {
        @Index(name = "idx_eventos_prof", columnList = "professor_responsavel_id"),
        @Index(name = "idx_eventos_mod", columnList = "modalidade_id")
})
@Getter
@Setter
@NoArgsConstructor
public class Evento extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_descricao", nullable = false, length = 240)
    private String nomeDescricao;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    @Column(name = "professor_responsavel_id", nullable = false)
    private Long professorResponsavelId;

    @Column(name = "modalidade_id", nullable = false)
    private Long modalidadeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private EventoStatus status;
}
