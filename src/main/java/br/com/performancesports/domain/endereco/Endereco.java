package br.com.performancesports.domain.endereco;

import br.com.performancesports.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "enderecos", indexes = {
        @Index(name = "idx_end_owner", columnList = "owner_type, owner_id")
})
@Getter
@Setter
@NoArgsConstructor
public class Endereco extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false, length = 20)
    private EnderecoOwnerType ownerType;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "cep", nullable = false, length = 8)
    private String cep;

    @Column(name = "logradouro", nullable = false, length = 160)
    private String logradouro;

    @Column(name = "numero", nullable = false, length = 20)
    private String numero;

    @Column(name = "complemento", length = 120)
    private String complemento;

    @Column(name = "bairro", nullable = false, length = 120)
    private String bairro;

    @Column(name = "cidade", nullable = false, length = 120)
    private String cidade;

    @Column(name = "uf", length = 2, columnDefinition = "char(2)", nullable = false)
    private String uf;

    @Column(name = "pais", nullable = false, length = 80)
    private String pais;
}
