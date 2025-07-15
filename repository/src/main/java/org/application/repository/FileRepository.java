package org.application.repository;

import org.application.StringUtil;
import org.application.framework.JdbcBuilder;
import org.application.model.File;

import java.sql.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.BiFunction;

public class FileRepository extends Repository<Integer, File> {

    public FileRepository(Connection connection) {
        super(connection);
    }

    @Override
    public File add(File model) {

        String sql = "INSERT INTO `file`(`id`, `name`, `content`) VALUES (? ,?,?)";
        model.setId(super.getFileSequence());

        return (File) JdbcBuilder
                .connection(super.connection)
                .setCatch((sqlConnection, exception) -> {
                    System.out.println(exception.toString());
                    try {
                        sqlConnection.rollback();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sql(sql, Statement.RETURN_GENERATED_KEYS)
                .setAutoCommit(false)
                .setInt(1, model.getId())
                .setString(2, model.getFilename()) // مقداردهی به name
                .setBytes(3, model.getContent()) // مقداردهی به age
                .<File>cast((BiFunction<Connection, ? super PreparedStatement, File>) (connection, statement) -> {

                    try {
                        int addRow = statement.executeUpdate();
                        if (addRow > 0) {
                            return model;
                        }
                    } catch (SQLException e) {
                        try {
                            connection.rollback();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }


                    }
                    return null;

                });


    }

    @Override
    public File update(File model) {

        return (File) JdbcBuilder
                .connection(super.connection)
                .setCatch((sqlConnection, exception) -> {
                    System.out.println(exception.toString());
                    try {
                        sqlConnection.rollback();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sql("UPDATE file SET name= ?, content= ? WHERE id = ?", Statement.RETURN_GENERATED_KEYS)
                .setAutoCommit(false)
                .setString(1, model.getFilename()) // مقداردهی به name
                .setBytes(2, model.getContent()) // مقداردهی به age
                .setInt(3, model.getId())
                .<File>cast((BiFunction<Connection, ? super PreparedStatement, File>) (connection, statement) -> {

                    try {
                        int addRow = statement.executeUpdate();
                        if (addRow > 0) {
                            return model;
                        }
                    } catch (SQLException e) {
                        try {
                            connection.rollback();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    return null;

                });

    }

    @Override
    public boolean deleteByIds(List<Integer> ids) {

        String idsAsString = StringUtil.join(ids);

        String sqlForCustomer = MessageFormat.format("DELETE FROM customer WHERE file_id IN ({0})", StringUtil.join(ids));

        String sqlForFile = MessageFormat.format("DELETE FROM file WHERE id IN ({0})", StringUtil.join(ids));

        JdbcBuilder
                .connection(this.connection)
                .setCatch((sqlConnection, exception) -> {
                    System.out.println(exception.toString());
                    try {
                        sqlConnection.rollback();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sql(sqlForCustomer)
                .setAutoCommit(false)
                .<Boolean>cast((BiFunction<Connection, ? super PreparedStatement, Boolean>) (connection, statement) -> {

                    try {
                        int deleteRow = statement.executeUpdate();

                        if (deleteRow > 0) {
                            return true;
                        }
                    } catch (SQLException e) {
                        try {
                            connection.rollback();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }


                    }
                    return false;

                });


        return (Boolean) JdbcBuilder
                .connection(this.connection)
                .setCatch((sqlConnection, exception) -> {
                    System.out.println(exception.toString());
                    try {
                        sqlConnection.rollback();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sql(sqlForFile)
                .setAutoCommit(false)
                .<Boolean>cast((BiFunction<Connection, ? super PreparedStatement, Boolean>) (connection, statement) -> {

                    try {
                        int deleteRow = statement.executeUpdate();

                        if (deleteRow > 0) {
                            return true;
                        }
                    } catch (SQLException e) {
                        try {
                            connection.rollback();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }


                    }
                    return false;

                });

    }

    @Override
    public boolean deleteByIds(Integer... ids) {
        return deleteByIds(List.of(ids));
    }


    @Override
    public List<File> get(File model) {

        Map<String, Object> map = new HashMap<>();

        String sql = "SELECT id, name, content FROM FILE WHERE 1 = 1";

        if (model != null) {

            if (model.getId() > 0) {
                map.put("id", model.getId());
            }
            if (model.getFilename() != null && !model.getFilename().trim().equals("")) {
                map.put("name", model.getFilename());
            }


            Set<String> keys = map.keySet();
            String where = "";
            for (String key : keys) {
                if (key.equals("name")) {
                    where += (" AND " + key + " LIKE ?");
                } else {
                    where += (" AND " + key + " =?");
                }
            }
            sql += where;
        }

        JdbcBuilder.SqlRunner sqlRunner = JdbcBuilder
                .connection(super.connection)
                .setCatch((sqlConnection, exception) -> {
                    System.out.println(exception.toString());
                    try {
                        sqlConnection.rollback();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sql(sql, Statement.RETURN_GENERATED_KEYS)
                .setAutoCommit(false);

        int i = 1;
        for (String key : map.keySet()) {
            if (key.equals(key.equals("name"))) {
                sqlRunner.setObject(i++, "%" + map.get(key) + "%");
            } else {
                sqlRunner.setObject(i++, map.get(key));
            }

        }

        return (List<File>) sqlRunner
                .<List<File>>cast((BiFunction<Connection, ? super PreparedStatement, List<File>>) (connection, statement) -> {

                    List<File> files = new ArrayList<>();
                    try {
                        ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            File customer1 = new File(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getBytes("content"));
                            files.add(customer1);
                        }
                    } catch (SQLException e) {
                        try {
                            connection.rollback();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
                    return files;

                });


    }
}
