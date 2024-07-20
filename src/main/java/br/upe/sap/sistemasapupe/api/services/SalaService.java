package br.upe.sap.sistemasapupe.api.services;

import br.upe.sap.sistemasapupe.api.dtos.atividades.SalaDTO;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.TipoSala;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.sala.SalaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SalaService {

    SalaRepository salaRepository;

    private List<SalaDTO> mapToSalaDTO(List<Sala> salas){
        return salas.stream().map(SalaDTO::from).toList();}

    public SalaDTO createSala (UUID uidSala){return null;}

    public SalaDTO updateInfo (UUID uidSala){ return null;}

    public SalaDTO removeSala (UUID uidSala){return null;}

    public List<SalaDTO> getAll(){
        return mapToSalaDTO(salaRepository.findAll());
    }

    public List<SalaDTO> getByUids(List<UUID> uidsSalas) {
        //return mapToSalaDTO();
        return null;
    }

    public SalaDTO getByUid(UUID uidSala){
        return null;
    }

    public SalaDTO getByNome(String nome) {
        //return mapToSalaDTO(salaRepository.findByNome(nome));
        return null;
    }

    public Sala getByTipo(TipoSala tipoSala){return null;}



    






}
