package org.application.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.application.ConfigUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
        importDb(getMariaDBConnection(), ConfigUtil.getSqlFile());
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

            // حذف کامنت‌های چندخطی /* ... */
            sqlScript = sqlScript.replaceAll("/\\*.*?\\*/", "");

            // حذف خطوطی که فقط شامل ; هستند
            sqlScript = sqlScript.replaceAll("(?m)^\\s*;\\s*$", "");

            // حذف delimiter‌ها چون JDBC از آن‌ها پشتیبانی نمی‌کند
            sqlScript = sqlScript.replaceAll("(?m)^DELIMITER\\s+\\$\\$", "");
            sqlScript = sqlScript.replaceAll("(?m)^DELIMITER\\s+;", "");

            Statement statement = connection.createStatement();

            // استخراج بلاک‌های CREATE PROCEDURE
            Pattern procPattern = Pattern.compile("CREATE\\s+.*?PROCEDURE.*?END\\s*\\$\\$", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Matcher matcher = procPattern.matcher(sqlScript);

            List<String> procedures = new ArrayList<>();
            while (matcher.find()) {
                String procBlock = matcher.group();
                procBlock = procBlock.replace("$$", "").trim(); // حذف $$ انتهایی
                procedures.add(procBlock);
            }

            // حذف بلاک‌های پروسیجر از متن اصلی
            sqlScript = matcher.replaceAll("");

            // اجرای بلاک‌های پروسیجر
            for (String proc : procedures) {
                System.out.println("Executing procedure:\n" + proc);
                try {
                    statement.execute(proc);
                } catch (Exception e) {
                    System.err.println("Procedure error: " + e.getMessage());
                }
            }

            // اجرای باقی دستورات با split(";")
            String[] sqlStatements = sqlScript.split(";");
            for (String sql : sqlStatements) {
                sql = sql.trim();
                if (!sql.isEmpty()) {
                    System.out.println("Executing SQL:\n" + sql);
                    try {
                        statement.execute(sql);
                    } catch (Exception e) {
                        System.err.println("SQL error: " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Import error: " + e.getMessage());
        }

    }


}

