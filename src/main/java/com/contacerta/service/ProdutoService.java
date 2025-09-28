package com.contacerta.service;

import com.contacerta.dto.ProdutoDTO;
import com.contacerta.entity.Produto;
import com.contacerta.repository.MovimentacaoRepository;
import com.contacerta.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    public List<ProdutoDTO> listarTodos() {
        return produtoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProdutoDTO> buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .map(this::convertToDTO);
    }

    public ProdutoDTO salvar(ProdutoDTO produtoDTO) {
        Produto produto = convertToEntity(produtoDTO);
        produto = produtoRepository.save(produto);
        return convertToDTO(produto);
    }

    public Optional<ProdutoDTO> atualizar(Long id, ProdutoDTO produtoDTO) {
        return produtoRepository.findById(id)
                .map(produto -> {
                    produto.setNome(produtoDTO.getNome());
                    produto.setPreco(produtoDTO.getPreco());
                    produto.setQuantidadeEstoque(produtoDTO.getQuantidadeEstoque());
                    produto = produtoRepository.save(produto);
                    return convertToDTO(produto);
                });
    }

    public boolean deletar(Long id) {
        if (produtoRepository.existsById(id)) {
            // Verificar se o produto está sendo usado em movimentações
            boolean produtoEmUso = movimentacaoRepository.existsByProdutoId(id);

            if (produtoEmUso) {
                throw new RuntimeException("Não é possível excluir o produto pois está vinculado a movimentações");
            }

            produtoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private ProdutoDTO convertToDTO(Produto produto) {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setPreco(produto.getPreco());
        dto.setQuantidadeEstoque(produto.getQuantidadeEstoque());
        return dto;
    }

    private Produto convertToEntity(ProdutoDTO dto) {
        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setPreco(dto.getPreco());
        produto.setQuantidadeEstoque(dto.getQuantidadeEstoque());
        return produto;
    }
}