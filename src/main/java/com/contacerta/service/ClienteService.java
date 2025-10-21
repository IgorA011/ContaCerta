package com.contacerta.service;

import com.contacerta.dto.ClienteDTO;
import com.contacerta.entity.Cliente;
import com.contacerta.repository.ClienteRepository;
import com.contacerta.repository.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    public List<ClienteDTO> listarTodos() {
        return clienteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ClienteDTO> buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional
    public ClienteDTO salvar(ClienteDTO clienteDTO) {
        // Verificar se já existe cliente com este email
        if (clienteRepository.existsByEmail(clienteDTO.getEmail())) {
            throw new RuntimeException("Já existe um cliente cadastrado com este e-mail: " + clienteDTO.getEmail());
        }

        Cliente cliente = convertToEntity(clienteDTO);
        cliente = clienteRepository.save(cliente);
        return convertToDTO(cliente);
    }

    @Transactional
    public Optional<ClienteDTO> atualizar(Long id, ClienteDTO clienteDTO) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    // Verificar se o email já existe em outro cliente
                    if (!cliente.getEmail().equals(clienteDTO.getEmail()) &&
                            clienteRepository.existsByEmail(clienteDTO.getEmail())) {
                        throw new RuntimeException("Já existe um cliente cadastrado com este e-mail: " + clienteDTO.getEmail());
                    }

                    cliente.setNome(clienteDTO.getNome());
                    cliente.setEmail(clienteDTO.getEmail());
                    cliente.setTelefone(clienteDTO.getTelefone());

                    cliente = clienteRepository.save(cliente);
                    return convertToDTO(cliente);
                });
    }

    @Transactional
    public boolean deletar(Long id) {
        if (clienteRepository.existsById(id)) {
            // Verificar se o cliente está sendo usado em movimentações
            boolean clienteEmUso = movimentacaoRepository.existsByClienteId(id);

            if (clienteEmUso) {
                throw new RuntimeException("Não é possível excluir o cliente pois está vinculado a movimentações financeiras. " +
                        "Delete as movimentações associadas primeiro.");
            }

            clienteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean verificarEmailExistente(String email) {
        return clienteRepository.existsByEmail(email);
    }

    public List<ClienteDTO> pesquisarClientes(String nome, String email, String telefone) {
        List<Cliente> clientes;

        if (nome != null && !nome.isEmpty()) {
            clientes = clienteRepository.findByNomeContainingIgnoreCase(nome);
        } else if (email != null && !email.isEmpty()) {
            clientes = clienteRepository.findByEmailContainingIgnoreCase(email);
        } else if (telefone != null && !telefone.isEmpty()) {
            clientes = clienteRepository.findByTelefoneContaining(telefone);
        } else {
            clientes = clienteRepository.findAll();
        }

        return clientes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ClienteDTO convertToDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setEmail(cliente.getEmail());
        dto.setTelefone(cliente.getTelefone());
        return dto;
    }

    private Cliente convertToEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        return cliente;
    }
}