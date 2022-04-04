package FootballBot;


import lombok.SneakyThrows;
import org.apache.http.HttpHeaders;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConnectApi {
    @SneakyThrows
    private JSONObject takeJson (URL url ){
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Auth-Token", "5abf8140319245dfb418db7aa4ee75f2");
        for (String headerKey : headers.keySet()){
            connection.setRequestProperty(headerKey, headers.get(headerKey));
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ( (inputLine = in.readLine()) != null){
            response.append(inputLine);
        }
        in.close();
        JSONObject json = new JSONObject(response.toString());
        return json;
    }

    @SneakyThrows
    private int getTeamIdByName(String name){
        URL url = new URL("https://api.football-data.org/v2/teams");
        JSONObject json = takeJson(url);
        JSONArray teams = json.getJSONArray("teams");

        for (int i = 0; i < teams.length(); i++) {
            if (teams.getJSONObject(i).getString("name").equals(name)){
                return teams.getJSONObject(i).getInt("id");
            }
        }
        throw new Exception();
    }

    @SneakyThrows
    public String getTeamsInfo(String name){
        URL url = new URL("https://api.football-data.org/v2/teams/" + getTeamIdByName(name));
        JSONObject team = takeJson(url);
        String teamPicture = team.getString("crestUrl");
        String teamInfo = team.getString("name") + " (" +team.getString("tla") +")\n\nДомашний стадион: "
                + team.getString("venue") + "\n\nСостав:\n";
        JSONArray squad = team.getJSONArray("squad");

        for (int i = 0; i < squad.length(); i++) {
            teamInfo += squad.getJSONObject(i).getString("name") + " - " + squad.getJSONObject(i).getString("position") + "\n";
        }

        return teamInfo;
    }

    public String convertDate(String utcDate){
        //System.out.println(utcDate.charAt(8)+utcDate.charAt(9));
        String res = utcDate.charAt(8)+""+utcDate.charAt(9) + "/"
                + utcDate.charAt(5)+utcDate.charAt(6) + "/"
                + utcDate.charAt(0)+utcDate.charAt(1)+utcDate.charAt(2)+utcDate.charAt(3) + "  "
                + utcDate.charAt(11)+utcDate.charAt(12) + "h"
                + utcDate.charAt(14)+utcDate.charAt(15);
        return res;
    }

    @SneakyThrows
    public String getCalendar(int id){
        URL url = new URL("https://api.football-data.org/v2/competitions/" + id + "/matches");
        JSONObject json = takeJson(url);
//        System.out.println(json);
        StringBuilder calendarInfo =  new StringBuilder();
        JSONArray table = json.getJSONArray("matches");

        int count_shown = 0;
        for (int i = 0; i < table.length(); i ++){
            String utcDate = convertDate(table.getJSONObject(i).getString("utcDate"));
            String home_team_name = table.getJSONObject(i).getJSONObject("homeTeam").getString("name");
            String away_team_name = table.getJSONObject(i).getJSONObject("awayTeam").getString("name");
            String status = table.getJSONObject(i).getString("status");  //FINISHED  or SCHEDULED
            if (status.equals("SCHEDULED")){
                calendarInfo.append(utcDate +" : " + home_team_name + " vs " + away_team_name+'\n');
                count_shown += 1;
            }
            if (count_shown == 10){
                break;
            }
        }
        return calendarInfo.toString();
    }

    @SneakyThrows
    public String getMatches(){
        URL url = new URL("https://api.football-data.org/v2/matches");
        JSONObject json = takeJson(url);
       //  System.out.println(json);
        StringBuilder matchesInfo =  new StringBuilder();
        JSONArray table = json.getJSONArray("matches");
        for (int i = 0; i < table.length(); i ++){
            if (Leagues.listID.contains(table.getJSONObject(i).getJSONObject("competition").getInt("id"))){
                String competition_name = table.getJSONObject(i).getJSONObject("competition").getString("name");
                String home_team_name = table.getJSONObject(i).getJSONObject("homeTeam").getString("name");
                String away_team_name = table.getJSONObject(i).getJSONObject("awayTeam").getString("name");
                matchesInfo.append(competition_name +" : " + home_team_name + " vs " + away_team_name+'\n');
            }
        }
        return matchesInfo.toString();
    }

    @SneakyThrows
    public String getBombardiers(int id) {
        URL url = new URL("https://api.football-data.org/v2/competitions/" + id + "/scorers");

        JSONObject json =takeJson(url);
        StringBuilder bombardiersInfo = new StringBuilder();
        JSONArray table = json.getJSONArray("scorers");
        for (int i = 0; i < table.length(); i ++){
            String player_name = table.getJSONObject(i).getJSONObject("player").getString("name");
            String team_name = table.getJSONObject(i).getJSONObject("team").getString("name");
            int goals = table.getJSONObject(i).getInt("numberOfGoals");
            bombardiersInfo.append(player_name +" team: " + team_name + " " + goals+'\n');
        }
        return bombardiersInfo.toString();
    }




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

    @SneakyThrows
    public List<String> getFavorite(int id){
        URL url = new URL("https://api.football-data.org/v2/competitions/"+id+"/standings");
        JSONObject json = takeJson(url);
//        System.out.println(json);
        List<String> favoriteInfo =  new ArrayList<>();
        JSONArray table = json.getJSONArray("standings").getJSONObject(0).getJSONArray("table");
        for (int i = 0; i < table.length(); i ++){
            String team_name = table.getJSONObject(i).getJSONObject("team").getString("name");
            favoriteInfo.add(team_name);
        }
        return favoriteInfo;
    }


    /*@SneakyThrows
    public String getNews(){
        URL url = new URL("https://vk.com/footballru");
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
//        System.out.println(json);
        StringBuilder newsInfo =  new StringBuilder();
        JSONArray table = json.getJSONArray("div class=");
        for (int i = 0; i < table.length(); i ++){

            String team_name = table.getJSONObject(i).getJSONObject("pi_text").getString("name");
            int points = table.getJSONObject(i).getInt("points");
            newsInfo.append(position +". " + team_name + " - games: " + playedGames +" points:" + points+'\n');
        }
        return newsInfo.toString();
    }*/


}
