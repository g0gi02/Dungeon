package ecs.systems;

import ecs.Quests.LevelQuest;
import ecs.Quests.Quest;
import ecs.components.ai.AITools;
import ecs.entities.Hero;
import ecs.entities.Questmaster;
import starter.Game;

import java.util.Optional;

public class QuestSystem extends ECS_System {

    @Override
    public void update() {
        Quest.getAllQuests().stream().forEach((Quest::trackProgress));
        Quest.getAllQuests().stream().filter(Quest ::isFinished).forEach((Quest::giveReward));
        Quest.getAllQuests().removeIf(Quest::isFinished);
    }

}

