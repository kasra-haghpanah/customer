package org.application;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

public class ConfigUtil {

    private static Properties properties = null;

    public static Properties getProperties() {

        if (properties == null) {
            synchronized (ConfigUtil.class) {

                try {
                    properties = new Properties();
                    InputStream input = ConfigUtil.class.getClassLoader().getResourceAsStream("application.properties");
                    // بارگذاری فایل properties
                    properties.load(input);


                    // خواندن مقادیر
                    String dbUrl = properties.getProperty("database.url");
                    String dbUser = properties.getProperty("database.username");
                    String dbPass = properties.getProperty("database.password");

                    System.out.println("DB URL: " + dbUrl);
                    System.out.println("DB: " + dbUrl.substring(dbUrl.lastIndexOf('/') + 1));
                    System.out.println("DB User: " + dbUser);
                    System.out.println("DB Pass: " + dbPass);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }

        }
        return properties;
    }

    public static String getResource(String path) {
        InputStream input = ConfigUtil.class.getClassLoader().getResourceAsStream(path);
        byte[] bytes = null;
        try {
            bytes = input.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String value = new String(bytes, Charset.forName("UTF-8"));
        return value;
    }

    public static byte[] getResourceAsByteArray(String path) {
        InputStream input = ConfigUtil.class.getClassLoader().getResourceAsStream(path);
        byte[] bytes = null;
        try {
            return input.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getDatabaseUrl() {
        return getProperties().getProperty("database.url");
    }

    public static String getDatabaseUsername() {
        return getProperties().getProperty("database.username");
    }

    public static String getDatabasePassword() {
        return getProperties().getProperty("database.password");
    }

    public static int getMaximumPoolSize() {
        return Integer.valueOf(getProperties().getProperty("database.maximum-pool-size"));
    }

    public static int getConnectionTimeout() {
        return Integer.valueOf(getProperties().getProperty("database.connection-timeout"));
    }

    public static String getSqlFile() {
        return getProperties().getProperty("database.sqlFile");
    }


}
