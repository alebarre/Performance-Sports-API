package br.com.performancesports.service;

import br.com.performancesports.domain.aluno.Aluno;
import br.com.performancesports.DTO.AlunoDetailDTO;
import br.com.performancesports.DTO.AlunoListItemDTO;
import br.com.performancesports.domain.exception.ResourceNotFoundException;
import br.com.performancesports.domain.user.AccountType;
import br.com.performancesports.domain.user.Role;
import br.com.performancesports.domain.user.User;
import br.com.performancesports.repository.AlunoRepository;
import br.com.performancesports.repository.ProfessorRepository;
import br.com.performancesports.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final UserRepository userRepository;
    private final ProfessorRepository professorRepository;

    public List<AlunoListItemDTO> listar(String emailLogado) {

        System.out.println("[DEBUG] auth.getName()=" + emailLogado);

        User user = userRepository.findByEmailIgnoreCase(emailLogado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        System.out.println("[DEBUG] resolved user.id=" + user.getId()
                + " email=" + user.getEmail()
                + " role=" + user.getRole()
                + " accountType=" + user.getAccountType());

        if (isAdminOuSuperior(user)) {
            return alunoRepository.findAll().stream().map(this::toListItem).toList();
        }

        if (isAluno(user)) {
            throw new AccessDeniedException("Acesso negado");
        }

        Long professorId = professorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado"))
                .getId();

        return alunoRepository.findAllByProfessorId(professorId).stream().map(this::toListItem).toList();
    }

    public AlunoDetailDTO buscar(Long alunoId, String emailLogado) {
        User user = userRepository.findByEmailIgnoreCase(emailLogado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (isAdminOuSuperior(user)) {
            Aluno aluno = alunoRepository.findById(alunoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));
            return toDetail(aluno);
        }

        if (isAluno(user)) {
            Aluno aluno = alunoRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));

            if (!aluno.getId().equals(alunoId)) {
                throw new ResourceNotFoundException("Aluno não encontrado"); // 404 anti-vazamento
            }
            return toDetail(aluno);
        }

        Long professorId = professorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado"))
                .getId();

        Aluno aluno = alunoRepository.findByIdAndProfessorId(alunoId, professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));

        return toDetail(aluno);
    }

    private boolean isAdminOuSuperior(User user) {
        return user.getRole() == Role.SUPER_ADMIN || user.getRole() == Role.ADMIN;
    }

    private boolean isAluno(User user) {
        return user.getAccountType() == AccountType.ALUNO;
    }

    private AlunoListItemDTO toListItem(Aluno a) {
        return new AlunoListItemDTO(
                a.getId(),
                a.getNome(),
                a.getSobrenome(),
                a.isApproved(),
                a.isActive(),
                a.getProfessorId()
        );
    }

    private AlunoDetailDTO toDetail(Aluno a) {
        return new AlunoDetailDTO(
                a.getId(),
                a.getNome(),
                a.getSobrenome(),
                maskCpf(a.getCpf()),
                a.isApproved(),
                a.isActive(),
                a.getProfessorId()
        );
    }

    
public br.com.performancesports.DTO.MessageResponseDTO approve(Long alunoId, br.com.performancesports.DTO.ApproveRequestDTO req, String actorEmail) {
    User actor = userRepository.findByEmailIgnoreCase(actorEmail)
            .orElseThrow(() -> new AccessDeniedException("Usuário inválido."));

    if (isAluno(actor)) {
        throw new AccessDeniedException("Acesso negado.");
    }

    Aluno aluno = alunoRepository.findById(alunoId)
            .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado."));

    if (!isAdminOuSuperior(actor)) {
        // PROFESSOR USER: somente escopo
        Long professorId = professorRepository.findByUserId(actor.getId())
                .map(p -> p.getId())
                .orElseThrow(() -> new AccessDeniedException("Acesso negado."));
        if (!professorId.equals(aluno.getProfessorId())) {
            throw new AccessDeniedException("Acesso negado.");
        }
    }

    boolean approved = Boolean.TRUE.equals(req.approved());
    aluno.setApproved(approved);
    aluno.setActive(approved);
    alunoRepository.save(aluno);

    // sincroniza user.enabled (destrava login)
    User alunoUser = userRepository.findById(aluno.getUserId())
            .orElseThrow(() -> new EntityNotFoundException("Usuário do aluno não encontrado."));
    alunoUser.setEnabled(approved);
    userRepository.save(alunoUser);

    return new br.com.performancesports.DTO.MessageResponseDTO(approved ? "Aluno aprovado." : "Aluno reprovado.");
}

private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) return null;
        // ***.***.***-xx (não expõe completo)
        return "***.***.***-" + cpf.substring(9);
    }
}
