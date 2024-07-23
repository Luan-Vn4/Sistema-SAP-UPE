package br.upe.sap.sistemasapupe.api.dtos.atividades.geral;

import br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentogrupo.AtendimentoGrupoDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentoindividual.AtendimentoIndividualDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.encontro.EncontroDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public record AnyAtividadeDTO (List<AtendimentoIndividualDTO> atendimentosIndividuais,
                               List<AtendimentoGrupoDTO> atendimentosGrupo,
                               List<EncontroDTO> encontros) {

    public static AnyAtividadeDTOBuilder builder() {
        return new AnyAtividadeDTO.AnyAtividadeDTOBuilder();
    }

    public static AtividadeDTOCollector collector() {
        return new AtividadeDTOCollector();
    }

    public boolean isEmpty() {
        return atendimentosIndividuais.isEmpty()
               && atendimentosGrupo.isEmpty()
               && encontros.isEmpty();
    }

    public static class AnyAtividadeDTOBuilder {

        private final List<AtendimentoIndividualDTO> atdIndividuais = new ArrayList<>();

        private final List<AtendimentoGrupoDTO> atdGrupo = new ArrayList<>();

        private final List<EncontroDTO> encontros = new ArrayList<>();

        public AnyAtividadeDTOBuilder addAtendimentoIndividual(AtendimentoIndividualDTO dto) {
            atdIndividuais.add(dto);
            return this;
        }

        public AnyAtividadeDTOBuilder addAtendimentoGrupo(AtendimentoGrupoDTO dto) {
            atdGrupo.add(dto);
            return this;
        }

        public AnyAtividadeDTOBuilder addEncontro(EncontroDTO dto) {
            encontros.add(dto);
            return this;
        }

        public AnyAtividadeDTO build() {
            return new AnyAtividadeDTO(atdIndividuais, atdGrupo, encontros);
        }



    }

    public static class AtividadeDTOCollector implements
            Collector<AtividadeDTO, AnyAtividadeDTOBuilder, AnyAtividadeDTO> {

        @Override
        public Supplier<AnyAtividadeDTOBuilder> supplier() {
            return AnyAtividadeDTO::builder;
        }

        @Override
        public BiConsumer<AnyAtividadeDTOBuilder, AtividadeDTO> accumulator() {
            return (accumulator, dto) -> {
                if (dto instanceof AtendimentoIndividualDTO atdIndividualDto) {
                    accumulator.addAtendimentoIndividual(atdIndividualDto);
                } else if (dto instanceof AtendimentoGrupoDTO atdGrupoDto) {
                    accumulator.addAtendimentoGrupo(atdGrupoDto);
                } else if (dto instanceof EncontroDTO encontroDto) {
                    accumulator.addEncontro(encontroDto);
                }
                throw new ClassCastException("O tipo de AtividadeDTO passado " +
                                             "não é suportado: " + dto.getClass());
            };
        }

        @Override
        public BinaryOperator<AnyAtividadeDTOBuilder> combiner() {
            return (builder1, builder2) -> {
                builder1.atdIndividuais.addAll(builder2.atdIndividuais);
                builder1.atdGrupo.addAll(builder2.atdGrupo);
                builder1.encontros.addAll(builder2.encontros);
                return builder1;
            };
        }

        @Override
        public Function<AnyAtividadeDTOBuilder, AnyAtividadeDTO> finisher() {
            return AnyAtividadeDTOBuilder::build;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Set.of(Characteristics.UNORDERED);
        }

    }

}
