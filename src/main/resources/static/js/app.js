console.log("app.js carregado com sucesso!");

// Dados de exemplo para demonstração
const usuarios = [
    { username: 'admin@contacerta.com', password: 'senha123', role: 'admin', nome: 'Administrador' },
    { username: 'atendente@contacerta.com', password: 'senha123', role: 'atendente', nome: 'Atendente' }
];

// Dados de exemplo
let clientes = [];
let produtos = [];
let movimentacoes = [];

// Estado da aplicação
let currentUser = null;
const API_BASE_URL = 'http://localhost:8080/api';

// Elementos da DOM
const loginPage = document.getElementById('login-page');
const app = document.getElementById('app');
const loginForm = document.getElementById('login-form');
const logoutBtn = document.getElementById('logout-btn');
const currentUserElement = document.getElementById('current-user');
const userRoleElement = document.getElementById('user-role');
const adminOnlyElements = document.querySelectorAll('.admin-only');
const atendenteRestrictedElements = document.querySelectorAll('.atendente-restricted');

// Função para chamadas API
async function apiCall(endpoint, options = {}) {
    try {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        };

        if (options.body) {
            config.body = JSON.stringify(options.body);
        }

        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Erro HTTP ${response.status}: ${errorText}`);
        }

        // Para respostas sem conteúdo (DELETE, etc.)
        if (response.status === 204) {
            return null;
        }

        return await response.json();
    } catch (error) {
        console.error('Erro na chamada API:', error);
        throw error;
    }
}

// Funções de inicialização
document.addEventListener('DOMContentLoaded', function() {
    // Verificar se há um usuário logado
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
        currentUser = JSON.parse(savedUser);
        showApp();
    }

    // Configurar eventos
    setupEventListeners();

    // Carregar dados iniciais se estiver logado
    if (currentUser) {
        loadInitialData();
    }
});

// Configurar event listeners
function setupEventListeners() {
    // Login
    loginForm.addEventListener('submit', handleLogin);

    // Logout
    logoutBtn.addEventListener('click', handleLogout);

    // Navegação
    document.querySelectorAll('.sidebar .nav-link').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const target = this.getAttribute('href').substring(1);
            showSection(target);
        });
    });

    // Formulários
    document.getElementById('cliente-form')?.addEventListener('submit', handleClienteSubmit);
    document.getElementById('produto-form')?.addEventListener('submit', handleProdutoSubmit);
    document.getElementById('movimentacao-form')?.addEventListener('submit', handleMovimentacaoSubmit);

    // Botões de pesquisa
    document.getElementById('search-cliente-btn')?.addEventListener('click', searchClientes);
    document.getElementById('search-produto-btn')?.addEventListener('click', searchProdutos);
    document.getElementById('cliente-search')?.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') searchClientes();
    });
    document.getElementById('produto-search')?.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') searchProdutos();
    });

    // Botões de exportação
    document.getElementById('export-clientes-btn')?.addEventListener('click', exportClientes);
    document.getElementById('export-produtos-btn')?.addEventListener('click', exportProdutos);
    document.getElementById('export-financeiro-btn')?.addEventListener('click', exportFinanceiro);
}

// Handlers de eventos
async function handleLogin(e) {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const userType = document.getElementById('user-type').value;

    try {
        // Verificar credenciais locais (para demonstração)
        const usuario = usuarios.find(u =>
            u.username === username &&
            u.password === password &&
            u.role === userType
        );

        if (usuario) {
            currentUser = usuario;
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            showApp();
            await loadInitialData();
            showNotification('Login realizado com sucesso!', 'success');
        } else {
            showNotification('Credenciais inválidas. Tente novamente.', 'error');
        }
    } catch (error) {
        showNotification('Erro ao fazer login. Tente novamente.', 'error');
        console.error('Erro no login:', error);
    }
}

function handleLogout() {
    currentUser = null;
    localStorage.removeItem('currentUser');
    hideApp();
    showNotification('Logout realizado com sucesso!', 'info');
}

async function handleClienteSubmit(e) {
    e.preventDefault();

    // Verificar permissão
    if (currentUser.role === 'atendente') {
        showNotification('Atendentes não podem cadastrar clientes. Apenas administradores.', 'error');
        return;
    }

    try {
        const clienteData = {
            nome: document.getElementById('cliente-nome').value,
            email: document.getElementById('cliente-email').value,
            telefone: document.getElementById('cliente-telefone').value
        };

        const novoCliente = await apiCall('/clientes', {
            method: 'POST',
            body: clienteData
        });

        clientes.push(novoCliente);
        e.target.reset();
        renderClientes();
        populateSelects();
        showNotification('Cliente cadastrado com sucesso!', 'success');
    } catch (error) {
        showNotification('Erro ao cadastrar cliente: ' + error.message, 'error');
    }
}

async function handleProdutoSubmit(e) {
    e.preventDefault();

    // Verificar permissão
    if (currentUser.role === 'atendente') {
        showNotification('Atendentes não podem cadastrar produtos. Apenas administradores.', 'error');
        return;
    }

    try {
        const produtoData = {
            nome: document.getElementById('produto-nome').value,
            preco: parseFloat(document.getElementById('produto-preco').value),
            quantidadeEstoque: parseInt(document.getElementById('produto-estoque').value)
        };

        const novoProduto = await apiCall('/produtos', {
            method: 'POST',
            body: produtoData
        });

        produtos.push(novoProduto);
        e.target.reset();
        renderProdutos();
        populateSelects();
        showNotification('Produto cadastrado com sucesso!', 'success');
    } catch (error) {
        showNotification('Erro ao cadastrar produto: ' + error.message, 'error');
    }
}

async function handleMovimentacaoSubmit(e) {
    e.preventDefault();

    // Verificar permissão - ATENDENTE NÃO PODE FAZER MOVIMENTAÇÕES
    if (currentUser.role === 'atendente') {
        showNotification('Atendentes não podem fazer movimentações financeiras. Apenas administradores.', 'error');
        return;
    }

    try {
        const movimentacaoData = {
            tipo: document.getElementById('movimentacao-tipo').value,
            valor: parseFloat(document.getElementById('movimentacao-valor').value),
            clienteId: document.getElementById('movimentacao-cliente').value ?
                       parseInt(document.getElementById('movimentacao-cliente').value) : null,
            produtoId: document.getElementById('movimentacao-produto').value ?
                       parseInt(document.getElementById('movimentacao-produto').value) : null,
            descricao: document.getElementById('movimentacao-descricao').value
        };

        const novaMovimentacao = await apiCall('/movimentacoes', {
            method: 'POST',
            body: movimentacaoData
        });

        movimentacoes.push(novaMovimentacao);
        e.target.reset();
        renderMovimentacoes();
        await updateDashboard();
        showNotification('Movimentação registrada com sucesso!', 'success');
    } catch (error) {
        showNotification('Erro ao registrar movimentação: ' + error.message, 'error');
    }
}

// Funções de renderização
function renderClientes() {
    const clientesList = document.getElementById('clientes-list');
    if (!clientesList) return;

    clientesList.innerHTML = '';

    if (clientes.length === 0) {
        clientesList.innerHTML = '<tr><td colspan="4" class="text-center">Nenhum cliente cadastrado</td></tr>';
        return;
    }

    clientes.forEach(cliente => {
        const row = document.createElement('tr');

        // Mostrar botão de excluir apenas para admin
        const acoesBtn = currentUser.role === 'admin' ?
            `<button class="btn btn-sm btn-danger" onclick="deleteCliente(${cliente.id})">
                <i class="bi bi-trash"></i> Excluir
            </button>` :
            '<span class="text-muted">Apenas leitura</span>';

        row.innerHTML = `
            <td>${cliente.nome}</td>
            <td>${cliente.email}</td>
            <td>${cliente.telefone}</td>
            <td>${acoesBtn}</td>
        `;
        clientesList.appendChild(row);
    });
}

function renderProdutos() {
    const produtosList = document.getElementById('produtos-list');
    if (!produtosList) return;

    produtosList.innerHTML = '';

    if (produtos.length === 0) {
        produtosList.innerHTML = '<tr><td colspan="4" class="text-center">Nenhum produto cadastrado</td></tr>';
        return;
    }

    produtos.forEach(produto => {
        const row = document.createElement('tr');

        // Mostrar botão de excluir apenas para admin
        const acoesBtn = currentUser.role === 'admin' ?
            `<button class="btn btn-sm btn-danger" onclick="deleteProduto(${produto.id})">
                <i class="bi bi-trash"></i> Excluir
            </button>` :
            '<span class="text-muted">Apenas leitura</span>';

        row.innerHTML = `
            <td>${produto.nome}</td>
            <td>R$ ${produto.preco?.toFixed(2) || '0.00'}</td>
            <td>${produto.quantidadeEstoque || 0}</td>
            <td>${acoesBtn}</td>
        `;
        produtosList.appendChild(row);
    });
}

function renderMovimentacoes() {
    const movimentacoesList = document.getElementById('movimentacoes-list');
    const ultimasMovimentacoes = document.getElementById('ultimas-movimentacoes');
    const relatorioMovimentacoes = document.getElementById('relatorio-movimentacoes');

    if (movimentacoesList) {
        movimentacoesList.innerHTML = '';
    }

    if (ultimasMovimentacoes) {
        ultimasMovimentacoes.innerHTML = '';
    }

    if (relatorioMovimentacoes) {
        relatorioMovimentacoes.innerHTML = '';
    }

    if (movimentacoes.length === 0) {
        const emptyRow = '<tr><td colspan="6" class="text-center">Nenhuma movimentação encontrada</td></tr>';
        if (movimentacoesList) movimentacoesList.innerHTML = emptyRow;
        if (ultimasMovimentacoes) ultimasMovimentacoes.innerHTML = '<tr><td colspan="4" class="text-center">Nenhuma movimentação</td></tr>';
        if (relatorioMovimentacoes) relatorioMovimentacoes.innerHTML = emptyRow;
        return;
    }

    // Ordenar por data (mais recente primeiro)
    const movimentacoesOrdenadas = [...movimentacoes].sort((a, b) =>
        new Date(b.dataMovimentacao) - new Date(a.dataMovimentacao)
    );

    // Renderizar todas as movimentações
    movimentacoesOrdenadas.forEach(mov => {
        const cliente = mov.clienteId ? clientes.find(c => c.id === mov.clienteId) : null;
        const produto = mov.produtoId ? produtos.find(p => p.id === mov.produtoId) : null;

        const data = new Date(mov.dataMovimentacao);
        const dataFormatada = data.toLocaleDateString('pt-BR');
        const horaFormatada = data.toLocaleTimeString('pt-BR');

        const row = `
            <tr>
                <td>${dataFormatada} ${horaFormatada}</td>
                <td>
                    <span class="badge ${mov.tipo === 'ENTRADA' ? 'bg-success' : 'bg-danger'}">
                        ${mov.tipo === 'ENTRADA' ? 'Entrada' : 'Saída'}
                    </span>
                </td>
                <td>R$ ${mov.valor?.toFixed(2) || '0.00'}</td>
                <td>${cliente ? cliente.nome : '-'}</td>
                <td>${produto ? produto.nome : '-'}</td>
                <td>${mov.descricao || '-'}</td>
            </tr>
        `;

        if (movimentacoesList) {
            movimentacoesList.innerHTML += row;
        }

        if (relatorioMovimentacoes) {
            relatorioMovimentacoes.innerHTML += row;
        }
    });

    // Últimas 5 movimentações para o dashboard
    if (ultimasMovimentacoes) {
        const ultimas = movimentacoesOrdenadas.slice(0, 5);
        ultimas.forEach(mov => {
            const cliente = mov.clienteId ? clientes.find(c => c.id === mov.clienteId) : null;
            const produto = mov.produtoId ? produtos.find(p => p.id === mov.produtoId) : null;

            const data = new Date(mov.dataMovimentacao);
            const dataFormatada = data.toLocaleDateString('pt-BR');
            const horaFormatada = data.toLocaleTimeString('pt-BR');

            ultimasMovimentacoes.innerHTML += `
                <tr>
                    <td>${dataFormatada} ${horaFormatada}</td>
                    <td>
                        <span class="badge ${mov.tipo === 'ENTRADA' ? 'bg-success' : 'bg-danger'}">
                            ${mov.tipo === 'ENTRADA' ? 'Entrada' : 'Saída'}
                        </span>
                    </td>
                    <td>R$ ${mov.valor?.toFixed(2) || '0.00'}</td>
                    <td>${mov.descricao || '-'}</td>
                </tr>
            `;
        });
    }
}

function renderMovimentacaoForm() {
    const movimentacaoForm = document.getElementById('movimentacao-form');
    if (!movimentacaoForm) return;

    // Se for atendente, desabilitar o formulário de movimentações
    if (currentUser.role === 'atendente') {
        movimentacaoForm.innerHTML = `
            <div class="alert alert-warning">
                <h5><i class="bi bi-exclamation-triangle"></i> Acesso Restrito</h5>
                <p class="mb-0">Atendentes não podem fazer movimentações financeiras.
                Esta função está disponível apenas para administradores.</p>
            </div>
        `;
    } else {
        // Formulário normal para admin
        movimentacaoForm.innerHTML = `
            <div class="row">
                <div class="col-md-3 mb-3">
                    <label for="movimentacao-tipo" class="form-label">Tipo</label>
                    <select class="form-select" id="movimentacao-tipo" required>
                        <option value="">Selecione...</option>
                        <option value="ENTRADA">Entrada</option>
                        <option value="SAIDA">Saída</option>
                    </select>
                </div>
                <div class="col-md-3 mb-3">
                    <label for="movimentacao-valor" class="form-label">Valor (R$)</label>
                    <input type="number" step="0.01" class="form-control" id="movimentacao-valor" required>
                </div>
                <div class="col-md-3 mb-3">
                    <label for="movimentacao-cliente" class="form-label">Cliente</label>
                    <select class="form-select" id="movimentacao-cliente">
                        <option value="">Selecione um cliente (opcional)</option>
                    </select>
                </div>
                <div class="col-md-3 mb-3">
                    <label for="movimentacao-produto" class="form-label">Produto</label>
                    <select class="form-select" id="movimentacao-produto">
                        <option value="">Selecione um produto (opcional)</option>
                    </select>
                </div>
            </div>
            <div class="row">
                <div class="col-md-12 mb-3">
                    <label for="movimentacao-descricao" class="form-label">Descrição</label>
                    <textarea class="form-control" id="movimentacao-descricao" rows="2"></textarea>
                </div>
            </div>
            <button type="submit" class="btn btn-primary">Registrar Movimentação</button>
        `;

        // Re-popular os selects e re-adicionar o event listener
        populateSelects();
        movimentacaoForm.addEventListener('submit', handleMovimentacaoSubmit);
    }
}

function renderRelatoriosSection() {
    const relatoriosContent = document.getElementById('relatorios-content');
    if (!relatoriosContent) return;

    // Se for atendente, mostrar mensagem de acesso restrito
    if (currentUser.role === 'atendente') {
        relatoriosContent.innerHTML = `
            <div class="card">
                <div class="card-body text-center">
                    <div class="alert alert-warning">
                        <h4><i class="bi bi-exclamation-triangle"></i> Acesso Restrito</h4>
                        <p class="mb-0">Atendentes não têm acesso aos relatórios financeiros.
                        Esta função está disponível apenas para administradores.</p>
                    </div>
                </div>
            </div>
        `;
    } else {
        // Conteúdo normal para admin
        relatoriosContent.innerHTML = `
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-funnel"></i> Filtros do Relatório
                    </h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-3 mb-3">
                            <label for="data-inicio" class="form-label">Data Início</label>
                            <input type="date" class="form-control" id="data-inicio">
                        </div>
                        <div class="col-md-3 mb-3">
                            <label for="data-fim" class="form-label">Data Fim</label>
                            <input type="date" class="form-control" id="data-fim">
                        </div>
                        <div class="col-md-3 mb-3">
                            <label for="tipo-relatorio" class="form-label">Tipo</label>
                            <select class="form-select" id="tipo-relatorio">
                                <option value="">Todos</option>
                                <option value="ENTRADA">Entrada</option>
                                <option value="SAIDA">Saída</option>
                            </select>
                        </div>
                        <div class="col-md-3 mb-3 d-flex align-items-end">
                            <button type="button" class="btn btn-primary w-100" onclick="filtrarRelatorio()">
                                <i class="bi bi-funnel"></i> Filtrar
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-list-check"></i> Relatório de Movimentações
                    </h5>
                    <button type="button" class="btn btn-success" onclick="exportRelatorioMovimentacoes()">
                        <i class="bi bi-download"></i> Exportar CSV
                    </button>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>Data/Hora</th>
                                    <th>Tipo</th>
                                    <th>Valor</th>
                                    <th>Cliente</th>
                                    <th>Produto</th>
                                    <th>Descrição</th>
                                </tr>
                            </thead>
                            <tbody id="relatorio-movimentacoes">
                                <!-- Dados serão carregados aqui -->
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        `;

        // Renderizar dados
        renderMovimentacoes();
    }
}

function populateSelects() {
    // Popular select de clientes
    const clienteSelect = document.getElementById('movimentacao-cliente');
    if (clienteSelect) {
        clienteSelect.innerHTML = '<option value="">Selecione um cliente (opcional)</option>';
        clientes.forEach(cliente => {
            clienteSelect.innerHTML += `<option value="${cliente.id}">${cliente.nome}</option>`;
        });
    }

    // Popular select de produtos
    const produtoSelect = document.getElementById('movimentacao-produto');
    if (produtoSelect) {
        produtoSelect.innerHTML = '<option value="">Selecione um produto (opcional)</option>';
        produtos.forEach(produto => {
            produtoSelect.innerHTML += `<option value="${produto.id}">${produto.nome}</option>`;
        });
    }
}

// Funções de UI
function showApp() {
    loginPage.classList.add('hidden');
    app.classList.remove('hidden');

    // Atualizar informações do usuário
    currentUserElement.textContent = currentUser.nome;
    userRoleElement.textContent = currentUser.role === 'admin' ? 'Administrador' : 'Atendente';

    // Aplicar restrições de permissão
    applyUserPermissions();

    // Mostrar dashboard por padrão
    showSection('dashboard');
}

function applyUserPermissions() {
    // Admin: ver tudo
    // Atendente: apenas consultas e relatórios

    adminOnlyElements.forEach(el => {
        if (currentUser.role === 'admin') {
            el.classList.remove('hidden');
        } else {
            el.classList.add('hidden');
        }
    });

    // Para atendente, remover a opção de movimentações e relatórios do sidebar
    const movimentacaoLink = document.querySelector('a[href="#movimentacoes"]');
    const relatoriosLink = document.querySelector('a[href="#relatorios"]');

    if (currentUser.role === 'atendente') {
        if (movimentacaoLink) movimentacaoLink.parentElement.classList.add('hidden');
        if (relatoriosLink) relatoriosLink.parentElement.classList.add('hidden');
    } else {
        if (movimentacaoLink) movimentacaoLink.parentElement.classList.remove('hidden');
        if (relatoriosLink) relatoriosLink.parentElement.classList.remove('hidden');
    }
}

function hideApp() {
    app.classList.add('hidden');
    loginPage.classList.remove('hidden');
}

function showSection(sectionId) {
    // Ocultar todas as seções
    document.querySelectorAll('.content-section').forEach(section => {
        section.classList.add('hidden');
    });

    // Mostrar a seção selecionada
    const targetSection = document.getElementById(`${sectionId}-content`);
    if (targetSection) {
        targetSection.classList.remove('hidden');
    }

    // Atualizar navegação ativa
    document.querySelectorAll('.sidebar .nav-link').forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('href') === `#${sectionId}`) {
            link.classList.add('active');
        }
    });

    // Carregar dados específicos da seção se necessário
    if (sectionId === 'dashboard') {
        updateDashboard();
    } else if (sectionId === 'clientes') {
        renderClientes();
        // Se for atendente, ocultar formulário de cadastro
        if (currentUser.role === 'atendente') {
            document.getElementById('cliente-form')?.closest('.card')?.classList.add('hidden');
        }
    } else if (sectionId === 'produtos') {
        renderProdutos();
        // Se for atendente, ocultar formulário de cadastro
        if (currentUser.role === 'atendente') {
            document.getElementById('produto-form')?.closest('.card')?.classList.add('hidden');
        }
    } else if (sectionId === 'movimentacoes') {
        renderMovimentacoes();
        renderMovimentacaoForm(); // Renderizar formulário com base na permissão
    } else if (sectionId === 'relatorios') {
        renderRelatoriosSection(); // Renderizar seção de relatórios com base na permissão
    }
}

function showNotification(message, type = 'info') {
    // Remover notificações existentes
    document.querySelectorAll('.alert.position-fixed').forEach(alert => {
        alert.remove();
    });

    // Criar notificação simples
    const alertClass = type === 'success' ? 'alert-success' :
                      type === 'error' ? 'alert-danger' :
                      type === 'warning' ? 'alert-warning' : 'alert-info';

    const notification = document.createElement('div');
    notification.className = `alert ${alertClass} alert-dismissible fade show position-fixed`;
    notification.style.cssText = 'top: 20px; right: 20px; z-index: 1050; min-width: 300px;';
    notification.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    document.body.appendChild(notification);

    // Auto-remover após 5 segundos
    setTimeout(() => {
        if (notification.parentNode) {
            notification.parentNode.removeChild(notification);
        }
    }, 5000);
}

// Funções de negócio
function searchClientes() {
    const query = document.getElementById('cliente-search').value.toLowerCase();
    const resultsContainer = document.getElementById('clientes-search-results');

    if (!resultsContainer) return;

    resultsContainer.innerHTML = '';

    const resultados = clientes.filter(cliente =>
        cliente.nome.toLowerCase().includes(query) ||
        cliente.email.toLowerCase().includes(query) ||
        cliente.telefone.includes(query)
    );

    if (resultados.length === 0) {
        resultsContainer.innerHTML = '<tr><td colspan="3" class="text-center">Nenhum cliente encontrado</td></tr>';
        return;
    }

    resultados.forEach(cliente => {
        resultsContainer.innerHTML += `
            <tr>
                <td>${cliente.nome}</td>
                <td>${cliente.email}</td>
                <td>${cliente.telefone}</td>
            </tr>
        `;
    });
}

function searchProdutos() {
    const query = document.getElementById('produto-search').value.toLowerCase();
    const resultsContainer = document.getElementById('produtos-search-results');

    if (!resultsContainer) return;

    resultsContainer.innerHTML = '';

    const resultados = produtos.filter(produto =>
        produto.nome.toLowerCase().includes(query)
    );

    if (resultados.length === 0) {
        resultsContainer.innerHTML = '<tr><td colspan="3" class="text-center">Nenhum produto encontrado</td></tr>';
        return;
    }

    resultados.forEach(produto => {
        resultsContainer.innerHTML += `
            <tr>
                <td>${produto.nome}</td>
                <td>R$ ${produto.preco?.toFixed(2) || '0.00'}</td>
                <td>${produto.quantidadeEstoque || 0}</td>
            </tr>
        `;
    });
}

async function updateDashboard() {
    try {
        // Atualizar totais
        document.getElementById('total-clientes').textContent = clientes.length;
        document.getElementById('total-produtos').textContent = produtos.length;

        // Contar movimentações de hoje
        const hoje = new Date().toLocaleDateString('pt-BR');
        const movHoje = movimentacoes.filter(mov => {
            const dataMov = new Date(mov.dataMovimentacao).toLocaleDateString('pt-BR');
            return dataMov === hoje;
        }).length;

        document.getElementById('movimentacoes-hoje').textContent = movHoje;

        // Calcular saldo através da API
        const saldo = await apiCall('/movimentacoes/saldo');
        const saldoElement = document.getElementById('saldo-atual');

        saldoElement.textContent = `Saldo: R$ ${saldo?.toFixed(2) || '0.00'}`;
        saldoElement.className = `fs-4 fw-bold ${(saldo || 0) >= 0 ? 'saldo-positivo' : 'saldo-negativo'}`;

        // Atualizar últimas movimentações
        renderMovimentacoes();
    } catch (error) {
        console.error('Erro ao atualizar dashboard:', error);
        showNotification('Erro ao carregar dados do dashboard', 'error');
    }
}

async function deleteCliente(id) {
    if (!confirm('Tem certeza que deseja excluir este cliente?')) return;

    try {
        await apiCall(`/clientes/${id}`, { method: 'DELETE' });
        clientes = clientes.filter(c => c.id !== id);
        renderClientes();
        populateSelects();
        await updateDashboard();
        showNotification('Cliente excluído com sucesso!', 'success');
    } catch (error) {
        showNotification('Erro ao excluir cliente: ' + error.message, 'error');
    }
}

async function deleteProduto(id) {
    if (!confirm('Tem certeza que deseja excluir este produto?')) return;

    try {
        await apiCall(`/produtos/${id}`, { method: 'DELETE' });
        produtos = produtos.filter(p => p.id !== id);
        renderProdutos();
        populateSelects();
        await updateDashboard();
        showNotification('Produto excluído com sucesso!', 'success');
    } catch (error) {
        showNotification('Erro ao excluir produto: ' + error.message, 'error');
    }
}

function filtrarRelatorio() {
    // Verificar se é atendente (não deveria conseguir acessar, mas por segurança)
    if (currentUser.role === 'atendente') {
        showNotification('Acesso negado. Apenas administradores podem acessar relatórios.', 'error');
        return;
    }

    const dataInicio = document.getElementById('data-inicio')?.value;
    const dataFim = document.getElementById('data-fim')?.value;
    const tipo = document.getElementById('tipo-relatorio')?.value;

    let movimentacoesFiltradas = [...movimentacoes];

    // Filtrar por data
    if (dataInicio) {
        const inicio = new Date(dataInicio);
        movimentacoesFiltradas = movimentacoesFiltradas.filter(m => {
            const dataMov = new Date(m.dataMovimentacao);
            return dataMov >= inicio;
        });
    }

    if (dataFim) {
        const fim = new Date(dataFim);
        movimentacoesFiltradas = movimentacoesFiltradas.filter(m => {
            const dataMov = new Date(m.dataMovimentacao);
            return dataMov <= fim;
        });
    }

    // Filtrar por tipo
    if (tipo) {
        movimentacoesFiltradas = movimentacoesFiltradas.filter(m => m.tipo === tipo);
    }

    // Renderizar resultados
    const relatorioMovimentacoes = document.getElementById('relatorio-movimentacoes');
    if (!relatorioMovimentacoes) return;

    relatorioMovimentacoes.innerHTML = '';

    if (movimentacoesFiltradas.length === 0) {
        relatorioMovimentacoes.innerHTML = '<tr><td colspan="6" class="text-center">Nenhuma movimentação encontrada</td></tr>';
        return;
    }

    // Ordenar por data (mais recente primeiro)
    movimentacoesFiltradas.sort((a, b) =>
        new Date(b.dataMovimentacao) - new Date(a.dataMovimentacao)
    );

    movimentacoesFiltradas.forEach(mov => {
        const cliente = mov.clienteId ? clientes.find(c => c.id === mov.clienteId) : null;
        const produto = mov.produtoId ? produtos.find(p => p.id === mov.produtoId) : null;

        const data = new Date(mov.dataMovimentacao);
        const dataFormatada = data.toLocaleDateString('pt-BR');
        const horaFormatada = data.toLocaleTimeString('pt-BR');

        relatorioMovimentacoes.innerHTML += `
            <tr>
                <td>${dataFormatada} ${horaFormatada}</td>
                <td>
                    <span class="badge ${mov.tipo === 'ENTRADA' ? 'bg-success' : 'bg-danger'}">
                        ${mov.tipo === 'ENTRADA' ? 'Entrada' : 'Saída'}
                    </span>
                </td>
                <td>R$ ${mov.valor?.toFixed(2) || '0.00'}</td>
                <td>${cliente ? cliente.nome : '-'}</td>
                <td>${produto ? produto.nome : '-'}</td>
                <td>${mov.descricao || '-'}</td>
            </tr>
        `;
    });
}

// Funções de exportação
function exportToCSV(data, filename) {
    try {
        // Converter dados para string CSV
        const csvContent = data.map(row =>
            row.map(field => {
                // Escapar campos que contenham vírgulas, quebras de linha ou aspas
                let fieldStr = String(field || '');
                if (fieldStr.includes(',') || fieldStr.includes('"') || fieldStr.includes('\n') || fieldStr.includes('\r')) {
                    fieldStr = '"' + fieldStr.replace(/"/g, '""') + '"';
                }
                return fieldStr;
            }).join(",")
        ).join("\r\n"); // Usar \r\n para compatibilidade Windows

        // Criar blob
        const blob = new Blob(["\uFEFF" + csvContent], { type: 'text/csv;charset=utf-8;' });

        // Criar link de download
        const link = document.createElement("a");
        const url = URL.createObjectURL(blob);

        link.setAttribute("href", url);
        link.setAttribute("download", filename);
        link.style.visibility = 'hidden';

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

        // Limpar URL
        setTimeout(() => URL.revokeObjectURL(url), 100);

    } catch (error) {
        console.error('Erro ao exportar CSV:', error);
        showNotification('Erro ao exportar arquivo. Tente novamente.', 'error');
    }
}

function exportClientes() {
    // Verificar permissão
    if (currentUser.role === 'atendente') {
        showNotification('Atendentes não podem exportar dados de clientes.', 'error');
        return;
    }

    const headers = ["ID", "Nome", "E-mail", "Telefone"];
    const data = clientes.map(cliente => [
        cliente.id,
        cliente.nome,
        cliente.email,
        cliente.telefone
    ]);

    exportToCSV([headers, ...data], "clientes.csv");
    showNotification('Exportação de clientes concluída!', 'success');
}

function exportProdutos() {
    // Verificar permissão
    if (currentUser.role === 'atendente') {
        showNotification('Atendentes não podem exportar dados de produtos.', 'error');
        return;
    }

    const headers = ["ID", "Nome", "Preço", "Estoque"];
    const data = produtos.map(produto => [
        produto.id,
        produto.nome,
        produto.preco?.toFixed(2) || '0.00',
        produto.quantidadeEstoque || 0
    ]);

    exportToCSV([headers, ...data], "produtos.csv");
    showNotification('Exportação de produtos concluída!', 'success');
}

function exportFinanceiro() {
    // Verificar permissão
    if (currentUser.role === 'atendente') {
        showNotification('Atendentes não podem exportar dados financeiros.', 'error');
        return;
    }

    const headers = ["Data", "Hora", "Tipo", "Valor (R$)", "Cliente", "Produto", "Descrição"];
    const data = movimentacoes.map(mov => {
        const cliente = mov.clienteId ? clientes.find(c => c.id === mov.clienteId) : null;
        const produto = mov.produtoId ? produtos.find(p => p.id === mov.produtoId) : null;
        const data = new Date(mov.dataMovimentacao);

        const dataFormatada = data.toLocaleDateString('pt-BR');
        const horaFormatada = data.toLocaleTimeString('pt-BR');
        const tipoMovimentacao = mov.tipo === 'ENTRADA' ? 'Entrada' : 'Saída';
        const valorFormatado = mov.valor?.toFixed(2) || '0.00';
        const nomeCliente = cliente ? cliente.nome : '-';
        const nomeProduto = produto ? produto.nome : '-';
        const descricao = mov.descricao || '-';

        return [
            dataFormatada,
            horaFormatada,
            tipoMovimentacao,
            valorFormatado,
            nomeCliente,
            nomeProduto,
            descricao
        ];
    });

    exportToCSV([headers, ...data], "movimentacoes_financeiras.csv");
    showNotification('Exportação financeira concluída!', 'success');
}

// NOVA FUNÇÃO ESPECÍFICA PARA EXPORTAR RELATÓRIO COM FILTROS
function exportRelatorioMovimentacoes() {
    // Verificar permissão
    if (currentUser.role === 'atendente') {
        showNotification('Atendentes não podem exportar movimentações.', 'error');
        return;
    }

    // Obter dados filtrados ou todos os dados
    let dadosParaExportar = [...movimentacoes];

    // Aplicar filtros se existirem
    const dataInicio = document.getElementById('data-inicio')?.value;
    const dataFim = document.getElementById('data-fim')?.value;
    const tipo = document.getElementById('tipo-relatorio')?.value;

    console.log('Filtros aplicados:', { dataInicio, dataFim, tipo });

    if (dataInicio || dataFim || tipo) {
        dadosParaExportar = dadosParaExportar.filter(mov => {
            let passaFiltro = true;

            // Filtrar por data
            if (dataInicio) {
                const inicio = new Date(dataInicio);
                const dataMov = new Date(mov.dataMovimentacao);
                // Resetar horas para comparar apenas datas
                inicio.setHours(0, 0, 0, 0);
                const dataMovReset = new Date(dataMov);
                dataMovReset.setHours(0, 0, 0, 0);
                passaFiltro = passaFiltro && dataMovReset >= inicio;
            }

            if (dataFim) {
                const fim = new Date(dataFim);
                const dataMov = new Date(mov.dataMovimentacao);
                // Resetar horas e adicionar um dia para incluir a data final
                fim.setHours(23, 59, 59, 999);
                passaFiltro = passaFiltro && dataMov <= fim;
            }

            // Filtrar por tipo
            if (tipo) {
                passaFiltro = passaFiltro && mov.tipo === tipo;
            }

            return passaFiltro;
        });
    }

    console.log('Dados para exportar:', dadosParaExportar.length);

    // Ordenar por data (mais recente primeiro)
    dadosParaExportar.sort((a, b) =>
        new Date(b.dataMovimentacao) - new Date(a.dataMovimentacao)
    );

    // Preparar dados para CSV
    const headers = ["Data", "Hora", "Tipo", "Valor (R$)", "Cliente", "Produto", "Descrição"];

    const data = dadosParaExportar.map(mov => {
        const cliente = mov.clienteId ? clientes.find(c => c.id === mov.clienteId) : null;
        const produto = mov.produtoId ? produtos.find(p => p.id === mov.produtoId) : null;
        const data = new Date(mov.dataMovimentacao);

        const dataFormatada = data.toLocaleDateString('pt-BR');
        const horaFormatada = data.toLocaleTimeString('pt-BR');
        const tipoMovimentacao = mov.tipo === 'ENTRADA' ? 'Entrada' : 'Saída';
        const valorFormatado = mov.valor?.toFixed(2) || '0.00';
        const nomeCliente = cliente ? cliente.nome : '-';
        const nomeProduto = produto ? produto.nome : '-';
        const descricao = mov.descricao || '-';

        return [
            dataFormatada,
            horaFormatada,
            tipoMovimentacao,
            valorFormatado,
            nomeCliente,
            nomeProduto,
            descricao
        ];
    });

    // Gerar nome do arquivo com data e filtros
    let filename = 'relatorio_movimentacoes';
    if (dataInicio || dataFim || tipo) {
        const filtros = [];
        if (dataInicio) filtros.push(`de_${dataInicio}`);
        if (dataFim) filtros.push(`ate_${dataFim}`);
        if (tipo) filtros.push(tipo.toLowerCase());

        if (filtros.length > 0) {
            filename += '_' + filtros.join('_');
        }
    }
    filename += '.csv';

    // Exportar
    exportToCSV([headers, ...data], filename);
    showNotification(`Relatório exportado com sucesso! ${dadosParaExportar.length} movimentações encontradas.`, 'success');
}

// Carregar dados iniciais da API
async function loadInitialData() {
    try {
        showNotification('Carregando dados...', 'info');

        // Carregar clientes
        clientes = await apiCall('/clientes');
        console.log('Clientes carregados:', clientes);

        // Carregar produtos
        produtos = await apiCall('/produtos');
        console.log('Produtos carregados:', produtos);

        // Carregar movimentações
        movimentacoes = await apiCall('/movimentacoes');
        console.log('Movimentações carregadas:', movimentacoes);

        // Atualizar UI
        renderClientes();
        renderProdutos();
        renderMovimentacoes();
        populateSelects();
        await updateDashboard();

        showNotification('Dados carregados com sucesso!', 'success');
    } catch (error) {
        console.error('Erro ao carregar dados iniciais:', error);
        showNotification('Erro ao carregar dados. Verifique se o servidor está rodando.', 'error');
    }
}

// Funções globais para os event listeners nos botões
window.deleteCliente = deleteCliente;
window.deleteProduto = deleteProduto;
window.exportMovimentacoes = exportRelatorioMovimentacoes;
window.exportClientes = exportClientes;
window.exportProdutos = exportProdutos;
window.exportFinanceiro = exportFinanceiro;
window.filtrarRelatorio = filtrarRelatorio;
window.exportRelatorioMovimentacoes = exportRelatorioMovimentacoes;