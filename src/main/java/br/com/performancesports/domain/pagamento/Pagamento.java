package br.com.performancesports.domain.pagamento;

import br.com.performancesports.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos", indexes = {
        @Index(name = "idx_pag_prof", columnList = "professor_id"),
        @Index(name = "idx_pag_aluno", columnList = "aluno_id")
})
@Getter
@Setter
@NoArgsConstructor
public class Pagamento extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aluno_id", nullable = false)
    private Long alunoId;

    @Column(name = "professor_id", nullable = false)
    private Long professorId;

    @Column(name = "modalidade_id")
    private Long modalidadeId;

    @Column(name = "evento_id")
    private Long eventoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PagamentoStatus status;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "gateway", nullable = false, length = 40)
    private String gateway;

    @Column(name = "gateway_reference_id", nullable = false, length = 120)
    private String gatewayReferenceId;

    @Column(name = "payment_link_url", columnDefinition = "TEXT")
    private String paymentLinkUrl;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
