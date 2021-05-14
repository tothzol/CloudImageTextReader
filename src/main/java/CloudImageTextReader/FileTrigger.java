package CloudImageTextReader;

import CloudImageTextReader.eventpojos.GcsEvent;
import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.logging.Logger;

public class FileTrigger implements BackgroundFunction<GcsEvent>{
private static final Logger logger = Logger.getLogger(FileTrigger.class.getName());
    @Override
    public void accept(GcsEvent event, Context context) {

        ArrayList<String> Texts;
        try {
logger.info("FileName: gs://"+event.getBucket()+"/"+event.getName());
            Texts= DetectText.detectText("gs://"+event.getBucket()+"/"+event.getName());
            String dbUser="root";
            String dbPassword="Cl0ud2021!";
            String dbName="Trainsdb";
            String cloudSQLConnectionName="cloudprog-2021:europe-west4:sqlcloud2021";
            DataSource Pool= CloudSQLConnectionPool.createConnectionPool(dbUser,dbPassword,dbName,cloudSQLConnectionName);

            for (String n : Texts) {
                CloudSQLConnectionPool.insertData(Pool, event.getName(), n);
            }

        }
        catch (Exception e) {
            logger.info("Error: "+e.getMessage());
            e.printStackTrace();

        }
    }



}
