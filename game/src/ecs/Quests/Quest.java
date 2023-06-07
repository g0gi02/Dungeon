package ecs.Quests;

import java.util.ArrayList;
import java.util.logging.Logger;


public abstract class Quest {
    public String questname;
    public String progressionMessage;
    public String description;
    public static ArrayList<Quest> questList = new ArrayList<>();

    /**
     * Creates a new Quest
     * @param questname
     * @param description
     */
    public Quest(String questname, String description) {
        this.description = description;
        this.questname = questname;
        questList.add(this);
    }

    /**
     * Returns the progression message of the quest
     * @return progressionMessage
     */
    public String getProgressionMessage() {
        return progressionMessage;
    }

    /**
     * Tracks the progress of the quest
     */
    public abstract void trackProgress();

    /**
     * Checks if the quest is finished
     * @return true if the quest is finished, false if not
     */
    public abstract boolean isFinished();

    /**
     * Reward for the quest
     */
    public abstract void giveReward();

    /**
     * Returns the description of the quest
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the name of the quest
     * @return questname
     */
    public String getQuestname() {
        return questname;
    }

    /**
     * Returns all quests
     * @return questList
     */
    public static ArrayList<Quest> getAllQuests() {
        return questList;
    }


}
