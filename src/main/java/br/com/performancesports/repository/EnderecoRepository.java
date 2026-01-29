package br.com.performancesports.repository;

import br.com.performancesports.domain.endereco.Endereco;
import br.com.performancesports.domain.endereco.EnderecoOwnerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    List<Endereco> findAllByOwnerTypeAndOwnerId(EnderecoOwnerType ownerType, Long ownerId);
    long countByOwnerTypeAndOwnerId(EnderecoOwnerType ownerType, Long ownerId);
}
