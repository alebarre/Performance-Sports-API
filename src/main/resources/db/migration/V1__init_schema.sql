-- V1__init_schema.sql

-- 1) USERS
CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       email VARCHAR(254) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       enabled BOOLEAN NOT NULL,
                       email_verified BOOLEAN NOT NULL,
                       role VARCHAR(20) NOT NULL,        -- SUPER_ADMIN | ADMIN | USER
                       account_type VARCHAR(20) NOT NULL,-- ALUNO | PROFESSOR
                       created_at DATETIME NOT NULL,
                       updated_at DATETIME NOT NULL,
                       UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2) PROFESSORES
CREATE TABLE professores (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             user_id BIGINT NOT NULL,
                             nome VARCHAR(120) NOT NULL,
                             sobrenome VARCHAR(120) NOT NULL,
                             data_nascimento DATE NOT NULL,
                             cpf VARCHAR(11) NOT NULL,
                             pagamento_formato VARCHAR(10) NOT NULL, -- AULA | PERCENT
                             pagamento_percentual DECIMAL(5,2) NULL,
                             approved BOOLEAN NOT NULL,
                             active BOOLEAN NOT NULL,
                             created_at DATETIME NOT NULL,
                             updated_at DATETIME NOT NULL,
                             UNIQUE KEY uk_professores_user (user_id),
                             CONSTRAINT fk_prof_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3) ALUNOS
CREATE TABLE alunos (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        user_id BIGINT NOT NULL,
                        nome VARCHAR(120) NOT NULL,
                        sobrenome VARCHAR(120) NOT NULL,
                        data_nascimento DATE NOT NULL,
                        cpf VARCHAR(11) NOT NULL,
                        professor_id BIGINT NOT NULL,
                        approved BOOLEAN NOT NULL,
                        active BOOLEAN NOT NULL,
                        created_at DATETIME NOT NULL,
                        updated_at DATETIME NOT NULL,
                        UNIQUE KEY uk_alunos_user (user_id),
                        KEY idx_alunos_professor (professor_id),
                        CONSTRAINT fk_aluno_user FOREIGN KEY (user_id) REFERENCES users(id),
                        CONSTRAINT fk_aluno_prof FOREIGN KEY (professor_id) REFERENCES professores(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4) MODALIDADES
CREATE TABLE modalidades (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             nome VARCHAR(120) NOT NULL,
                             descricao TEXT NULL,
                             valor DECIMAL(10,2) NOT NULL,
                             professor_default_id BIGINT NULL,
                             created_at DATETIME NOT NULL,
                             updated_at DATETIME NOT NULL,
                             UNIQUE KEY uk_modalidades_nome (nome),
                             KEY idx_modalidades_prof_default (professor_default_id),
                             CONSTRAINT fk_modalidade_prof_default FOREIGN KEY (professor_default_id) REFERENCES professores(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5) PROFESSOR_MODALIDADES (N:N)
CREATE TABLE professor_modalidades (
                                       professor_id BIGINT NOT NULL,
                                       modalidade_id BIGINT NOT NULL,
                                       created_at DATETIME NOT NULL,
                                       PRIMARY KEY (professor_id, modalidade_id),
                                       CONSTRAINT fk_pm_prof FOREIGN KEY (professor_id) REFERENCES professores(id),
                                       CONSTRAINT fk_pm_mod FOREIGN KEY (modalidade_id) REFERENCES modalidades(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6) ALUNO_MODALIDADES (N:N)
CREATE TABLE aluno_modalidades (
                                   aluno_id BIGINT NOT NULL,
                                   modalidade_id BIGINT NOT NULL,
                                   created_at DATETIME NOT NULL,
                                   PRIMARY KEY (aluno_id, modalidade_id),
                                   CONSTRAINT fk_am_aluno FOREIGN KEY (aluno_id) REFERENCES alunos(id),
                                   CONSTRAINT fk_am_mod FOREIGN KEY (modalidade_id) REFERENCES modalidades(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7) ENDERECOS (polimorfico por owner_type/owner_id)
CREATE TABLE enderecos (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           owner_type VARCHAR(20) NOT NULL, -- ALUNO | PROFESSOR
                           owner_id BIGINT NOT NULL,
                           cep VARCHAR(8) NOT NULL,
                           logradouro VARCHAR(160) NOT NULL,
                           numero VARCHAR(20) NOT NULL,
                           complemento VARCHAR(120) NULL,
                           bairro VARCHAR(120) NOT NULL,
                           cidade VARCHAR(120) NOT NULL,
                           uf CHAR(2) NOT NULL,
                           pais VARCHAR(80) NOT NULL,
                           created_at DATETIME NOT NULL,
                           updated_at DATETIME NOT NULL,
                           KEY idx_end_owner (owner_type, owner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8) EVENTOS
CREATE TABLE eventos (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         nome_descricao VARCHAR(240) NOT NULL,
                         data_inicio DATE NOT NULL,
                         data_fim DATE NOT NULL,
                         hora TIME NOT NULL,
                         professor_responsavel_id BIGINT NOT NULL,
                         modalidade_id BIGINT NOT NULL,
                         status VARCHAR(30) NOT NULL, -- PENDING_VALIDATION | APPROVED | REJECTED | CANCELLED
                         created_at DATETIME NOT NULL,
                         updated_at DATETIME NOT NULL,
                         KEY idx_eventos_prof (professor_responsavel_id),
                         KEY idx_eventos_mod (modalidade_id),
                         CONSTRAINT fk_evento_prof FOREIGN KEY (professor_responsavel_id) REFERENCES professores(id),
                         CONSTRAINT fk_evento_mod FOREIGN KEY (modalidade_id) REFERENCES modalidades(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9) EVENTO_INSCRICOES
CREATE TABLE evento_inscricoes (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   evento_id BIGINT NOT NULL,
                                   aluno_id BIGINT NOT NULL,
                                   status VARCHAR(20) NOT NULL, -- INVITED | CONFIRMED_YES | CONFIRMED_NO
                                   invited_at DATETIME NOT NULL,
                                   responded_at DATETIME NULL,
                                   UNIQUE KEY uk_evento_aluno (evento_id, aluno_id),
                                   CONSTRAINT fk_ei_evento FOREIGN KEY (evento_id) REFERENCES eventos(id),
                                   CONSTRAINT fk_ei_aluno FOREIGN KEY (aluno_id) REFERENCES alunos(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10) PAGAMENTOS
CREATE TABLE pagamentos (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            aluno_id BIGINT NOT NULL,
                            professor_id BIGINT NOT NULL,
                            modalidade_id BIGINT NULL,
                            evento_id BIGINT NULL,
                            status VARCHAR(20) NOT NULL, -- PENDING | PAID | OVERDUE | CANCELLED | REFUNDED
                            valor DECIMAL(10,2) NOT NULL,
                            due_date DATE NOT NULL,
                            gateway VARCHAR(40) NOT NULL,
                            gateway_reference_id VARCHAR(120) NOT NULL,
                            payment_link_url TEXT NULL,
                            paid_at DATETIME NULL,
                            created_at DATETIME NOT NULL,
                            updated_at DATETIME NOT NULL,
                            KEY idx_pag_prof (professor_id),
                            KEY idx_pag_aluno (aluno_id),
                            CONSTRAINT fk_pag_aluno FOREIGN KEY (aluno_id) REFERENCES alunos(id),
                            CONSTRAINT fk_pag_prof FOREIGN KEY (professor_id) REFERENCES professores(id),
                            CONSTRAINT fk_pag_mod FOREIGN KEY (modalidade_id) REFERENCES modalidades(id),
                            CONSTRAINT fk_pag_evento FOREIGN KEY (evento_id) REFERENCES eventos(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11) TOKENS
CREATE TABLE tokens (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        email VARCHAR(254) NOT NULL,
                        type VARCHAR(40) NOT NULL, -- REGISTER_VERIFY | PASSWORD_RESET | PASSWORD_CHANGE_CONFIRM
                        token_hash VARCHAR(255) NOT NULL,
                        expires_at DATETIME NOT NULL,
                        consumed_at DATETIME NULL,
                        attempts INT NOT NULL DEFAULT 0,
                        cooldown_until DATETIME NULL,
                        created_at DATETIME NOT NULL,
                        KEY idx_tokens_email_type (email, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

