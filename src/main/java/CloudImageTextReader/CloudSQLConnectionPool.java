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
        logger.info("Starting to create new HikariConfig...");
        HikariConfig config = new HikariConfig();
        logger.info("Setting JDBC...");
        config.setJdbcUrl(String.format("jdbc:mysql:///%s", dbName));
        logger.info("Adding user...");
        config.setUsername(dbUser);
        logger.info("Adding password...");
        config.setPassword(dbPass);
        logger.info("Adding Socketfactory property...");
        config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
        logger.info("Adding SQLInstance property...");
        config.addDataSourceProperty("cloudSqlInstance", cloudSqlConnectionName);
        logger.info("Adding IPType property...");
        config.addDataSourceProperty("ipTypes", "PUBLIC,PRIVATE");
        logger.info("Starting to create new HikariDataSource...");
        DataSource Pool=new HikariDataSource(config);
        logger.info("HikariDataSource Done.");
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
