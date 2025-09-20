package com.ContaCerta.ContaCerta.service;

import com.ContaCerta.ContaCerta.dto.ClienteDTO;
import com.ContaCerta.ContaCerta.entity.Cliente;
import com.ContaCerta.ContaCerta.repository.ClienteRepository;
import com.ContaCerta.ContaCerta.repository.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public ClienteDTO salvar(ClienteDTO clienteDTO) {
        if (clienteRepository.existsByEmail(clienteDTO.getEmail())) {
            throw new RuntimeException("Já existe um cliente com este e-mail");
        }

        Cliente cliente = convertToEntity(clienteDTO);
        cliente = clienteRepository.save(cliente);
        return convertToDTO(cliente);
    }

    public Optional<ClienteDTO> atualizar(Long id, ClienteDTO clienteDTO) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    // Verifica se o email já existe em outro cliente
                    if (!cliente.getEmail().equals(clienteDTO.getEmail()) &&
                            clienteRepository.existsByEmail(clienteDTO.getEmail())) {
                        throw new RuntimeException("Já existe um cliente com este e-mail");
                    }

                    cliente.setNome(clienteDTO.getNome());
                    cliente.setEmail(clienteDTO.getEmail());
                    cliente.setTelefone(clienteDTO.getTelefone());
                    cliente = clienteRepository.save(cliente);
                    return convertToDTO(cliente);
                });
    }

    public boolean deletar(Long id) {
        if (clienteRepository.existsById(id)) {
            // Verificar se o cliente está sendo usado em movimentações
            boolean clienteEmUso = movimentacaoRepository.existsByClienteId(id);

            if (clienteEmUso) {
                throw new RuntimeException("Não é possível excluir o cliente pois está vinculado a movimentações");
            }

            clienteRepository.deleteById(id);
            return true;
        }
        return false;
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