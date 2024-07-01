package br.upe.sap.sistemasapupe.data.jdbcutils.queries;

import jakarta.persistence.Column;
import org.springframework.jdbc.core.RowMapper;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * Mapeia os resultados de uma consulta definindo os campos de acordo com suas respectivas colunas. Para usar
 * este mapeador com sua classe, os campos desejados devem ser anotados com {@link Column} e ter "name"
 * especificado.
 */
public class ColumnRowMapper<T> implements RowMapper<T> {

    Supplier<T> constructor;

    Class<T> targetClass;

    private ColumnRowMapper(Class<T> targetClass, Supplier<T> constructor) {
        this.targetClass = targetClass;
        this.constructor = constructor;
    }

    public static <T> ColumnRowMapper<T> newInstance(Class<T> targetClass, Supplier<T> constructor) {
        return new ColumnRowMapper<>(targetClass, constructor);
    }

    /**
     * Mapeia os resultados de uma consulta definindo os campos de acordo com suas respectivas colunas.
     * Para usar este método com sua classe, os campos desejados devem ser anotados com {@link Column} e ter
     * "name" especificado.
     * @param rs o {@code ResultSet} para mapear (pré-inicializado para a linha atual)
     * @param rowNum o número da linha atual
     * @throws SQLException se uma SQLException for encontrada ao obter os valores das colunas (ou seja, não
     *                      há necessidade de capturar SQLException)
     * @throws IllegalArgumentException se o campo não puder ser acessado
     */
    @Override
    @SuppressWarnings("RedundantThrows")
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        T instance = constructor.get();

        for (Field field : targetClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                String columnName = field.getAnnotation(Column.class).name();
                try {
                    field.setAccessible(true);
                    field.set(instance, rs.getObject(columnName));
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                } catch (SQLException ignored) {}
            }
        }

        return instance;
    }

}
