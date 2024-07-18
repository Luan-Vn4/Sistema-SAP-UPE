package br.upe.sap.sistemasapupe.api.services;

import br.upe.sap.sistemasapupe.api.dtos.atividades.SalaDTO;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.SalaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SalaService {
    SalaRepository salaRepository;

    public List<SalaDTO> getAll(){
        return null;
    }

    public List<SalaDTO> getByStatus(){
        return null;
    }

    public List<SalaDTO> getByUids(){
        return null;
    }

    public SalaDTO getByUid(){
        return null;
    }

    public List<SalaDTO> getByNome(){return null;}

    public List<SalaDTO> getByTipo(){return null;}



    






}
