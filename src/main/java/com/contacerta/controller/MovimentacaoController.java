package com.contacerta.controller;

import com.contacerta.dto.MovimentacaoDTO;
import com.contacerta.service.MovimentacaoService;
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
    public ResponseEntity<List<MovimentacaoDTO>> listarTodos() {
        try {
            List<MovimentacaoDTO> movimentacoes = movimentacaoService.listarTodas();
            return ResponseEntity.ok(movimentacoes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar movimentação por ID")
    public ResponseEntity<MovimentacaoDTO> buscarPorId(@PathVariable Long id) {
        try {
            Optional<MovimentacaoDTO> movimentacao = movimentacaoService.buscarPorId(id);
            return movimentacao.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    @Operation(summary = "Criar uma nova movimentação")
    public ResponseEntity<MovimentacaoDTO> criar(@Valid @RequestBody MovimentacaoDTO movimentacaoDTO) {
        try {
            MovimentacaoDTO movimentacaoSalva = movimentacaoService.salvar(movimentacaoDTO);
            return ResponseEntity.ok(movimentacaoSalva);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/saldo")
    @Operation(summary = "Calcular saldo atual")
    public ResponseEntity<BigDecimal> calcularSaldo() {
        try {
            BigDecimal saldo = movimentacaoService.calcularSaldo();
            return ResponseEntity.ok(saldo);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
