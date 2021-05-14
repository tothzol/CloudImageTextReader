package CloudImageTextReader;

import CloudImageTextReader.eventpojos.GcsEvent;
import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.logging.Logger;

import static CloudImageTextReader.DetectText.detectText;

public class FileTrigger implements BackgroundFunction<GcsEvent>{
private static final Logger logger = Logger.getLogger(FileTrigger.class.getName());
    @Override
    public void accept(GcsEvent event, Context context) {

        ArrayList<String> Texts;
        try {
            String Path="gs://"+event.getBucket()+"/"+event.getName();
logger.info("FileName: "+Path);
            Texts= detectText(Path);
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
