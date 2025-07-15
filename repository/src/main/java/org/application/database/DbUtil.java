package org.application.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.application.ConfigUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DbUtil {
    private static final HikariDataSource dataSource;

    static {
        createDbIfNotExist(ConfigUtil.getDatabaseUrl(), ConfigUtil.getDatabaseUsername(), ConfigUtil.getDatabasePassword());
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(ConfigUtil.getDatabaseUrl());
        config.setUsername(ConfigUtil.getDatabaseUsername());
        config.setPassword(ConfigUtil.getDatabasePassword());
        config.setMaximumPoolSize(ConfigUtil.getMaximumPoolSize()); // تعداد session های دیتابیس
        config.setConnectionTimeout(ConfigUtil.getConnectionTimeout()); //که یک درخواست حداکثر 30 ثانیه می‌تواند منتظر بماند
        dataSource = new HikariDataSource(config);
        importDb(getMariaDBConnection(), "sql/application.sql");
    }

    public static Connection getMariaDBConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createDbIfNotExist(String dbUrl, String user, String password) {
        try {
            String url = dbUrl.substring(0, dbUrl.lastIndexOf('/'));
            String dbName = dbUrl.substring(dbUrl.lastIndexOf('/') + 1);
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            // قدم 2: اجرای دستور ایجاد دیتابیس اگر وجود ندارد
            String createDbQuery = "CREATE DATABASE IF NOT EXISTS " + dbName;
            stmt.executeUpdate(createDbQuery);
            System.out.println("Database checked/created successfully.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public static void importDb(Connection connection, String path) {
        try {
            String sqlScript = ConfigUtil.getResource(path);
            sqlScript = sqlScript.replaceAll("/\\*.*?\\*/*", "");
            // حذف خطوطی که فقط شامل ; هستند
            sqlScript = sqlScript.replaceAll("(?m)^\\s*;\\s*$", "");

            String[] sqlStatements = sqlScript.toString().split(";");

            Statement statement = connection.createStatement();

            for (String sql : sqlStatements) {
                sql = sql.trim();
                if (!sql.isEmpty()) {
                    System.out.println("*********************************** " + sql);
                    statement.execute(sql);
                }
            }


            // تجزیه اسکریپت به دستورات مجزا با استفاده از JSqlParser
/*
            List<String> statements = CCJSqlParserUtil
                    .parseStatements(sqlScript)
                    .getStatements()
                    .stream()
                    .map(statement -> {
                        return statement.toString();
                    })
                    .collect(Collectors.toList());
*/

            //  Statement statement = connection.createStatement();

/*            statements.forEach(command -> {
                try {
                    System.out.println("*********************************** " + command);
                    statement.execute(command);
                } catch (SQLException e) {
                    System.out.println(e.toString());
                }
            });*/

        } catch (Exception e) {
            System.out.println(e.toString());
        }


    }


}

