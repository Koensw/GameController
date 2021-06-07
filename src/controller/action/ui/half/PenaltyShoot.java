package controller.action.ui.half;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.AdvancedData;
import data.GameControlData;
import data.Rules;


/**
 * @author Michel Bartsch
 * 
 * This action means that a penalty shoot is to be starting.
 */
public class PenaltyShoot extends GCAction
{
    /**
     * Creates a new PenaltyShoot action.
     * Look at the ActionBoard before using this.
     */
    public PenaltyShoot()
    {
        super(ActionType.UI);
    }

    /**
     * Performs this action to manipulate the data (model).
     * 
     * @param data      The current data to work on.
     */
    @Override
    public void perform(AdvancedData data)
    {
        if (data.gamePhase != GameControlData.GAME_PHASE_PENALTYSHOOT) {
            data.gamePhase = GameControlData.GAME_PHASE_PENALTYSHOOT;
            // Don't set data.whenCurrentGameStateBegan, because it's used to count the pause
            data.gameState = GameControlData.STATE_INITIAL;
            data.setPlay = GameControlData.SET_PLAY_NONE;
            data.timeBeforeCurrentGameState = 0;
            data.timeBeforeStoppageOfPlay = 0;
            data.kickOffReason = AdvancedData.KICKOFF_PENALTYSHOOT;
            data.resetPenalties();
            if (Rules.league.timeOutPerHalf) {
                data.timeOutTaken = new boolean[] {false, false};
            }
            Log.state(data, "Penalty Shoot-out");
        }
    }
    
    /**
     * Checks if this action is legal with the given data (model).
     * Illegal actions are not performed by the EventHandler.
     * 
     * @param data      The current data to check with.
     */
    @Override
    public boolean isLegal(AdvancedData data)
    {
        return data.competitionType == GameControlData.COMPETITION_TYPE_NORMAL
                && ((data.gamePhase == GameControlData.GAME_PHASE_PENALTYSHOOT)
                    || (data.previousGamePhase == GameControlData.GAME_PHASE_PENALTYSHOOT)
                    || ((data.firstHalf != GameControlData.C_TRUE)
                        && (data.gameState == GameControlData.STATE_FINISHED)))
                || (data.testmode);
    }
}
