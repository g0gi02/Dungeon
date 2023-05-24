package ecs.components.ai.transition;

import ecs.entities.Entity;
public class friendlyTransition  implements ITransition{
    @Override
    public boolean isInFightMode(Entity entity){
        return false;
    }
}
