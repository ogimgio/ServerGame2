package ch.uzh.ifi.seal.soprafs19.entity.game.rules.playerrules;

import ch.uzh.ifi.seal.soprafs19.entity.game.Player;
import ch.uzh.ifi.seal.soprafs19.entity.game.turn.Build;
import ch.uzh.ifi.seal.soprafs19.entity.game.turn.Move;

import java.util.ArrayList;
import java.util.List;

public abstract class PlayerRuleEngine {

    public abstract List<Move> getLegalMoves(List<Player> players);

    public abstract List<Build> getLegalBuilds(List<Player> players);



    // TODO: Implement
    protected List<Move> getBasicLegalMoves() {
        List<Move> legalMoves = new ArrayList<Move>();
        return legalMoves;
    }

    // TODO: Implement
    protected List<Build> getBasicLegalBuilds() {
        List<Build> legalBuilds = new ArrayList<Build>();
        return legalBuilds;
    }

}
