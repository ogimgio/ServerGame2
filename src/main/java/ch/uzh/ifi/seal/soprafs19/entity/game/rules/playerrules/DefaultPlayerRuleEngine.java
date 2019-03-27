package ch.uzh.ifi.seal.soprafs19.entity.game.rules.playerrules;

import ch.uzh.ifi.seal.soprafs19.entity.game.Player;
import ch.uzh.ifi.seal.soprafs19.entity.game.turn.Build;
import ch.uzh.ifi.seal.soprafs19.entity.game.turn.Move;

import java.util.List;

public class DefaultPlayerRuleEngine extends PlayerRuleEngine {


    // TODO: Implement
    @Override
    public List<Move> getLegalMoves(List<Player> players) {
        return getBasicLegalMoves();
    }

    // TODO: Implement
    @Override
    public List<Build> getLegalBuilds(List<Player> players) {
        return getBasicLegalBuilds();
    }
}