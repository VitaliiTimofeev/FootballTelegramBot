package FootballBot;

import java.util.HashMap;
import java.util.Map;

public class HashMapLeaguesPressButton implements LeaguesInfoPressButton{
    private  final Map<Long, Leagues> originalLeagues = new HashMap<>();
    @Override
    public Leagues getOriginalLegues(long chadId) {
        return originalLeagues.getOrDefault(chadId, Leagues.PremierLeague);
    }

    @Override
    public void setOriginalLeagues(long chadId, Leagues leagues) {
        originalLeagues.put(chadId, leagues);

    }
}
