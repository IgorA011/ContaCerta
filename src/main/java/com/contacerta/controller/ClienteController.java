package com.contacerta.controller;

import com.contacerta.dto.ClienteDTO;
import com.contacerta.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Gerenciamento de clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    @Operation(summary = "Listar todos os clientes")
    public ResponseEntity<List<ClienteDTO>> listarTodos() {
        try {
            List<ClienteDTO> clientes = clienteService.listarTodos();
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    public ResponseEntity<ClienteDTO> buscarPorId(@PathVariable Long id) {
        try {
            Optional<ClienteDTO> cliente = clienteService.buscarPorId(id);
            return cliente.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    @Operation(summary = "Criar um novo cliente")
    public ResponseEntity<?> criar(@Valid @RequestBody ClienteDTO clienteDTO) {
        try {
            ClienteDTO clienteSalvo = clienteService.salvar(clienteDTO);
            return ResponseEntity.ok(clienteSalvo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro interno ao criar cliente");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um cliente existente")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody ClienteDTO clienteDTO) {
        try {
            Optional<ClienteDTO> clienteAtualizado = clienteService.atualizar(id, clienteDTO);
            return clienteAtualizado.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro interno ao atualizar cliente");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um cliente")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            boolean deletado = clienteService.deletar(id);
            return deletado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro interno ao excluir cliente");
        }
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Verificar se email existe")
    public ResponseEntity<Boolean> verificarEmail(@PathVariable String email) {
        try {
            boolean existe = clienteService.verificarEmailExistente(email);
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Pesquisar clientes por nome, email ou telefone")
    public ResponseEntity<List<ClienteDTO>> pesquisarClientes(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telefone) {
        try {
            List<ClienteDTO> clientes = clienteService.pesquisarClientes(nome, email, telefone);
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}