package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.atividades.*;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;

import java.time.LocalDateTime;
import java.util.List;

public interface AtividadesRepository extends BasicRepository<Atividade, Integer> {

    List<Atividade> findBySala(Integer idSala);

    List<Atividade> findByFuncionario(Integer idFuncionario);

    List<AtendimentoIndividual> findByFuncionarioAtendimentoIndividual(Integer idFuncionario);

    List<AtendimentoGrupo> findByFuncionarioAtendimentoGrupo(Integer idFuncionario);

    List<Encontro> findByFuncionarioEncontroEstudo(Integer idFuncionario);

    List<Atividade> findByTempo(LocalDateTime tempoInicio, LocalDateTime tempoFim);

    List<Atividade> findByStatus(StatusAtividade statusAtividade);

    AtendimentoIndividual createAtendimentoIndividual(AtendimentoIndividual atendimentoIndividual);

    AtendimentoGrupo createAtendimentoGrupo(AtendimentoGrupo atendimentoGrupo);

    Encontro createEncontroEstudo(Encontro encontroEstudo);

    AtendimentoIndividual updateAtendimentoIndividual(AtendimentoIndividual atendimentoIndividual);

    AtendimentoGrupo updateAtendimentoGrupo(AtendimentoGrupo atendimentoGrupo);

    Encontro updateEncontroEstudo(Encontro encontroEstudo);

    Atividade updateStatusAtividade(Integer idAtividade, StatusAtividade statusAtividade);

}