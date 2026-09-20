# Requisitos Não Funcionais

Os requisitos não funcionais definem restrições técnicas e atributos de qualidade do POOrtal do Leitor.

| ID | Descrição | Categoria |
|---|---|---|
| RNF01 | A interface gráfica deve ser desenvolvida utilizando Java Swing e manter padrões consistentes de navegação, nomenclatura e apresentação das informações. | Facilidade de uso |
| RNF02 | Os dados persistentes do sistema devem ser armazenados em banco de dados MySQL e acessados pela aplicação Java por meio de JDBC. | Persistência |
| RNF03 | A aplicação deve ser estruturada segundo o padrão arquitetural MVC, mantendo separadas as responsabilidades de apresentação, controle e modelo. | Arquitetura |
| RNF04 | O código-fonte deve ser estruturado segundo os princípios de Programação Orientada a Objetos. | Padrões |
| RNF05 | O código-fonte deve ser versionado utilizando Git e mantido em repositório no GitHub. | Padrões |
| RNF06 | O projeto deve utilizar Maven para gerenciamento de dependências e construção da aplicação. | Padrões |
| RNF07 | As operações de consulta realizadas em ambiente local devem apresentar tempo de resposta de até 3 segundos. | Desempenho |
| RNF08 | O sistema deve tratar falhas de entrada de dados e de acesso ao banco, apresentando mensagens de erro ao usuário sem encerrar a aplicação de forma inesperada. | Confiabilidade |