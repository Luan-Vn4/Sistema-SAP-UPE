package br.upe.sap.sistemasapupe.api.services;


import br.upe.sap.sistemasapupe.api.dtos.paciente.CreateFichaDTO;
import br.upe.sap.sistemasapupe.api.dtos.paciente.FichaDTO;
import br.upe.sap.sistemasapupe.api.dtos.paciente.UpdateFichaDTO;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FichaRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.GrupoTerapeuticoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.BidiMap;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FichaService {
    GrupoTerapeuticoRepository grupoTerapeuticoRepository;
    FichaRepository fichaRepository;
    FuncionarioRepository funcionarioRepository;

    public FichaDTO create(CreateFichaDTO fichaDTO){
        int idResponsavel = funcionarioRepository.findIds(fichaDTO.idResponsavel()).get(fichaDTO.idResponsavel());
        Ficha ficha = CreateFichaDTO.fromDTO(fichaDTO, idResponsavel);
        Ficha fichaCriada = fichaRepository.create(ficha);
        return FichaDTO.from(fichaCriada, null, fichaDTO.idResponsavel());
    }

    public FichaDTO update(UpdateFichaDTO fichaDTO) {
        Ficha fichaExistente = fichaRepository.findById(fichaRepository.
            findIds(fichaDTO.uid())
            .get(fichaDTO.uid()));

        UUID uidResponsavel = fichaDTO.idResponsavel();
        Integer idResponsavel = funcionarioRepository.findIds(uidResponsavel).get(uidResponsavel);

        if (fichaExistente == null) {
            throw new EntityNotFoundException("Ficha não encontrada para o UID: " + fichaDTO.uid());
        }

        UUID uidGrupoTerapeutico = fichaDTO.idGrupoTerapeutico();
        Integer idGrupoTerapeutico = grupoTerapeuticoRepository.findIds(uidGrupoTerapeutico).get(uidGrupoTerapeutico);

        fichaExistente.setIdResponsavel(valueOrElse(idResponsavel, fichaExistente.getIdResponsavel()));
        fichaExistente.setNome(valueOrElse(fichaDTO.nome(), fichaExistente.getNome()));
        fichaExistente.setIdGrupoTerapeutico(valueOrElse(idGrupoTerapeutico, fichaExistente.getIdGrupoTerapeutico()));

        Ficha fichaAtualizada = fichaRepository.update(fichaExistente);
        return FichaDTO.from(fichaAtualizada, uidGrupoTerapeutico, fichaDTO.idResponsavel());
    }

    private <T> T valueOrElse(T value, T alternative) {
        return (value != null ? value : alternative);
    }

    public FichaDTO getByUid(UUID uid){
        Integer id = fichaRepository.findIds(uid).get(uid);
        Ficha fichaEncontrada = fichaRepository.findById(id);
        UUID idResponsavel = funcionarioRepository.findById(fichaEncontrada.getIdResponsavel()).getUid();
        Integer idGrupoTerapeutico = fichaEncontrada.getIdGrupoTerapeutico();
        UUID uidGrupoTerapeutico = grupoTerapeuticoRepository.findById(idGrupoTerapeutico).getUid();

        return FichaDTO.from(fichaEncontrada, uidGrupoTerapeutico, idResponsavel);
    }

    public List<FichaDTO> getByUids(List<UUID> uids) {
        List<Integer> ids = fichaRepository.findIds(uids).values().stream().toList();
        List<Ficha> fichasEncontradas = fichaRepository.findById(ids);
        if (fichasEncontradas.isEmpty()) {
            throw new EntityNotFoundException("Fichas não encontradas para os IDs: " + ids);
        }

        return fichaRepository.findById(ids).stream()
                .map(ficha -> {
                    UUID uidGrupoTerapeutico = grupoTerapeuticoRepository.findById(ficha.getIdGrupoTerapeutico()).getUid();
                    UUID uidResponsavel = funcionarioRepository.findById(ficha.getIdResponsavel()).getUid();
                    return FichaDTO.from(ficha, uidGrupoTerapeutico, uidResponsavel);
                })
                .toList();
    }

    public List<FichaDTO> getAll(){
        return fichaRepository.findAll().stream().map(ficha -> {
                    UUID uidGrupoTerapeutico = grupoTerapeuticoRepository.findById(ficha.getIdGrupoTerapeutico()).getUid();
                    UUID uidResponsavel = funcionarioRepository.findById(ficha.getIdResponsavel()).getUid();
                    return FichaDTO.from(ficha, uidGrupoTerapeutico, uidResponsavel);
                })
                .toList();
    }

    public List<FichaDTO> getByFuncionario(UUID uidFuncionario){
        Integer idFuncionario = funcionarioRepository.findIds(uidFuncionario).get(uidFuncionario);
        List<Ficha> fichasEncontradas = fichaRepository.findByFuncionario(idFuncionario);

        return fichasEncontradas.stream().map(ficha -> {
            UUID uidGrupoTerapeutico = ficha.getIdGrupoTerapeutico() != null ?
                    grupoTerapeuticoRepository.findById(ficha.getIdGrupoTerapeutico()).getUid() :
                    null;
            UUID uidResponsavel = funcionarioRepository.findById(ficha.getIdResponsavel()).getUid();
            return FichaDTO.from(ficha, uidGrupoTerapeutico, uidResponsavel);
        }).toList();

    }

    public Ficha getFichaByUid(UUID uid) {
        Integer id = fichaRepository.findIds(uid).get(uid);

        if (id == null) return null;

        return fichaRepository.findById(id);
    }

    public List<Ficha> getFichaByUid(List<UUID> uids) {
        BidiMap<UUID, Integer> ids = fichaRepository.findIds(uids);

        if (ids.isEmpty()) return List.of();

        return fichaRepository.findById(ids.values().stream().toList());
    }

    public Boolean deleteFichaByUid(UUID uid) {
        Integer idFicha = fichaRepository.findIds(uid).get(uid);
        Ficha ficha = fichaRepository.findById(idFicha);

        if (ficha == null) {
            throw new EntityNotFoundException("Ficha não encontrada para o UID: " + uid);
        }

        return fichaRepository.delete(idFicha) > 0;
    }

    public boolean deleteFichaByUids(List<UUID> uids) {
        List<Integer> idsFichas = fichaRepository.findIds(uids).values().stream().toList();
        List<Ficha> fichasEncontradas = fichaRepository.findById(idsFichas);

        boolean allDeleted = true;

        for (Integer id : idsFichas) {
            if (fichaRepository.findById(id) == null) {
                throw new EntityNotFoundException("A ficha com ID " + id + " não existe no banco de dados");
            }

            boolean deleted = fichaRepository.delete(id) > 0;

            if (!deleted) {
                allDeleted = false;
            }
        }
        return allDeleted;
    }

}
