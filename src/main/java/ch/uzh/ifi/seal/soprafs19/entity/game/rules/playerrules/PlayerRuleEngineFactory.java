package ch.uzh.ifi.seal.soprafs19.entity.game.rules.playerrules;

import ch.uzh.ifi.seal.soprafs19.constant.GodPower;

public class PlayerRuleEngineFactory {

    public PlayerRuleEngine createPlayerRuleEngine(GodPower godPower) {
        switch(godPower) {
            default: {
                return new DefaultPlayerRuleEngine();
            }
        }
    }

}
