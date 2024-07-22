package br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades;

import br.upe.sap.sistemasapupe.data.model.atividades.*;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.BasicRepository;
import org.apache.commons.collections4.BidiMap;

import java.util.List;
import java.util.UUID;

public interface AtividadeRepositoryFacade extends BasicRepository<Atividade, Integer> {

    // CREATE //
        // Relacionado - AtendimentoGrupo
    int addParticipanteToAtendimentoGrupo(int idFicha, int idAtividade);

    BidiMap<UUID, Integer> findIds(UUID uuid);

    BidiMap<UUID, Integer> findIds(List<UUID> uuids);

    List<Integer> addParticipantesToAtendimentoGrupo(List<Integer> idFicha, int idAtividade);

    int addMinistranteToAtendimentoGrupo(Integer idFuncionario, int idAtividade);

    List<Integer> addMinistrantesToAtendimentoGrupo(List<Integer> idsFuncionarios, int idAtividade);

        // Relacionado - Encontro
    int addComparecimentoToEncontro(int funcionario, int idAtividade);

    List<Integer> addComparecimentosToEncontro(List<Integer> funcionarios, int idAtividade);


    // UPDATE //
    /**
     * <b>ATENÇÃO:</b>Atualiza apenas as informações de {@link Atividade}, sem atualizar dados específicos de outros subtipos
     * de atividades. Esse método será utilizado na maioria das vezes.
     * @param atividade {@link Atividade} com dados atualizados, mas não persistidos
     * @return {@link Atividade} com dados, apenas correspondentes à superclasse {@link Atividade}, persistidos
     */
    Atividade simpleUpdate(Atividade atividade);

    /**
     * <b>ATENÇÃO:</b>Atualiza apenas as informações de {@link Atividade}, sem atualizar dados específicos de outros subtipos
     * de atividades. Esse método será utilizado na maioria das vezes.
     * @param atividades {@link List<Atividade>} com dados atualizados, mas não persistidos
     * @return {@link Atividade} com dados, apenas correspondentes à superclasse {@link Atividade}, persistidos
     */
    List<Atividade> simpleUpdate(List<Atividade> atividades);

    /**
     * <b>ATENÇÃO:</b> esse método atualiza todos os dados daquele tipo específico de atividade. Por exemplo,
     * se for passado um {@link AtendimentoGrupo} todos os participantes e ministrantes serão atualizados.
     * Logo, utilize com cuidado. <br><br>
     * Caso queira apenas atualizar os dados específicos da superclasse {@link Atividade} utilize o método:
     * {@link AtividadeRepositoryFacade#simpleUpdate(Atividade)}
     * @param atividade {@link Atividade} com dados atualizados, mas não persistidos
     * @return {@link Atividade} com absolutamente todos os dados persistidos
     */
    @Override
    Atividade update(Atividade atividade);

    /**
     * <b>ATENÇÃO:</b> esse método atualiza todos os dados daquele tipo específico de atividade. Por exemplo,
     * se for passado um {@link AtendimentoGrupo} todos os participantes e ministrantes serão atualizados.
     * Logo, utilize com cuidado. <br><br>
     * Caso queira apenas atualizar os dados específicos da superclasse {@link Atividade} utilize o método:
     * {@link AtividadeRepositoryFacade#simpleUpdate(Atividade)}
     * @param atividades {@link List<Atividade>} com dados atualizados, mas não persistidos
     * @return {@link Atividade} com absolutamente todos os dados persistidos
     */
    @Override
    List<Atividade> update(List<Atividade> atividades);


    // READ //
        // Relacionado - AtendimentoGrupo
    List<Integer> findIdsMinistrantesFromAtendimentoGrupo(int idAtividade);

    List<Integer> findIdsParticipantesFromAtendimentoGrupo(int idAtividade);

    List<Integer> findIdsComparecidosFromEncontro(int idAtividade);


    // DELETE
        // Relacionado - AtendimentoGrupo
    int deleteMinistranteFromAtendimentoGrupo(int idMinistrante, int idAtividade);

    int deleteMinistrantesFromAtendimentoGrupo(List<Integer> idsMinistrantes, int idAtividade);

    int deleteParticipanteFromAtendimentoGrupo(int idParticipante, int idAtividade);

    int deleteParticipantesFromAtendimentoGrupo(List<Integer> idsParticipantes, int idAtividade);
        // Relacionado - Encontro
    int deleteComparecidoFromEncontro(int idFuncionario, int idAtividade);

    int deleteComparecidosFromEncontro(List<Integer> idsFuncionarios, int idAtividade);
}
