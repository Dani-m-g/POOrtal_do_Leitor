# POOrtal do Leitor: sistema de gerenciamento de biblioteca

Universidade Tecnológica Federal do Paraná - Campus Cornélio Procópio

Bacharelado em Engenharia de Computação

Programação Orientada a Objetos 2 - EC46A - C61 - 2026.2

Daniel Chien Chen Wang - RA 2649306

Daniella Melo Galdini - RA 2648237

João Vitor Taques Marques - RA 2648156

Professora: Gisele Alves Santana

Cornélio Procópio, 2026

## RESUMO

Este relatório apresenta o POOrtal do Leitor, sistema acadêmico de gerenciamento de biblioteca desenvolvido em Java 17 com Spring Boot, interface web e persistência em MySQL. O objetivo é aplicar orientação a objetos e separação de responsabilidades ao controle de usuários, livros, empréstimos, devoluções, renovações, reservas e multas. A documentação parte dos requisitos funcionais, não funcionais e regras de negócio do repositório. A implementação organiza os componentes em apresentação, controllers, serviços, repositórios e entidades, utilizando DTOs para entrada e saída de dados. A consulta ao acervo é pública; as operações administrativas exigem autenticação de funcionários por HTTP Basic, com senhas armazenadas em BCrypt. Os resultados incluem validação dos fluxos críticos, preservação do histórico após exclusões físicas e verificação do desempenho das consultas em ambiente local. A solução mantém o escopo de uma biblioteca acadêmica e utiliza recursos de Spring e JPA sem acrescentar infraestrutura de produção. São apresentados os diagramas UML, o mapeamento das entidades para o banco e as limitações da validação realizada.

Palavras-chave: orientação a objetos; biblioteca; Spring Boot; UML; MySQL.

## 1 INTRODUÇÃO

O gerenciamento de uma biblioteca envolve o cadastro de pessoas e livros e a manutenção de informações consistentes sobre circulação, disponibilidade e pendências. No POOrtal do Leitor, essas operações constituem um domínio para aplicar os conteúdos de Programação Orientada a Objetos 2, integrando modelagem UML, programação em Java, acesso HTTP e persistência relacional.

O sistema disponibiliza consulta pública ao acervo e concentra as operações administrativas em funcionários autenticados. Alunos, professores e funcionários podem receber empréstimos, com limites e prazos diferentes. Reservas seguem a ordem de inclusão, enquanto multas por atraso restringem novas operações até a quitação. O relatório descreve a implementação existente, sem propor funcionalidades adicionais.

### 1.1 Objetivos

O objetivo geral é desenvolver um sistema de gerenciamento de biblioteca que aplique os conceitos de orientação a objetos e mantenha consistência entre requisitos, código e banco de dados.

Os objetivos específicos são: representar as entidades e suas responsabilidades; disponibilizar uma interface simples para consulta e administração; aplicar as regras de empréstimo, devolução, renovação, reserva e multa; persistir os dados no MySQL; proteger o acesso administrativo; e validar os fluxos críticos por testes e verificações locais.

### 1.2 Escopo

O escopo corresponde aos RF01 a RF17 e às RN01 a RN10 mantidos no repositório. Não há autoatendimento de empréstimos por alunos ou professores, integração com meios de pagamento nem identificação individual de exemplares. Cada cadastro de livro mantém quantidades total e disponível. A quitação de multa é um registro administrativo feito por um funcionário.

## 2 FUNDAMENTAÇÃO TEÓRICA

Na orientação a objetos, classes reúnem estado e comportamento, e o encapsulamento organiza o acesso a essas informações (Oracle, 2024). No projeto, Livro controla alterações de disponibilidade, Emprestimo valida sua renovação e devolução, e Multa calcula o valor e registra a quitação. As categorias não são subclasses de Usuario: o enum TipoUsuario concentra os limites e prazos correspondentes.

A UML fornece uma linguagem de modelagem para representar aspectos estruturais e comportamentais de sistemas (Object Management Group, 2017). Os casos de uso delimitam as interações externas, o diagrama de classes representa o domínio e os diagramas de atividades descrevem os fluxos de circulação.

O Spring Boot organiza a inicialização e a configuração da aplicação Spring (Spring, 2026b). A implementação utiliza Spring Web MVC para os endpoints HTTP. A separação entre apresentação, controle, serviços e persistência facilita a localização das responsabilidades e evita concentrar acesso ao banco na interface ou nos controllers.

A JPA relaciona entidades Java a tabelas, e os repositórios do Spring Data JPA oferecem operações de persistência por interfaces (Spring, 2026c). As chaves estrangeiras do MySQL permitem representar referências entre registros e manter integridade referencial (Oracle, 2026). As verificações de negócio permanecem no modelo e nos serviços, pois não se resumem às restrições estruturais do banco.

Para senhas, PasswordEncoder permite uma transformação de sentido único; a implementação adota BCrypt (Spring, 2026a). HTTP Basic fornece as credenciais para autenticação das requisições administrativas. BCrypt protege o armazenamento, mas não cifra o transporte HTTP. A configuração apresentada destina-se à execução acadêmica local.

## 3 METODOLOGIA

O trabalho tomou como referência o planejamento das fases anteriores, os documentos atuais de requisitos e o código do repositório. A migração para Spring Boot substituiu a solução inicialmente planejada com interface Swing por uma aplicação web. Em conflitos técnicos entre os documentos anteriores e a implementação aprovada, foram preservados os requisitos atuais em documentacao/requisitos.

As regras foram distribuídas entre entidades e serviços. Entidades tratam invariantes do próprio objeto, enquanto serviços consultam os vínculos necessários e coordenam as operações em transações. Controllers recebem DTOs validados, chamam os serviços e convertem os resultados para respostas. Repositórios estendem JpaRepository e declaram consultas derivadas dos nomes de métodos.

O desenvolvimento utilizou Maven para construção, Git/GitHub para versionamento e Astah para modelagem. A revisão final comparou os diagramas com as classes e com os fluxos dos serviços. A interface utiliza arquivos estáticos de HTML, CSS e JavaScript servidos pela própria aplicação.

A apresentação segue orientações de normalização acadêmica da UTFPR, da UFES e do IFB, adaptadas a um relatório de disciplina (Universidade Tecnológica Federal do Paraná, 2025; Universidade Federal do Espírito Santo, 2026; Instituto Federal de Brasília, 2023).

A validação combinou testes de modelo, testes HTTP contra uma instância local com MySQL descartável, conferência da interface e medição do tempo de resposta. Os resultados registrados na seção 8 correspondem à verificação de 29 de outubro de 2026. Os testes foram preservados localmente, mas retirados do versionamento por decisão da equipe; o código de funcionamento permanece no módulo principal.

## 4 REQUISITOS E REGRAS DE NEGÓCIO

### 4.1 Requisitos funcionais

O Quadro 1 sintetiza os requisitos atuais. O funcionário executa as operações administrativas; a consulta ao acervo dispensa autenticação.

Quadro 1 - Requisitos funcionais

| ID | Funcionalidade | Prioridade |
|---|---|---|
| RF01 | Autenticar funcionários. | Alta |
| RF02 | Cadastrar usuários Aluno, Professor e Funcionário. | Alta |
| RF03 | Excluir usuários, respeitando os vínculos. | Alta |
| RF04 | Atualizar informações dos usuários. | Alta |
| RF05 | Consultar usuários cadastrados. | Alta |
| RF06 | Cadastrar livros com título, gênero, ano, autor, edição e quantidade. | Alta |
| RF07 | Solicitar exclusão de livros. | Alta |
| RF08 | Atualizar informações dos livros. | Alta |
| RF09 | Consultar o acervo publicamente. | Alta |
| RF10 | Registrar empréstimos para as três categorias de usuários. | Alta |
| RF11 | Calcular a data prevista de devolução. | Alta |
| RF12 | Registrar devolução e atualizar a disponibilidade. | Alta |
| RF13 | Calcular multa por atraso. | Média |
| RF14 | Renovar empréstimos. | Média |
| RF15 | Registrar quitação de multas. | Baixa |
| RF16 | Adicionar usuário à fila de reserva. | Baixa |
| RF17 | Remover usuário da fila de reserva. | Baixa |

Fonte: requisitos funcionais do projeto (2026), síntese dos autores.

### 4.2 Requisitos não funcionais

Quadro 2 - Requisitos não funcionais e evidências

| ID | Restrição | Evidência na implementação |
|---|---|---|
| RNF01 | Spring Boot e Spring Web. | Dependências no pom.xml e controllers HTTP. |
| RNF02 | Separação de responsabilidades em MVC. | Interface estática, controller, service, repository e model. |
| RNF03 | Persistência em MySQL. | Entidades JPA e MySQL Connector/J. |
| RNF04 | Credenciais fora do código e do repositório. | Variáveis DB_USERNAME e DB_PASSWORD. |
| RNF05 | Orientação a objetos. | Entidades encapsuladas e enum com políticas de prazo e limite. |
| RNF06 | Construção com Maven. | pom.xml e Maven Wrapper. |
| RNF07 | Versionamento em Git/GitHub. | Histórico e repositório do projeto. |
| RNF08 | Consultas locais em até 3 segundos. | Medições descritas na seção 8. |
| RNF09 | Erros de entrada e de banco tratados. | Validation, ApiExceptionHandler e tratamento de indisponibilidade na autenticação. |

Fonte: requisitos não funcionais e código do projeto (2026).

### 4.3 Regras de negócio

Tabela 1 - Limites e prazos de empréstimo (RN01 e RN02)

| Categoria | Empréstimos ativos | Prazo |
|---|---|---|
| Aluno | Até 5 | 7 dias |
| Professor | Até 8 | 28 dias |
| Funcionário | Até 8 | 28 dias |

Fonte: regras de negócio do projeto (2026).

Um empréstimo exige unidade disponível (RN03). A devolução atrasada gera multa de R$ 1,00 por dia, calculada entre a data prevista e a data efetiva de devolução (RN04). Uma multa pendente impede novo empréstimo, renovação e ingresso em reserva (RN05).

Cada empréstimo pode ser renovado uma única vez, desde que não esteja atrasado, o usuário não possua multa pendente e o livro não tenha reservas. O prazo da categoria é acrescentado à data prevista anterior (RN06).

A reserva exige ausência de unidades disponíveis, de multa pendente, de reserva duplicada e de empréstimo ativo do mesmo livro (RN07). A fila segue FIFO. Ao emprestar para seu primeiro usuário, a respectiva reserva é removida (RN08).

Usuários com empréstimos ativos, multas pendentes ou reservas não podem ser excluídos (RN09). Livros com empréstimos ou reservas são marcados como pendentes de exclusão: deixam de aceitar novas reservas e só podem atender reservas já existentes. A exclusão é concluída por nova solicitação quando os vínculos ativos terminarem (RN10).

Além dessas regras, a implementação protege a continuidade do acesso administrativo: o último funcionário não pode ser excluído nem ter sua categoria alterada. Essa proteção e a preservação de nomes e títulos históricos são descritas como decisões já implementadas, sem alterar a numeração ou o conteúdo dos requisitos validados.

## 5 MODELAGEM UML

### 5.1 Casos de uso

A Figura 1 apresenta os 17 casos de uso e os atores Funcionário e Visitante. O mesmo ator Funcionário é representado nas duas laterais para manter as associações curtas. A consulta ao acervo é pública; as demais operações exigem funcionário autenticado. Calcular prazo integra o empréstimo, enquanto calcular multa estende a devolução em caso de atraso.

![Figura 1 - Diagrama de casos de uso](../diagramas/diagrama-de-casos-de-uso.pdf)

### 5.2 Classes do domínio

A Figura 2 apresenta as cinco entidades, seus atributos e operações principais, as multiplicidades e o enum TipoUsuario. Os vínculos opcionais de Emprestimo com Usuario e Livro permitem preservar registros encerrados após exclusões físicas, conforme descrito na seção 6.3.

![Figura 2 - Diagrama de classes do domínio](../diagramas/diagrama-de-classes.pdf)

### 5.3 Atividades de circulação

As Figuras 3 a 5 apresentam os fluxos de empréstimo, devolução e reserva. As raias distinguem as ações do Funcionário e do Sistema, e as decisões indicam as condições para continuar ou impedir cada operação.

![Figura 3 - Registrar empréstimo](../diagramas/diagramas-de-atividades/registrar-emprestimo.pdf)

![Figura 4 - Registrar devolução](../diagramas/diagramas-de-atividades/registrar-devolucao.pdf)

![Figura 5 - Adicionar usuário à fila de reserva](../diagramas/diagramas-de-atividades/adicionar-usuario-fila-reserva.pdf)

## 6 IMPLEMENTAÇÃO E BOAS PRÁTICAS

### 6.1 Percurso das requisições

O navegador carrega index.html, styles.css e app.js. O JavaScript consulta a API com fetch e apresenta os dados recebidos. As buscas da interface filtram as listas carregadas. Os controllers recebem os DTOs com Jakarta Validation e delegam as operações aos serviços. As respostas são records definidos em Respostas, sem expor o hash de senha ou serializar diretamente os relacionamentos das entidades.

AutenticacaoController consulta o funcionário por UsuarioService. UsuarioService cuida do cadastro, normalização de e-mail, codificação de senha e exclusão de usuários. LivroService gerencia o acervo e a exclusão de livros. CirculacaoService coordena empréstimos, devoluções, renovações, reservas e quitação de multas.

Quadro 3 - Recursos HTTP principais

| Recurso | Operações |
|---|---|
| /api/livros | GET público; POST, PUT e DELETE administrativos. |
| /api/usuarios | GET, POST, PUT e DELETE autenticados. |
| /api/emprestimos | GET e POST; POST /{id}/devolucao e /{id}/renovacao. |
| /api/reservas | GET com livroId; POST; DELETE /{id}. |
| /api/multas | GET; POST /{id}/quitacao. |
| /api/autenticacao | GET com as credenciais do funcionário. |

Fonte: controllers do projeto (2026).

### 6.2 Orientação a objetos e responsabilidades

As entidades possuem atributos privados e métodos que preservam suas invariantes. Livro impede disponibilidade negativa ou maior que o total; Emprestimo impede devolução repetida e renovação inválida; Multa usa BigDecimal e impede quitação repetida. TipoUsuario evita espalhar números de prazo e limite pelos serviços.

A separação de responsabilidades segue o princípio de responsabilidade única. As dependências são recebidas por construtor; repositórios e PasswordEncoder são usados por suas interfaces. Esses recursos aplicam princípios SOLID pertinentes ao projeto, sem exigir camadas ou abstrações adicionais.

### 6.3 Autenticação, histórico e erros

Spring Security aceita somente funcionários na área administrativa. A configuração é stateless e usa HTTP Basic; as credenciais da interface permanecem em memória, sem armazenamento persistente no navegador. O funcionário inicial pode ser criado por variáveis de ambiente, somente quando não existem usuários. Excluir ou reclassificar o último funcionário é bloqueado no serviço.

Antes de excluir fisicamente um usuário ou livro, o serviço percorre seus empréstimos encerrados. A entidade copia o nome ou título atual para o campo histórico e remove a referência correspondente. Os empréstimos e as multas permanecem armazenados. Informações de cadastros já excluídos antes dessa preservação não são recuperáveis automaticamente.

Falhas de validação e argumentos inválidos retornam 400, ausência de recurso retorna 404 e conflitos de negócio retornam 409. Requisições administrativas sem credenciais válidas retornam 401. Falhas de acesso aos dados retornam 503 com mensagem compreensível, inclusive quando ocorrem durante a autenticação. Detalhes internos e stack traces não são expostos nas respostas de erro configuradas.

## 7 BANCO DE DADOS

O sistema utiliza MySQL e cinco entidades JPA. Todas possuem identificador Long gerado com GenerationType.IDENTITY. TipoUsuario é um enum, não uma tabela separada. O Hibernate cria ou atualiza as tabelas a partir do mapeamento; o banco poortal_do_leitor deve existir antes da inicialização.

Quadro 4 - Entidades e persistência

| Tabela | Informações principais | Restrições e referências |
|---|---|---|
| usuarios | id, nome, email, senha_hash, tipo | E-mail único; nome, e-mail e tipo obrigatórios. Senha exigida pelo serviço para funcionário. |
| livros | id, titulo, genero, ano, autor, edicao, quantidade_total, quantidade_disponivel, pendente_exclusao | Campos obrigatórios; estoque validado no modelo. |
| emprestimos | id, usuario_id, livro_id, datas, renovado, nome_usuario_historico, titulo_livro_historico | FKs de usuário e livro anuláveis para preservar o histórico após exclusões. |
| reservas | id, usuario_id, livro_id, data_solicitacao | FKs obrigatórias; combinação usuário/livro única. |
| multas | id, emprestimo_id, dias_atraso, valor, quitada, data_quitacao | FK de empréstimo obrigatória e única; valor decimal com precisão 19 e escala 2. |

Fonte: anotações JPA das entidades (2026).

Um usuário e um livro podem participar de vários empréstimos e reservas. Cada reserva referencia exatamente um usuário e um livro; cada multa referencia um empréstimo, que pode possuir zero ou uma multa. Essas cardinalidades são apresentadas na Figura 2. O nome das colunas segue o mapeamento adotado pelo Hibernate na aplicação.

Os serviços usam transações para agrupar alterações relacionadas. Por exemplo, emprestar altera a disponibilidade, cria o empréstimo e remove a reserva atendida; devolver registra a data, recompõe a disponibilidade e cria multa quando necessário. Não há exclusão em cascata do histórico. As FKs de empréstimos são desvinculadas explicitamente antes de remover os cadastros.

As consultas de prioridade usam o identificador crescente da reserva. As consultas de pendências utilizam métodos exists e count dos repositórios. O projeto não inclui mecanismos adicionais de controle de concorrência ou paginação; a validação cobre o uso acadêmico local, sem demonstrar comportamento sob operações simultâneas em grande volume.

## 8 RESULTADOS E VALIDAÇÃO

Na validação funcional de 29 de outubro de 2026, o comando Maven clean package terminou com BUILD SUCCESS. Foram aprovados 17 testes: 11 de modelo e 6 execuções HTTP com MySQL, sem falhas ou erros. Os fluxos cobriram limites e prazos das três categorias, renovação única, reservas FIFO, multa por atraso, bloqueio por pendência, quitação, exclusões físicas, preservação do histórico e proteção do último funcionário.

O navegador foi utilizado para conferir consulta pública, autenticação, apresentação do histórico e layout em desktop e celular. A revisão de legibilidade ampliou textos pequenos sem trocar o desenho da interface. Ao desligar o MySQL temporário, consultas públicas e administrativas responderam 503, e a aplicação continuou em execução até seu encerramento explícito.

Tabela 2 - Desempenho de consultas HTTP em ambiente local

| Consulta | Registros retornados | Média (ms) | Máximo (ms) |
|---|---|---|---|
| Livros | 507 | 30,4 | 101,3 |
| Usuários | 504 | 66,4 | 78,0 |
| Empréstimos | 1.025 | 69,6 | 91,1 |
| Multas | 1.001 | 66,4 | 80,7 |
| Reservas de um livro | 500 | 66,7 | 84,3 |
| Funcionário autenticado | 1 | 60,6 | 67,3 |

Fonte: medições locais da validação do projeto em 29 out. 2026.

Foram realizadas dez medições por rota, totalizando 60 requisições, incluindo o recebimento da resposta e a leitura do JSON. As rotas privadas incluíram autenticação. Não foi descartada uma rodada inicial de aquecimento. Todas as amostras ficaram abaixo dos 3.000 ms previstos no RNF08. O resultado demonstra atendimento no ambiente e conjunto de dados medidos; não equivale a teste de carga concorrente nem garante o mesmo tempo em qualquer equipamento.

Os testes automatizados permanecem somente no ambiente local, conforme a organização escolhida para a entrega. Portanto, um clone do repositório não contém essa suíte. As medições usam dados descartáveis, que não integram o banco entregue nem os arquivos versionados.

## 9 CONSIDERAÇÕES FINAIS

O POOrtal do Leitor integra modelagem, orientação a objetos, interface web e persistência relacional em um sistema de escopo compatível com POO2. A organização permite acompanhar as operações desde os DTOs recebidos pelos controllers até as verificações dos serviços, os comportamentos das entidades e a persistência no MySQL.

Os requisitos documentados orientam a circulação do acervo, o tratamento das pendências e as restrições de exclusão. A preservação de informações históricas evita perder a identificação dos registros encerrados, enquanto a proteção do último funcionário mantém um caminho de acesso administrativo.

A validação local demonstrou os fluxos críticos e os tempos de consulta registrados. Permanecem como limites do trabalho a ausência de ensaio de concorrência, a dependência de Java/MySQL configurados corretamente e a disponibilidade local dos testes. A entrega inclui README de execução, requisitos preservados, diagramas atualizados e este relatório, sem ampliar as funcionalidades da aplicação.

## REFERÊNCIAS

INSTITUTO FEDERAL DE BRASÍLIA. Margens. In: INSTITUTO FEDERAL DE BRASÍLIA. **Normaliza IFB**: manual de normalização de trabalhos acadêmicos. Brasília: IFB, 2023. Disponível em: https://normaliza.ifb.edu.br/doku.php?id=trabalho_academico:apresentacao_grafica:margens. Acesso em: 29 out. 2026.

OBJECT MANAGEMENT GROUP. **Unified Modeling Language**: version 2.5.1. [S. l.]: OMG, 2017. Disponível em: https://www.omg.org/spec/UML/2.5.1. Acesso em: 29 out. 2026.

ORACLE. FOREIGN KEY constraints. In: ORACLE. **MySQL 8.0 reference manual**. [S. l.]: Oracle, c2026. Disponível em: https://dev.mysql.com/doc/refman/8.0/en/create-table-foreign-keys.html. Acesso em: 30 out. 2026.

ORACLE. Object-oriented programming concepts. In: ORACLE. **The Java tutorials**. [S. l.]: Oracle, c2024. Disponível em: https://docs.oracle.com/javase/tutorial/java/concepts/. Acesso em: 30 out. 2026.

SPRING. Password storage. In: SPRING. **Spring Security**: reference. [S. l.]: Spring, [2026]a. Disponível em: https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html. Acesso em: 30 out. 2026.

SPRING. **Spring Boot**: reference. [S. l.]: Spring, [2026]b. Disponível em: https://docs.spring.io/spring-boot/reference/index.html. Acesso em: 30 out. 2026.

SPRING. **Spring Data JPA**: reference documentation. [S. l.]: Spring, c2026c. Disponível em: https://docs.spring.io/spring-data/jpa/reference/index.html. Acesso em: 30 out. 2026.

UNIVERSIDADE FEDERAL DO ESPÍRITO SANTO. **Normalização**. Vitória: Sistema Integrado de Bibliotecas, 2026. Disponível em: https://biblioteca.ufes.br/normalizacao. Acesso em: 29 out. 2026.

UNIVERSIDADE TECNOLÓGICA FEDERAL DO PARANÁ. **Trabalhos acadêmicos**. [S. l.]: UTFPR, 2025. Disponível em: https://www.utfpr.edu.br/bibliotecas/trabalhos-academicos. Acesso em: 29 out. 2026.

WANG, Daniel Chien Chen; GALDINI, Daniella Melo; MARQUES, João Vitor Taques. **POOrtal do Leitor**: código-fonte, requisitos e regras de negócio. Cornélio Procópio, 2026. Repositório GitHub. Disponível em: https://github.com/Dani-m-g/POOrtal_do_Leitor. Acesso em: 29 out. 2026.
