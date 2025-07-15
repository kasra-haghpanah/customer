package org.application.framework;

import java.math.BigDecimal;
import java.sql.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class JdbcBuilder {


    public static ExceptionHandler connection(Connection connection) {
        return new ExceptionHandler(connection);
    }

    public static class ExceptionHandler {

        final Connection connection;

        public ExceptionHandler(Connection connection) {
            this.connection = connection;
        }

        public SqlRunnerBuilder setCatch(BiConsumer<Connection, Exception> exceptionConsumer) {
            return new SqlRunnerBuilder(this.connection, exceptionConsumer);
        }

        public static class SqlRunnerBuilder {

            final Connection connection;
            final BiConsumer<Connection, Exception> exceptionConsumer;

            public SqlRunnerBuilder(Connection connection, BiConsumer<Connection, Exception> exceptionConsumer) {
                this.connection = connection;
                this.exceptionConsumer = exceptionConsumer;
            }

            public SqlRunner<CallableStatement> procedure(String query) {
                CallableStatement statement = null;
                try {
                    statement = this.connection.prepareCall(query);
                } catch (SQLException e) {
                    this.exceptionConsumer.accept(this.connection, e);
                }
                SqlRunner<CallableStatement> sqlRunner = new SqlRunner<CallableStatement>(connection, query, statement, this.exceptionConsumer);
                return sqlRunner;
            }

            public SqlRunner<PreparedStatement> sql(String query) {
                PreparedStatement statement = null;
                try {
                    statement = this.connection.prepareStatement(query);
                } catch (SQLException e) {
                    this.exceptionConsumer.accept(this.connection, e);
                }
                SqlRunner<PreparedStatement> sqlRunner = new SqlRunner<PreparedStatement>(connection, query, statement, this.exceptionConsumer);
                return sqlRunner;
            }

            public SqlRunner<PreparedStatement> sql(String query, int state) {
                PreparedStatement statement = null;
                try {
                    statement = this.connection.prepareStatement(query, state);
                } catch (SQLException e) {
                    this.exceptionConsumer.accept(this.connection, e);
                }
                SqlRunner<PreparedStatement> sqlRunner = new SqlRunner<PreparedStatement>(connection, query, statement, this.exceptionConsumer);
                return sqlRunner;
            }

        }

    }


    public static class SqlRunner<T extends PreparedStatement> {
        final Connection connection;
        final BiConsumer<Connection, Exception> exceptionConsumer;
        final String query;
        final T statement;


        public SqlRunner(Connection connection, String query, T statement, BiConsumer<Connection, Exception> exceptionConsumer) {
            this.connection = connection;
            this.query = query;
            this.statement = statement;
            this.exceptionConsumer = exceptionConsumer;
        }

        public SqlRunner setAutoCommit(boolean autoCommit) {
            try {
                this.connection.setAutoCommit(autoCommit);
            } catch (SQLException e) {
                this.exceptionConsumer.accept(this.connection, e);
            } finally {
                return this;
            }
        }

        public SqlRunner registerOutParameter(int parameterIndex, int sqlType) {
            if (this.statement instanceof CallableStatement) {
                try {
                    ((CallableStatement) this.statement).registerOutParameter(parameterIndex, sqlType);
                } catch (SQLException e) {
                    this.exceptionConsumer.accept(this.connection, e);
                }

            }
            return this;
        }

        public SqlRunner setBoolean(int parameterIndex, boolean x) {

            try {
                this.statement.setObject(parameterIndex, x);
            } catch (SQLException e) {
                this.exceptionConsumer.accept(this.connection, e);
            }
            return this;
        }

        public SqlRunner setByte(int parameterIndex, byte x) {
            try {
                this.statement.setByte(parameterIndex, x);
            } catch (SQLException e) {
                this.exceptionConsumer.accept(this.connection, e);
            }
            return this;
        }


        public SqlRunner setShort(int parameterIndex, short x) {
            try {
                this.statement.setShort(parameterIndex, x);
            } catch (SQLException e) {
                this.exceptionConsumer.accept(this.connection, e);
            }
            return this;
        }


        public SqlRunner setInt(int parameterIndex, int x) {
            try {
                this.statement.setInt(parameterIndex, x);
            } catch (SQLException e) {
                this.exceptionConsumer.accept(this.connection, e);
            }
            return this;
        }


        public SqlRunner setLong(int parameterIndex, long x) {
            try {
                this.statement.setLong(parameterIndex, x);
            } catch (SQLException e) {
                this.exceptionConsumer.accept(this.connection, e);
            }
            return this;
        }


        public SqlRunner setFloat(int parameterIndex, float x) {
            try {
                this.statement.setFloat(parameterIndex, x);
            } catch (SQLException e) {
                this.exceptionConsumer.accept(this.connection, e);
            }
            return this;
        }


        public SqlRunner setDouble(int parameterIndex, double x) {
            try {
                this.statement.setDouble(parameterIndex, x);
            } catch (SQLException e) {
                this.exceptionConsumer.accept(this.connection, e);
            }
            return this;
        }


        public SqlRunner setBigDecimal(int parameterIndex, BigDecimal x) {
            try {
                this.statement.setBigDecimal(parameterIndex, x);
            } catch (SQLException e) {
                this.exceptionConsumer.accept(this.connection, e);
            }
            return this;
        }


        public SqlRunner setString(int parameterIndex, String x) {
            try {
                this.statement.setString(parameterIndex, x);
            } catch (SQLException e) {
                this.exceptionConsumer.accept(this.connection, e);
            }
            return this;
        }


        public SqlRunner setBytes(int parameterIndex, byte x[]) {
            try {
                this.statement.setBytes(parameterIndex, x);
            } catch (SQLException e) {
                this.exceptionConsumer.accept(this.connection, e);
            }
            return this;
        }


        public SqlRunner setDate(int parameterIndex, java.sql.Date x) {
            try {
                this.statement.setDate(parameterIndex, x);
            } catch (SQLException e) {
                this.exceptionConsumer.accept(this.connection, e);
            }
            return this;
        }


        public SqlRunner setTime(int parameterIndex, java.sql.Time x) {
            try {
                this.statement.setTime(parameterIndex, x);
            } catch (SQLException e) {
                this.exceptionConsumer.accept(this.connection, e);
            }
            return this;
        }

        public SqlRunner setTimestamp(int parameterIndex, java.sql.Timestamp x) {
            try {
                this.statement.setTimestamp(parameterIndex, x);
            } catch (SQLException e) {
                this.exceptionConsumer.accept(this.connection, e);
            }
            return this;
        }


        public SqlRunner setAsciiStream(int parameterIndex, java.io.InputStream x, int length) {
            try {
                this.statement.setAsciiStream(parameterIndex, x, length);
            } catch (SQLException e) {
                this.exceptionConsumer.accept(this.connection, e);
            }
            return this;
        }


        public SqlRunner setBinaryStream(int parameterIndex, java.io.InputStream x, int length) {
            try {
                this.statement.setBinaryStream(parameterIndex, x, length);
            } catch (SQLException e) {
                this.exceptionConsumer.accept(this.connection, e);
            }
            return this;
        }


        public SqlRunner setObject(int parameterIndex, Object x, int targetSqlType) {
            try {
                this.statement.setObject(parameterIndex, x, targetSqlType);
            } catch (SQLException e) {
                this.exceptionConsumer.accept(this.connection, e);
            }
            return this;
        }

        public SqlRunner setObject(int parameterIndex, Object x) {
            try {
                this.statement.setObject(parameterIndex, x);
            } catch (SQLException e) {
                this.exceptionConsumer.accept(this.connection, e);
            }
            return this;
        }

        public <C> C cast(BiFunction<Connection, ? super PreparedStatement, C> function) {
            return function.apply(this.connection, this.statement);
        }


    }

}
