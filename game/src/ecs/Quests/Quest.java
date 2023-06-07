package ecs.Quests;

import java.util.ArrayList;

public abstract class Quest {
    public String questname;
    public String progressionMessage;
    public String description;
    public static ArrayList<Quest> questList = new ArrayList<>();


    public Quest(String questname, String description) {
        this.description = description;
        this.questname = questname;
        questList.add(this);
    }

    public String getProgressionMessage() {
        return progressionMessage;
    }

    public abstract void trackProgress();

    public abstract boolean isFinished();

    public abstract void giveReward();

    public String getDescription() {
        return description;
    }

    public String getQuestname() {
        return questname;
    }

    public static ArrayList<Quest> getAllQuests() {
        return questList;
    }


}
