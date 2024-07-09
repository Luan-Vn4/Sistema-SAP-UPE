package br.upe.sap.sistemasapupe.data.jdbcutils.updates;

import jakarta.persistence.Column;
import lombok.NonNull;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Usado como argumento em operações do
 * {@link org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate}. Mapeia os valores dos campos
 * de uma instância, conforme os nomes especificados no campo "name" de {@link Column}
 */
public class ColumnParameterSource<T> implements SqlParameterSource {

    // ATRIBUTOS
    private final T entity;

    private final Class<T> targetClass;

    private final HashMap<String, Object> columnsMap;


    // INSTANCIADORES
    /**
     * Cria novas instâncias de {@link ColumnParameterSource} que mapeiam os valores dos campos
     * dos objetos passados, conforme os nomes especificados no campo "name" de {@link Column}
     * da classe daquela objeto
     * @param targetClass classe alvo
     * @param entities objetos da classe alvo dos quais serão extraídos os valores dos campos
     */
    public static <T> ColumnParameterSource<?>[] newInstances(Class<T> targetClass,
                                                              @NonNull List<T> entities) {
        ColumnParameterSource<?>[] result = new ColumnParameterSource[entities.size()];

        for (int i = 0; i < entities.size(); i++) {
            result[i] = newInstance(targetClass, entities.get(i));
        }

        return result;
    }

    /**
     * Cria novas instâncias de {@link ColumnParameterSource} que mapeiam os valores dos campos
     * dos objetos passados, conforme os nomes especificados no campo "name" de {@link Column}
     * da classe daquela objeto
     * @param targetClass classe alvo
     * @param entities objetos da classe alvo dos qual serão extraídos os valores dos campos
     * @param customParameters parâmetros personalizados para serem usados adicionalmente àqueles presentes
     *                         no objeto passada. Caso seja passado o mapeamento para um parâmetro cujo
     *                         nome seja igual a outro definido em algum {@link Column} na classe do
     *                         objeto passado, a prioridade será do parâmetro customizado
     */
    public static <T> ColumnParameterSource<?>[] newInstances(Class<T> targetClass, @NonNull List<T> entities,
                                                              List<Map<String, Object>> customParameters) {

        ColumnParameterSource<?>[] result = new ColumnParameterSource[entities.size()];

        for (int i = 0; i < entities.size(); i++) {
            if (i < customParameters.size()) {
                result[i] = newInstance(targetClass, entities.get(i), customParameters.get(i));
                continue;
            }
            result[i] = newInstance(targetClass, entities.get(i));
        }

        return result;
    }

    /**
     * Cria uma nova instância de {@link ColumnParameterSource} que mapeia os valores dos campos
     * de um objeto passado, conforme os nomes especificados no campo "name" de {@link Column}
     * da classe daquela objeto
     * @param targetClass classe alvo
     * @param entity objeto da classe alvo da qual serão extraídos os valores dos campos
     */
    public static <T> ColumnParameterSource<T> newInstance(Class<T> targetClass, @NonNull T entity) {
        return new ColumnParameterSource<>(targetClass, entity);
    }

    /**
     * Cria uma nova instância de {@link ColumnParameterSource} que mapeia os valores dos campos
     * de um objeto passado, conforme os nomes especificados no campo "name" de {@link Column}
     * da classe daquela objeto
     * @param targetClass classe alvo
     * @param entity objeto da classe alvo da qual serão extraídos os valores dos campos
     * @param customParameters parâmetros personalizados para serem usados adicionalmente àqueles presentes
     *                         no objeto passada. Caso seja passado o mapeamento para um parâmetro cujo
     *                         nome, seja igual a outro definido em algum {@link Column} na classe do
     *                         objeto passado, a prioridade será do parâmetro customizado
     */
    public static <T> ColumnParameterSource<T> newInstance(Class<T> targetClass, @NonNull T entity,
                                                           Map<String, Object> customParameters) {
        return new ColumnParameterSource<>(targetClass, entity, customParameters);
    }


    // CONSTRUTORES
    private ColumnParameterSource(Class<T> targetClass, T entity, Map<String, Object> customParameters) {
        this.targetClass = targetClass;
        this.entity = entity;
        this.columnsMap = mapColumns(customParameters);
    }

    private ColumnParameterSource(Class<T> targetClass, T entity) {
        this.targetClass = targetClass;
        this.entity = entity;
        this.columnsMap = mapColumns(null);
    }

    private HashMap<String, Object> mapColumns(Map<String, Object> customParameters) {
        HashMap<String, Object> columnsMap = (customParameters != null ? new HashMap<>(customParameters) :
                                                                         new HashMap<>());

        for (Field field : targetClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                String columnName = field.getAnnotation(Column.class).name();
                if (!columnsMap.containsKey(columnName)) {
                    Object value = extractValue(field);
                    columnsMap.put(columnName, value);
                }
            }
        }

        return columnsMap;
    }

    private Object extractValue(Field field) {
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }


    // IMPLEMENTAÇÕES

    @Override
    public boolean hasValue(@NonNull  String paramName) {
        return columnsMap.containsKey(paramName);
    }

    @Override
    public Object getValue(@NonNull String paramName) throws IllegalArgumentException {
        if (!columnsMap.containsKey(paramName)) {
            throw new IllegalArgumentException("No value found for: " + paramName);
        }
        return columnsMap.get(paramName);
    }

}
