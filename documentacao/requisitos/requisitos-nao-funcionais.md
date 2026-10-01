# Requisitos Não Funcionais

Os requisitos não funcionais definem restrições técnicas e atributos de qualidade do POOrtal do Leitor.

| ID | Descrição | Categoria |
|---|---|---|
| RNF01 | A aplicação deve utilizar Spring Boot para o desenvolvimento do backend e Spring Web para disponibilizar suas funcionalidades por meio de requisições HTTP. | Arquitetura |
| RNF02 | A aplicação deve manter separadas as responsabilidades de apresentação, controle, lógica de negócio e acesso aos dados, seguindo os princípios da arquitetura MVC. | Arquitetura |
| RNF03 | Os dados persistentes do sistema devem ser armazenados em banco de dados MySQL. | Persistência |
| RNF04 | As credenciais de acesso ao banco de dados não devem ser mantidas diretamente no código-fonte nem versionadas no repositório. | Segurança |
| RNF05 | O código-fonte deve ser estruturado segundo os princípios de Programação Orientada a Objetos. | Padrões |
| RNF06 | O projeto deve utilizar Maven para gerenciamento de dependências e construção da aplicação. | Padrões |
| RNF07 | O código-fonte deve ser versionado utilizando Git e mantido em repositório no GitHub. | Padrões |
| RNF08 | As operações de consulta realizadas em ambiente local devem apresentar tempo de resposta de até 3 segundos. | Desempenho |
| RNF09 | O sistema deve tratar falhas de entrada de dados e de acesso ao banco, retornando mensagens de erro adequadas sem encerrar a aplicação de forma inesperada. | Confiabilidade |