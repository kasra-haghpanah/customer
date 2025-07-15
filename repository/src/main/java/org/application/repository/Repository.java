package org.application.repository;

import org.application.framework.JdbcBuilder;
import org.application.model.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.BiFunction;

public abstract class Repository<ID, M extends Model> {

    final Connection connection;

    public Repository(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public int getCustomerSequence() {
        return getSequence("SELECT NEXT VALUE FOR application.customer_sequence");
    }

    public int getFileSequence() {
        return getSequence("SELECT NEXT VALUE FOR application.file_sequence");
    }

    private int getSequence(String sql) {

        return (Integer) JdbcBuilder
                .connection(this.connection)
                .setCatch((sqlConnection, exception) -> {
                    System.out.println(exception.toString());
//                    try {
//                        sqlConnection.rollback();
//                    } catch (SQLException e) {
//                        throw new RuntimeException(e);
//                    }
                })
                .sql(sql)
                .<Integer>cast((BiFunction<Connection, ? super PreparedStatement, Integer>) (connection, statement) -> {

                    try {
                        ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            return resultSet.getInt(1);
                        }

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return 0;

                });

    }

    public abstract M add(M model);

    public abstract M update(M model);

    public abstract boolean deleteByIds(List<ID> ids);

    public abstract boolean deleteByIds(ID... ids);

    public abstract List<M> get(M model);

}
