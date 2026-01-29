package br.com.performancesports.repository;

import br.com.performancesports.domain.evento.EventoInscricao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventoInscricaoRepository extends JpaRepository<EventoInscricao, Long> {
    Optional<EventoInscricao> findByEventoIdAndAlunoId(Long eventoId, Long alunoId);
}
