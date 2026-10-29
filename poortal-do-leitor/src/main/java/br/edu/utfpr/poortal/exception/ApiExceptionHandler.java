package br.edu.utfpr.poortal.exception;

import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {
    public record Erro(int status, String mensagem, List<String> detalhes) {
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Erro> naoEncontrado(NotFoundException exception) {
        return erro(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({RegraNegocioException.class, IllegalStateException.class})
    public ResponseEntity<Erro> regraNegocio(RuntimeException exception) {
        return erro(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Erro> argumentoInvalido(IllegalArgumentException exception) {
        return erro(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Erro> validacao(MethodArgumentNotValidException exception) {
        List<String> detalhes = exception.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .distinct().toList();
        return ResponseEntity.badRequest().body(new Erro(400, "Dados de entrada inválidos.", detalhes));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<Erro> requisicaoInvalida(Exception exception) {
        return erro(HttpStatus.BAD_REQUEST, "Verifique o corpo da requisição e os parâmetros informados.");
    }

    @ExceptionHandler({DataAccessException.class, TransactionException.class})
    public ResponseEntity<Erro> bancoIndisponivel(RuntimeException exception) {
        return erro(HttpStatus.SERVICE_UNAVAILABLE, "Não foi possível acessar os dados. Tente novamente mais tarde.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Erro> conflitoDados(DataIntegrityViolationException exception) {
        return erro(HttpStatus.CONFLICT, "Os dados conflitam com um registro existente.");
    }

    private ResponseEntity<Erro> erro(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(new Erro(status.value(), mensagem, List.of()));
    }
}
