package tur.com.projetounum;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ConsultaJSON extends AsyncTask <Void, Void, String> {

    private ConsultaSituacaoListener listener;

    private static final String URL_STRING = "http://api.openweathermap.org/data/2.5/weather?q=Natal&lang=pt";

    public ConsultaJSON(ConsultaSituacaoListener listener){
        this.listener = listener;
    }


    @Override
    protected String doInBackground(Void... params) {
        try {
            String resultado = ConsultaServidor();

            return interpretaResultado(resultado);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String interpretaResultado(String resultado) throws JSONException {
        JSONObject object = new JSONObject(resultado);

        JSONArray jsonArray = object.getJSONArray("weather");
        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);

        int id = jsonObjectWeather.getInt("id");
        String descricao = jsonObjectWeather.getString("description");
        String main = jsonObjectWeather.getString("main");

        return "Situação do Tempo em Natal: "+ id + " - "+ descricao + " - "+ main;
    }

    private String ConsultaServidor() throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(URL_STRING);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            conn.getResponseCode();

            is = conn.getInputStream();

            Reader reader;
            reader = new InputStreamReader(is);
            char[] buffer = new char[2048];
            reader.read(buffer);
            return new String(buffer);

        }finally {
            if (is != null){
                is.close();
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        listener.onConsultaConcluida(result);
    }

    public interface ConsultaSituacaoListener{
        void onConsultaConcluida(String situacaoTempo);
    }
}
