package br.upe.sap.sistemasapupe.data.jdbcutils.updates;

import org.postgresql.util.PSQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Realiza operações sobre um {@link PreparedStatement}. Usualmente, é utilizado por operações
 * execute do {@link org.springframework.jdbc.core.JdbcTemplate}. Basicamente, ele irá receber o
 * {@link PreparedStatement} gerado internamente no método execute a query e depois injetar as chaves geradas
 * pela query nas entidades passadas, conforme o atributo "name" da anotação
 * {@link jakarta.persistence.Column} que deve estar nos atributos nos quais se deseja injetar as chaves.
 * <br><br>
 * <b>IMPORTANTE</b>: A implementação do {@link PreparedStatement} passada deve implementar o toString() de maneira
 * que retorne a query SQL. Caso contrário, será lançado um {@link IllegalArgumentException}
 */
class UpdateThenInjectGeneratedKeys<T> implements PreparedStatementCallback<Integer> {

    private final String[] generatedKeys;

    private final Object[] entities;

    UpdateThenInjectGeneratedKeys(T entity, String... generatedKeys) {
        this.entities = new Object[]{entity};
        this.generatedKeys = generatedKeys;
    }

    UpdateThenInjectGeneratedKeys(T[] entities, String... generatedKeys) {
        this.entities = entities;
        this.generatedKeys = generatedKeys;
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
        int affectedRows = ps.executeUpdate(ps.toString(), PreparedStatement.RETURN_GENERATED_KEYS);

        List<Map<String, Object>> keyMap = new LinkedList<>();
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder(keyMap);

        try (ResultSet rs = ps.getGeneratedKeys()) {
            while (rs.next()) {
                Map<String, Object> keyMapRow = new HashMap<>();
                for (String key : generatedKeys) keyMapRow.put(key, rs.getObject(key));
                keyMap.add(keyMapRow);
            }
        }

        UpdateUtils.injectKeys(keyHolder, entities);

        return affectedRows;
    }

}
