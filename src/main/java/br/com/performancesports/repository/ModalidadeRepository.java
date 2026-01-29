package br.com.performancesports.repository;

import br.com.performancesports.domain.modalidade.Modalidade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModalidadeRepository extends JpaRepository<Modalidade, Long> {
    boolean existsByNomeIgnoreCase(String nome);
}