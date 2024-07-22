package br.upe.sap.sistemasapupe.api.services;

import br.upe.sap.sistemasapupe.api.dtos.atividades.sala.CreateSalaDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.sala.SalaDTO;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.TipoSala;

import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.sala.SalaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.BidiMap;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SalaService {
    SalaRepository salaRepository;

    public SalaDTO create(CreateSalaDTO salaDTO) {
        Sala sala = CreateSalaDTO.fromDTO(salaDTO);
        Sala salaCriada = salaRepository.create(sala);
        return SalaDTO.from(salaCriada);
    }

    public SalaDTO update(SalaDTO salaDTO) {
        Sala sala = SalaDTO.from(salaDTO);
        Sala salaExistente = salaRepository.findById(salaRepository.findIds(salaDTO.uid()).get(salaDTO.uid()));
        if (salaExistente == null) {
            throw new EntityNotFoundException("Sala não encontrada para o ID: " + salaDTO.uid());
        }

        salaExistente.setTipoSala(salaDTO.tipoSala());
        salaExistente.setNome(salaDTO.nome());

        Sala salaAtualizada = salaRepository.update(salaExistente);
        return SalaDTO.from(salaAtualizada);
    }

    public SalaDTO getByUid(UUID uid) {
        Integer id = salaRepository.findIds(uid).get(uid);
        Sala salaEncontrada = salaRepository.findById(id);

        if (salaEncontrada == null) {
            throw new EntityNotFoundException("Sala não encontrada para o ID: " + uid);
        }
        return SalaDTO.from(salaEncontrada);
    }

    public List<SalaDTO> getByUids(List<UUID> uids) {
        List<Integer> ids = salaRepository.findIds(uids).values().stream().toList();
        List<Sala> salasEncontradas = salaRepository.findById(ids);
        if (salasEncontradas.isEmpty()) {
            throw new EntityNotFoundException("Salas não encontradas para os IDs: " + ids);
        }

        return salasEncontradas.stream().map(SalaDTO::from).collect(Collectors.toList());

    }

    public List<SalaDTO> getByTipo(TipoSala tipoSala) {
        List<Sala> salasEncontrada = salaRepository.findByTipo(tipoSala);

        return salasEncontrada.stream()
                .map(SalaDTO::from)
                .toList();
    }

    public SalaDTO getByNome(String nome) {
        Sala salaEncontrada = salaRepository.findByNome(nome);
        SalaDTO salaDTO = SalaDTO.from(salaEncontrada);

        return salaDTO;
    }

    public List<SalaDTO> getAll(){
        return salaRepository.findAll().stream().map(SalaDTO::from).collect(Collectors.toList());
    }

    public Sala getSalaByUid(UUID uid) {
        Integer id = salaRepository.findIds(uid).get(uid);

        if (id == null) return null;

        return salaRepository.findById(id);
    }

    public List<Sala> getSalaByUid(List<UUID> uids) {
        BidiMap<UUID, Integer> ids = salaRepository.findIds(uids);

        return salaRepository.findById(ids.values().stream().toList());
    }

    public Boolean deleteByUid(UUID uid) {
        Integer idSala = salaRepository.findIds(uid).get(uid);
        Sala sala = salaRepository.findById(idSala);

        if (sala == null) {
            throw new EntityNotFoundException("Sala não encontrada para o ID: " + uid);
        }

        return salaRepository.delete(idSala) > 0;
    }

    public boolean deleteByUids(List<UUID> uids) {
        List<Integer> idsSalas = salaRepository.findIds(uids).values().stream().toList();
        List<Sala> salasEncontradas = salaRepository.findById(idsSalas);

        boolean allDeleted = true;
        for (Integer id : idsSalas) {
            if (salaRepository.findById(id) == null) {
                throw new EntityNotFoundException("O post com ID " + id + " não existe no banco de dados");
            }
            boolean deleted = salaRepository.delete(id) > 0;
            if (!deleted) {
                allDeleted = false;
            }
        }
        return allDeleted;
    }

    






}
