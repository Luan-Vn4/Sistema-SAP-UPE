package br.upe.sap.sistemasapupe.data.jdbcutils.queries;

import br.upe.sap.sistemasapupe.data.jdbcutils.ReflectionsUtils;
import jakarta.persistence.Column;
import lombok.NonNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Mapeia os resultados de uma consulta definindo os campos de acordo com suas respectivas colunas. Para usar
 * este mapeador com sua classe, os campos desejados devem ser anotados com {@link Column} e ter "name"
 * especificado.
 */
public class ColumnRowMapper<T> implements RowMapper<T> {

    // ATRIBUTOS
    Supplier<T> constructor;

    Class<T> targetClass;

    Map<String, Function<Object, Object>> mappings;


    // CONSTRUTORES
    private ColumnRowMapper(Class<T> targetClass, Supplier<T> constructor) {
        this(targetClass, constructor, Map.of());
    }

    private ColumnRowMapper(Class<T> targetClass, Supplier<T> constructor,
                            Map<String, Function<Object, Object>> mappings) {
        this.targetClass = targetClass;
        this.constructor = constructor;
        this.mappings = mappings;
    }

    public static <T> ColumnRowMapper<T> newInstance(Class<T> targetClass, Supplier<T> constructor) {
        return new ColumnRowMapper<>(targetClass, constructor);
    }

    public static <T> ColumnRowMapper<T> newInstance(Class<T> targetClass, Supplier<T> constructor,
                                                     Map<String, Function<Object, Object>> mappings) {
        return new ColumnRowMapper<>(targetClass, constructor, mappings);
    }


    //IMPLEMENTAÇÕES
    /**
     * Mapeia os resultados de uma consulta definindo os campos de acordo com suas respectivas colunas.
     * Para usar este método com sua classe, os campos desejados devem ser anotados com {@link Column} e ter
     * "name" especificado.
     * @param rs o {@code ResultSet} para mapear (pré-inicializado para a linha atual)
     * @param rowNum o número da linha atual
     * @throws IllegalArgumentException se o campo não puder ser acessado
     */
    @Override
    @SuppressWarnings("RedundantThrows")
    public T mapRow(@NonNull ResultSet rs, int rowNum) {
        T instance = constructor.get();
        List<Class<?>> targetAscendants = ReflectionsUtils.getAllAscendants(targetClass);

        for (Class<?> clazz : targetAscendants) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Column.class)) {
                    String columnName = field.getAnnotation(Column.class).name();
                    try {
                        mapField(rs, instance, columnName, field, mappings);
                    } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException(e);
                    } catch (SQLException ignored) {}
                }
            }
        }

        return instance;
    }

    private void mapField(ResultSet rowSet, T entity, String columnName, Field field,
                          Map<String, Function<Object, Object>> mappings)
                          throws SQLException, IllegalAccessException {
        field.setAccessible(true);
        if (mappings.containsKey(columnName)) {
            Function<Object, ?> function = mappings.get(columnName);
            field.set(entity, function.apply(rowSet.getObject(columnName)));
            return;
        }
        field.set(entity, rowSet.getObject(columnName));
    }



    public List<T> mapRowSet(SqlRowSet rowSet, boolean restart) {
        List<T> results = new ArrayList<>();
        List<Class<?>> targetAscendants = ReflectionsUtils.getAllAscendants(targetClass);

        if (restart) rowSet.beforeFirst();
        while (rowSet.next()) {
            T instance = constructor.get();

            for (Class<?> clazz : targetAscendants) {
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Column.class)) {
                        String columnName = field.getAnnotation(Column.class).name();
                        try {
                            mapField(rowSet, instance, columnName, field, mappings);
                        } catch (IllegalAccessException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                }
            }
            results.add(instance);
        }

        return results;
    }

    private void mapField(SqlRowSet rowSet, T entity, String columnName, Field field,
                          Map<String, Function<Object, Object>> mappings) throws IllegalAccessException {
        field.setAccessible(true);
        if (mappings.containsKey(columnName)) {
            Function<Object, ?> function = mappings.get(columnName);
            field.set(entity, function.apply(rowSet.getObject(columnName)));
            return;
        }
        field.set(entity, rowSet.getObject(columnName));
    }

}
