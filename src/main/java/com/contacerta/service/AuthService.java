package com.contacerta.service;

import com.contacerta.dto.LoginRequest;
import com.contacerta.dto.LoginResponse;
import com.contacerta.entity.Usuario;
import com.contacerta.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public LoginResponse login(LoginRequest loginRequest) {
        // Buscar usuário pelo email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(loginRequest.getEmail());

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar senha
        if (!passwordEncoder.matches(loginRequest.getSenha(), usuario.getSenha())) {
            throw new RuntimeException("Senha incorreta");
        }

        // REMOVIDO: Verificação de tipo de usuário
        // O sistema agora detecta automaticamente pelo email

        // Gerar token JWT
        String token = jwtService.generateToken(usuario);

        return new LoginResponse(token, usuario.getNome(), usuario.getTipo(), usuario.getEmail());
    }

    public void criarUsuariosPadrao() {
        // Criar usuário admin padrão se não existir
        if (!usuarioRepository.existsByEmail("admin@contacerta.com")) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@contacerta.com");
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setTipo(com.contacerta.entity.TipoUsuario.ADMIN);
            usuarioRepository.save(admin);
            System.out.println("Usuário admin criado: admin@contacerta.com / admin123");
        }

        // Criar usuário atendente padrão se não existir
        if (!usuarioRepository.existsByEmail("atendente@contacerta.com")) {
            Usuario atendente = new Usuario();
            atendente.setNome("Atendente");
            atendente.setEmail("atendente@contacerta.com");
            atendente.setSenha(passwordEncoder.encode("atendente123"));
            atendente.setTipo(com.contacerta.entity.TipoUsuario.ATENDENTE);
            usuarioRepository.save(atendente);
            System.out.println("Usuário atendente criado: atendente@contacerta.com / atendente123");
        }
    }
}