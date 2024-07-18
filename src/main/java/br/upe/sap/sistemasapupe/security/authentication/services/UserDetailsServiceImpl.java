package br.upe.sap.sistemasapupe.security.authentication.services;

import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    FuncionarioRepository funcionarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDetails user = funcionarioRepository.findByEmail(email);

        if (user == null) throw new UsernameNotFoundException("Não foi identificado um usuário " +
                "com o email: " + email);

        return user;
    }

    public UserDetails loadUserByUID(UUID uid) {
        Integer id = funcionarioRepository.findIds(uid).get(uid);

        UserDetails user = funcionarioRepository.findById(id);

        if (user == null) throw new UsernameNotFoundException("Não foi identificado um usuário " +
                "com o uid = " + uid);

        return user;
    }

}
