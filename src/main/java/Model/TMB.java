package Model;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class TMB {

    /***
     * Realitza la crida a la API de la TMB i retorna la resposta en String
     * @param entity    Modalitat de la crida
     * @param filters   Filtres a concretar segons modalitat
     * @return  Resposta de la API
     */
    public static String callAPI(String entity, String filters) {

        String info = "";
        try {
            //Fem una instància de okhttp
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://api.tmb.cat/v1/" + entity +
                            "?app_id=791c5059&app_key=4e02bf31435e2e763075ef63c75ce6ed" +
                            filters)
                    .build();
            Response response = client.newCall(request).execute();
            info = response.body().string();
        }catch (IOException e){
            e.printStackTrace();
        }

        return info;
    }




}
