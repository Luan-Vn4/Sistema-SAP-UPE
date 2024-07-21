package br.upe.sap.sistemasapupe.api.services;

import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.grupo.GrupoTerapeuticoDTO;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FichaRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.GrupoTerapeuticoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class GrupoTerapeuticoService {

    GrupoTerapeuticoRepository grupoTerapeuticoRepository;
    FuncionarioRepository funcionarioRepository;
    FichaRepository fichaRepository;

    public GrupoTerapeuticoDTO convertToDTO(GrupoTerapeutico grupo){
        UUID uidAuto = funcionarioRepository.findById(grupo.getIdDono()).getUid();

        return new GrupoTerapeuticoDTO(grupo.getUid(), grupo.getTema(), grupo.getDescricao(), uidAuto);
    }

    public GrupoTerapeutico convertToGrupo(GrupoTerapeuticoDTO dto){
        Integer idDono = funcionarioRepository.findIds(dto.idDono()).get(dto.idDono());
        Integer idGrupo = grupoTerapeuticoRepository.findIds(dto.uid()).get(dto.uid());

        return new GrupoTerapeutico(idGrupo, dto.uid(), dto.tema(), dto.descricao(), idDono);
    }

    public GrupoTerapeuticoDTO create(GrupoTerapeutico grupo){
        grupo = grupoTerapeuticoRepository.create(grupo);
        return convertToDTO(grupo);
    }

    public List<GrupoTerapeuticoDTO> create(List<GrupoTerapeutico> grupos){
        grupos = grupoTerapeuticoRepository.create(grupos);

        List<GrupoTerapeuticoDTO> dtos = new ArrayList<>();
        for (GrupoTerapeutico grupo: grupos){
            dtos.add(convertToDTO(grupo));
        }
        return dtos;
    }

    public GrupoTerapeuticoDTO update(GrupoTerapeuticoDTO grupoAtualizado){
        GrupoTerapeutico novoGrupo = convertToGrupo(grupoAtualizado);
        GrupoTerapeutico grupoExistente = grupoTerapeuticoRepository.findById(novoGrupo.getId());

        if (grupoExistente == null){
            throw new EntityNotFoundException(
                    "Não foi encontrado um grupo terapêutico com esse id: " + novoGrupo.getId());
        }

        grupoExistente.setTema(novoGrupo.getTema());
        grupoExistente.setDescricao(novoGrupo.getDescricao());
        grupoExistente.setIdDono(novoGrupo.getIdDono());

        grupoTerapeuticoRepository.update(grupoExistente);
        return GrupoTerapeuticoDTO.from(grupoExistente, funcionarioRepository);
    }

    public List<GrupoTerapeuticoDTO> findByFuncionario(FuncionarioDTO dto) {
        Integer idFuncionario = funcionarioRepository.findIds(dto.id()).get(dto.id());
        List<GrupoTerapeutico> grupos = grupoTerapeuticoRepository.findByFuncionario(idFuncionario);
        List<GrupoTerapeuticoDTO> gruposDtos = new ArrayList<>();

        for (GrupoTerapeutico grupo : grupos){
            gruposDtos.add(convertToDTO(grupo));
        }

        return gruposDtos;
    }

    public GrupoTerapeuticoDTO addFuncionario(UUID uidFuncionario, UUID uidGrupo){
        Integer id_funcionario = funcionarioRepository.findIds(uidFuncionario).get(uidFuncionario);
        Integer id_grupo = grupoTerapeuticoRepository.findIds(uidGrupo).get(uidGrupo);

        if (id_funcionario == null){
            throw new EntityNotFoundException("Não foi possível encontrar um funcionario com esse UUID: "
                    + uidFuncionario);
        }
        if (id_grupo == null){
            throw new EntityNotFoundException("Não foi possível encontrar um grupo terapêutico com esse UUID "
                    + uidGrupo);
        }

        return GrupoTerapeuticoDTO.from(grupoTerapeuticoRepository.findById(id_grupo), funcionarioRepository);
    }

    public GrupoTerapeuticoDTO addFuncionario(List<UUID> uidsFuncionarios, UUID uidGrupo){
        List<Integer> idsFuncionarios = funcionarioRepository.findIds(uidsFuncionarios)
                .values().stream().toList();
        Integer id_grupo = grupoTerapeuticoRepository.findIds(uidGrupo).get(uidGrupo);

        for (Integer id: idsFuncionarios){
            if (id == null){
                throw new EntityNotFoundException("Não foi possível encontrar uma ficha com esse UUID ");
            }
        }

        if (id_grupo == null){
            throw new EntityNotFoundException("Não foi possível encontrar um grupo com esse UUID " + uidGrupo);
        }

        grupoTerapeuticoRepository.addFuncionario(idsFuncionarios, id_grupo);
        GrupoTerapeutico grupoTerapeutico = grupoTerapeuticoRepository.findById(id_grupo);
        return GrupoTerapeuticoDTO.from(grupoTerapeutico, funcionarioRepository);
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

        grupoTerapeuticoRepository.addFicha(id_ficha, id_grupo);

        GrupoTerapeutico grupoAtualizado = grupoTerapeuticoRepository.findById(id_grupo);

        return GrupoTerapeuticoDTO.from(grupoAtualizado, funcionarioRepository);
    }

    public GrupoTerapeuticoDTO addFicha(List<UUID> uidsFicha, UUID uidGrupo){
        List<Integer> idsFichas = fichaRepository.findIds(uidsFicha)
                .values().stream().toList();
        Integer idGrupo = grupoTerapeuticoRepository.findIds(uidGrupo).get(uidGrupo);

        if (idGrupo == null){
            throw new EntityNotFoundException("Não foi possível encontrar esse grupo terapêutico");
        }

        grupoTerapeuticoRepository.addFicha(idsFichas, idGrupo);
        GrupoTerapeutico grupoTerapeutico = grupoTerapeuticoRepository.findById(idGrupo);

        return GrupoTerapeuticoDTO.from(grupoTerapeutico, funcionarioRepository);
    }

    public GrupoTerapeuticoDTO removeFuncionario(UUID uidFuncionario, UUID uidGrupo){
        Integer idFuncionario = funcionarioRepository.findIds(uidFuncionario).get(uidFuncionario);
        Integer idGrupo = grupoTerapeuticoRepository.findIds(uidGrupo).get(uidGrupo);

        if (idFuncionario == null){
            throw new EntityNotFoundException("Não foi possível encontrar esse funcionario");
        }
        if (idGrupo == null){
            throw new EntityNotFoundException("Não foi possível encontrar esse grupo terapêutico");
        }

        GrupoTerapeutico grupoTerapeutico = grupoTerapeuticoRepository.findById(idGrupo);
        grupoTerapeuticoRepository.removerFuncionario(idFuncionario, idGrupo);

        return GrupoTerapeuticoDTO.from(grupoTerapeutico, funcionarioRepository);
    }

    public GrupoTerapeuticoDTO removerFicha(UUID uidFicha, UUID uidGrupo){
        Integer idFicha = fichaRepository.findIds(uidFicha).get(uidFicha);
        Integer idGrupo = grupoTerapeuticoRepository.findIds(uidGrupo).get(uidGrupo);

        if (idGrupo == null){
            throw new EntityNotFoundException("Não foi possível encontrar esse grupo terapêutico");
        }
        if (idFicha == null){
            throw new EntityNotFoundException("Não foi possível encontrar esse grupo terapêutico");
        }

        GrupoTerapeutico grupoTerapeutico = grupoTerapeuticoRepository.findById(idGrupo);

        grupoTerapeuticoRepository.removerFicha(idFicha);
        return GrupoTerapeuticoDTO.from(grupoTerapeutico, funcionarioRepository);
    }

    public boolean deleteGrupoTerapeutico(GrupoTerapeuticoDTO dto){
        GrupoTerapeutico grupoTerapeutico = convertToGrupo(dto);
        grupoTerapeuticoRepository.delete(grupoTerapeutico.getId());

        return grupoTerapeutico.getId() != 0;
    }
}
