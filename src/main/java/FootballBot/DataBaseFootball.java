package FootballBot;

import lombok.SneakyThrows;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class DataBaseFootball {
    Connection con;

    @SneakyThrows
    public DataBaseFootball(){
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:sql.db");
        System.out.println("Connected");
    }

    @SneakyThrows
    public String[] selectTeams(Long userId){
        // "A|B|V"
        // "["A", "B", "V"]"
        Statement statement = con.createStatement();
        String query = "SELECT FavoriteTeams FROM FavoriteTeam WHERE UserID = '"+userId+"' ";
        ResultSet rs = statement.executeQuery(query);
        return rs.getString("FavoriteTeams").split("\\|");
    }


    @SneakyThrows
    private String selectTeamsString (Long userId){
        Statement statement = con.createStatement();
        String query = "SELECT FavoriteTeams FROM FavoriteTeam WHERE UserId = '"+userId+"' ";
        ResultSet rs = statement.executeQuery(query);
        return rs.getString("FavoriteTeams");
    }

    public void insertUser(Integer userId){
        try {
            System.out.println(userId);
            String query = "INSERT INTO FavoriteTeam (UserId, FavoriteTeams) VALUES ('"+userId+"', '')";
            Statement statement = con.createStatement();
            statement.executeUpdate(query);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @SneakyThrows
    public void addTeam(Long userId, String team){
        String prevTeam = selectTeamsString(userId);
        if (prevTeam.equals("")){
            prevTeam = team;
        } else {
            prevTeam += '|' + team;
        }

        String query = "UPDATE FavoriteTeam SET FavoriteTeams = ? WHERE UserId = ?";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1, prevTeam);
        pstmt.setLong(2, userId);
        pstmt.executeUpdate();

    }
}