package com.ContaCerta.ContaCerta.controller;

import com.ContaCerta.ContaCerta.dto.MovimentacaoDTO;
import com.ContaCerta.ContaCerta.service.MovimentacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/movimentacoes")
@Tag(name = "Movimentações", description = "Gerenciamento de movimentações financeiras")
public class MovimentacaoController {

    @Autowired
    private MovimentacaoService movimentacaoService;

    @GetMapping
    @Operation(summary = "Listar todas as movimentações")
    public List<MovimentacaoDTO> listarTodos() {
        return movimentacaoService.listarTodas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar movimentação por ID")
    public ResponseEntity<MovimentacaoDTO> buscarPorId(@PathVariable Long id) {
        Optional<MovimentacaoDTO> movimentacao = movimentacaoService.buscarPorId(id);
        return movimentacao.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar uma nova movimentação")
    public ResponseEntity<MovimentacaoDTO> criar(@Valid @RequestBody MovimentacaoDTO movimentacaoDTO) {
        MovimentacaoDTO movimentacaoSalva = movimentacaoService.salvar(movimentacaoDTO);
        return ResponseEntity.ok(movimentacaoSalva);
    }

    @GetMapping("/saldo")
    @Operation(summary = "Calcular saldo atual")
    public ResponseEntity<BigDecimal> calcularSaldo() {
        BigDecimal saldo = movimentacaoService.calcularSaldo();
        return ResponseEntity.ok(saldo);
    }


}