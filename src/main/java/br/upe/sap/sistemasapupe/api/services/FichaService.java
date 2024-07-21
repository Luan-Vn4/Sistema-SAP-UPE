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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FichaService {
    private final GrupoTerapeuticoRepository grupoTerapeuticoRepository;
    FichaRepository fichaRepository;
    FuncionarioRepository funcionarioRepository;

    public FichaDTO createFicha (CreateFichaDTO fichaDTO){
        Ficha ficha = CreateFichaDTO.fromDTO(fichaDTO);
        Ficha fichaCriada = fichaRepository.create(ficha);
        return FichaDTO.from(fichaCriada);
    }

    public FichaDTO updateFicha (UpdateFichaDTO fichaDTO) {
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
        return FichaDTO.from(fichaAtualizada);
    }

    private <T> T valueOrElse(T value, T alternative) {
        return (value != null ? value : alternative);
    }

    public FichaDTO getFichaByUid (UUID uid){
        Integer id = fichaRepository.findIds(uid).get(uid);
        Ficha fichaEncontrada = fichaRepository.findById(id);

        if (fichaEncontrada == null){
            throw new EntityNotFoundException("Ficha não encontrada para o UID: " + uid);
        }

        return FichaDTO.from(fichaEncontrada);
    }

    public List<FichaDTO> getFichaByUids (List<UUID> uids) {
        List<Integer> ids = fichaRepository.findIds(uids).values().stream().toList();
        List<Ficha> fichasEncontradas = fichaRepository.findById(ids);
        if (fichasEncontradas.isEmpty()) {
            throw new EntityNotFoundException("Fichas não encontradas para os IDs: " + ids);
        }

        return fichasEncontradas.stream().map(FichaDTO::from).collect(Collectors.toList());
    }

    public List<FichaDTO> getAll(){
        return fichaRepository.findAll().stream().map(FichaDTO::from).collect(Collectors.toList());
    }
    public List<FichaDTO> getFichaByFuncionario(UUID uidFuncionario){
        Integer idFuncionario = funcionarioRepository.findIds(uidFuncionario).get(uidFuncionario);
        List<Ficha> fichasEncontradas = fichaRepository.findByFuncionario(idFuncionario);

        if (fichasEncontradas.isEmpty()) {
            throw new EntityNotFoundException("Fichas não encontradas para o UID do funcionario: " + uidFuncionario);
        }
        return fichasEncontradas.stream().map(FichaDTO::from).collect(Collectors.toList());
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
