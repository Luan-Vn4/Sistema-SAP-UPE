package br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades;

import br.upe.sap.sistemasapupe.data.model.atividades.Encontro;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.BasicRepository;
import java.util.List;

public interface EncontroRepository extends BasicRepository<Encontro, Integer> {

    // CREATE //
    int addComparecimento(int funcionario, int idAtividade);

    List<Integer> addComparecimentos(List<Integer> funcionarios, int idAtividade);


    // UPDATE //
    /**
     * Atualiza a atividade de encontro com os dados novos, além de atualizar todos os
     * comparecimentos (funcionários)
     * @param encontro - {@link Encontro} com dados atualizados, mas não persistidos
     * @return - {@link Encontro} com dados persistidos
     */
    @Override
    Encontro update(Encontro encontro);

    /**
     * Atualiza as atividades de encontro com os dados novos, além de atualizar todos os
     * comparecimentos (funcionários)
     * @param encontros - {@link Encontro} com dados atualizados, mas não persistidos
     * @return - {@link Encontro} com dados persistidos
     */
    @Override
    List<Encontro> update(List<Encontro> encontros);


    // READ //
    List<Integer> findIdsComparecidos(int idAtividade);


    // DELETE //
    int deleteComparecido(int idFuncionario, int idAtividade);

    int deleteComparecidos(List<Integer> idsFuncionarios, int idAtividade);

}
