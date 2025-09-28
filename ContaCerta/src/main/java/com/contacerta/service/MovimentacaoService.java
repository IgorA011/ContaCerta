package com.contacerta.service;

import com.contacerta.dto.MovimentacaoDTO;
import com.contacerta.entity.Cliente;
import com.contacerta.entity.Movimentacao;
import com.contacerta.entity.Produto;
import com.contacerta.repository.ClienteRepository;
import com.contacerta.repository.MovimentacaoRepository;
import com.contacerta.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovimentacaoService {

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<MovimentacaoDTO> listarTodas() {
        return movimentacaoRepository.findAllByOrderByDataMovimentacaoDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<MovimentacaoDTO> buscarPorId(Long id) {
        return movimentacaoRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional
    public MovimentacaoDTO salvar(MovimentacaoDTO movimentacaoDTO) {
        Movimentacao movimentacao = convertToEntity(movimentacaoDTO);

        // Validar se cliente existe
        if (movimentacaoDTO.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(movimentacaoDTO.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + movimentacaoDTO.getClienteId()));
            movimentacao.setCliente(cliente);
        }

        // Validar se produto existe
        if (movimentacaoDTO.getProdutoId() != null) {
            Produto produto = produtoRepository.findById(movimentacaoDTO.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + movimentacaoDTO.getProdutoId()));
            movimentacao.setProduto(produto);
        }

        movimentacao = movimentacaoRepository.save(movimentacao);
        return convertToDTO(movimentacao);
    }

    public BigDecimal calcularSaldo() {
        BigDecimal entradas = movimentacaoRepository.sumEntradas();
        BigDecimal saidas = movimentacaoRepository.sumSaidas();
        return entradas.subtract(saidas);
    }

    private MovimentacaoDTO convertToDTO(Movimentacao movimentacao) {
        MovimentacaoDTO dto = new MovimentacaoDTO();
        dto.setId(movimentacao.getId());
        dto.setDataMovimentacao(movimentacao.getDataMovimentacao());
        dto.setTipo(movimentacao.getTipo());
        dto.setValor(movimentacao.getValor());
        dto.setDescricao(movimentacao.getDescricao());

        if (movimentacao.getCliente() != null) {
            dto.setClienteId(movimentacao.getCliente().getId());
        }

        if (movimentacao.getProduto() != null) {
            dto.setProdutoId(movimentacao.getProduto().getId());
        }

        return dto;
    }

    private Movimentacao convertToEntity(MovimentacaoDTO dto) {
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setTipo(dto.getTipo());
        movimentacao.setValor(dto.getValor());
        movimentacao.setDescricao(dto.getDescricao());

        // Sempre define data do backend
        movimentacao.setDataMovimentacao(LocalDateTime.now());

        return movimentacao;
    }

    @Transactional
    public void deletar(Long id) {
        if (!movimentacaoRepository.existsById(id)) {
            throw new RuntimeException("Movimentação não encontrada com ID: " + id);
        }
        movimentacaoRepository.deleteById(id);
    }
}
