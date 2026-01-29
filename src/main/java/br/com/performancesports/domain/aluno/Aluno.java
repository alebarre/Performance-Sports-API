package br.com.performancesports.domain.aluno;

import br.com.performancesports.domain.common.BaseEntity;
import br.com.performancesports.domain.modalidade.Modalidade;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "alunos")
public class Aluno extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nesta etapa, mantemos apenas o user_id como coluna simples.
    // O relacionamento 1:1 com User entra no próximo passo.
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "sobrenome", nullable = false, length = 120)
    private String sobrenome;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "cpf", nullable = false, length = 11)
    private String cpf;

    @Column(name = "professor_id", nullable = false)
    private Long professorId;

    @Column(name = "approved", nullable = false)
    private boolean approved;

    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "aluno_modalidades",
            joinColumns = @JoinColumn(name = "aluno_id"),
            inverseJoinColumns = @JoinColumn(name = "modalidade_id")
    )
    private Set<Modalidade> modalidades = new HashSet<>();

}