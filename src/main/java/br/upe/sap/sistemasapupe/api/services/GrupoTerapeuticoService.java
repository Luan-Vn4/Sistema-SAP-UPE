package br.upe.sap.sistemasapupe.api.services;

import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.grupo.CreateGrupoTerapeuticoDTO;
import br.upe.sap.sistemasapupe.api.dtos.grupo.GrupoTerapeuticoDTO;
import br.upe.sap.sistemasapupe.api.dtos.paciente.FichaDTO;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FichaRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.GrupoTerapeuticoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Service
@AllArgsConstructor
public class GrupoTerapeuticoService {

    private FichaService fichaService;

    private GrupoTerapeuticoRepository grupoTerapeuticoRepository;

    private FuncionarioRepository funcionarioRepository;

    private FuncionarioService funcionarioService;

    private FichaRepository fichaRepository;

    public GrupoTerapeuticoDTO create(CreateGrupoTerapeuticoDTO grupo) {
        Integer idDono = funcionarioRepository.findIds(grupo.idDono()).get(grupo.idDono());
        GrupoTerapeutico grupoTerapeutico = CreateGrupoTerapeuticoDTO.toGrupo(grupo, idDono);
        GrupoTerapeutico grupoCriado = grupoTerapeuticoRepository.create(grupoTerapeutico);
        addFuncionario(grupo.idDono(), grupoCriado.getUid());

        return GrupoTerapeuticoDTO.from(grupoCriado, funcionarioRepository.findById(
                grupoCriado.getIdDono()).getUid());
    }

    public GrupoTerapeuticoDTO update(GrupoTerapeuticoDTO grupoAtualizado){
        Integer idGrupo = grupoTerapeuticoRepository.findIds(grupoAtualizado.uid()).get(grupoAtualizado.uid());
        Integer idDono = funcionarioRepository.findIds(grupoAtualizado.idDono()).get(grupoAtualizado.idDono());

        GrupoTerapeutico novoGrupo = GrupoTerapeuticoDTO.convertToGrupo(grupoAtualizado, idDono);
        GrupoTerapeutico grupoExistente = grupoTerapeuticoRepository.findById(novoGrupo.getId());

        if (grupoExistente == null){
            throw new EntityNotFoundException(
                    "Não foi encontrado um grupo terapêutico com esse id: " + novoGrupo.getId());
        }

        grupoExistente.setTema(novoGrupo.getTema());
        grupoExistente.setDescricao(novoGrupo.getDescricao());
        grupoExistente.setIdDono(novoGrupo.getIdDono());

        grupoTerapeuticoRepository.update(grupoExistente);
        return GrupoTerapeuticoDTO.from(grupoExistente, funcionarioRepository.findById(
                grupoExistente.getIdDono()).getUid());
    }

    public GrupoTerapeuticoDTO getById(UUID idGrupo){
        GrupoTerapeutico grupo = grupoTerapeuticoRepository.findById(getId(idGrupo));

        if (idGrupo == null) return null;

        return GrupoTerapeuticoDTO.from(grupo,funcionarioRepository.findById(
                grupo.getIdDono()).getUid());
    }

    public GrupoTerapeutico getById(int id){

        return grupoTerapeuticoRepository.findById(id);
    }

    public List<GrupoTerapeuticoDTO> getAll(){
        List<GrupoTerapeutico> grupos = grupoTerapeuticoRepository.findAll();
        List<GrupoTerapeuticoDTO> dtos = new ArrayList<>();

        for (GrupoTerapeutico grupo : grupos){
            dtos.add(GrupoTerapeuticoDTO.from(grupo, funcionarioRepository.findById(
                    grupo.getIdDono()).getUid()));
        }

        return dtos;
    }

    public List<GrupoTerapeuticoDTO> getByFuncionario(UUID uidFuncionario){
        Integer idFuncionario = funcionarioRepository.findIds(uidFuncionario).get(uidFuncionario);
        List<GrupoTerapeutico> grupos = grupoTerapeuticoRepository.findByFuncionario(idFuncionario);
        List<GrupoTerapeuticoDTO> gruposDtos = new ArrayList<>();

        for (GrupoTerapeutico grupo : grupos){
            gruposDtos.add(GrupoTerapeuticoDTO.from(grupo,
                    funcionarioRepository.findById(grupo.getIdDono()).getUid()));
        }

        return gruposDtos;
    }

    public GrupoTerapeuticoDTO getByFicha(UUID uidFicha){
        Integer idFicha = fichaRepository.findIds(uidFicha).get(uidFicha);

        if(idFicha == null){
            throw new EntityNotFoundException("Não foi possível encontrar uma ficha com o UUID: " +
                    uidFicha);
        }

         GrupoTerapeutico grupo = grupoTerapeuticoRepository.findByFicha(idFicha);
        return GrupoTerapeuticoDTO.from(grupo,
                funcionarioRepository.findById(grupo.getIdDono()).getUid());
    }

    public FuncionarioDTO addFuncionario(UUID uidFuncionario, UUID uidGrupo){
        Funcionario funcionario = funcionarioService.getFuncionarioByUid(uidFuncionario);
        GrupoTerapeutico grupo = getGrupoTerapeuticoByUid(uidGrupo);

        if (funcionario == null){
            throw new EntityNotFoundException("Não foi possível encontrar um funcionario com esse UUID: "
                    + uidFuncionario);
        }
        if (grupo == null){
            throw new EntityNotFoundException("Não foi possível encontrar um grupo terapêutico com esse UUID "
                    + uidGrupo);
        }

        grupoTerapeuticoRepository.addFuncionario(funcionario.getId(), grupo.getId());

        return FuncionarioDTO.from(funcionario);
    }

    public List<FuncionarioDTO> addFuncionario(List<UUID> uidsFuncionarios, UUID uidGrupo){
        List<Funcionario> funcionarios = funcionarioService.getFuncionarioByUid(uidsFuncionarios);
        GrupoTerapeutico grupo = getGrupoTerapeuticoByUid(uidGrupo);

        if (funcionarios.size() != uidsFuncionarios.size()) {
            throw new EntityNotFoundException("Não foi possível encontrar todos os funcionários. " +
                                              "Verifique se os UIDs corretos foram informados");
        }

        if (grupo == null) {
            throw new EntityNotFoundException("Não foi possível encontrar um grupo com esse UUID " + uidGrupo);
        }

        List<Integer> idsFuncionarios = funcionarios.stream()
            .map(Funcionario::getId)
            .toList();
        grupoTerapeuticoRepository.addFuncionario(idsFuncionarios, grupo.getId());

        return funcionarios.stream()
            .map(FuncionarioDTO::from)
            .toList();
    }

    public FichaDTO addFicha(UUID uidFicha, UUID uidGrupo) {
        Ficha ficha = fichaService.getFichaByUid(uidFicha);
        GrupoTerapeutico grupo = getGrupoTerapeuticoByUid(uidGrupo);

        if (ficha == null) {
            throw new EntityNotFoundException("Não foi possível encontrar uma ficha com esse " +
                                              "UID: " + uidFicha);
        }
        if (grupo == null) {
            throw new EntityNotFoundException("Não foi possível encontrar um grupo terapêutico com " +
                                              "o UID " + uidGrupo);
        }

        grupoTerapeuticoRepository.addFicha(ficha.getId(), grupo.getId());

        return fichaService.getByUid(ficha.getUid());
    }

    public List<FichaDTO> addFicha(List<UUID> uidsFicha, UUID uidGrupo){
        List<Ficha> fichas = fichaService.getFichaByUid(uidsFicha);
        GrupoTerapeutico grupo = getGrupoTerapeuticoByUid(uidGrupo);

        if (fichas.size() != uidsFicha.size()) {
            throw new EntityNotFoundException("Não foi possível encontrar alguma das fichas passadas. " +
                                              "Verifique se os UIDs corretos foram informados");
        }

        if (grupo == null){
            throw new EntityNotFoundException("Não foi possível encontrar esse grupo terapêutico");
        }

        List<Integer> idsFichas = fichas.stream()
            .map(Ficha::getId)
            .toList();
        grupoTerapeuticoRepository.addFicha(idsFichas, grupo.getId());

        return fichaService.getByUids(fichas.stream().map(Ficha::getUid).toList());
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

        return GrupoTerapeuticoDTO.from(grupoTerapeutico,
                funcionarioRepository.findById(grupoTerapeutico.getIdDono()).getUid());
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
        return GrupoTerapeuticoDTO.from(grupoTerapeutico,
                funcionarioRepository.findById(grupoTerapeutico.getIdDono()).getUid());
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
        int funcionario = funcionarioRepository.findIds(uidParticipante).get(uidParticipante);
        List<Integer> resultadoDB = grupoTerapeuticoRepository.findGruposTerapeuticosNaoParticipadosPor(funcionario);
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
