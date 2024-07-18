package br.upe.sap.sistemasapupe.api.services;

import br.upe.sap.sistemasapupe.api.dtos.CreateGrupoTerapeuticoDTO;
import br.upe.sap.sistemasapupe.api.dtos.GrupoTerapeuticoDTO;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.repositories.jdbi.JdbiFichaRepository;
import br.upe.sap.sistemasapupe.data.repositories.jdbi.JdbiFuncionariosRepository;
import br.upe.sap.sistemasapupe.data.repositories.jdbi.JdbiGrupoTerapeuticoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class GrupoTerapeuticoService {
    JdbiGrupoTerapeuticoRepository grupoTerapeuticoRepository;
    JdbiFuncionariosRepository funcionariosRepository;
    JdbiFichaRepository fichaRepository;


    public GrupoTerapeutico create(GrupoTerapeutico grupoTerapeutico){
        return grupoTerapeuticoRepository.create(grupoTerapeutico);
    }

    public GrupoTerapeuticoDTO convertToDTO(GrupoTerapeutico grupoTerapeutico){
        return new GrupoTerapeuticoDTO(grupoTerapeutico.getId(), grupoTerapeutico.getUid(),
                grupoTerapeutico.getTemaTerapia(), grupoTerapeutico.getCoordenadores(), grupoTerapeutico.getFichas());
    }

    public GrupoTerapeutico convertToGrupoTerapeutico(GrupoTerapeuticoDTO grupoTerapeuticoDTO){
        GrupoTerapeutico grupoTerapeutico = grupoTerapeuticoRepository.findById(grupoTerapeuticoDTO.uid());

        if (grupoTerapeutico == null){
            throw new EntityNotFoundException("Não possível econtrar esse grupo terapêutico");
        }

        return grupoTerapeutico;
    }

    public GrupoTerapeutico convertToGrupoTerapeutico(CreateGrupoTerapeuticoDTO dto){
        return new GrupoTerapeutico(dto.temaTerapia(),
                dto.coordenadores(), dto.fichas());
    }

    public GrupoTerapeuticoDTO update(GrupoTerapeuticoDTO grupoTerapeuticoDTO){
        GrupoTerapeutico update = convertToGrupoTerapeutico(grupoTerapeuticoDTO);
        GrupoTerapeutico grupoAntigo = grupoTerapeuticoRepository.findById(grupoTerapeuticoDTO.uid());

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
        grupoTerapeuticoRepository.delete(grupoTerapeutico.getUid());

        return grupoTerapeutico.getId() > 0;
    }
}
