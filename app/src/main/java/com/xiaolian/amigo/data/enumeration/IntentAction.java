package com.xiaolian.amigo.data.enumeration;

/**
 * intent action
 * <p>
 * Created by zcd on 17/11/10.
 */

public enum  IntentAction {
    UNKNOWN(0),
    ACTION_CHOOSE_FAVORITE_FOR_REPAIR(1);
    private int type;

    IntentAction(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static IntentAction getAction(int type) {
        for (IntentAction action : IntentAction.values()) {
            if (action.getType() == type) {
                return action;
            }
        }
        return UNKNOWN;
    }

}
