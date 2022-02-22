package FootballBot;



public interface LeaguesInfoPressButton {

    static LeaguesInfoPressButton getInstance(){
        return new HashMapLeaguesPressButton();
    }

    Leagues getOriginalLegues(long chadId);

   void setOriginalLeagues(long chadId, Leagues leagues);

}

