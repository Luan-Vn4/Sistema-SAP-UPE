package br.upe.sap.sistemasapupe.api.services;

import br.upe.sap.sistemasapupe.api.dtos.grupo.GrupoTerapeuticoDTO;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.repositories.jdbi.JdbiFichaRepository;
import br.upe.sap.sistemasapupe.data.repositories.jdbi.JdbiFuncionariosRepository;
import br.upe.sap.sistemasapupe.data.repositories.jdbi.JdbiGrupoTerapeuticoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
                grupoTerapeutico.getDescricao(), funcionarios, fichas);
    }

    public GrupoTerapeutico convertToGrupoTerapeutico(GrupoTerapeuticoDTO dto){
        List<Integer> ids_funcionario = funcionariosRepository.findIds(
                dto.coordenadores().stream().toList()).values().stream().toList();
        List<Funcionario> funcionarios = funcionariosRepository.findById(ids_funcionario);

        List<Integer> ids_ficha = fichaRepository.findIds(
                dto.fichas().stream().toList()).values().stream().toList();
        List<Ficha> fichas = fichaRepository.findById(ids_ficha);

        return new GrupoTerapeutico(dto.tema(),dto.descricao(), funcionarios,fichas);
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

    public GrupoTerapeuticoDTO addFuncionario(UUID uidFuncionario, UUID uidGrupo){
        Integer id_funcionario = funcionariosRepository.findIds(uidFuncionario).get(uidFuncionario);
        Integer id_grupo = grupoTerapeuticoRepository.findIds(uidGrupo).get(uidGrupo);

        if (id_funcionario == null){
            throw new EntityNotFoundException("Não foi possível encontrar esse funcionario");
        }
        if (id_grupo == null){
            throw new EntityNotFoundException("Não foi possível encontrar esse grupo terapêutico");
        }

        GrupoTerapeutico grupoAtualizado = grupoTerapeuticoRepository.findById(id_grupo);
        Funcionario funcionario = funcionariosRepository.findById(id_funcionario);

        grupoAtualizado.getCoordenadores().add(funcionario);

        grupoTerapeuticoRepository.addFuncionario(id_funcionario, id_grupo);
        return GrupoTerapeuticoDTO.from(grupoAtualizado);
    }

    public GrupoTerapeuticoDTO addFuncionario(List<UUID> uidsFuncionarios, UUID uidGrupo){
        List<Integer> idsFuncionarios = funcionariosRepository.findIds(uidsFuncionarios)
                                        .values().stream().toList();
        Integer id_grupo = grupoTerapeuticoRepository.findIds(uidGrupo).get(uidGrupo);

        if (id_grupo == null){
            throw new EntityNotFoundException("Não foi possível encontrar esse grupo terapêutico");
        }

        List<Funcionario> funcionarios = funcionariosRepository.findById(idsFuncionarios);
        GrupoTerapeutico grupoTerapeutico = grupoTerapeuticoRepository.findById(id_grupo);
        grupoTerapeutico.getCoordenadores().addAll(funcionarios);

        grupoTerapeuticoRepository.addFuncionario(idsFuncionarios, id_grupo);
        return GrupoTerapeuticoDTO.from(grupoTerapeutico);
    }

    public GrupoTerapeuticoDTO addFicha(UUID uidFicha, UUID uidGrupo){
        Integer id_ficha = fichaRepository.findIds(uidFicha).get(uidFicha);
        Integer id_grupo = grupoTerapeuticoRepository.findIds(uidGrupo).get(uidGrupo);

        if (id_ficha == null){
            throw new EntityNotFoundException("Não foi possível encontrar essa ficha");
        }
        if (id_grupo == null){
            throw new EntityNotFoundException("Não foi possível encontrar esse grupo terapêutico");
        }

        Ficha ficha = fichaRepository.findById(id_ficha);
        GrupoTerapeutico grupoAtualizado = grupoTerapeuticoRepository.findById(id_grupo);

        grupoAtualizado.getFichas().add(ficha);

        return GrupoTerapeuticoDTO.from(grupoAtualizado);
    }

    public GrupoTerapeuticoDTO addFicha(List<UUID> uidsFicha, UUID uidGrupo){
        List<Integer> idsFichas = fichaRepository.findIds(uidsFicha)
                                    .values().stream().toList();
        Integer idGrupo = grupoTerapeuticoRepository.findIds(uidGrupo).get(uidGrupo);

        if (idGrupo == null){
            throw new EntityNotFoundException("Não foi possível encontrar esse grupo terapêutico");
        }

        List<Ficha> fichas = fichaRepository.findById(idsFichas);
        GrupoTerapeutico grupoTerapeutico = grupoTerapeuticoRepository.findById(idGrupo);

        grupoTerapeutico.getFichas().addAll(fichas);
        return GrupoTerapeuticoDTO.from(grupoTerapeutico);
    }

    public boolean deleteGrupoTerapeutico(GrupoTerapeuticoDTO dto){
        GrupoTerapeutico grupoTerapeutico = convertToGrupoTerapeutico(dto);
        grupoTerapeuticoRepository.delete(grupoTerapeutico.getId());

        return grupoTerapeutico.getId() != 0;
    }

}
