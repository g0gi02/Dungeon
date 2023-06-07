package ecs.entities;


import ecs.Quests.BossmonsterQuest;
import ecs.Quests.LevelQuest;
import ecs.Quests.Quest;
import ecs.components.InteractionComponent;
import level.tools.LevelElement;
import starter.Game;
import tools.Point;
import java.util.Random;

public class Questmaster extends NPC {
    private static final float X_SPEED = 0.0f;
    private static final float Y_SPEED = 0.0f;
    private static final String PATH_TO_IDLE_LEFT = "wizard/idleLeft";
    private static final String PATH_TO_IDLE_RIGHT = "wizard/idleRight";
    private static final String PATH_TO_RUN_LEFT = "wizard/idleLeft";
    private static final String PATH_TO_RUN_RIGHT = "wizard/idleRight";
    private static final Float defaultInteractionRadius = 2.0f;

    public boolean hasInteracted = false;

    public Questmaster(Point position) {
        super(
            X_SPEED,
            Y_SPEED,
            PATH_TO_IDLE_LEFT,
            PATH_TO_IDLE_RIGHT,
            PATH_TO_RUN_LEFT,
            PATH_TO_RUN_RIGHT,
            position
        );
        new InteractionComponent(this, defaultInteractionRadius, false, this::onTrigger);
    }

    public static Questmaster createNewQuestmaster() {
        return new Questmaster(
            Game.currentLevel.getRandomTile(LevelElement.FLOOR).getCoordinate().toPoint());
    }

    private void onTrigger(Entity entity) {
        hasInteracted = true;
    }
}
//Math.random() > 0.5
