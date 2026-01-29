package br.com.performancesports.service;

import br.com.performancesports.DTO.*;
import br.com.performancesports.domain.endereco.Endereco;
import br.com.performancesports.domain.endereco.EnderecoOwnerType;
import br.com.performancesports.domain.exception.AccountStateException;
import br.com.performancesports.domain.modalidade.Modalidade;
import br.com.performancesports.domain.pagamento.FormatoPagamento;
import br.com.performancesports.domain.professor.Professor;
import br.com.performancesports.domain.token.Token;
import br.com.performancesports.domain.token.TokenType;
import br.com.performancesports.domain.user.AccountType;
import br.com.performancesports.domain.user.Role;
import br.com.performancesports.domain.user.User;
import br.com.performancesports.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final UserRepository userRepository;
    private final ProfessorRepository professorRepository;
    private final ModalidadeRepository modalidadeRepository;
    private final TokenRepository tokenRepository;
    private final EnderecoRepository enderecoRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    /**
     * Cadastro público de professor: cria User + Professor em estado pendente.
     * Resposta sempre neutra (não vaza se e-mail existe).
     */
    @Transactional
    public MessageResponseDTO register(ProfessorRegisterRequestDTO req) {
        String email = req.email().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            return new MessageResponseDTO("Cadastro recebido. Aguarde aprovação do super admin.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setRole(Role.USER);
        user.setAccountType(AccountType.PROFESSOR);
        user.setEnabled(false);
        user.setEmailVerified(false);
        userRepository.save(user);

        Professor prof = new Professor();
        prof.setUserId(user.getId());
        prof.setNome(req.nome());
        prof.setSobrenome(req.sobrenome());
        prof.setDataNascimento(req.dataNascimento());
        prof.setCpf(req.cpf());
        prof.setPagamentoFormato(FormatoPagamento.valueOf(req.pagamentoFormato()));
        prof.setPagamentoPercentual(req.pagamentoPercentual());
        prof.setApproved(false);
        prof.setActive(false);

        if (req.modalidadeIds() != null && !req.modalidadeIds().isEmpty()) {
            List<Modalidade> mods = modalidadeRepository.findAllById(req.modalidadeIds());
            if (mods.size() != new HashSet<>(req.modalidadeIds()).size()) {
                throw new IllegalArgumentException("Uma ou mais modalidades não existem.");
            }
            prof.getModalidades().addAll(mods);
        }

        professorRepository.save(prof);

        // Endereços (até 2)
        if (req.enderecos() != null) {
            if (req.enderecos().size() > 2) {
                throw new IllegalArgumentException("Máximo de 2 endereços.");
            }
            for (EnderecoRequestDTO e : req.enderecos()) {
                Endereco end = new Endereco();
                end.setOwnerType(EnderecoOwnerType.PROFESSOR);
                end.setOwnerId(prof.getId());
                end.setCep(e.cep());
                end.setLogradouro(e.logradouro());
                end.setNumero(e.numero());
                end.setComplemento(e.complemento());
                end.setBairro(e.bairro());
                end.setCidade(e.cidade());
                end.setUf(e.uf());
                end.setPais(e.pais());
                enderecoRepository.save(end);
            }
        }

        // Token de confirmação de e-mail (1 min)
        tokenService.generate(email, TokenType.REGISTER_VERIFY);

        return new MessageResponseDTO("Cadastro recebido. Aguarde aprovação do super admin.");
    }

    @Transactional
    public MessageResponseDTO approve(Long professorId, ProfessorApproveRequestDTO req, String actorEmail) {
        User actor = userRepository.findByEmailIgnoreCase(actorEmail)
                .orElseThrow(() -> new AccessDeniedException("Acesso negado."));

        if (actor.getRole() != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Acesso negado.");
        }

        Professor prof = professorRepository.findById(professorId)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado."));

        boolean approved = Boolean.TRUE.equals(req.approved());
        prof.setApproved(approved);
        prof.setActive(approved);
        professorRepository.save(prof);

        User profUser = userRepository.findById(prof.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário do professor não encontrado."));

        // role opcional (ADMIN|USER)
        if (req.role() != null && !req.role().isBlank()) {
            profUser.setRole(Role.valueOf(req.role()));
        }
        profUser.setEnabled(approved);
        userRepository.save(profUser);

        return new MessageResponseDTO(approved ? "Professor aprovado." : "Professor reprovado.");
    }
}
