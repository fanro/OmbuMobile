package com.max.ombumobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by max on 25/10/2016.
 */

public class RestHandler extends AsyncTask<String, Long, String> {

    public static final String REST_ACTION_LOGIN = "http://www.fiscalias.gob.ar/mjys/denuncia/login.php";
    //public static final String REST_ACTION_LOGIN = "http://10.0.2.2/kiwi/www/webservices/denuncia/login.php";
    public static final String RIGHT_TECNICO = "ombu.soporte.tecnico";
    public static final String REST_OK = "OK";
    private ProgressDialog progress;
    public AsyncResponse delegate = null;

    public void setActivity(Activity activity){
        progress = new ProgressDialog(activity);
        delegate = (AsyncResponse) activity;
    }

    @Override
    protected void onPreExecute() {
        progress.setMessage("Cargando...");
        progress.show();
    }

    @Override
    protected String doInBackground(String... args) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL(args[1]);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(args[0]);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();

            // DEPENDE DE LA URL, LOS PARAMETROS QUE MANDO
            JSONObject jsonParam = new JSONObject();
            switch (args[1]){
                case REST_ACTION_LOGIN:
                    jsonParam.put("user", args[2]);
                    jsonParam.put("pass", args[3]);
                    break;
                default:
                    break;
            }
            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(jsonParam.toString());
            out.close();


            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            forecastJsonStr = buffer.toString();
            return forecastJsonStr;

        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        progress.dismiss();
        // string json in result
        Log.d("@string/app_name", result);
        delegate.processFinish(result);
    }


}