package FootballBot;

import lombok.SneakyThrows;
import org.apache.http.HttpHeaders;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ConnectApi{
    @SneakyThrows
    public void getRating(){
        URL url = new URL("https://api.football-data.org/v2/competitions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.getHeaderField("5abf8140319245dfb418db7aa4ee75f2");
        connection.setRequestMethod("GET");
        List<String> headers = new ArrayList<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ( (inputLine = in.readLine()) != null){
            response.append(inputLine);
        }
        in.close();
        JSONObject json = new JSONObject(response.toString());
        System.out.println(json);
    }
}
