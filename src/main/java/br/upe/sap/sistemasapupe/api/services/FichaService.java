package br.upe.sap.sistemasapupe.api.services;


import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.grupo.GrupoTerapeuticoDTO;
import br.upe.sap.sistemasapupe.api.dtos.paciente.FichaDTO;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FichaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FichaService {
    FichaRepository fichaRepository;

    private List<FichaDTO> mapToFichaDTO(List<Ficha> fichas){
        return fichas.stream().map(FichaDTO::from).toList();}

    public FichaDTO createFicha (UUID uidFicha){return null;}

    public FichaDTO updateFicha (UUID uidFicha) {return null;}

    public FichaDTO removeFicha (UUID uidFicha) {return null;}

    public List<FichaDTO> getAll(){return null;};

    public List<FichaDTO> getByUids (List<UUID> uidsFichas){return null;}

    public FichaDTO getByUid (UUID uidFicha) { return null;}

    public FichaDTO getByNome (String nomeFicha){return null;}

    public FichaDTO getByGrupoTerapeutico (GrupoTerapeuticoDTO grupoTerapeutico){
        return null;
    }

    public List<FichaDTO> getByFuncionario (FuncionarioDTO responsavel){ return null;}

}
