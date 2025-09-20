package com.ContaCerta.ContaCerta.repository;

import com.ContaCerta.ContaCerta.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}