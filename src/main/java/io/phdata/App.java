package io.phdata;

import io.phdata.kudu.Kudu;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Main class
 * @author Raghavendra S
 *
 * Reads Properties file
 * Delegates to Kudu class for Reading from Kudu Table
 *
 */

public class App {

    //Comma separated list of Kudu Masters. Example: master1.valhalla.phdata.io,master2.valhalla.phdata.io,master3.valhalla.phdata.io
    private static String kuduMaster = System.getProperty("kuduMaster");
    //Kudu Table with schema. Example: default.governance_metadata
    private static String kuduTable = System.getProperty("kuduTable");
    //Properties file path
    private static String configFile = System.getProperty("configFile");
    InputStream inputStream;

    private static final Logger logger = LogManager.getLogger(App.class);


    public static void main(String[] args) {

        //String tableName = "impala::"+kuduTable;
        String tableName = kuduTable;
        App app = new App();


        if(kuduMaster == null || kuduTable == null || configFile == null){
            logger.info("Please provide the environment variables -DkuduMaster and -DkuduTable (with Schema separated by '.') and -DconfigFile (Property file path)");
            return;
        }

        Kudu kudu = new Kudu(kuduMaster);

        try {
            Properties prop = app.loadPropFile();
            String navigatorUrl = prop.getProperty("navigator.url");
            String userName = prop.getProperty("navigator.username");
            String password = prop.getProperty("navigator.password");

            kudu.readTable(tableName, navigatorUrl, userName, password);

        } catch (IOException ie) {
            logger.info(ie.getMessage(),ie);
        }


    }





    public Properties loadPropFile() throws IOException {


        Properties prop = new Properties();
        try {

            inputStream = new FileInputStream(configFile);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                logger.info("property file '" + configFile + "' not found ");
                throw new FileNotFoundException("property file '" + configFile + "' not found ");
            }

        } finally {
            inputStream.close();
        }

        return prop;
    }

}

