package br.com.performancesports.repository;

import br.com.performancesports.domain.pagamento.Pagamento;
import br.com.performancesports.domain.pagamento.PagamentoStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    List<Pagamento> findAllByProfessorId(Long professorId);
    List<Pagamento> findAllByProfessorIdAndStatus(Long professorId, PagamentoStatus status);

    List<Pagamento> findAllByAlunoId(Long alunoId);
    List<Pagamento> findAllByAlunoIdAndStatus(Long alunoId, PagamentoStatus status);

    Optional<Pagamento> findFirstByGatewayReferenceId(String gatewayReferenceId);
}
