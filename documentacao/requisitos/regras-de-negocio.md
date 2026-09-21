# Regras de Negócio

As regras de negócio definem as restrições e condições aplicadas às operações do POOrtal do Leitor.

## RN01 — Limite de empréstimos

A quantidade máxima de empréstimos ativos é definida de acordo com o tipo de usuário:

| Tipo de usuário | Limite |
|---|---:|
| Aluno | 5 livros |
| Professor | 8 livros |
| Funcionário | 8 livros |

Um novo empréstimo não pode ser registrado caso o usuário já tenha atingido o limite correspondente.

## RN02 — Prazo de empréstimo

O prazo de empréstimo é definido de acordo com o tipo de usuário:

| Tipo de usuário | Prazo |
|---|---:|
| Aluno | 7 dias |
| Professor | 28 dias |
| Funcionário | 28 dias |

A renovação deve acrescentar à data prevista de devolução o prazo de empréstimo aplicável ao tipo de usuário.

## RN03 — Disponibilidade para empréstimo

Um empréstimo somente pode ser registrado quando houver **pelo menos uma unidade disponível** do livro.

## RN04 — Multa por atraso

A devolução realizada após a data prevista gera multa de **R$ 1,00 por dia de atraso**.

O valor da multa deve ser calculado com base na quantidade de dias entre a data prevista e a data efetiva de devolução.

## RN05 — Restrição por multa pendente

O usuário que possuir multa pendente não pode:

- realizar novo empréstimo;
- renovar empréstimo;
- ingressar em fila de reserva.

As restrições deixam de ser aplicadas quando não houver multas pendentes associadas ao usuário.

## RN06 — Renovação de empréstimo

Cada empréstimo pode ser renovado **uma única vez**, desde que:

- o empréstimo não esteja atrasado;
- o usuário não possua multa pendente;
- o livro não possua reservas ativas.

A renovação deve acrescentar à data prevista de devolução o prazo correspondente ao tipo de usuário.

## RN07 — Reserva de livro

Um usuário somente pode ser adicionado à fila de reserva de um livro quando:

- o livro não possuir unidades disponíveis;
- o usuário não possuir multa pendente;
- o usuário não estiver na fila de reserva do mesmo livro;
- o usuário não possuir empréstimo ativo do mesmo livro.

## RN08 — Fila de reserva

A fila de reserva de cada livro deve seguir a ordem de inclusão dos usuários (**FIFO — First In, First Out**).

Quando uma unidade do livro se tornar disponível, o primeiro usuário da fila terá prioridade.

Ao registrar o empréstimo para o primeiro usuário da fila, sua reserva deve ser removida automaticamente.

## RN09 — Exclusão de usuário

Um usuário somente pode ser excluído quando não possuir:

- empréstimos ativos;
- multas pendentes;
- reservas ativas.

Caso exista qualquer um desses vínculos, a exclusão deve ser bloqueada.

## RN10 — Exclusão de livro

Um livro somente pode ser excluído quando não possuir:

- empréstimos ativos;
- usuários na fila de reserva.

Caso existam vínculos ativos, o livro deve ser marcado como **pendente de exclusão**.

Enquanto estiver pendente de exclusão:

- novas reservas não podem ser registradas;
- novos empréstimos somente podem atender usuários que já estejam na fila de reserva.

A exclusão poderá ser concluída quando não existirem mais empréstimos ativos nem usuários na fila de reserva.
