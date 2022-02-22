package CurrencyBot;

import lombok.SneakyThrows;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CurrencyConvectorImplement implements CurrencyConvector{
    @Override
    public double getConversionRate(Currency original, Currency target) {
        double originalRate = getRate(original);
        double targetRate = getRate(target);
        return originalRate/targetRate;
    }

    @SneakyThrows
    private double getRate(Currency currency){
        URL url = new URL("https://www.nbrb.by/api/exrates/rates/" + currency.getId());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ( (inputLine = in.readLine()) != null){
            response.append(inputLine);
        }
        in.close();
        JSONObject json = new JSONObject(response.toString());
        return json.getDouble("Cur_OfficialRate")/ json.getDouble("Cur_Scale");
    }
}
