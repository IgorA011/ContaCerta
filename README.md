Video do Projeto: https://youtu.be/emW88lU609s

# 💰 Sistema Financeiro - Conta Certa

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.0-green?style=for-the-badge&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-purple?style=for-the-badge&logo=bootstrap)

Sistema completo de gestão financeira com controle de clientes, produtos e movimentações financeiras. Desenvolvido com arquitetura moderna e interface responsiva.

## 🚀 Demonstração

**Acesso à Aplicação:** `http://localhost:8080`  
**Documentação API:** `http://localhost:8080/swagger-ui.html`

### 👥 Credenciais de Teste

| Perfil | Usuário | Senha | Acesso |
|--------|---------|-------|---------|
| Administrador | `admin@contacerta.com` | `senha123` | Completo |
| Atendente | `atendente@contacerta.com` | `senha123` | Consultas |

## ✨ Funcionalidades

### 👨‍💼 Módulo Administrativo
- ✅ **Gestão Completa de Clientes** - Cadastro, edição e exclusão
- ✅ **Controle de Produtos** - Cadastro com preço e estoque
- ✅ **Movimentações Financeiras** - Entradas, saídas e saldo
- ✅ **Relatórios Detalhados** - Exportação em CSV
- ✅ **Dashboard Interativo** - Métricas em tempo real

### 👩‍💼 Módulo Atendente
- ✅ **Consulta de Clientes** - Busca avançada
- ✅ **Consulta de Produtos** - Listagem completa
- ✅ **Visualização Limitada** - Acesso seguro e controlado

## 🛠️ Tecnologias

### Backend
| Tecnologia | Versão | Finalidade |
|------------|--------|------------|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.2.0 | Framework backend |
| Spring Data JPA | 3.2.0 | Persistência de dados |
| Spring Security | 3.2.0 | Autenticação e autorização |
| MySQL | 8.0 | Banco de dados |
| OpenAPI 3 | 2.2.0 | Documentação da API |

### Frontend
| Tecnologia | Versão | Finalidade |
|------------|--------|------------|
| HTML5 | - | Estrutura semântica |
| CSS3 | - | Estilização moderna |
| JavaScript | ES6+ | Lógica da aplicação |
| Bootstrap | 5.3 | Framework CSS |
| Bootstrap Icons | 1.10 | Conjunto de ícones |

## 📦 Estrutura do Projeto

financeiro/
├── src/main/java/com/contacerta/
│ ├── config/ # Configurações
│ ├── controller/ # APIs REST
│ ├── entity/ # Entidades JPA
│ ├── repository/ # Spring Data
│ ├── service/ # Lógica de negócio
│ └── dto/ # DTOs
├── src/main/resources/
│ ├── static/ # CSS, JS, Imagens
│ ├── templates/ # HTML
│ └── application.properties
└── pom.xml

## 🚀 Instalação e Configuração

### Pré-requisitos
- Java 21+
- MySQL 8.0+
- Maven 3.6+

### 📋 Passo a Passo

1. **Clone o repositório**

git clone https://github.com/IgorA011/ContaCerta

## Configure o Banco de Dados ##

CREATE DATABASE financeiro_db;
CREATE USER 'financeiro_user'@'localhost' IDENTIFIED BY 'sua_senha_segura';
GRANT ALL PRIVILEGES ON financeiro_db.* TO 'financeiro_user'@'localhost';
FLUSH PRIVILEGES;

## Configure a Aplicação ##

# src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/financeiro_db
spring.datasource.username=financeiro_user
spring.datasource.password=sua_senha_segura

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
server.port=8080

**Execute a Aplicação**

mvn clean install
mvn spring-boot:run

## Acesse a Aplicação ##
http://localhost:8080

## API Reference ##

**Clientes**
Método	Endpoint	Descrição	Permissão
GET	/api/clientes	Listar clientes	Todos
GET	/api/clientes/{id}	Buscar por ID	Todos
POST	/api/clientes	Criar cliente	Admin
PUT	/api/clientes/{id}	Atualizar cliente	Admin
DELETE	/api/clientes/{id}	Excluir cliente	Admin

**Produtos**
Método	Endpoint	Descrição	Permissão
GET	/api/produtos	Listar produtos	Todos
GET	/api/produtos/{id}	Buscar por ID	Todos
POST	/api/produtos	Criar produto	Admin
PUT	/api/produtos/{id}	Atualizar produto	Admin
DELETE	/api/produtos/{id}	Excluir produto	Admin

**Movimentações Financeiras**
Método	Endpoint	Descrição	Permissão
GET	/api/movimentacoes	Listar movimentações	Admin
GET	/api/movimentacoes/{id}	Buscar por ID	Admin
POST	/api/movimentacoes	Criar movimentação	Admin
GET	/api/movimentacoes/saldo	Calcular saldo	Admin

## Exemplos de Uso da API ##

**Criar Cliente**
http
POST /api/clientes
Content-Type: application/json

{
  "nome": "Maria Silva",
  "email": "maria@empresa.com",
  "telefone": "(11) 99999-9999"
}
**Registrar Movimentação**
http
POST /api/movimentacoes
Content-Type: application/json

{
  "tipo": "ENTRADA",
  "valor": 1500.50,
  "clienteId": 1,
  "produtoId": 1,
  "descricao": "Venda de software"
}
**Consultar Saldo**
http
GET /api/movimentacoes/saldo

Response: 12500.75

## Scripts SQL ##

-- Tabela de Clientes
CREATE TABLE clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    telefone VARCHAR(20) NOT NULL
);

-- Tabela de Produtos
CREATE TABLE produtos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    preco DECIMAL(10,2) NOT NULL CHECK (preco > 0),
    quantidade_estoque INT NOT NULL CHECK (quantidade_estoque >= 0)
);

-- Tabela de Movimentações
CREATE TABLE movimentacoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo ENUM('ENTRADA', 'SAIDA') NOT NULL,
    valor DECIMAL(10,2) NOT NULL CHECK (valor > 0),
    descricao TEXT,
    data_movimentacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cliente_id BIGINT,
    produto_id BIGINT,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    FOREIGN KEY (produto_id) REFERENCES produtos(id)
);


<img width="1536" height="1024" alt="contac" src="https://github.com/user-attachments/assets/3cd4b65e-d304-4738-8fe0-106d99651bf6" />


<img width="626" height="563" alt="Captura de tela 2025-10-15 185650" src="https://github.com/user-attachments/assets/13187cc7-5804-4ca8-bcbc-a1dcd810d6ce" />


- Interface do Usuário
Design System
Cores Primárias: #1e2a3a (Azul escuro), #2ecc71 (Verde), #e74c3c (Vermelho)

Tipografia: Inter font family

Layout: Sidebar fixa com design responsivo

Componentes: Cards com sombras e bordas arredondadas

**Telas Principais**

🏠 Dashboard

- Métricas em tempo real
- Últimas movimentações
- Saldo atual
- Gráficos de performance

👥 Gestão de Clientes

- Formulário de cadastro
- Tabela com lista de clientes
- Busca e filtros
- Ações de edição/exclusão

📦 Gestão de Produtos

- Controle de estoque
- Preços e atualizações
- Histórico de movimentações

💰 Movimentações Financeiras

- Registro de entradas/saídas
- Vinculação com clientes/produtos
- Histórico completo
- Cálculo automático de saldo

📈 Próximas Melhorias

- Implementação de Cache - Redis para performance
- Upload de Imagens - Fotos de produtos e clientes
- Notificações em Tempo Real - WebSocket para alertas
- Dashboard com Gráficos - Chart.js ou similar
- API Mobile - Endpoints otimizados para mobile
- Testes Automatizados - JUnit e Mockito
- Dockerização - Containers para deploy
- Logs Centralizados - ELK Stack

🤝 Contribuição

- Fork o projeto
- Crie uma branch para sua feature (git checkout -b feature/AmazingFeature)
- Commit suas mudanças (git commit -m 'Add some AmazingFeature')
- Push para a branch (git push origin feature/AmazingFeature)
- Abra um Pull Request

👨‍💻 Desenvolvedor - Igor Alves

📧 Email: igor_alvesoliveira@hotmail.com.br

🔗 LinkedIn: https://www.linkedin.com/in/igora011/

🐙 GitHub: @IgorA011
