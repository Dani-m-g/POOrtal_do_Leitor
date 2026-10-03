package br.edu.utfpr.poortal.config;

import br.edu.utfpr.poortal.model.TipoUsuario;
import br.edu.utfpr.poortal.model.Usuario;
import br.edu.utfpr.poortal.repository.UsuarioRepository;
import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

@Configuration
public class SegurancaConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarios) {
        return email -> {
            Usuario usuario = usuarios.findByEmail(email.trim().toLowerCase(Locale.ROOT))
                    .filter(encontrado -> encontrado.getTipo() == TipoUsuario.FUNCIONARIO)
                    .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas."));
            return User.withUsername(usuario.getEmail()).password(usuario.getSenhaHash())
                    .roles("FUNCIONARIO").build();
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        BasicAuthenticationEntryPoint autenticacaoBasic = new BasicAuthenticationEntryPoint();
        autenticacaoBasic.setRealmName("Realm");
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(autorizacao -> autorizacao
                        .requestMatchers(HttpMethod.GET, "/api/livros", "/api/livros/*").permitAll()
                        .anyRequest().hasRole("FUNCIONARIO"))
                .httpBasic(basic -> basic.authenticationEntryPoint((request, response, exception) -> {
                    if (exception instanceof AuthenticationServiceException) {
                        response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
                        response.setContentType("text/plain;charset=UTF-8");
                        response.getWriter().write("Não foi possível acessar os dados. Tente novamente mais tarde.");
                    } else {
                        autenticacaoBasic.commence(request, response, exception);
                    }
                }))
                .build();
    }
}
