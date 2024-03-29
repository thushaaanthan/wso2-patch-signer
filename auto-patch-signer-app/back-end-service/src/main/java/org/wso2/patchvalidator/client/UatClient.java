package org.wso2.patchvalidator.client;

import java.io.InputStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.wso2.patchvalidator.exceptions.ServiceException;
import org.wso2.patchvalidator.util.Util;

/**
 * <h1>UAT Client</h1>
 * Client for accessing WUM UAT API.
 * Only used for reverting a signed update.
 *
 * @author Pramodya Mendis
 * @version 1.3
 * @since 2018-07-12
 */

public class UatClient {


    public static JSONObject getUatAccessToken(String uri, String grantType, String grantTypeValue, String authToken) {

        String httpBody = grantType + "=" + grantTypeValue;

        JSONParser parser = new JSONParser();
        JSONObject resultObject;

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

            HttpPost request = new HttpPost(uri);
            StringEntity params = new StringEntity(httpBody);

            request.addHeader("Authorization", authToken);
            request.setEntity(params);

            HttpResponse response = httpClient.execute(request);
            InputStream inStream = response.getEntity().getContent();
            String result = Util.convertStreamToString(inStream);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                resultObject = (JSONObject) parser.parse(result);
                return resultObject;
            } else {
                throw new ServiceException("retrieving UAT access token failed, " + "statusCode:" + statusCode +
                        " uri:" + uri + " grantType:" + grantType + " grantTypeValue:" + grantTypeValue +
                        " authToken:" + authToken,
                        "Retrieving UAT access token failed, Please contact admin.");
            }
        } catch (Exception ex) {
            throw new ServiceException("Exception occurred, when retrieving access token from UAT, " +
                    " uri:" + uri + " grantType:" + grantType + " grantTypeValue:" + grantTypeValue +
                    " authToken:" + authToken, "Retrieving UAT access token failed, Please contact admin.", ex);
        }
    }


    public static boolean deleteUatUpdate(String updateId, String uri, String jwtAssertionValue, String forwardedForValue,
                                          String authorizationValue) {

        uri = uri + updateId;

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

            HttpDelete request = new HttpDelete(uri);

            request.addHeader("X-JWT-Assertion", jwtAssertionValue);
            request.addHeader("X-Forwarded-For", forwardedForValue);
            request.addHeader("Authorization", authorizationValue);

            HttpResponse response = httpClient.execute(request);
            InputStream inStream = response.getEntity().getContent();
            String result = Util.convertStreamToString(inStream);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                return true;
            } else {
                throw new ServiceException("delete UAT update failed, " + " response:" + result +
                        "statusCode:" + statusCode + " request:" + request + " updateId:" + updateId +
                        " uri:" + uri + " jwtAssertionValue:" + jwtAssertionValue + " forwardedForValue:" +
                        forwardedForValue + " authorizationValue:" + authorizationValue,
                        "Deleting UAT update failed for the update \"" + updateId + "\", " +
                                "Please contact admin.");
            }
        } catch (Exception ex) {
            throw new ServiceException("Exception occurred, when deleting UAT update, " + " updateId:" + updateId +
                    " uri:" + uri + " jwtAssertionValue:" + jwtAssertionValue + " forwardedForValue:" +
                    forwardedForValue + " authorizationValue:" + authorizationValue,
                    "Deleting UAT update failed for the update \"" + updateId + "\", Please contact admin.",
                    ex);
        }

    }

}
