package br.upe.sap.sistemasapupe.api.services;

import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.grupo.GrupoTerapeuticoDTO;
import br.upe.sap.sistemasapupe.api.dtos.paciente.FichaDTO;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.repositories.jdbi.JdbiFichaRepository;
import br.upe.sap.sistemasapupe.data.repositories.jdbi.JdbiFuncionariosRepository;
import br.upe.sap.sistemasapupe.data.repositories.jdbi.JdbiGrupoTerapeuticoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.BidiMap;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class GrupoTerapeuticoService {
    JdbiGrupoTerapeuticoRepository grupoTerapeuticoRepository;
    JdbiFuncionariosRepository funcionariosRepository;
    JdbiFichaRepository fichaRepository;


    public GrupoTerapeuticoDTO convertToDTO(GrupoTerapeutico grupoTerapeutico){
        List<UUID> funcionarios = grupoTerapeutico.getCoordenadores().stream()
                .map(Funcionario::getUid).toList();

        List<UUID> fichas = grupoTerapeutico.getFichas().stream()
                .map(Ficha::getUid).toList();

        return new GrupoTerapeuticoDTO(grupoTerapeutico.getUid(), grupoTerapeutico.getTemaTerapia(),
                funcionarios, fichas);
    }

    public GrupoTerapeutico convertToGrupoTerapeutico(GrupoTerapeuticoDTO dto){
        List<Integer> ids = funcionariosRepository.findIds(
                dto.coordenadores().stream().toList()).values().stream().toList();

        List<Funcionario> funcionarios = funcionariosRepository.findById(ids);



        // preciso dos funcionarios para criar um novo grupo terapeutico
        GrupoTerapeutico grupoTerapeutico = new GrupoTerapeutico(dto.tema(),funcionarios, );

        return grupoTerapeutico;
    }

    public GrupoTerapeutico create(GrupoTerapeuticoDTO dto){
        GrupoTerapeutico grupoTerapeutico = convertToGrupoTerapeutico(dto);
        return grupoTerapeuticoRepository.create(grupoTerapeutico);
    }

    public GrupoTerapeuticoDTO updateTema(GrupoTerapeuticoDTO grupoTerapeuticoDTO){
        GrupoTerapeutico update = convertToGrupoTerapeutico(grupoTerapeuticoDTO);
        GrupoTerapeutico grupoAntigo = grupoTerapeuticoRepository.findById(update.getId());

        if (grupoAntigo == null){
            throw new EntityNotFoundException("Não foi possível encontrar esse grupo terapêutico");
        }

        grupoAntigo.setTemaTerapia(update.getTemaTerapia());
        grupoAntigo.setCoordenadores(update.getCoordenadores());
        grupoAntigo.setFichas(update.getFichas());

        grupoTerapeuticoRepository.update(grupoAntigo);
        return GrupoTerapeuticoDTO.from(grupoAntigo);
    }

    public boolean deleteGrupoTerapeutico(GrupoTerapeuticoDTO dto){
        GrupoTerapeutico grupoTerapeutico = convertToGrupoTerapeutico(dto);
        grupoTerapeuticoRepository.delete(grupoTerapeutico.getId());

        return grupoTerapeutico.getId() != 0;
    }
}
