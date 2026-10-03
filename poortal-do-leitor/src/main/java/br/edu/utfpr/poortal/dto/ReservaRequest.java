package br.edu.utfpr.poortal.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReservaRequest(
        @NotNull(message = "Informe o usuário.")
        @Positive(message = "O identificador do usuário deve ser positivo.") Long usuarioId,
        @NotNull(message = "Informe o livro.")
        @Positive(message = "O identificador do livro deve ser positivo.") Long livroId) {
}
