package br.upe.sap.sistemasapupe.data.model.funcionarios;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
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

    @ColumnName("uid")
    private UUID uid;

    @ColumnName("nome")
    private String nome;

    @ColumnName("sobrenome")
    private String sobrenome;

    @ColumnName("email")
    private String email;

    @ColumnName("senha")
    private String senha;

    @ColumnName("url_imagem")
    private String urlImagem;

    @ColumnName("is_ativo")
    private boolean isAtivo;

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
