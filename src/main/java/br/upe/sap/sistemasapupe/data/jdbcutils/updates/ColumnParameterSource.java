package br.upe.sap.sistemasapupe.data.jdbcutils.updates;

import jakarta.persistence.Column;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

/**
 * Usado como argumento em operações do
 * {@link org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate}. Mapeia os valores dos campos
 * de uma instância, conforme os nomes especificados no campo "name" de {@link Column}
 */
public class ColumnParameterSource<T> implements SqlParameterSource {

    T entity;

    Class<T> targetClass;

    HashMap<String, Object> columnsMap;


    public static <T> ColumnParameterSource<?>[] newInstances(Class<T> targetClass, List<T> entities) {
        ColumnParameterSource<?>[] result = new ColumnParameterSource[entities.size()];

        for (int i = 0; i < entities.size(); i++) {
            result[i] = newInstance(targetClass, entities.get(i));
        }

        return result;
    }

    public static <T> ColumnParameterSource<T> newInstance(Class<T> targetClass, T entity) {
        return new ColumnParameterSource<>(targetClass, entity);
    }

    private ColumnParameterSource(Class<T> targetClass, T entity) {
        this.targetClass = targetClass;
        this.entity = entity;
        this.columnsMap = mapColumns();
    }


    private HashMap<String, Object> mapColumns() {
        HashMap<String, Object> columnsMap = new HashMap<>();

        for (Field field : targetClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                String columnName = field.getAnnotation(Column.class).name();
                Object value = extractValue(field);
                columnsMap.put(columnName, value);
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

    @Override
    public boolean hasValue(String paramName) {
        return columnsMap.containsKey(paramName);
    }

    @Override
    public Object getValue(String paramName) throws IllegalArgumentException {
        if (!columnsMap.containsKey(paramName)) {
            throw new IllegalArgumentException("No value found for: " + paramName);
        }
        return columnsMap.get(paramName);
    }

}
