package org.application.repository;

import org.application.StringUtil;
import org.application.framework.JdbcBuilder;
import org.application.model.Customer;

import java.sql.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.BiFunction;

public class CustomerRepository extends Repository<Integer, Customer> {

    public CustomerRepository(Connection connection) {
        super(connection);
    }

    @Override
    public Customer add(Customer model) {

        String sql = "INSERT INTO `customer`(`id`, `first_name`, `last_name`, `phone_number`, `purchase`, `file_id`) VALUES (?,?,?,?,?,?)";
        model.setId(super.getCustomerSequence());

        return (Customer) JdbcBuilder
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
                .setString(2, model.getFirstName()) // مقداردهی به name
                .setString(3, model.getLastName()) // مقداردهی به age
                .setString(4, model.getPhoneNumber())
                .setInt(5, model.getPurchase())
                .setInt(6, model.getFile().getId())
                .<Customer>cast((BiFunction<Connection, ? super PreparedStatement, Customer>) (connection, statement) -> {

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
    public Customer update(Customer customer) {

        String sql = """
                UPDATE customer c JOIN file f ON c.file_id = f.id
                SET c.first_name  = ?,
                    c.last_name   = ?,
                    c.phone_number= ?,
                    c.purchase    = ?,
                    f.name= ?,
                    f.content     = ?
                WHERE c.id = ?
                """;

        return (Customer) JdbcBuilder
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
                .setString(1, customer.getFirstName()) // مقداردهی به name
                .setString(2, customer.getLastName()) // مقداردهی به age
                .setString(3, customer.getPhoneNumber())
                .setInt(4, customer.getPurchase())
                .setString(5, customer.getFile().getFilename())
                .setBytes(6, customer.getFile().getContent())
                .setInt(7, customer.getId())
                .<Customer>cast((BiFunction<Connection, ? super PreparedStatement, Customer>) (connection, statement) -> {

                    try {
                        int addRow = statement.executeUpdate();
                        if (addRow > 0) {
                            return customer;
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

        String sql = MessageFormat.format("DELETE c, f FROM customer c JOIN file f ON c.file_id = f.id WHERE c.id IN ({0})", StringUtil.join(ids));

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
                .sql(sql)
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
    public List<Customer> get(Customer customer) {

        Map<String, Object> map = new HashMap<>();

        String sql = """
                SELECT c.id id,
                       c.first_name firstName,
                       c.last_name lastName,
                       c.phone_number phoneNumber,
                       c.purchase purchase,
                       c.file_id fileId,
                       f.name fileName,
                       f.content content
                FROM CUSTOMER c, FILE f
                WHERE c.file_id = f.id
                """;

        if (customer != null) {
            if (customer.getId() > 0) {
                map.put("c.id", customer.getId());
            }
            if (customer.getFirstName() != null && !customer.getFirstName().trim().equals("")) {
                map.put("c.first_name", customer.getFirstName());
            }
            if (customer.getLastName() != null && !customer.getLastName().trim().equals("")) {
                map.put("c.last_name", customer.getLastName());
            }
            if (customer.getPhoneNumber() != null && !customer.getPhoneNumber().trim().equals("")) {
                map.put("c.phone_number", customer.getPhoneNumber());
            }
            if (customer.getPurchase() > 0) {
                map.put("c.purchase", customer.getPurchase());
            }

            if (customer.getFile() != null) {
                if (customer.getFile().getId() > 0) {
                    map.put("c.file_id", customer.getFile().getId());
                }
                if (customer.getFile().getFilename() != null && !customer.getFile().getFilename().trim().equals("")) {
                    map.put("f.name", customer.getFile().getFilename());
                }
            }

            Set<String> keys = map.keySet();
            String where = "";
            for (String key : keys) {
                if (key.equals("c.first_name") || key.equals("c.last_name") || key.equals("c.phone_number") || key.equals("f.name")) {
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
            if (key.equals("c.first_name") || key.equals("c.last_name") || key.equals("c.phone_number") || key.equals("f.name")) {
                sqlRunner.setObject(i++, "%" + map.get(key) + "%");
            } else {
                sqlRunner.setObject(i++, map.get(key));
            }

        }

        return (List<Customer>) sqlRunner
                .<List<Customer>>cast((BiFunction<Connection, ? super PreparedStatement, List<Customer>>) (connection, statement) -> {

                    List<Customer> customers = new ArrayList<>();
                    try {
                        ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {

                            Customer customer1 = new Customer(
                                    resultSet.getInt("id"),
                                    resultSet.getString("firstName"),
                                    resultSet.getString("lastName"),
                                    resultSet.getString("phoneNumber"),
                                    resultSet.getInt("purchase"),
                                    resultSet.getInt("fileId"),
                                    resultSet.getString("fileName"),
                                    resultSet.getBytes("content")
                            );

                            customers.add(customer1);
                        }
                    } catch (SQLException e) {
                        try {
                            connection.rollback();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }


                    }
                    return customers;

                });

    }

}
