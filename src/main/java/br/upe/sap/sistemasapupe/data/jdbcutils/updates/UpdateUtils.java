package br.upe.sap.sistemasapupe.data.jdbcutils.updates;

import jakarta.persistence.Column;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.support.KeyHolder;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

public class UpdateUtils {

    /**
     * Realiza operações sobre um {@link PreparedStatement}. Usualmente, é utilizado por operações
     * execute do {@link org.springframework.jdbc.core.JdbcTemplate}. Basicamente, ele irá receber o
     * {@link PreparedStatement} gerado internamente no método execute a query e depois injetar as chaves
     * geradas pela query nas entidades passadas, conforme o atributo "name" da anotação
     * {@link jakarta.persistence.Column} que deve estar nos atributos nos quais se deseja injetar as chaves.
     * <br><br>
     * <b>IMPORTANTE</b>: caso você deseje usar fora do método "execute" do
     * {@link org.springframework.jdbc.core.JdbcTemplate}, a implementação do {@link PreparedStatement}
     * passada deve implementar o toString() de maneira que retorne a query SQL
     * @param entities Entidades nas quais deseja injetar as chaves geradas
     * @param keys Nomes correspondentes às colunas-chave cujos valores serão gerados
     * @return Quantidade de linhas alteradas
     * @throws IllegalArgumentException se a implementação do {@link PreparedStatement} fornecida não
     *                                  implementar um "toString" que retorne a query SQL
     */
    public static <T> PreparedStatementCallback<Integer> updateThenInjectGeneratedKeys(
            T[] entities, String... keys) {
        return new UpdateThenInjectGeneratedKeys<>(entities, keys);
    }

    /**
     * Realiza operações sobre um {@link PreparedStatement}. Usualmente, é utilizado por operações
     * execute do {@link org.springframework.jdbc.core.JdbcTemplate}. Basicamente, ele irá receber o
     * {@link PreparedStatement} gerado internamente no método execute a query e depois injetar as chaves
     * geradas pela query na entidade passada, conforme o atributo "name" da anotação
     * {@link jakarta.persistence.Column} que deve estar nos atributos nos quais se deseja injetar as chaves.
     * <br><br>
     * <b>IMPORTANTE</b>: caso você deseje usar fora do método "execute" do
     * {@link org.springframework.jdbc.core.JdbcTemplate}, a implementação do {@link PreparedStatement}
     * passada deve implementar o toString() de maneira que retorne a query SQL
     * @param entity Entidades nas quais deseja injetar as chaves geradas
     * @param keys Nomes correspondentes às colunas-chave cujos valores serão gerados
     * @return Quantidade de linhas alteradas
     * @throws IllegalArgumentException se a implementação do {@link PreparedStatement} fornecida não
     *                                  implementar um "toString" que retorne a query SQL
     */
    public static <T> PreparedStatementCallback<Integer> updateThenInjectGeneratedKeys(
            T entity, String... keys) {
        return new UpdateThenInjectGeneratedKeys<>(entity, keys);
    }


    /**
     * Mapeia as chaves presentes em um {@link KeyHolder} para os atributos das entidades passadas.
     * Os atributos das entidades cujos valores serão injetados deverão possuir a anotação {@link Column} com
     * o campo "name" especificado e correspondente ao nome da coluna que representa.
     * @param keyHolder portador das chaves que serão injetadas nas entidades
     * @param entities entidades nas quais serão injetadas os valores gerados pelas chaves
     */
    @SafeVarargs
    public static <T> void injectKeys(KeyHolder keyHolder, T... entities) {
        Class<?> targetClass = entities[0].getClass();
        List<Map<String, Object>> mapList = keyHolder.getKeyList();

        for (int i = 0; i < entities.length; i++) {
            Map<String, Object> mapping = mapList.get(i);
            T entity = entities[i];

            for (String key : mapList.get(i).keySet()) {
                for (Field field : targetClass.getDeclaredFields()) {
                    if (verifyThenInsertKey(field, key, entity, mapping)) break;
                }
            }
        }
    }


    /**
     * Mapeia as chaves presentes em um {@link KeyHolder} para os atributos das entidades passadas.
     * Os atributos das entidades cujos valores serão injetados deverão possuir a anotação {@link Column} com
     * o campo "name" especificado e correspondente ao nome da coluna que representa.
     * @param keyHolder portador das chaves que serão injetadas nas entidades
     * @param entities entidades nas quais serão injetadas os valores gerados pelas chaves
     */
    public static <T> void injectKeys(KeyHolder keyHolder, List<T> entities) {
        Class<?> targetClass = entities.get(0).getClass();
        List<Map<String, Object>> mapList = keyHolder.getKeyList();

        for (int i = 0; i < entities.size(); i++) {
            Map<String, Object> mapping = mapList.get(i);
            T entity = entities.get(i);

            for (String key : mapList.get(i).keySet()) {
                for (Field field : targetClass.getDeclaredFields()) {
                    if (verifyThenInsertKey(field, key, entity, mapping)) break;
                }
            }
        }
    }

    private static <T> boolean verifyThenInsertKey(Field field, String key, T entity,
                                               Map<String, Object> mapping) {
        if (field.isAnnotationPresent(Column.class)
                && field.getAnnotation(Column.class).name().equals(key)) {
            try {
                field.setAccessible(true);
                field.set(entity, mapping.get(key));
                return true;
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException();
            }
        }
        return false;
    }

}
