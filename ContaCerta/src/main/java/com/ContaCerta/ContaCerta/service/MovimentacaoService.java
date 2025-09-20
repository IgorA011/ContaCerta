package com.ContaCerta.ContaCerta.service;

import com.ContaCerta.ContaCerta.dto.MovimentacaoDTO;
import com.ContaCerta.ContaCerta.entity.Cliente;
import com.ContaCerta.ContaCerta.entity.Movimentacao;
import com.ContaCerta.ContaCerta.entity.Produto;
import com.ContaCerta.ContaCerta.entity.TipoMovimentacao;
import com.ContaCerta.ContaCerta.repository.ClienteRepository;
import com.ContaCerta.ContaCerta.repository.MovimentacaoRepository;
import com.ContaCerta.ContaCerta.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
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
        return movimentacaoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<MovimentacaoDTO> buscarPorId(Long id) {
        return movimentacaoRepository.findById(id)
                .map(this::convertToDTO);
    }

    public MovimentacaoDTO salvar(MovimentacaoDTO movimentacaoDTO) {
        Movimentacao movimentacao = convertToEntity(movimentacaoDTO);
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

        if (dto.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
            movimentacao.setCliente(cliente);
        }

        if (dto.getProdutoId() != null) {
            Produto produto = produtoRepository.findById(dto.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
            movimentacao.setProduto(produto);
        }

        return movimentacao;
    }
}