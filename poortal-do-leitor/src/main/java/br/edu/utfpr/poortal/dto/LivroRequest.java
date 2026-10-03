package br.edu.utfpr.poortal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record LivroRequest(
        @NotBlank(message = "Informe o título.")
        @Size(max = 255, message = "O título deve ter no máximo 255 caracteres.") String titulo,
        @NotBlank(message = "Informe o gênero.")
        @Size(max = 255, message = "O gênero deve ter no máximo 255 caracteres.") String genero,
        @NotNull(message = "Informe o ano.")
        @Positive(message = "O ano deve ser positivo.") Integer ano,
        @NotBlank(message = "Informe o autor.")
        @Size(max = 255, message = "O autor deve ter no máximo 255 caracteres.") String autor,
        @NotBlank(message = "Informe a edição.")
        @Size(max = 255, message = "A edição deve ter no máximo 255 caracteres.") String edicao,
        @NotNull(message = "Informe a quantidade total.")
        @PositiveOrZero(message = "A quantidade não pode ser negativa.") Integer quantidadeTotal) {
}
