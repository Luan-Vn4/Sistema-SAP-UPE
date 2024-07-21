package br.upe.sap.sistemasapupe.api.services;

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

@Service
@AllArgsConstructor
public class GrupoEstudoService {

    FuncionarioRepository funcionarioRepository;
    GrupoEstudoRepository grupoEstudoRepository;

    public GrupoEstudoDTO create(CreateGrupoEstudoDTO grupoEstudoDTO) {
        GrupoEstudo grupoEstudo = CreateGrupoEstudoDTO.to(grupoEstudoDTO);
        GrupoEstudo grupoCriado = grupoEstudoRepository.create(grupoEstudo);
        return GrupoEstudoDTO.from(grupoCriado);
    }

    public GrupoEstudoDTO update(GrupoEstudoDTO grupoEstudoDTO) {
        GrupoEstudo gupoExistente = grupoEstudoRepository
                .findById(grupoEstudoRepository.findIds(grupoEstudoDTO.uid()).get(grupoEstudoDTO.uid()));
        if (gupoExistente == null) {
            throw new EntityNotFoundException("Grupo de estudos não encontrado para o id " + grupoEstudoDTO.uid());
        }

        gupoExistente.setDescricao(grupoEstudoDTO.descricao());
        gupoExistente.setDono(grupoEstudoDTO.dono());
        gupoExistente.setTema(grupoEstudoDTO.tema());
        GrupoEstudo grupoAtualizado = grupoEstudoRepository.update(gupoExistente);

        return GrupoEstudoDTO.from(grupoAtualizado);
    }

    public GrupoEstudoDTO getById(UUID uid) throws EntityNotFoundException {
        int id = grupoEstudoRepository.findIds(uid).get(uid);
        GrupoEstudo grupoEncontrado = grupoEstudoRepository.findById(id);
        return GrupoEstudoDTO.from(grupoEncontrado);
    }

    public List<GrupoEstudoDTO> getAll() {
        return grupoEstudoRepository.findAll().stream().map(GrupoEstudoDTO::from).toList();
    }


}
