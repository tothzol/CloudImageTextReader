package CloudImageTextReader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class CloudSQLConnectionPool {
    private static final Logger logger = Logger.getLogger(FileTrigger.class.getName());
    public static DataSource createConnectionPool(String dbUser, String dbPass, String dbName,
                                                  String cloudSqlConnectionName) {

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(String.format("jdbc:mysql:///%s34.90.19.204:3306//", dbName));

        config.setUsername(dbUser);

        config.setPassword(dbPass);

        config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");

        config.addDataSourceProperty("cloudSqlInstance", cloudSqlConnectionName);

        config.addDataSourceProperty("ipTypes", "PUBLIC,PRIVATE");

        DataSource Pool=new HikariDataSource(config);

        return Pool;
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
        logger.info("insertdata->GetConnection...");
        try (Connection conn = pool.getConnection()) {
            logger.info("Trying to create Query...");
            String stmt=String.format("insert into testtable (CreationDate, FileName,imagetext) values (Now(), '%s', '%s');",filename,imagetext);
            logger.info("Query: "+stmt);
            logger.info("Trying to create prepare statement...");
            try(PreparedStatement insertStatement = conn.prepareStatement(stmt)) {
                logger.info("Trying to create execute statement...");
                insertStatement.execute();

            }

        }

    }
}
