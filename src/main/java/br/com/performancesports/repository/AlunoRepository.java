package br.com.performancesports.repository;


import br.com.performancesports.domain.aluno.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    List<Aluno> findAllByProfessorId(Long professorId);

    Optional<Aluno> findByIdAndProfessorId(Long id, Long professorId);

    Optional<Aluno> findByUserId(Long userId);
}
