package br.edu.utfpr.poortal.dto;

import br.edu.utfpr.poortal.model.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank(message = "Informe o nome.")
        @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres.") String nome,
        @NotBlank(message = "Informe o e-mail.")
        @Size(max = 255, message = "O e-mail deve ter no máximo 255 caracteres.")
        @Email(message = "Informe um e-mail válido.") String email,
        String senha,
        @NotNull(message = "Informe o tipo de usuário.") TipoUsuario tipo) {
}
