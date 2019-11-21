package io.phdata.kudu;

import io.phdata.App;
import io.phdata.bean.Properties;
import io.phdata.navigator.NavigatorAPI;
import org.apache.kudu.client.KuduClient;
import org.apache.kudu.client.KuduScanner;
import org.apache.kudu.client.KuduTable;
import org.apache.kudu.client.RowResult;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Class to implement CRUD operations on Kudu Table
 * Implemented only READ function
 * For each record get Identity from Navigator and Update it with
 * User Defined Properties
 */

public class Kudu {

    public static final String DATABASE_NAME = "database_name";
    public static final String TABLE_NAME = "table_name";
    public static final String COLUMN_NAME = "column_name";
    public static final String DESCRIPTION = "description";
    public static final String OWNER = "owner";
    public static final String LAST_UPDATED_DATA = "last_updated_data";
    public static final String PERSONAL_DATA = "personal_data";
    public static final String SOURCE_SYSTEM = "source_system";

    private static final Logger logger = LogManager.getLogger(App.class);

    private String kuduMaster;
    private KuduClient client;

    public Kudu(String kuduMaster){
        this.kuduMaster = kuduMaster;
        this.client = new KuduClient.KuduClientBuilder(this.kuduMaster).build();

    }

    /**
     *
     * @param tableName
     * @throws IOException
     *
     * Goes through the Kudu table record by record
     * For each record, it:
     *   a) Gets the entity id for the column using the Navigator GET API .
     *   b) Takes the Kudu record and turns it into the JSON payload
     *   c) Uses the Navigator PUT command to upload the metadata.
     *
     */
    public void readTable(String tableName, String navigatorUrl, String userName, String password) throws IOException {

        NavigatorAPI navigatorAPI = new NavigatorAPI(navigatorUrl, userName, password);
        KuduTable table = this.client.openTable(tableName);

        KuduScanner scanner = client.newScannerBuilder(table)
                .build();

        while (scanner.hasMoreRows()) {
            for (RowResult row : scanner.nextRows()) {
                logger.info("Row: " + row.rowToString());
                
                String database_name = row.getString(DATABASE_NAME);
                String table_name = row.getString(TABLE_NAME);
                String column_name = row.getString(COLUMN_NAME);
                String description = row.getString(DESCRIPTION);

                Properties props = new Properties();
                props.setOwner(row.getString(OWNER));
                props.setLast_update_date(row.getString(LAST_UPDATED_DATA));
                props.setPersonal_data(row.getString(PERSONAL_DATA));
                props.setSource_system(row.getString(SOURCE_SYSTEM));

                //Get Identity for column using Navigator GET API using Schema and Table Name
                String identity = navigatorAPI.getIdentity(column_name,database_name, table_name);

                if(!identity.isEmpty()){
                    //Update Metadata Usinf Navigator PUT API by passing relevant info.
                    navigatorAPI.updateMetadata(props, description, identity);
                } else{
                   logger.info("Identity not found for column : " +  column_name + " in /" + database_name + "/" + table_name);
                }

            }

        }


    }
}
