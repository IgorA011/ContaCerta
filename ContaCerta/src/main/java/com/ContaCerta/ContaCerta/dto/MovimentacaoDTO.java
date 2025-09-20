package com.ContaCerta.ContaCerta.dto;

import com.contacerta.entity.TipoMovimentacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class MovimentacaoDTO {
    private Long id;

    @NotNull(message = "Tipo de movimentação é obrigatório")
    private TipoMovimentacao tipo;

    @DecimalMin(value = "0.0", inclusive = false, message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    private String descricao;

    private Long clienteId;

    private Long produtoId;


    public MovimentacaoDTO() {}

    public MovimentacaoDTO(TipoMovimentacao tipo, BigDecimal valor, String descricao, Long clienteId, Long produtoId) {
        this.tipo = tipo;
        this.valor = valor;
        this.descricao = descricao;
        this.clienteId = clienteId;
        this.produtoId = produtoId;
    }

    // Get e set
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoMovimentacao getTipo() { return tipo; }
    public void setTipo(TipoMovimentacao tipo) { this.tipo = tipo; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
}