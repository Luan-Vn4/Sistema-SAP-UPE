package br.upe.sap.sistemasapupe.api.services;

import br.upe.sap.sistemasapupe.api.dtos.grupo.CreateGrupoTerapeuticoDTO;
import br.upe.sap.sistemasapupe.api.dtos.grupo.GrupoTerapeuticoDTO;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.GrupoTerapeuticoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
public class GrupoTerapeuticoService {

    GrupoTerapeuticoRepository grupoTerapeuticoRepository;
    FuncionarioService funcionarioService;
    FichaService fichaService;

    public GrupoTerapeuticoDTO mapToDTO(GrupoTerapeutico grupoTerapeutico) {
        UUID uidDono = funcionarioService.getFuncionarioById(grupoTerapeutico.getIdDono()).getUid();

        return GrupoTerapeuticoDTO.from(grupoTerapeutico, uidDono);
    }

    public GrupoTerapeuticoDTO create(CreateGrupoTerapeuticoDTO grupo) {
        Funcionario funcionario = funcionarioService.getFuncionarioByUid(grupo.idDono());
        GrupoTerapeutico grupoTerapeutico = CreateGrupoTerapeuticoDTO.toGrupo(grupo, funcionario.getId());
        GrupoTerapeutico grupoCriado = grupoTerapeuticoRepository.create(grupoTerapeutico);
        grupoTerapeuticoRepository.addFuncionario(funcionario.getId(), grupoCriado.getId());

        return GrupoTerapeuticoDTO.from(grupoCriado, funcionario.getUid());
    }

    public GrupoTerapeuticoDTO update(GrupoTerapeuticoDTO grupoTerapeuticoDTO) {
        GrupoTerapeutico grupoTerapeutico = getGrupoTerapeuticoByUid(grupoTerapeuticoDTO.uid());
        Funcionario funcionario = funcionarioService.getFuncionarioByUid(grupoTerapeuticoDTO.uid());

        GrupoTerapeutico grupoExistente = grupoTerapeuticoRepository.findById(grupoTerapeutico.getId());

        if (grupoExistente == null){
            throw new EntityNotFoundException(
                    "Não foi encontrado um grupo terapêutico com esse uid: " + grupoExistente.getUid());
        }

        grupoExistente.setTema(grupoExistente.getTema());
        grupoExistente.setDescricao(grupoExistente.getDescricao());
        grupoExistente.setIdDono(grupoExistente.getIdDono());

        GrupoTerapeutico grupoAtualizado = grupoTerapeuticoRepository.update(grupoExistente);
        return GrupoTerapeuticoDTO.from(grupoAtualizado, funcionario.getUid());
    }

    public GrupoTerapeuticoDTO getById(UUID idGrupo){
        GrupoTerapeutico grupo = grupoTerapeuticoRepository.findById(getId(idGrupo));
        Funcionario funcionario = funcionarioService.getFuncionarioByUid(grupo.getUid());

        if (idGrupo == null) return null;

        return GrupoTerapeuticoDTO.from(grupo,funcionario.getUid());
    }

    public GrupoTerapeutico getById(int id){
        return grupoTerapeuticoRepository.findById(id);
    }

    public List<GrupoTerapeuticoDTO> getAll(){
        List<GrupoTerapeutico> grupos = grupoTerapeuticoRepository.findAll();
        List<GrupoTerapeuticoDTO> dtos = new ArrayList<>();

        for (GrupoTerapeutico grupo : grupos){
            Funcionario funcionario = funcionarioService.getFuncionarioByUid(grupo.getUid());
            dtos.add(GrupoTerapeuticoDTO.from(grupo, funcionario.getUid()));
        }

        return dtos;
    }

    public List<GrupoTerapeuticoDTO> getByFuncionario(UUID uidFuncionario){
        Funcionario funcionario = funcionarioService.getFuncionarioByUid(uidFuncionario);
        List<GrupoTerapeutico> grupos = grupoTerapeuticoRepository.findByFuncionario(funcionario.getId());
        List<GrupoTerapeuticoDTO> gruposDtos = new ArrayList<>();

        for (GrupoTerapeutico grupo : grupos){
            gruposDtos.add(GrupoTerapeuticoDTO.from(grupo,
                    funcionario.getUid()));
        }

        return gruposDtos;
    }

    public GrupoTerapeuticoDTO getByFicha(UUID uidFicha){
        Ficha ficha = fichaService.getFichaByUid(uidFicha);

        if(ficha.getId() == null){
            throw new EntityNotFoundException("Não foi possível encontrar uma ficha com o UUID: " +
                    uidFicha);
        }

        GrupoTerapeutico grupo = grupoTerapeuticoRepository.findByFicha(ficha.getId());
        Funcionario funcionario = funcionarioService.getFuncionarioByUid(grupo.getUid());
        return GrupoTerapeuticoDTO.from(grupo,
                funcionario.getUid());
    }

    public GrupoTerapeuticoDTO addFuncionario(UUID uidFuncionario, UUID uidGrupo){
        Funcionario funcionario = funcionarioService.getFuncionarioByUid(uidGrupo);
        Integer id_grupo = grupoTerapeuticoRepository.findIds(uidGrupo).get(uidGrupo);

        if (funcionario.getId() == null){
            throw new EntityNotFoundException("Não foi possível encontrar um funcionario com esse UUID: "
                    + uidFuncionario);
        }
        if (id_grupo == null){
            throw new EntityNotFoundException("Não foi possível encontrar um grupo terapêutico com esse UUID "
                    + uidGrupo);
        }

        GrupoTerapeutico grupo = grupoTerapeuticoRepository.findById(id_grupo);

        return GrupoTerapeuticoDTO.from(grupo, funcionario.getUid());
    }

    public GrupoTerapeuticoDTO addFuncionario(List<UUID> uidsFuncionarios, UUID uidGrupo){
        List<Integer> idsFuncionarios = funcionarioService.getFuncionarioByUid(uidsFuncionarios).stream()
        .map(Funcionario::getId).toList();
        GrupoTerapeutico grupoTerapeutico = getGrupoTerapeuticoByUid(uidGrupo);

        for (Integer id: idsFuncionarios){
            if (id == null){
                throw new EntityNotFoundException("Não foi possível encontrar uma ficha com esse UUID ");
            }
        }

        if (grupoTerapeutico.getUid() == null){
            throw new EntityNotFoundException("Não foi possível encontrar um grupo com esse UUID " + uidGrupo);
        }

        grupoTerapeuticoRepository.addFuncionario(idsFuncionarios, grupoTerapeutico.getId());
        return GrupoTerapeuticoDTO.from(grupoTerapeutico, funcionarioService.getFuncionarioByUid(uidGrupo).getUid());
    }

    public GrupoTerapeuticoDTO addFicha(UUID uidFicha, UUID uidGrupo){
        Ficha ficha = fichaService.getFichaByUid(uidFicha);
        Integer id_grupo = grupoTerapeuticoRepository.findIds(uidGrupo).get(uidGrupo);

        if (ficha.getId() == null){
            throw new EntityNotFoundException("Não foi possível encontrar essa ficha");
        }
        if (id_grupo == null){
            throw new EntityNotFoundException("Não foi possível encontrar esse grupo terapêutico");
        }

        grupoTerapeuticoRepository.addFicha(ficha.getId(), id_grupo);

        GrupoTerapeutico grupoAtualizado = grupoTerapeuticoRepository.findById(id_grupo);

        return GrupoTerapeuticoDTO.from(grupoAtualizado, funcionarioService.getFuncionarioByUid(uidGrupo).getUid());
    }

    public GrupoTerapeuticoDTO addFicha(List<UUID> uidsFicha, UUID uidGrupo){
        List<Integer> idsFichas = fichaService.getFichaByUid(uidsFicha).stream().map(Ficha::getId).toList();
        GrupoTerapeutico grupoTerapeutico = getGrupoTerapeuticoByUid(uidGrupo);

        if (grupoTerapeutico.getId() == null){
            throw new EntityNotFoundException("Não foi possível encontrar esse grupo terapêutico");
        }

        grupoTerapeuticoRepository.addFicha(idsFichas, grupoTerapeutico.getId());
        return GrupoTerapeuticoDTO.from(grupoTerapeutico,
                funcionarioService.getFuncionarioByUid(uidGrupo).getUid());
    }

    public GrupoTerapeuticoDTO removeFuncionario(UUID uidFuncionario, UUID uidGrupo){
        Funcionario funcionario = funcionarioService.getFuncionarioByUid(uidFuncionario);
        GrupoTerapeutico grupoTerapeutico = getGrupoTerapeuticoByUid(uidGrupo);

        if (funcionario.getId() == null){
            throw new EntityNotFoundException("Não foi possível encontrar esse funcionario");
        }
        if (grupoTerapeutico.getId() == null){
            throw new EntityNotFoundException("Não foi possível encontrar esse grupo terapêutico");
        }

        grupoTerapeuticoRepository.removerFuncionario(funcionario.getId(), grupoTerapeutico.getId());

        return GrupoTerapeuticoDTO.from(grupoTerapeutico, funcionario.getUid());
    }

    public GrupoTerapeuticoDTO removerFicha(UUID uidFicha, UUID uidGrupo){
        Ficha ficha = fichaService.getFichaByUid(uidFicha);
        GrupoTerapeutico grupoTerapeutico = getGrupoTerapeuticoByUid(uidGrupo);

        if (grupoTerapeutico.getId() == null){
            throw new EntityNotFoundException("Não foi possível encontrar esse grupo terapêutico");
        }
        if (ficha.getId() == null){
            throw new EntityNotFoundException("Não foi possível encontrar esse grupo terapêutico");
        }

        grupoTerapeuticoRepository.removerFicha(ficha.getId());
        return GrupoTerapeuticoDTO.from(grupoTerapeutico, funcionarioService.getFuncionarioByUid(uidGrupo).getUid());
    }

    public void deleteGrupoTerapeutico(UUID uidGrupo){
        Integer idGrupo = grupoTerapeuticoRepository.findIds(uidGrupo).get(uidGrupo);
        GrupoTerapeutico grupo = grupoTerapeuticoRepository.findById(idGrupo);
        if (grupo == null){
            throw new EntityNotFoundException("Não foi possivel encontrar um grupo terapêutico com esse UUID");
        }
        grupoTerapeuticoRepository.delete(idGrupo);
    }

    public int getId(UUID uid){
        return grupoTerapeuticoRepository.findIds(uid).get(uid);
    }

    public GrupoTerapeutico getGrupoTerapeuticoByUid(UUID uidGrupo){
        Integer id = grupoTerapeuticoRepository.findIds(uidGrupo).get(uidGrupo);

        if (id == null) return null;

        return grupoTerapeuticoRepository.findById(id);
    }

    public List<GrupoTerapeuticoDTO> getGruposNaoParticipados(UUID uidParticipante){
        Funcionario funcionario = funcionarioService.getFuncionarioByUid(uidParticipante);
        List<Integer> resultadoDB = grupoTerapeuticoRepository.findGruposTerapeuticosNaoParticipadosPor(funcionario.getId());
        return resultadoDB.stream()
                .map(this::getById)
                .filter(Objects::nonNull)
                .map(grupo -> {
                    UUID uidDono = getById(grupo.getUid()).idDono();
                    return GrupoTerapeuticoDTO.from(grupo, uidDono);
                })
                .toList();
    }

}
