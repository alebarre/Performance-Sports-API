package br.com.performancesports.domain.modalidade;

import br.com.performancesports.domain.common.BaseEntity;
import br.com.performancesports.domain.professor.Professor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "modalidades")
public class Modalidade extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 120, unique = true)
    private String nome;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    /**
     * Regra determinística: modalidade.professor_default_id
     * Pode ser null (ex.: ainda não definida).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_default_id")
    private Professor professorDefault;

    // Relacionamentos inversos (opcionais). Mantidos para conveniência.
    @ManyToMany(mappedBy = "modalidades", fetch = FetchType.LAZY)
    private Set<Professor> professores = new HashSet<>();

    @ManyToMany(mappedBy = "modalidades", fetch = FetchType.LAZY)
    private Set<br.com.performancesports.domain.aluno.Aluno> alunos = new HashSet<>();


}
