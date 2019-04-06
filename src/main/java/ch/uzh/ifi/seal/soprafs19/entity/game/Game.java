package ch.uzh.ifi.seal.soprafs19.entity.game;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.*;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.entity.game.rules.GameRuleEngine;
import ch.uzh.ifi.seal.soprafs19.entity.game.rules.GodPowers;
import ch.uzh.ifi.seal.soprafs19.entity.game.states.GameState;

@Entity
public class Game {

    List<User> listOfUser = new ArrayList<User>();
    List<GodPowers> listAvailableGodPowers = new ArrayList<>();
    public GameRuleEngine gameruleEngine = new GameRuleEngine();
    public Board board = new Board();
    public GameState gameState = new GameState();

    @Id
    @GeneratedValue
    public int idGame;

    @Column(nullable = false)
    public String nameGame;

    @Column(nullable = false)
    public boolean allowGodPowers;

    public int getIdGame() {
        return idGame;
    }

    public void setIdGame(int idGame) {
        this.idGame = idGame;
    }

    public String getNameGame() {
        return nameGame;
    }

    public void setNameGame(String nameGame) {
        this.nameGame = nameGame;
    }

    public boolean isAllowGodPowers() {
        return allowGodPowers;
    }

    public void setAllowGodPowers(boolean allowGodPowers) { this.allowGodPowers = allowGodPowers; }

    //add user to lobby
    public void addUser(User user){
        listOfUser.add(user);
        if (listOfUser.size() == 2){
            start();
        }
    }
    public void removeUser(User user){
        listOfUser.remove(user);

    }

    //set godpowers
    public void setGodPower(GodPowers godPower1, GodPowers godPower2){
        listAvailableGodPowers.add(godPower1);
        listAvailableGodPowers.add(godPower2);
    }

    public void start(){
        //board.load();
    }


}
