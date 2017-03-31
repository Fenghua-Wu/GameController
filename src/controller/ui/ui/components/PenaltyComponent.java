package controller.ui.ui.components;

import controller.EventHandler;
import controller.action.ActionBoard;
import controller.action.GCAction;
import controller.action.ui.penalty.BallManipulation;
import controller.action.ui.penalty.Penalty;
import controller.ui.ui.customized.ToggleButton;
import data.Rules;
import data.hl.HL;
import data.spl.SPL;
import data.states.AdvancedData;
import data.values.Penalties;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rkessler on 2017-03-29.
 */
public class PenaltyComponent extends AbstractComponent implements SelectedPenalty {

    private static final String PEN_PUSHING = "Pushing";
    private static final String PEN_LEAVING = "Leaving the Field";
    private static final String PEN_MOTION_IN_SET = "Motion in Set";
    private static final String PEN_MOTION_IN_SET_SHORT = "Motion";
    private static final String PEN_INACTIVE = "Fallen / Inactive / Local Game Stuck";
    private static final String PEN_DEFENDER = "Illegal Defender";
    private static final String PEN_BALL_CONTACT = "Ball Holding / Hands";
    private static final String PEN_KICK_OFF_GOAL = "Kickoff Goal";
    private static final String PEN_COACH_MOTION = "Coach Motion";
    private static final String PEN_PICKUP = "Pick-Up";
    private static final String PEN_MANIPULATION = "Ball Manipulation";
    private static final String PEN_PHYSICAL = "Physical Contact";
    private static final String PEN_DEFENSE = "Illegal Defense";
    private static final String PEN_ATTACK = "Illegal Attack";
    private static final String PEN_PICKUP_INCAPABLE = "Pickup/Incapable";
    private static final String PEN_SERVICE = "Service";
    private static final String PEN_SUBSTITUTE = "Substitute";
    private static final String PEN_SUBSTITUTE_SHORT = "Sub";

    private Map<Penalties, JToggleButton> penaltyButtons;
    private ButtonGroup penaltyGroup;
    private ArrayList<Notifiable> notifiables;

    public PenaltyComponent(){
        penaltyButtons = new HashMap<>();
        penaltyGroup = new ButtonGroup();
        notifiables = new ArrayList<>();

        defineLayout();
    }

    public void addNotifiers(Notifiable nf){
        notifiables.add(nf);
    }

    public void notifyObservers(){
        for(Notifiable n: notifiables){
            n.notifyObservers();
        }
    }

    @Override
    public Penalties selectedPenalty(){
        for(Penalties pen : penaltyButtons.keySet()){
            ButtonModel bm = penaltyButtons.get(pen).getModel();
            if (bm == penaltyGroup.getSelection()){
                return pen;
            }
        }

        return null;
    }

    @Override
    public void reset() {
        penaltyGroup.clearSelection();
    }

    public boolean selectPenalty(Penalties pen){
        if (penaltyButtons.containsKey(pen)){
            penaltyButtons.get(pen).setSelected(true);
            return true;
        }else {
            return false;
        }

    }

    void addButtonByPenalty(Penalties pen, Penalty ballManipulation){
        ToggleButton butt = new ToggleButton(pen.toString());
        butt.addActionListener(ballManipulation);
        penaltyButtons.put(pen, butt);
        penaltyGroup.add(butt);
        this.add(butt);
    }

    public void defineLayout(){
        // TODO Customizable with inheritance

        if (Rules.league instanceof SPL) {
            addButtonByPenalty(Penalties.SPL_PLAYER_PUSHING, ActionBoard.pushing);
            addButtonByPenalty(Penalties.SPL_LEAVING_THE_FIELD, ActionBoard.leaving );
            addButtonByPenalty(Penalties.SPL_INACTIVE_PLAYER, ActionBoard.inactive);
            addButtonByPenalty(Penalties.SPL_ILLEGAL_DEFENDER,  ActionBoard.defender);
            addButtonByPenalty(Penalties.SPL_ILLEGAL_MOTION_IN_SET,  ActionBoard.motionInSet);
            addButtonByPenalty(Penalties.SPL_KICK_OFF_GOAL, ActionBoard.kickOffGoal);
            addButtonByPenalty(Penalties.SPL_ILLEGAL_BALL_CONTACT, ActionBoard.ballContact);
            addButtonByPenalty(Penalties.SPL_REQUEST_FOR_PICKUP, ActionBoard.pickUp);
            addButtonByPenalty(Penalties.SUBSTITUTE, ActionBoard.substitute);

            if (Rules.league.dropInPlayerMode){
                // TODO - Figure out what the SPL wants here?
                //addButtonByPenalty(Penalties.TEAM_MATE_PUSHING);
            } else {
                addButtonByPenalty(Penalties.SPL_COACH_MOTION, ActionBoard.ballManipulation);
            }
        }

        if (Rules.league instanceof HL) {
            addButtonByPenalty(Penalties.HL_BALL_MANIPULATION, ActionBoard.ballManipulation);
            addButtonByPenalty(Penalties.HL_PHYSICAL_CONTACT, ActionBoard.pushing);
            addButtonByPenalty(Penalties.HL_ILLEGAL_ATTACK, ActionBoard.attack);
            addButtonByPenalty(Penalties.HL_ILLEGAL_DEFENSE, ActionBoard.defense);
            addButtonByPenalty(Penalties.HL_PICKUP_OR_INCAPABLE, ActionBoard.pickUpHL);
            addButtonByPenalty(Penalties.HL_SERVICE, ActionBoard.serviceHL);
            addButtonByPenalty(Penalties.SUBSTITUTE, ActionBoard.substitute);
        }

        this.setVisible(true);
        this.setLayout(
                new BoxLayout(this, BoxLayout.Y_AXIS)
        );
    }

    @Override
    public void update(AdvancedData data) {
        if (Rules.league instanceof HL) {
            updatePenaltiesHL(data);
        } else {
            updatePenaltiesSPL(data);
        }
    }


    private void updatePenaltiesSPL(AdvancedData data)
    {
        penaltyButtons.get(Penalties.SPL_PLAYER_PUSHING).setEnabled(ActionBoard.pushing.isLegal(data));
        penaltyButtons.get(Penalties.SPL_LEAVING_THE_FIELD).setEnabled(ActionBoard.leaving.isLegal(data));
        penaltyButtons.get(Penalties.SPL_INACTIVE_PLAYER).setEnabled(ActionBoard.inactive.isLegal(data));

        penaltyButtons.get(Penalties.SPL_INACTIVE_PLAYER).setText("<html><center>"
                +(ActionBoard.inactive.isLegal(data) ? "<font color=#000000>" : "<font color=#808080>")
                +PEN_INACTIVE);

        penaltyButtons.get(Penalties.SPL_ILLEGAL_DEFENDER).setEnabled(ActionBoard.defender.isLegal(data));
        penaltyButtons.get(Penalties.SPL_ILLEGAL_MOTION_IN_SET).setEnabled(ActionBoard.motionInSet.isLegal(data));
        penaltyButtons.get(Penalties.SPL_KICK_OFF_GOAL).setEnabled(ActionBoard.kickOffGoal.isLegal(data));
        penaltyButtons.get(Penalties.SPL_ILLEGAL_BALL_CONTACT).setEnabled(ActionBoard.ballContact.isLegal(data));
        penaltyButtons.get(Penalties.SPL_REQUEST_FOR_PICKUP).setEnabled(ActionBoard.pickUp.isLegal(data));

//        pen[8].setEnabled(Rules.league.dropInPlayerMode ? ActionBoard.teammatePushing.isLegal(data)
//                : ActionBoard.coachMotion.isLegal(data));

        penaltyButtons.get(Penalties.SUBSTITUTE).setEnabled(ActionBoard.substitute.isLegal(data));

        GCAction hightlightEvent = EventHandler.getInstance().lastUIEvent;
        penaltyButtons.get(Penalties.SPL_PLAYER_PUSHING).setSelected(hightlightEvent == ActionBoard.pushing);
        penaltyButtons.get(Penalties.SPL_LEAVING_THE_FIELD).setSelected(hightlightEvent == ActionBoard.leaving);
        penaltyButtons.get(Penalties.SPL_INACTIVE_PLAYER).setSelected(hightlightEvent == ActionBoard.inactive);

        penaltyButtons.get(Penalties.SPL_ILLEGAL_DEFENDER).setSelected(hightlightEvent == ActionBoard.defender);
        penaltyButtons.get(Penalties.SPL_KICK_OFF_GOAL).setSelected(hightlightEvent == ActionBoard.kickOffGoal);
        penaltyButtons.get(Penalties.SPL_ILLEGAL_BALL_CONTACT).setSelected(hightlightEvent == ActionBoard.ballContact);
        penaltyButtons.get(Penalties.SPL_REQUEST_FOR_PICKUP).setSelected(hightlightEvent == ActionBoard.pickUp);
//
//        pen[8].setSelected(Rules.league.dropInPlayerMode ? hightlightEvent == ActionBoard.teammatePushing
//                : hightlightEvent == ActionBoard.coachMotion);
//
        penaltyButtons.get(Penalties.SUBSTITUTE).setSelected(hightlightEvent == ActionBoard.substitute);

        // Handle quick select for ILLEGAL_MOTION_IN_SET
        //        if (pen[4].isEnabled()) {
        //            boolean otherButtonSelected = false;
        //            for (JToggleButton button : pen) {
        //                otherButtonSelected |= button != pen[4] && button.isSelected();
        //            }
        //            for (JToggleButton button : undo) {
        //                otherButtonSelected |= button.isSelected();
        //            }
        //            pen[4].setSelected(!otherButtonSelected);
        //            if (!otherButtonSelected) {
        //                EventHandler.getInstance().lastUIEvent = ActionBoard.motionInSet;
        //            }
        //        } else {
        //            pen[4].setSelected(EventHandler.getInstance().lastUIEvent == ActionBoard.motionInSet);
        //        }
    }

    private void updatePenaltiesHL(AdvancedData data)
    {
        penaltyButtons.get(Penalties.HL_BALL_MANIPULATION).setEnabled(ActionBoard.ballManipulation.isLegal(data));
        penaltyButtons.get(Penalties.HL_PHYSICAL_CONTACT).setEnabled(ActionBoard.pushing.isLegal(data));
        penaltyButtons.get(Penalties.HL_ILLEGAL_ATTACK).setEnabled(ActionBoard.attack.isLegal(data));
        penaltyButtons.get(Penalties.HL_ILLEGAL_DEFENSE).setEnabled(ActionBoard.defense.isLegal(data));
        penaltyButtons.get(Penalties.HL_PICKUP_OR_INCAPABLE).setEnabled(ActionBoard.pickUpHL.isLegal(data));
        penaltyButtons.get(Penalties.HL_SERVICE).setEnabled(ActionBoard.serviceHL.isLegal(data));
        penaltyButtons.get(Penalties.SUBSTITUTE).setEnabled(ActionBoard.substitute.isLegal(data));


        GCAction hightlightEvent = EventHandler.getInstance().lastUIEvent;
        penaltyButtons.get(Penalties.HL_BALL_MANIPULATION).setSelected(hightlightEvent == ActionBoard.ballManipulation);
        penaltyButtons.get(Penalties.HL_PHYSICAL_CONTACT).setSelected(hightlightEvent == ActionBoard.pushing);
        penaltyButtons.get(Penalties.HL_ILLEGAL_ATTACK).setSelected(hightlightEvent == ActionBoard.attack);
        penaltyButtons.get(Penalties.HL_ILLEGAL_DEFENSE).setSelected(hightlightEvent == ActionBoard.defense);
        penaltyButtons.get(Penalties.HL_PICKUP_OR_INCAPABLE).setSelected(hightlightEvent == ActionBoard.pickUpHL);
        penaltyButtons.get(Penalties.HL_SERVICE).setSelected(hightlightEvent == ActionBoard.serviceHL);
        penaltyButtons.get(Penalties.SUBSTITUTE).setSelected(hightlightEvent == ActionBoard.substitute);
    }
}
