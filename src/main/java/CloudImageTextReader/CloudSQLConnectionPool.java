package CloudImageTextReader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class CloudSQLConnectionPool {
    public static DataSource createConnectionPool(String dbUser, String dbPass, String dbName,
                                                  String cloudSqlConnectionName) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mysql:///%s", dbName));
        config.setUsername(dbUser);
        config.setPassword(dbPass);
        config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
        config.addDataSourceProperty("cloudSqlInstance", cloudSqlConnectionName);
        config.addDataSourceProperty("ipTypes", "PUBLIC,PRIVATE");
        return new HikariDataSource(config);
    }

    public static void createTable(DataSource pool, String tableName) throws SQLException {
        // Safely attempt to create the table schema.
        try (Connection conn = pool.getConnection()) {
            String stmt = "CREATE TABLE IF NOT exists testtable (ID int not null auto_increment primary key, CreationDate DATETIME, FileName varchar(255), imagetext varchar(4000) );";
            try (PreparedStatement createTableStatement = conn.prepareStatement(stmt)) {
                createTableStatement.execute();
            }
        }
    }
    public static void  insertData (DataSource pool, String filename, String imagetext) throws SQLException {
        try (Connection conn = pool.getConnection()) {
            String stmt=String.format("insert into testtable (CreationDate, FileName,imagetext) values (Now(), '%s', '%s');",filename,imagetext);
            try(PreparedStatement insertStatement = conn.prepareStatement(stmt)) {
                insertStatement.execute();

            }

        }

    }
}
