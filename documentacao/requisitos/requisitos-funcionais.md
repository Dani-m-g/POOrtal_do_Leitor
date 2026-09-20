# Requisitos Funcionais

Os requisitos funcionais definem as funcionalidades disponibilizadas pelo sistema POOrtal do Leitor.

A consulta ao acervo é pública e não exige autenticação. As demais funcionalidades são restritas a funcionários autenticados.

| ID | Descrição | Prioridade |
|---|---|---|
| RF01 | O sistema deve permitir a autenticação de funcionários. | Alta |
| RF02 | O sistema deve permitir que o funcionário cadastre usuários dos tipos Aluno, Professor e Funcionário. | Alta |
| RF03 | O sistema deve permitir que o funcionário exclua usuários cadastrados, respeitando a RN09. | Alta |
| RF04 | O sistema deve permitir que o funcionário atualize as informações de usuários cadastrados. | Alta |
| RF05 | O sistema deve permitir que o funcionário consulte usuários cadastrados. | Alta |
| RF06 | O sistema deve permitir que o funcionário cadastre livros, informando título, gênero, ano, autor, edição e quantidade. | Alta |
| RF07 | O sistema deve permitir que o funcionário solicite a exclusão de livros cadastrados, respeitando a RN10. | Alta |
| RF08 | O sistema deve permitir que o funcionário atualize as informações dos livros cadastrados. | Alta |
| RF09 | O sistema deve permitir a consulta dos livros cadastrados sem necessidade de autenticação. | Alta |
| RF10 | O sistema deve permitir que o funcionário registre empréstimos para usuários dos tipos Aluno, Professor e Funcionário, respeitando as RN01, RN03, RN05 e RN10. | Alta |
| RF11 | O sistema deve calcular automaticamente a data de devolução conforme o prazo definido na RN02. | Alta |
| RF12 | O sistema deve permitir que o funcionário registre a devolução de um livro, atualizando sua quantidade disponível. | Alta |
| RF13 | O sistema deve calcular automaticamente a multa por atraso conforme a RN04. | Média |
| RF14 | O sistema deve permitir que o funcionário renove um empréstimo, respeitando a RN06. | Média |
| RF15 | O sistema deve permitir que o funcionário registre a quitação de uma multa pendente. | Baixa |
| RF16 | O sistema deve permitir que o funcionário adicione um usuário à fila de reserva de um livro, respeitando as RN07, RN08 e RN10. | Baixa |
| RF17 | O sistema deve permitir que o funcionário remova um usuário da fila de reserva de um livro. | Baixa |