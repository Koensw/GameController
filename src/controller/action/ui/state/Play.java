package controller.action.ui.state;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.AdvancedData;
import data.GameControlData;
import data.PlayerInfo;
import data.TeamInfo;


/**
 * @author Michel Bartsch
 *
 * This action means that the state is to be set to play.
 */
public class Play extends GCAction
{
    /**
     * Creates a new Play action.
     * Look at the ActionBoard before using this.
     */
    public Play()
    {
        super(ActionType.UI);
    }

    /**
     * Performs this action to manipulate the data (model).
     *
     * @param data      The current data to work on.
     */
    @Override
    public void perform(AdvancedData data) {
        if (data.gameState == GameControlData.STATE_PLAYING) {
            if (data.setPlay != GameControlData.SET_PLAY_NONE) {
                final byte setPlay = data.setPlay;
                data.setPlay = GameControlData.SET_PLAY_NONE;
                if (setPlay == GameControlData.SET_PLAY_GOAL_KICK) {
                    Log.state(data, "Goal Kick Complete");
                } else if (setPlay == GameControlData.SET_PLAY_PUSHING_FREE_KICK) {
                    Log.state(data, "Pushing Free Kick Complete");
                } else if (setPlay == GameControlData.SET_PLAY_CORNER_KICK) {
                    Log.state(data, "Corner Kick Complete");
                } else if (setPlay == GameControlData.SET_PLAY_KICK_IN) {
                    Log.state(data, "Kick In Complete");
                } else if (setPlay == GameControlData.SET_PLAY_PENALTY_KICK) {
                    Log.state(data, "Penalty Kick Complete");
                } else {
                    assert false;
                }
            }
            return;
        }
        if (data.competitionPhase != GameControlData.COMPETITION_PHASE_PLAYOFF && data.timeBeforeCurrentGameState != 0) {
            data.addTimeInCurrentState();
        }
        if (data.gameState == GameControlData.STATE_SET) {
            data.addTimeInCurrentStateToPenalties();
        }

        data.whenCurrentGameStateBegan = data.getTime();
        data.gameState = GameControlData.STATE_PLAYING;

        Log.state(data, "Playing");
    }

    /**
     * Checks if this action is legal with the given data (model).
     * Illegal actions are not performed by the EventHandler.
     *
     * @param data      The current data to check with.
     */
    @Override
    public boolean isLegal(AdvancedData data) {
        return (data.gameState == GameControlData.STATE_SET
                && (data.gamePhase != GameControlData.GAME_PHASE_PENALTYSHOOT || bothTeamsHavePlayers(data)))
                || (data.gameState == GameControlData.STATE_PLAYING) || data.testmode;
    }

    /**
     * Checks whether both teams have at least one player that is not penalized.
     * This is a precondition for a penalty shot.
     *
     * @param data      The current data to check with.
     * @return At least one player not penalized in both teams?
     */
    private boolean bothTeamsHavePlayers(AdvancedData data)
    {
        boolean bothPlaying = true;
        for (TeamInfo teamInfo : data.team) {
            boolean playing = false;
            for (PlayerInfo playerInfo : teamInfo.player) {
                playing |= playerInfo != null && playerInfo.penalty == PlayerInfo.PENALTY_NONE;
            }
            bothPlaying &= playing;
        }
        return bothPlaying;
    }
}
