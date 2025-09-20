package com.ContaCerta.ContaCerta.repository;

import com.ContaCerta.ContaCerta.entity.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    @Query("SELECT COALESCE(SUM(m.valor), 0) FROM Movimentacao m WHERE m.tipo = 'ENTRADA'")
    BigDecimal sumEntradas();

    @Query("SELECT COALESCE(SUM(m.valor), 0) FROM Movimentacao m WHERE m.tipo = 'SAIDA'")
    BigDecimal sumSaidas();

    @Query("SELECT COUNT(m) > 0 FROM Movimentacao m WHERE m.produto.id = :produtoId")
    boolean existsByProdutoId(Long produtoId);

    @Query("SELECT COUNT(m) > 0 FROM Movimentacao m WHERE m.cliente.id = :clienteId")
    boolean existsByClienteId(Long clienteId);
}