# POOrtal do Leitor

Sistema de gerenciamento de biblioteca desenvolvido para **Programação Orientada a Objetos 2 (EC46A, turma C61)**, semestre **2026.2**, da UTFPR, Campus Cornélio Procópio. Professora: **Gisele Alves Santana**.

## Integrantes

- [Daniel Chien Chen Wang](https://github.com/danielchien2024)
- [Daniella Melo Galdini](https://github.com/Dani-m-g)
- [João Vitor Taques Marques](https://github.com/JoaoVitorTaques)

## O sistema

A consulta ao acervo é pública. Funcionários autenticados gerenciam usuários e livros, registram empréstimos, devoluções e renovações, organizam reservas FIFO e registram a quitação de multas. Aluno, Professor e Funcionário são categorias de usuários com limites e prazos próprios.

As exclusões de usuários e livros são físicas, respeitando os vínculos e as pendências previstos nas regras de negócio. Empréstimos encerrados preservam nomes e títulos após a exclusão. O último funcionário não pode ser excluído nem mudar de categoria enquanto não houver outro funcionário cadastrado.

## Tecnologias e organização

| Tecnologia | Uso |
|---|---|
| Java 17 e Spring Boot 4.1.1 | Aplicação e inicialização do servidor |
| Spring Web MVC | API HTTP com respostas JSON |
| Spring Data JPA / Hibernate | Mapeamento e persistência das entidades |
| MySQL e MySQL Connector/J | Banco de dados e conexão JDBC usada pela JPA |
| Jakarta Validation | Validação dos DTOs de entrada |
| Spring Security | HTTP Basic e senhas armazenadas com BCrypt |
| HTML, CSS e JavaScript | Interface web, sem framework de frontend |
| Maven Wrapper | Dependências, compilação e empacotamento |
| Astah UML; Git/GitHub | Modelagem e versionamento |

O módulo está em `poortal-do-leitor/`, com pacote base `br.edu.utfpr.poortal`. A apresentação fica em `src/main/resources/static`; `controller` recebe as requisições, `service` coordena as regras e transações, `repository` acessa o banco e `model` representa o domínio. `dto`, `config` e `exception` concentram contratos de dados, configuração e tratamento de erros. O Apache NetBeans pode abrir o módulo pelo `pom.xml`.

## Executar localmente

### 1. Preparar o ambiente

Instale um **JDK 17**, configure `JAVA_HOME` e mantenha o **MySQL 8** em execução. É necessário acesso à internet na primeira execução para baixar o Maven e as dependências. Não é necessário instalar Maven ou Node.js separadamente.

No MySQL, crie o banco usando uma conta com permissão para isso:

```sql
CREATE DATABASE IF NOT EXISTS poortal_do_leitor CHARACTER SET utf8mb4;
```

A conta usada pela aplicação precisa poder ler e alterar dados e criar/atualizar tabelas nesse banco. As tabelas são geradas pela JPA na inicialização (`spring.jpa.hibernate.ddl-auto=update`).

### 2. Configurar as variáveis de ambiente

No PowerShell, entre no módulo e informe as credenciais da sua instalação do MySQL. Os valores abaixo são exemplos e devem ser substituídos:

```powershell
cd poortal-do-leitor
$env:DB_USERNAME = "seu_usuario_mysql"
$env:DB_PASSWORD = "sua_senha_mysql"
```

| Variável | Finalidade |
|---|---|
| `DB_USERNAME` | Usuário do MySQL; obrigatório |
| `DB_PASSWORD` | Senha do MySQL; obrigatória |
| `DB_URL` | Opcional; padrão: `jdbc:mysql://localhost:3306/poortal_do_leitor` |
| `POORTAL_FUNCIONARIO_NOME` | Nome do funcionário inicial |
| `POORTAL_FUNCIONARIO_EMAIL` | E-mail usado no primeiro acesso administrativo |
| `POORTAL_FUNCIONARIO_SENHA` | Senha do funcionário inicial |

Para o **primeiro acesso**, quando ainda não existe nenhum usuário no banco, configure as três variáveis do funcionário inicial na mesma sessão:

```powershell
$env:POORTAL_FUNCIONARIO_NOME = "Responsável pela biblioteca"
$env:POORTAL_FUNCIONARIO_EMAIL = "biblioteca@exemplo.com"
$env:POORTAL_FUNCIONARIO_SENHA = "defina_uma_senha"
```

Esse cadastro é opcional, exige os três valores e só ocorre se a tabela de usuários estiver vazia. Ele não redefine contas existentes. Depois do primeiro cadastro, essas variáveis podem ser removidas; as credenciais ficam persistidas no banco, com a senha em BCrypt. Não salve senhas no código ou no repositório. A aplicação lê as variáveis em `application.properties`; não carrega um arquivo `.env` automaticamente.

### 3. Iniciar e acessar

Na mesma sessão do PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Abra **http://localhost:8080**. O acervo pode ser consultado sem login. Use **Entrar como funcionário** com a conta cadastrada para acessar as operações administrativas. As credenciais da interface ficam somente na memória da página; ao recarregar, entre novamente. Encerre o servidor com `Ctrl+C`.

No Linux/macOS, configure as mesmas variáveis com `export` e execute `sh mvnw spring-boot:run` dentro do módulo. A API também fica disponível em `/api/livros`, `/api/usuarios`, `/api/emprestimos`, `/api/reservas`, `/api/multas` e `/api/autenticacao`; somente as consultas GET de livros são públicas.

### Gerar o executável

```powershell
.\mvnw.cmd clean package
java -jar target\poortal-do-leitor-0.0.1-SNAPSHOT.jar
```

Para executar o JAR, mantenha o MySQL ativo e as variáveis configuradas. O build não exige uma conexão com o banco. Os testes foram mantidos apenas no ambiente local e não fazem parte dos arquivos versionados da entrega.

Se a inicialização falhar, confira `JAVA_HOME`, as credenciais, a existência do banco e a disponibilidade da porta 8080. Um erro 401 indica credenciais inválidas ou acesso sem autenticação; conflitos de negócio retornam 409 e falhas de acesso ao banco retornam 503.

## Documentação

- [Requisitos funcionais](documentacao/requisitos/requisitos-funcionais.md)
- [Requisitos não funcionais](documentacao/requisitos/requisitos-nao-funcionais.md)
- [Regras de negócio](documentacao/requisitos/regras-de-negocio.md)
- [Projeto UML editável no Astah](documentacao/diagramas/POOrtal_do_Leitor.asta)
- Diagramas: [casos de uso](documentacao/diagramas/diagrama-de-casos-de-uso.pdf), [classes](documentacao/diagramas/diagrama-de-classes.pdf), [empréstimo](documentacao/diagramas/diagramas-de-atividades/registrar-emprestimo.pdf), [devolução](documentacao/diagramas/diagramas-de-atividades/registrar-devolucao.pdf) e [reserva](documentacao/diagramas/diagramas-de-atividades/adicionar-usuario-fila-reserva.pdf).
- [Relatório final em PDF](documentacao/relatorio/relatorio-final.pdf)
