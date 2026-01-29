package br.com.performancesports.repository;

import br.com.performancesports.domain.evento.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findAllByProfessorResponsavelId(Long professorId);
    Optional<Evento> findByIdAndProfessorResponsavelId(Long id, Long professorId);
}
