package FootballBot;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
public enum Leagues {
    PremierLeague (2021), BundesLiga (2002), LaLiga (2014), SerieA(2019), Ligue1(2015); // , RussianLeague(2137)

    static List<Integer> listID = new ArrayList<>();
    
    Leagues(int id) {
        this.id = id;
    }

    static {
        for (Leagues leagues : Leagues.values()){
            listID.add(leagues.getId());
        }
    }
    public static int getIdByName(String name){
        int id = -1;
        switch (name){
            case "PremierLeague":
                id = 2021;
                break;
            case "BundesLiga":
                id = 2002;
                break;
            case "LaLiga":
                id = 2014;
                break;
            case "SerieA":
                id = 2019;
                break;
            case "Ligue1":
                id = 2015;
                break;
        }
        return id;
    }

    public int getId() {
        return id;
    }

    private final int id;
    }
