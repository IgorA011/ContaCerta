package com.contacerta.repository;

import com.contacerta.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByEmail(String email);

    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    List<Cliente> findByEmailContainingIgnoreCase(String email);

    List<Cliente> findByTelefoneContaining(String telefone);

    @Query("SELECT COUNT(c) > 0 FROM Cliente c WHERE c.email = :email AND c.id <> :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);
}