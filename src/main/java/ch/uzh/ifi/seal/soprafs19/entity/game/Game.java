package ch.uzh.ifi.seal.soprafs19.entity.game;

import javax.persistence.Entity;
import java.util.*;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.entity.game.rules.GameRuleEngine;
import ch.uzh.ifi.seal.soprafs19.entity.game.states.GameState;

@Entity
public class Game {

    List<User> listOfUser = new ArrayList<User>();
    public int idGame;
    public String NameGame;
    public GameRuleEngine gameruleEngine = new GameRuleEngine();

    public Board board = new Board();
    public GameState gameState = new GameState();

    public boolean allowGodPowers(boolean answer){
        if(answer == true){
            return true;
        }
        else{
            return false;
        }
    }
    public void addUser(User user){
        listOfUser.add(user);

    }
    public void removeUser(User user){
        listOfUser.remove(user);

    }
    public void setGodPower(String godPower1, String godPower2){}

    public void start(){
        //Board.load();
    }


}
