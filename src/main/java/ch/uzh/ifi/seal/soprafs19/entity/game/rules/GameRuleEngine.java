package ch.uzh.ifi.seal.soprafs19.entity.game.rules;

import ch.uzh.ifi.seal.soprafs19.entity.game.Board;
import ch.uzh.ifi.seal.soprafs19.entity.game.Player;
import ch.uzh.ifi.seal.soprafs19.entity.game.turn.Move;

import java.util.List;

public class GameRuleEngine {

    private List<Player> players;

    private Board board;

    // TODO: Implement
    public List<Move> getLegalMoves(Player player) {
        return player.getPlayerRuleEngine().getLegalMoves(players);
    }

}
