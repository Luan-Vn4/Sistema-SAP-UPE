package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.atividades.*;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AtividadeSalaRepository extends BasicRepository<Atividade, UUID> {

    List<Atividade> findBySala(UUID uidSala);

    List<Atividade> findByFuncionario(UUID uidFuncionario);
    List<AtendimentoIndividual> findByFuncionarioAtendimentoIndividual(UUID uidFuncionario);
    List<AtendimentoGrupo> findByFuncionarioAtendimentoGrupo(UUID uidFuncionario);
    List<Encontro> findByFuncionarioEncontroEstudo(UUID uidFuncionario);

    List<Atividade> findByTempo(LocalDateTime tempoInicio, LocalDateTime tempoFim);
    List<Atividade> findByStatus(StatusAtividade statusAtividade);

    AtendimentoIndividual createAtendimentoIndividual(AtendimentoIndividual atendimentoIndividual);
    AtendimentoGrupo createAtendimentoGrupo(AtendimentoGrupo atendimentoGrupo);
    Encontro createEncontroEstudo(Encontro encontroEstudo);

    AtendimentoIndividual updateAtendimentoIndividual(AtendimentoIndividual atendimentoIndividual);
    AtendimentoGrupo updateAtendimentoGrupo(AtendimentoGrupo atendimentoGrupo);
    Encontro updateEncontroEstudo(Encontro encontroEstudo);

    Atividade updateStatusAtividade(UUID uidAtividade, StatusAtividade statusAtividade);

    public Sala createSala(Sala sala);


}
