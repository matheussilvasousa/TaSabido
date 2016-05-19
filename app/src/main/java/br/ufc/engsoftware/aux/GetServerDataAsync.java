package br.ufc.engsoftware.aux;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by limaneto on 17/04/16.
 */
public class GetServerDataAsync extends AsyncTask<String, String, Void> {
    static String result;
    public String url;
    private final String USER_AGENT = "Mozilla/5.0";

    // you may separate this or combined to caller class.
    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;

    public GetServerDataAsync(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected Void doInBackground(String... params) {

        InputStream inputStream = null;
        try {
            URL url = new URL(params[0]);

            // Set up HTTP post
            HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();
            inputStream = new BufferedInputStream(httpClient.getInputStream());
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder sBuilder = new StringBuilder();

            String line = null;
            while ((line = bReader.readLine()) != null) {
                sBuilder.append(line + "\n");
            }

            inputStream.close();
            result = sBuilder.toString();
            httpClient.disconnect();

        } catch (UnsupportedEncodingException e1) {
            Log.e("UnsupportedEncoding", e1.toString());
            e1.printStackTrace();
        } catch (IllegalStateException e3) {
            Log.e("IllegalStateException", e3.toString());
            e3.printStackTrace();
        } catch (IOException e4) {
            Log.e("IOException", e4.toString());
            e4.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        //parse JSON data
        try {
            JSONArray jArray = new JSONArray(result);
            for(int i=0; i < jArray.length(); i++) {

                JSONObject jObject = jArray.getJSONObject(i);

                String name = jObject.getString("nome");
                String tab1_text = jObject.getString("tab1_text");
                int active = jObject.getInt("active");

            } // End Loop
//            this.progressDialog.dismiss();
        } catch (JSONException e) {
            Log.e("JSONException", "Error: " + e.toString());
        } // catch (JSONException e)
        delegate.processFinish(result);
    }
}