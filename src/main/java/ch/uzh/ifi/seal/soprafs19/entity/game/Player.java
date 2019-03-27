package ch.uzh.ifi.seal.soprafs19.entity.game;

import ch.uzh.ifi.seal.soprafs19.entity.game.rules.playerrules.PlayerRuleEngine;

public class Player {

    private PlayerRuleEngine playerRuleEngine;

    public PlayerRuleEngine getPlayerRuleEngine() {
        return playerRuleEngine;
    }

    public void setPlayerRuleEngine(PlayerRuleEngine playerRuleEngine) {
        this.playerRuleEngine = playerRuleEngine;
    }
}
