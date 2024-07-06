package br.upe.sap.sistemasapupe.data.jdbcutils.updates;

import br.upe.sap.sistemasapupe.data.jdbcutils.queries.ColumnRowMapper;
import lombok.NonNull;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.util.function.Supplier;

public class ColumnResultSetExtractor<T> implements ResultSetExtractor {

    private final Class<T> targetClass;

    private Supplier<T> constructor;

    public ColumnResultSetExtractor(Class<T> targetClass, Supplier<T> constructor) {
        this.targetClass = targetClass;
    }

    /**
     * Implementations must implement this method to process the entire ResultSet.
     *
     * @param rs the ResultSet to extract data from. Implementations should
     *           not close this: it will be closed by the calling JdbcTemplate.
     * @return an arbitrary result object, or {@code null} if none
     * (the extractor will typically be stateful in the latter case).
     * @throws DataAccessException in case of custom exceptions
     */
    @Override
    public T extractData(@NonNull ResultSet rs) throws DataAccessException {
        return ColumnRowMapper.newInstance(targetClass, constructor).mapRow(rs, 1);
    }
}
