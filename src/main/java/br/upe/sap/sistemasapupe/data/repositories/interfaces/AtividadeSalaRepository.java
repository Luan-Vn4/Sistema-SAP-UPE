package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.atividades.*;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AtividadeSalaRepository extends Repository<Atividade, UUID> {

    List<Atividade> findBySala(UUID uidSala);

    List<Atividade> findByFuncionario(UUID uidFuncionario);

    List<Atividade> findByTempo(LocalDateTime tempoInicio, LocalDateTime tempoFim);

    List<AtendimentoIndividual> updateAtendimentosIndividuais(UUID uidFicha, UUID uidfuncionario,
                                                              LocalDateTime tempoInicio,
                                                              LocalDateTime tempoFim);

    Atividade updateStatusAtividade(UUID uidAtividade, StatusAtividade statusAtividade);


}
