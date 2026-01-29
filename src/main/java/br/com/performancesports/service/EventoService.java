package br.com.performancesports.service;

import br.com.performancesports.DTO.*;
import br.com.performancesports.domain.aluno.Aluno;
import br.com.performancesports.domain.evento.*;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final UserRepository userRepository;
    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;
    private final ModalidadeRepository modalidadeRepository;
    private final EventoRepository eventoRepository;
    private final EventoInscricaoRepository eventoInscricaoRepository;

    @Transactional
    public EventoCreateResponseDTO create(EventoCreateRequestDTO req, String actorEmail) {
        User actor = userRepository.findByEmailIgnoreCase(actorEmail)
                .orElseThrow(() -> new AccessDeniedException("Acesso negado."));

        if (actor.getAccountType() != AccountType.PROFESSOR) {
            throw new AccessDeniedException("Acesso negado.");
        }

        Long professorId = professorRepository.findByUserId(actor.getId())
                .map(p -> p.getId())
                .orElseThrow(() -> new AccessDeniedException("Acesso negado."));

        // modalidade deve existir
        modalidadeRepository.findById(req.modalidadeId())
                .orElseThrow(() -> new EntityNotFoundException("Modalidade não encontrada."));

        // escopo: professor USER só pode convidar alunos do próprio escopo
        if (actor.getRole() == Role.USER) {
            for (Long alunoId : req.alunoIds()) {
                alunoRepository.findByIdAndProfessorId(alunoId, professorId)
                        .orElseThrow(() -> new AccessDeniedException("Acesso negado."));
            }
        }

        Evento evento = new Evento();
        evento.setNomeDescricao(req.nomeDescricao());
        evento.setDataInicio(req.dataInicio());
        evento.setDataFim(req.dataFim());
        evento.setHora(req.hora());
        evento.setProfessorResponsavelId(professorId);
        evento.setModalidadeId(req.modalidadeId());
        evento.setStatus(EventoStatus.PENDING_VALIDATION);
        eventoRepository.save(evento);

        LocalDateTime now = LocalDateTime.now();
        for (Long alunoId : req.alunoIds()) {
            // aluno deve existir (não vazar ownership: erro genérico)
            Aluno aluno = alunoRepository.findById(alunoId)
                    .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado."));

            EventoInscricao ei = new EventoInscricao();
            ei.setEventoId(evento.getId());
            ei.setAlunoId(aluno.getId());
            ei.setStatus(InscricaoStatus.INVITED);
            ei.setInvitedAt(now);
            eventoInscricaoRepository.save(ei);
        }

        return new EventoCreateResponseDTO(evento.getId(), evento.getStatus().name());
    }

    @Transactional
    public MessageResponseDTO approve(Long eventoId, ApproveRequestDTO req, String actorEmail) {
        User actor = userRepository.findByEmailIgnoreCase(actorEmail)
                .orElseThrow(() -> new AccessDeniedException("Acesso negado."));

        if (actor.getRole() != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Acesso negado.");
        }

        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado."));

        boolean approved = Boolean.TRUE.equals(req.approved());
        evento.setStatus(approved ? EventoStatus.APPROVED : EventoStatus.REJECTED);
        eventoRepository.save(evento);

        // TODO: disparar convites por e-mail quando aprovado (hardening: não logar payload)
        return new MessageResponseDTO(approved ? "Evento aprovado." : "Evento reprovado.");
    }

    @Transactional
    public MessageResponseDTO confirm(Long eventoId, ConfirmParticipacaoRequestDTO req, String actorEmail) {
        User actor = userRepository.findByEmailIgnoreCase(actorEmail)
                .orElseThrow(() -> new AccessDeniedException("Acesso negado."));

        if (actor.getAccountType() != AccountType.ALUNO) {
            throw new AccessDeniedException("Acesso negado.");
        }

        Aluno aluno = alunoRepository.findByUserId(actor.getId())
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado."));

        EventoInscricao ei = eventoInscricaoRepository.findByEventoIdAndAlunoId(eventoId, aluno.getId())
                .orElseThrow(() -> new EntityNotFoundException("Inscrição não encontrada."));

        String c = req.confirm().trim().toUpperCase();
        if ("YES".equals(c)) {
            ei.setStatus(InscricaoStatus.CONFIRMED_YES);
        } else if ("NO".equals(c)) {
            ei.setStatus(InscricaoStatus.CONFIRMED_NO);
        } else {
            throw new IllegalArgumentException("confirm deve ser YES ou NO.");
        }

        ei.setRespondedAt(LocalDateTime.now());
        eventoInscricaoRepository.save(ei);

        return new MessageResponseDTO("Confirmação registrada.");
    }
}
