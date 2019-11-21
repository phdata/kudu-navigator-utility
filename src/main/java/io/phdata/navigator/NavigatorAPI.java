package io.phdata.navigator;

import com.google.gson.Gson;
import io.phdata.App;
import io.phdata.bean.NavigatorBean;
import io.phdata.bean.Properties;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.kudu.shaded.com.google.common.net.HttpHeaders;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


/**
 * Responsible for handling Navigator GET and PUT
 * Uses GSON library for parsing json to Java object and vice versa
 * URL and Credentials are read from Properties file
 *
 */
public class NavigatorAPI {

    private static final String query = "query=(originalName:";
    private static final String AND = ")AND(";
    private static final String parentPath = "parentPath:";
    private static final String quotes = "%22";
    public static final String EMPTY_RESPONSE = "[ ]";

    private String navigatorUrl;
    private String userName;
    private String password;

    private static final Logger logger = LogManager.getLogger(App.class);

    public NavigatorAPI(String navigatorUrl, String userName, String password){

        this.navigatorUrl = navigatorUrl;
        this.userName = userName;
        this.password = password;

    }

    // Get Identity for column using Navigator GET API
    public String getIdentity(String colName, String dbName, String tableName) throws IOException {

        String identity = "";

        CloseableHttpClient httpClient = HttpClients.createDefault();

        String url = navigatorUrl + "?" + query + colName + AND + parentPath + quotes + "/" + dbName+"/"+tableName + quotes + ")";

        HttpGet request = new HttpGet(url);
        request = (HttpGet) createHeaders(request);


        try (CloseableHttpResponse response = httpClient.execute(request)) {

            // Get HttpResponse Status
            logger.info(response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();

            if (entity != null) {

                String result = EntityUtils.toString(entity);
                if(result.equals(EMPTY_RESPONSE)){
                    return identity;
                }
                Gson g = new Gson();
                NavigatorBean[] navigatorBean = g.fromJson(result, NavigatorBean[].class);

                identity = navigatorBean[0].getIdentity();

            }

        }
        return identity;
    }

    //Update Metadata using Navigator PUT API
    public void updateMetadata(Properties props, String description, String identity) throws IOException{

        CloseableHttpClient httpclient = HttpClients.createDefault();

        String url = navigatorUrl + "/" + identity;


        HttpPut request = new HttpPut(url);
        request = (HttpPut) createHeaders(request);

        NavigatorBean bean = new NavigatorBean();
        bean.setDescription(description);
        bean.setProperties(props);

        Gson g = new Gson();
        String data = g.toJson(bean);


        StringEntity stringEntity = new StringEntity(data, ContentType.APPLICATION_JSON);
        request.setEntity(stringEntity);

        try (CloseableHttpResponse response = httpclient.execute(request)) {
           logger.info("Put request execution status: " + response.getStatusLine().toString());
        }


    }

    private HttpRequest createHeaders(HttpRequest request){

        String auth = this.userName + ":" + this.password;
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        return request;

    }
}
