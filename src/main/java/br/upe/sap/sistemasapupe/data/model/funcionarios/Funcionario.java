package br.upe.sap.sistemasapupe.data.model.funcionarios;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.UUID;

@ToString
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode
public abstract class Funcionario implements UserDetails {

    @Id
    @Column(name = "id")
    protected Integer id;

    @Column(name = "uid")
    private UUID uid;

    @Column(name = "nome")
    private String nome;

    @Column(name = "sobrenome")
    private String sobrenome;

    @Column(name = "email")
    private String email;

    @Column(name = "senha")
    private String senha;

    @Column(name = "imagem")
    private String urlImagem;

    @Column(name="is_ativo")
    private boolean isAtivo = true;

    public String getNomeCompleto() {
        return this.nome + " " + this.sobrenome;
    }

    public abstract Cargo getCargo();

    @Override
    public final Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(this.getCargo().getPrefixedRole());
    }

    @Override
    public String getPassword() {
        return this.getSenha();
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }

}
