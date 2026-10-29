"use strict";

// Os dados vêm da API. As credenciais ficam apenas na memória desta página.
const estado = {
    funcionario: null, autorizacao: null, pagina: "acervo",
    livros: [], usuarios: [], emprestimos: [], multas: [], reservas: []
};
const paginas = {
    acervo: ["Acervo", "Encontre sua próxima leitura.", "Explore o acervo e consulte a disponibilidade dos livros."],
    usuarios: ["Usuários", "Pessoas que fazem a biblioteca.", "Cadastre e atualize os usuários do sistema."],
    emprestimos: ["Empréstimos", "Livros em movimento.", "Registre empréstimos, renovações e devoluções."],
    reservas: ["Reservas", "Uma leitura a caminho.", "Consulte e organize a fila de cada livro."],
    multas: ["Multas", "Pendências em dia.", "Consulte as multas e registre a quitação."]
};
const tipos = { ALUNO: "Aluno", PROFESSOR: "Professor", FUNCIONARIO: "Funcionário" };
const moeda = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" });
let confirmarOperacao = null;

function elemento(id) { return document.getElementById(id); }
function escapar(valor) {
    return String(valor ?? "").replace(/[&<>"']/g, caractere => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
    })[caractere]);
}
function normalizar(valor) { return String(valor ?? "").normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase(); }
function data(valor) { return valor ? new Date(valor + (valor.length === 10 ? "T12:00:00" : "")).toLocaleDateString("pt-BR") : "—"; }
function nomeUsuario(id, historico) {
    return estado.usuarios.find(usuario => usuario.id === id)?.nome
        ?? (historico ? `${historico} (excluído)` : "Usuário excluído · nome não registrado");
}
function tituloLivro(id, historico) {
    return estado.livros.find(livro => livro.id === id)?.titulo
        ?? (historico ? `${historico} (excluído)` : "Livro excluído · título não registrado");
}
function badge(texto, cor = "") { return `<span class="badge ${cor}">${escapar(texto)}</span>`; }
function acao(texto, nome, id, classe = "") {
    return `<button type="button" class="acao ${classe}" data-action="${nome}" data-id="${id}">${texto}</button>`;
}
function preencherTabela(id, linhas, colunas, vazio) {
    elemento(id).innerHTML = linhas.join("") || `<tr><td colspan="${colunas}" class="vazio">${escapar(vazio)}</td></tr>`;
}
function avisar(texto, erro = false) {
    elemento("aviso-texto").textContent = texto;
    elemento("aviso").classList.toggle("erro", erro);
    elemento("aviso").hidden = false;
}

async function api(caminho, { method = "GET", body, autorizacao = estado.autorizacao } = {}) {
    const headers = { Accept: "application/json" };
    if (autorizacao) headers.Authorization = autorizacao;
    if (body !== undefined) headers["Content-Type"] = "application/json";
    let resposta;
    try {
        resposta = await fetch(`/api/${caminho}`, {
            method, headers, credentials: "omit", cache: "no-store",
            body: body === undefined ? undefined : JSON.stringify(body)
        });
    } catch {
        throw new Error("Não foi possível conectar à biblioteca. Verifique a conexão e tente novamente.");
    }
    if (resposta.status === 401) {
        if (estado.funcionario && autorizacao === estado.autorizacao) sair();
        throw new Error("E-mail ou senha inválidos. Entre novamente como funcionário.");
    }
    const texto = await resposta.text();
    let resultado = null;
    if (texto && resposta.headers.get("Content-Type")?.includes("application/json")) {
        resultado = JSON.parse(texto);
    }
    if (!resposta.ok) {
        const mensagem = resultado?.mensagem ?? (resposta.status === 503
            ? "Não foi possível acessar os dados. Tente novamente mais tarde."
            : "Não foi possível concluir a operação. Tente novamente.");
        throw new Error([mensagem, ...(resultado?.detalhes ?? [])].join("\n"));
    }
    return resultado;
}

async function carregarDados() {
    const acessoAtual = estado.autorizacao;
    elemento("carregando").hidden = false;
    elemento("atualizar").disabled = true;
    try {
        const consultas = [api("livros", { autorizacao: null })];
        if (estado.funcionario) consultas.push(api("usuarios"), api("emprestimos"), api("multas"));
        const [livros, usuarios = [], emprestimos = [], multas = []] = await Promise.all(consultas);
        if (acessoAtual !== estado.autorizacao) return;
        Object.assign(estado, { livros, usuarios, emprestimos, multas, reservas: [] });
        renderizar();
        if (estado.pagina === "reservas") await carregarReservas();
    } catch (erro) {
        avisar(erro.message, true);
    } finally {
        elemento("carregando").hidden = true;
        elemento("atualizar").disabled = false;
    }
}

function renderizar() {
    document.querySelectorAll("[data-funcionario]").forEach(item => { item.hidden = !estado.funcionario; });
    document.querySelectorAll("[data-pagina]").forEach(item => {
        if (item.dataset.pagina === estado.pagina) item.setAttribute("aria-current", "page");
        else item.removeAttribute("aria-current");
    });
    Object.keys(paginas).forEach(pagina => { elemento(`pagina-${pagina}`).hidden = pagina !== estado.pagina; });
    const [nome, titulo, descricao] = paginas[estado.pagina];
    elemento("breadcrumb").textContent = nome;
    elemento("titulo-pagina").textContent = titulo;
    elemento("descricao-pagina").textContent = descricao;
    elemento("sobretitulo").textContent = estado.pagina === "acervo" ? "CONHECIMENTO AO SEU ALCANCE" : "GESTÃO DA BIBLIOTECA";
    document.title = `${nome} · POOrtal do Leitor`;
    elemento("nome-acesso").textContent = estado.funcionario?.nome ?? "Visitante";
    elemento("tipo-acesso").textContent = estado.funcionario ? "Funcionário da biblioteca" : "Consulta pública ao acervo";
    elemento("acesso-botao").textContent = estado.funcionario ? "Sair do acesso de funcionário →" : "Entrar como funcionário →";
    elemento("nav-total").textContent = estado.livros.length;
    elemento("total-titulos").textContent = estado.livros.length;
    elemento("total-disponiveis").textContent = estado.livros.reduce((soma, livro) => soma + livro.quantidadeDisponivel, 0);
    renderizarLivros();
    renderizarUsuarios();
    renderizarEmprestimos();
    renderizarMultas();
    const livroSelecionado = elemento("livro-fila").value;
    preencherLivros(elemento("livro-fila"));
    elemento("livro-fila").value = estado.livros.some(livro => String(livro.id) === livroSelecionado) ? livroSelecionado : "";
    renderizarReservas();
}

function renderizarLivros() {
    const busca = normalizar(elemento("busca-livros").value);
    const filtro = elemento("filtro-livros").value;
    const livros = estado.livros.filter(livro => normalizar(`${livro.titulo} ${livro.autor} ${livro.genero}`).includes(busca)
        && (filtro !== "disponiveis" || livro.quantidadeDisponivel > 0)
        && (filtro !== "indisponiveis" || livro.quantidadeDisponivel === 0)
        && (filtro !== "pendentes" || livro.pendenteExclusao));
    elemento("quantidade-acervo").textContent = livros.length;
    preencherTabela("lista-livros", livros.map(livro => `<tr>
        <td><div class="livro-celula"><span class="capa" aria-hidden="true">${escapar(livro.titulo.charAt(0))}</span><div><strong>${escapar(livro.titulo)}</strong><small>${escapar(livro.autor)}</small></div></div></td>
        <td>${badge(livro.genero)}</td><td>${livro.ano}<small>Edição ${escapar(livro.edicao)}</small></td>
        <td>${badge(livro.quantidadeDisponivel > 0 ? "Disponível" : "Indisponível", livro.quantidadeDisponivel > 0 ? "verde" : "amarelo")}<small>${livro.quantidadeDisponivel} de ${livro.quantidadeTotal} exemplares</small>${livro.pendenteExclusao ? badge("Exclusão pendente", "amarelo") : ""}</td>
        ${estado.funcionario ? `<td><div class="acoes">${acao("Editar", "editar-livro", livro.id)}${acao("Excluir", "excluir-livro", livro.id, "excluir")}</div></td>` : ""}</tr>`),
    estado.funcionario ? 5 : 4, estado.livros.length ? "Nenhum livro encontrado para esta busca." : "O acervo ainda não possui livros cadastrados.");
}

function renderizarUsuarios() {
    const busca = normalizar(elemento("busca-usuarios").value);
    preencherTabela("lista-usuarios", estado.usuarios.filter(usuario => normalizar(`${usuario.nome} ${usuario.email}`).includes(busca)).map(usuario => `<tr>
        <td><strong>${escapar(usuario.nome)}</strong><small>Usuário #${usuario.id}</small></td><td>${escapar(usuario.email)}</td><td>${badge(tipos[usuario.tipo])}</td>
        <td><div class="acoes">${acao("Editar", "editar-usuario", usuario.id)}${acao("Excluir", "excluir-usuario", usuario.id, "excluir")}</div></td></tr>`), 4, "Nenhum usuário encontrado.");
}

function renderizarEmprestimos() {
    const busca = normalizar(elemento("busca-emprestimos").value);
    const filtro = elemento("filtro-emprestimos").value;
    preencherTabela("lista-emprestimos", estado.emprestimos.filter(emprestimo =>
        (filtro !== "ativos" || !emprestimo.dataDevolucao) && (filtro !== "devolvidos" || emprestimo.dataDevolucao)
        && normalizar(`${tituloLivro(emprestimo.livroId, emprestimo.tituloLivroHistorico)} ${nomeUsuario(emprestimo.usuarioId, emprestimo.nomeUsuarioHistorico)}`).includes(busca)).map(emprestimo => {
        const hoje = new Date();
        hoje.setHours(0, 0, 0, 0);
        const atrasado = !emprestimo.dataDevolucao && new Date(emprestimo.dataPrevistaDevolucao + "T00:00:00") < hoje;
        return `<tr><td><strong>${escapar(tituloLivro(emprestimo.livroId, emprestimo.tituloLivroHistorico))}</strong><small>${escapar(nomeUsuario(emprestimo.usuarioId, emprestimo.nomeUsuarioHistorico))} · #${emprestimo.id}</small></td>
            <td>${data(emprestimo.dataEmprestimo)}</td><td>${data(emprestimo.dataPrevistaDevolucao)}${emprestimo.renovado ? "<small>Renovado uma vez</small>" : ""}</td>
            <td>${badge(emprestimo.dataDevolucao ? "Devolvido" : atrasado ? "Em atraso" : "Em dia", emprestimo.dataDevolucao ? "" : atrasado ? "vermelho" : "verde")}${emprestimo.dataDevolucao ? `<small>${data(emprestimo.dataDevolucao)}</small>` : ""}</td>
            <td><div class="acoes">${!emprestimo.dataDevolucao ? acao("Devolver", "devolver", emprestimo.id) + (!emprestimo.renovado && !atrasado ? acao("Renovar", "renovar", emprestimo.id) : "") : "—"}</div></td></tr>`;
    }), 5, "Nenhum empréstimo encontrado para esta consulta.");
}

function renderizarMultas() {
    const busca = normalizar(elemento("busca-multas").value);
    const filtro = elemento("filtro-multas").value;
    preencherTabela("lista-multas", estado.multas.filter(multa => (filtro !== "pendentes" || !multa.quitada) && (filtro !== "quitadas" || multa.quitada)).map(multa => {
        const emprestimo = estado.emprestimos.find(item => item.id === multa.emprestimoId);
        const livro = tituloLivro(emprestimo?.livroId, emprestimo?.tituloLivroHistorico);
        const usuario = nomeUsuario(emprestimo?.usuarioId, emprestimo?.nomeUsuarioHistorico);
        if (!normalizar(`${livro} ${usuario}`).includes(busca)) return "";
        return `<tr><td><strong>${escapar(livro)}</strong><small>${escapar(usuario)} · Empréstimo #${multa.emprestimoId}</small></td><td>${multa.diasAtraso} dia(s)</td><td><strong>${moeda.format(multa.valor)}</strong></td>
            <td>${badge(multa.quitada ? "Quitada" : "Pendente", multa.quitada ? "verde" : "amarelo")}${multa.quitada ? `<small>${data(multa.dataQuitacao)}</small>` : ""}</td><td>${multa.quitada ? "—" : acao("Registrar quitação", "quitar", multa.id)}</td></tr>`;
    }), 5, "Nenhuma multa encontrada para esta consulta.");
}

function preencherLivros(select) {
    select.innerHTML = '<option value="">Selecione um livro</option>' + estado.livros.map(livro => `<option value="${livro.id}">${escapar(livro.titulo)} · ${livro.quantidadeDisponivel} disponível(is)</option>`).join("");
}
async function carregarReservas() {
    const livroId = elemento("livro-fila").value;
    const acessoAtual = estado.autorizacao;
    estado.reservas = [];
    renderizarReservas();
    if (!livroId || !estado.funcionario) return;
    elemento("lista-reservas").innerHTML = '<tr><td colspan="4" class="vazio">Carregando a fila…</td></tr>';
    try {
        const reservas = await api(`reservas?livroId=${livroId}`);
        if (elemento("livro-fila").value !== livroId || acessoAtual !== estado.autorizacao) return;
        estado.reservas = reservas;
        renderizarReservas();
    } catch (erro) {
        renderizarReservas();
        avisar(erro.message, true);
    }
}
function renderizarReservas() {
    preencherTabela("lista-reservas", estado.reservas.map((reserva, indice) => `<tr><td>${badge(`${indice + 1}º`, indice === 0 ? "verde" : "")}</td>
        <td><strong>${escapar(nomeUsuario(reserva.usuarioId))}</strong></td><td>${data(reserva.dataSolicitacao)}</td>
        <td><div class="acoes">${indice === 0 ? acao("Emprestar", "emprestar-reserva", reserva.id) : ""}${acao("Remover", "remover-reserva", reserva.id, "excluir")}</div></td></tr>`),
    4, elemento("livro-fila").value ? "Este livro não possui reservas." : "Selecione um livro para consultar a fila.");
}

function abrirDialog(nome) {
    const dialog = elemento(`dialog-${nome}`);
    dialog.querySelector("form").reset();
    dialog.querySelectorAll('input[type="hidden"]').forEach(input => { input.value = ""; });
    dialog.querySelector(".erro-form").hidden = true;
    dialog.showModal();
    return dialog.querySelector("form");
}
function abrirLivro(id) {
    const form = abrirDialog("livro");
    const livro = estado.livros.find(item => item.id === id);
    elemento("titulo-livro").textContent = livro ? "Editar livro" : "Cadastrar livro";
    if (livro) ["id", "titulo", "autor", "genero", "ano", "edicao", "quantidadeTotal"].forEach(campo => { form.elements[campo].value = livro[campo]; });
}
function exigirSenha() {
    const form = elemento("form-usuario");
    form.elements.senha.required = form.elements.tipo.value === "FUNCIONARIO" && !form.elements.id.value;
}
function abrirUsuario(id) {
    const form = abrirDialog("usuario");
    const usuario = estado.usuarios.find(item => item.id === id);
    elemento("titulo-usuario").textContent = usuario ? "Editar usuário" : "Cadastrar usuário";
    if (usuario) ["id", "nome", "email", "tipo"].forEach(campo => { form.elements[campo].value = usuario[campo]; });
    exigirSenha();
}
function abrirCirculacao(operacao, livroId = "", usuarioId = "") {
    const form = abrirDialog("circulacao");
    form.elements.operacao.value = operacao;
    form.elements.usuarioId.innerHTML = '<option value="">Selecione um usuário</option>' + estado.usuarios.map(usuario => `<option value="${usuario.id}">${escapar(usuario.nome)} · ${escapar(usuario.email)}</option>`).join("");
    preencherLivros(form.elements.livroId);
    form.elements.livroId.value = livroId;
    form.elements.usuarioId.value = usuarioId;
    elemento("titulo-circulacao").textContent = operacao === "reservas" ? "Adicionar reserva" : "Registrar empréstimo";
    elemento("ajuda-circulacao").textContent = operacao === "reservas"
        ? "Reservas são permitidas quando não há exemplares disponíveis. A fila segue a ordem de inclusão."
        : "O prazo é calculado automaticamente: 7 dias para alunos e 28 dias para professores e funcionários.";
}
function confirmar(titulo, texto, operacao) {
    abrirDialog("confirmacao");
    elemento("titulo-confirmacao").textContent = titulo;
    elemento("texto-confirmacao").textContent = texto;
    confirmarOperacao = operacao;
}
function sair() {
    estado.funcionario = null;
    estado.autorizacao = null;
    estado.pagina = "acervo";
    estado.usuarios = [];
    estado.emprestimos = [];
    estado.multas = [];
    estado.reservas = [];
    document.querySelectorAll("dialog[open]").forEach(dialog => dialog.close());
    document.querySelectorAll("form").forEach(form => form.reset());
    elemento("form-circulacao").elements.usuarioId.innerHTML = "";
    elemento("texto-confirmacao").textContent = "";
    renderizar();
}

function registrarFormulario(id, enviar) {
    elemento(id).addEventListener("submit", async evento => {
        evento.preventDefault();
        const form = evento.currentTarget;
        const botao = form.querySelector('[type="submit"]');
        if (botao.disabled) return;
        const erroForm = form.querySelector(".erro-form");
        erroForm.hidden = true;
        botao.disabled = true;
        try {
            await enviar(Object.fromEntries(new FormData(form)), form);
        } catch (erro) {
            if (form.closest("dialog").open) {
                erroForm.textContent = erro.message;
                erroForm.hidden = false;
            } else avisar(erro.message, true);
        } finally {
            botao.disabled = false;
        }
    });
}
async function concluir(form, mensagem) {
    form.closest("dialog").close();
    avisar(mensagem);
    await carregarDados();
}

registrarFormulario("form-login", async (dados, form) => {
    const bytes = new TextEncoder().encode(`${dados.email.trim()}:${dados.senha}`);
    const autorizacao = "Basic " + btoa(Array.from(bytes, byte => String.fromCharCode(byte)).join(""));
    const funcionario = await api("autenticacao", { autorizacao });
    if (!form.closest("dialog").open) return;
    estado.autorizacao = autorizacao;
    estado.funcionario = funcionario;
    form.reset();
    await concluir(form, `Bem-vindo(a), ${funcionario.nome}.`);
});
registrarFormulario("form-livro", async (dados, form) => {
    const { id, ...livro } = dados;
    livro.ano = Number(livro.ano);
    livro.quantidadeTotal = Number(livro.quantidadeTotal);
    await api(id ? `livros/${id}` : "livros", { method: id ? "PUT" : "POST", body: livro });
    await concluir(form, id ? "Livro atualizado." : "Livro cadastrado.");
});
registrarFormulario("form-usuario", async (dados, form) => {
    const { id, ...usuario } = dados;
    await api(id ? `usuarios/${id}` : "usuarios", { method: id ? "PUT" : "POST", body: usuario });
    if (Number(id) === estado.funcionario?.id) {
        sair();
        await concluir(form, "Seu cadastro foi atualizado. Entre novamente para continuar.");
    } else await concluir(form, id ? "Usuário atualizado." : "Usuário cadastrado.");
});
registrarFormulario("form-circulacao", async (dados, form) => {
    await api(dados.operacao, { method: "POST", body: { usuarioId: Number(dados.usuarioId), livroId: Number(dados.livroId) } });
    await concluir(form, dados.operacao === "reservas" ? "Usuário adicionado à fila de reserva." : "Empréstimo registrado.");
});
registrarFormulario("form-confirmacao", async (dados, form) => {
    if (confirmarOperacao) await concluir(form, await confirmarOperacao());
});

document.querySelectorAll("[data-pagina]").forEach(botao => botao.addEventListener("click", async () => {
    if (botao.dataset.pagina !== "acervo" && !estado.funcionario) return;
    estado.pagina = botao.dataset.pagina;
    renderizar();
    if (estado.pagina === "reservas") await carregarReservas();
}));
elemento("acesso-botao").addEventListener("click", () => {
    if (estado.funcionario) { sair(); avisar("Você saiu do acesso de funcionário."); }
    else abrirDialog("login");
});
elemento("atualizar").addEventListener("click", carregarDados);
elemento("fechar-aviso").addEventListener("click", () => { elemento("aviso").hidden = true; });
elemento("livro-fila").addEventListener("change", carregarReservas);
elemento("form-usuario").elements.tipo.addEventListener("change", exigirSenha);
[["busca-livros", renderizarLivros], ["filtro-livros", renderizarLivros], ["busca-usuarios", renderizarUsuarios],
    ["busca-emprestimos", renderizarEmprestimos], ["filtro-emprestimos", renderizarEmprestimos],
    ["busca-multas", renderizarMultas], ["filtro-multas", renderizarMultas]].forEach(([id, render]) => elemento(id).addEventListener("input", render));
document.querySelectorAll("[data-fechar]").forEach(botao => botao.addEventListener("click", () => botao.closest("dialog").close()));
document.querySelectorAll("dialog").forEach(dialog => dialog.addEventListener("close", () => {
    dialog.querySelectorAll('input[type="password"]').forEach(input => { input.value = ""; });
    if (dialog.id === "dialog-confirmacao") confirmarOperacao = null;
}));

document.addEventListener("click", evento => {
    const botao = evento.target.closest("[data-action]");
    if (!botao || !estado.funcionario) return;
    const id = Number(botao.dataset.id);
    switch (botao.dataset.action) {
        case "novo-livro": abrirLivro(); break;
        case "editar-livro": abrirLivro(id); break;
        case "novo-usuario": abrirUsuario(); break;
        case "editar-usuario": abrirUsuario(id); break;
        case "novo-emprestimo": abrirCirculacao("emprestimos"); break;
        case "nova-reserva": abrirCirculacao("reservas", elemento("livro-fila").value); break;
        case "emprestar-reserva": {
            const reserva = estado.reservas.find(item => item.id === id);
            abrirCirculacao("emprestimos", reserva.livroId, reserva.usuarioId);
            break;
        }
        case "excluir-livro": confirmar("Excluir livro", `Solicitar a exclusão de “${tituloLivro(id)}”? Se houver empréstimos ou reservas, o livro ficará pendente de exclusão.`, async () => {
            const resultado = await api(`livros/${id}`, { method: "DELETE" });
            return resultado?.pendenteExclusao ? "Livro marcado como pendente de exclusão. Conclua a exclusão quando não houver vínculos ativos." : "Livro excluído.";
        }); break;
        case "excluir-usuario": confirmar("Excluir usuário", `Excluir o cadastro de ${nomeUsuario(id)}? A exclusão só é permitida sem empréstimos, multas ou reservas pendentes.`, async () => {
            await api(`usuarios/${id}`, { method: "DELETE" });
            if (id === estado.funcionario?.id) sair();
            return "Usuário excluído.";
        }); break;
        case "devolver": confirmar("Registrar devolução", "Confirmar a devolução? Havendo atraso, a multa será calculada automaticamente.", async () => {
            await api(`emprestimos/${id}/devolucao`, { method: "POST" });
            return "Devolução registrada. Eventuais multas estão disponíveis na área Multas.";
        }); break;
        case "renovar": confirmar("Renovar empréstimo", "O prazo será prorrogado conforme o tipo de usuário. Cada empréstimo pode ser renovado uma vez.", async () => {
            await api(`emprestimos/${id}/renovacao`, { method: "POST" });
            return "Empréstimo renovado.";
        }); break;
        case "remover-reserva": confirmar("Remover reserva", "Retirar este usuário da fila de reserva do livro?", async () => {
            await api(`reservas/${id}`, { method: "DELETE" });
            return "Reserva removida.";
        }); break;
        case "quitar": {
            const multa = estado.multas.find(item => item.id === id);
            confirmar("Registrar quitação", `Confirmar a quitação da multa de ${moeda.format(multa.valor)}?`, async () => {
                await api(`multas/${id}/quitacao`, { method: "POST" });
                return "Quitação registrada.";
            });
            break;
        }
    }
});

renderizar();
carregarDados();
