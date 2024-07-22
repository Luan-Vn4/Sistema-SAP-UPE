package br.upe.sap.sistemasapupe.api.services;

import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.grupo.CreateGrupoEstudoDTO;
import br.upe.sap.sistemasapupe.api.dtos.grupo.GrupoEstudoDTO;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoEstudo;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.GrupoEstudoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GrupoEstudoService {

    FuncionarioRepository funcionarioRepository;
    GrupoEstudoRepository grupoEstudoRepository;

    public GrupoEstudoDTO create(CreateGrupoEstudoDTO grupoEstudoDTO) {
        int idDono = funcionarioRepository.findIds(grupoEstudoDTO.dono()).get(grupoEstudoDTO.dono());
        GrupoEstudo grupoEstudo = CreateGrupoEstudoDTO.to(grupoEstudoDTO, idDono);
        GrupoEstudo grupoCriado = grupoEstudoRepository.create(grupoEstudo);
        addFuncionario(grupoEstudoDTO.dono(), grupoCriado.getUid());
        return GrupoEstudoDTO.from(grupoCriado, grupoEstudoDTO.dono());
    }

    public GrupoEstudoDTO update(GrupoEstudoDTO grupoEstudoDTO) {
        GrupoEstudo gupoExistente = grupoEstudoRepository
                .findById(grupoEstudoRepository.findIds(grupoEstudoDTO.id()).get(grupoEstudoDTO.id()));
        if (gupoExistente == null) {
            throw new EntityNotFoundException("Grupo de estudos não encontrado para o id " + grupoEstudoDTO.id());
        }

        int idDono = funcionarioRepository.findIds(grupoEstudoDTO.dono()).get(grupoEstudoDTO.dono());

        gupoExistente.setDescricao(grupoEstudoDTO.descricao());
        gupoExistente.setDono(idDono);
        gupoExistente.setTema(grupoEstudoDTO.tema());
        GrupoEstudo grupoAtualizado = grupoEstudoRepository.update(gupoExistente);

        return GrupoEstudoDTO.from(grupoAtualizado, grupoEstudoDTO.dono());
    }

    public GrupoEstudoDTO getById(UUID uid) throws EntityNotFoundException {
        int id = grupoEstudoRepository.findIds(uid).get(uid);
        GrupoEstudo grupoEncontrado = grupoEstudoRepository.findById(id);
        return GrupoEstudoDTO.from(grupoEncontrado, uid);
    }

    public List<GrupoEstudoDTO> getAll() {
        return grupoEstudoRepository.findAll().stream()
                .map(grupoEstudo -> {
                    UUID donoUUID = funcionarioRepository.findById(grupoEstudo.getDono()).getUid();
                    return GrupoEstudoDTO.from(grupoEstudo, donoUUID);
                })
                .toList();
    }

    public List<GrupoEstudoDTO> getByIds(List<UUID> uids) {
        List<Integer> ids = grupoEstudoRepository.findIds(uids).values().stream().toList();
        List<GrupoEstudo> gruposEncontrados = grupoEstudoRepository.findById(ids);
        if (gruposEncontrados.isEmpty()) {
            throw new EntityNotFoundException("Grupos não encontrados para os ids " + ids);
        }
        return grupoEstudoRepository.findById(ids).stream()
                .map(grupoEstudo -> {
                    UUID donoUUID = funcionarioRepository.findById(grupoEstudo.getDono()).getUid();
                    return GrupoEstudoDTO.from(grupoEstudo, donoUUID);
                })
                .toList();
    }

    public Boolean deleteById(UUID uid) {
        Integer id = grupoEstudoRepository.findIds(uid).get(uid);
        if (grupoEstudoRepository.findById(id) == null) {
            throw new EntityNotFoundException("Grupo não encontrado para o id " + id);
        }
        grupoEstudoRepository.delete(id);
        return true;
    }

    public Boolean deleteManyByIds(List<UUID> uids) {
        List<Integer> ids = grupoEstudoRepository.findIds(uids).values().stream().toList();
        if (grupoEstudoRepository.findById(ids) == null) {
            throw new EntityNotFoundException("Grupos não encontrados para os ids " + ids);
        }
        return grupoEstudoRepository.delete(ids) > 0;
    }

    public List<GrupoEstudoDTO> getByFuncionarioId(UUID uid) {
        int id = funcionarioRepository.findIds(uid).get(uid);
        return grupoEstudoRepository.findByFuncionario(id).stream()
                .map(grupoEstudo -> {
                    UUID donoUUID = funcionarioRepository.findById(grupoEstudo.getDono()).getUid();
                    return GrupoEstudoDTO.from(grupoEstudo, donoUUID);
                })
                .toList();
    }

    public FuncionarioDTO addFuncionario(UUID uid, UUID uidGrupo) {
        int id = funcionarioRepository.findIds(uid).get(uid);
        int idGrupo = grupoEstudoRepository.findIds(uidGrupo).get(uidGrupo);
        grupoEstudoRepository.addFuncionario(id, idGrupo);
        if (funcionarioRepository.findById(id) == null){
            throw new EntityNotFoundException("Funcionario não encontrado");
        }
        if (grupoEstudoRepository.findByFuncionario(id) == null){
            throw new RuntimeException("Erro ao adicionar funcionario");
        }
        return FuncionarioDTO.from(funcionarioRepository.findById(id));
    }

    public Boolean deletedParticipacao(UUID uidParticipante, UUID uidGrupoEstudo) {
        int idParticipante = funcionarioRepository.findIds(uidParticipante).get(uidParticipante);
        int idGrupoEstudo = grupoEstudoRepository.findIds(uidGrupoEstudo).get(uidGrupoEstudo);
        if (funcionarioRepository.findById(idParticipante) == null){
            throw new EntityNotFoundException("Funcionario não encontrado");
        }
        return grupoEstudoRepository.deleteParticipacao(idParticipante, idGrupoEstudo) > 0;
    }

    public List<UUID> getParticipantesByGrupoEsudo(UUID idGrupoEstudo){
        int id = grupoEstudoRepository.findIds(idGrupoEstudo).get(idGrupoEstudo);
        List<Integer> resultadoBD = grupoEstudoRepository.findParticipantesByGrupoEstudo(id);
        return resultadoBD.stream()
                .map(participanteId -> funcionarioRepository.findById(participanteId).getUid())
                .collect(Collectors.toList());
    }

}
