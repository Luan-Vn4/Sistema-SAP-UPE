package br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades;

import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoGrupo;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.BasicRepository;

import java.util.List;

public interface AtendimentoGrupoRepository extends BasicRepository<AtendimentoGrupo, Integer> {

    // CREATE //
    int addParticipante(int idFicha, int idAtividade);

    List<Integer> addParticipantes(List<Integer> idFicha, int idAtividade);

    int addMinistrante(Integer idFuncionario, int idAtividade);

    List<Integer> addMinistrantes(List<Integer> idsFuncionarios, int idAtividade);


    // UPDATE //
    /**
     * Atualiza os dados do atendimento em grupo, além de atualizar todos os seus participantes (fichas) e
     * ministrantes (funcionários)
     * @param atendimentoGrupo {@link AtendimentoGrupo} com dados atualizados, mas não persistidos
     * @return {@link AtendimentoGrupo} com os dados atualizados
     */
    @Override
    AtendimentoGrupo update(AtendimentoGrupo atendimentoGrupo);

    /**
     * Atualiza os dados dos atendimentos em grupo, além de atualizar todos os seus participantes (fichas) e
     * ministrantes (funcionários)
     * @param atendimentosGrupo {@link AtendimentoGrupo} com dados atualizados, mas não persistidos
     * @return {@link AtendimentoGrupo} com os dados atualizados
     */
    @Override
    List<AtendimentoGrupo> update(List<AtendimentoGrupo> atendimentosGrupo);


    List<AtendimentoGrupo> findByGrupoTerapeutico(Integer idGrupoTerapeutico);

    // READ //
    List<Integer> findIdsMinistrantes(int idAtividade);

    List<Integer> findIdsParticipantes(int idAtividade);


    // DELETE //
    int deleteMinistrante(int idMinistrante, int idAtividade);

    int deleteMinistrantes(List<Integer> idsMinistrantes, int idAtividade);

    int deleteParticipante(int idFicha, int idAtendimentoGrupo);

    int deleteParticipantes(List<Integer> idsParticipantes, int idAtendimentoGrupo);

}
