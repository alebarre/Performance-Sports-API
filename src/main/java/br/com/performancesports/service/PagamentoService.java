package br.com.performancesports.service;

import br.com.performancesports.DTO.*;
import br.com.performancesports.domain.aluno.Aluno;
import br.com.performancesports.domain.pagamento.Pagamento;
import br.com.performancesports.domain.pagamento.PagamentoStatus;
import br.com.performancesports.domain.user.AccountType;
import br.com.performancesports.domain.user.Role;
import br.com.performancesports.domain.user.User;
import br.com.performancesports.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final UserRepository userRepository;
    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;
    private final PagamentoRepository pagamentoRepository;

    @Transactional
    public PagamentoLinkResponseDTO createLink(PagamentoLinkRequestDTO req, String actorEmail) {
        User actor = userRepository.findByEmailIgnoreCase(actorEmail)
                .orElseThrow(() -> new AccessDeniedException("Acesso negado."));

        if (actor.getAccountType() != AccountType.PROFESSOR) {
            throw new AccessDeniedException("Acesso negado.");
        }

        Long professorId = professorRepository.findByUserId(actor.getId())
                .map(p -> p.getId())
                .orElseThrow(() -> new AccessDeniedException("Acesso negado."));

        // escopo: professor USER só pode cobrar aluno do próprio escopo
        if (actor.getRole() == Role.USER) {
            alunoRepository.findByIdAndProfessorId(req.alunoId(), professorId)
                    .orElseThrow(() -> new AccessDeniedException("Acesso negado."));
        } else {
            alunoRepository.findById(req.alunoId())
                    .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado."));
        }

        Pagamento p = new Pagamento();
        p.setAlunoId(req.alunoId());
        p.setProfessorId(professorId);
        p.setModalidadeId(req.modalidadeId());
        p.setEventoId(req.eventoId());
        p.setStatus(PagamentoStatus.PENDING);
        p.setValor(req.valor());
        p.setDueDate(req.dueDate());
        p.setGateway("PAGSEGURO");
        p.setGatewayReferenceId(UUID.randomUUID().toString());
        p.setPaymentLinkUrl("https://pagseguro.local/pay/" + p.getGatewayReferenceId());
        pagamentoRepository.save(p);

        // IMPORTANTE (contrato): não retornar gateway_reference_id
        return new PagamentoLinkResponseDTO(p.getId(), p.getStatus().name(), p.getDueDate(), p.getPaymentLinkUrl());
    }

    @Transactional
    public MessageResponseDTO webhookPagSeguro(Map<String, Object> payload) {
        // Em dev, aceitamos payload simplificado. Em prod, validar assinatura e tipo de evento.
        Object ref = payload.get("gateway_reference_id");
        if (ref == null) {
            return new MessageResponseDTO("ok");
        }
        String gatewayRef = String.valueOf(ref);

        Optional<Pagamento> existing = pagamentoRepository.findFirstByGatewayReferenceId(gatewayRef);
        if (existing.isEmpty()) {
            return new MessageResponseDTO("ok");
        }

        Pagamento p = existing.get();

        // idempotência básica: se já está PAID, não reprocessa
        Object statusRaw = payload.get("status");
        if (statusRaw != null) {
            String st = String.valueOf(statusRaw).trim().toUpperCase();
            if ("PAID".equals(st) && p.getStatus() != PagamentoStatus.PAID) {
                p.setStatus(PagamentoStatus.PAID);
                p.setPaidAt(LocalDateTime.now());
                pagamentoRepository.save(p);
            }
        }

        return new MessageResponseDTO("ok");
    }

    @Transactional(readOnly = true)
    public List<PagamentoListItemDTO> list(String status, String actorEmail) {
        User actor = userRepository.findByEmailIgnoreCase(actorEmail)
                .orElseThrow(() -> new AccessDeniedException("Acesso negado."));

        PagamentoStatus st;
        if (status != null && !status.isBlank()) {
            st = PagamentoStatus.valueOf(status.trim().toUpperCase());
        } else {
            st = null;
        }

        List<Pagamento> pagamentos;

        if (actor.getAccountType() == AccountType.ALUNO) {
            Aluno aluno = alunoRepository.findByUserId(actor.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado."));
            pagamentos = (st == null)
                    ? pagamentoRepository.findAllByAlunoId(aluno.getId())
                    : pagamentoRepository.findAllByAlunoIdAndStatus(aluno.getId(), st);
        } else if (actor.getAccountType() == AccountType.PROFESSOR) {
            Long professorId = professorRepository.findByUserId(actor.getId())
                    .map(p -> p.getId())
                    .orElseThrow(() -> new AccessDeniedException("Acesso negado."));
            if (actor.getRole() == Role.USER) {
                pagamentos = (st == null)
                        ? pagamentoRepository.findAllByProfessorId(professorId)
                        : pagamentoRepository.findAllByProfessorIdAndStatus(professorId, st);
            } else {
                // ADMIN/SUPER_ADMIN: global
                pagamentos = (st == null) ? pagamentoRepository.findAll()
                        : pagamentoRepository.findAll().stream().filter(p -> p.getStatus() == st).toList();
            }
        } else {
            throw new AccessDeniedException("Acesso negado.");
        }

        return pagamentos.stream()
                .map(p -> new PagamentoListItemDTO(p.getId(), p.getStatus().name(), p.getValor(), p.getDueDate(), p.getAlunoId(), p.getProfessorId()))
                .toList();
    }
}
