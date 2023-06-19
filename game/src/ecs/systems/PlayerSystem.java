package ecs.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import configuration.KeyboardConfig;
import ecs.components.InventoryComponent;
import ecs.components.MissingComponentException;
import ecs.components.PlayableComponent;
import ecs.components.VelocityComponent;
import ecs.entities.Entity;
import ecs.tools.interaction.InteractionTool;
import starter.Game;

/** Used to control the player */
public class PlayerSystem extends ECS_System {

    private record KSData(Entity e, PlayableComponent pc, VelocityComponent vc, InventoryComponent ic) {}

    @Override
    public void update() {
        Game.getEntities().stream()
                .flatMap(e -> e.getComponent(PlayableComponent.class).stream())
                .map(pc -> buildDataObject((PlayableComponent) pc))
                .forEach(this::checkKeystroke);
    }

    private void checkKeystroke(KSData ksd) {
        if (Gdx.input.isKeyPressed(KeyboardConfig.MOVEMENT_UP.get()))
            ksd.vc.setCurrentYVelocity(1 * ksd.vc.getYVelocity());
        else if (Gdx.input.isKeyPressed(KeyboardConfig.MOVEMENT_DOWN.get()))
            ksd.vc.setCurrentYVelocity(-1 * ksd.vc.getYVelocity());
        else if (Gdx.input.isKeyPressed(KeyboardConfig.MOVEMENT_RIGHT.get()))
            ksd.vc.setCurrentXVelocity(1 * ksd.vc.getXVelocity());
        else if (Gdx.input.isKeyPressed(KeyboardConfig.MOVEMENT_LEFT.get()))
            ksd.vc.setCurrentXVelocity(-1 * ksd.vc.getXVelocity());

        if (Gdx.input.isKeyPressed(KeyboardConfig.INTERACT_WORLD.get()))
            InteractionTool.interactWithClosestInteractable(ksd.e);

        // check skills
        else if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
            ksd.pc.getMeleeSkill().ifPresent(skill -> skill.execute(ksd.e));
        else if (Gdx.input.isKeyPressed(KeyboardConfig.FIRST_SKILL.get()))
            ksd.pc.getSkillSlot1().ifPresent(skill -> skill.execute(ksd.e));
        else if (Gdx.input.isKeyPressed(KeyboardConfig.SECOND_SKILL.get()))
            ksd.pc.getSkillSlot2().ifPresent(skill -> skill.execute(ksd.e));
        else if (Gdx.input.isKeyPressed(KeyboardConfig.THIRD_SKILL.get()))
            ksd.pc.getSkillSlot3().ifPresent(skill -> skill.execute(ksd.e));
        else if (Gdx.input.isKeyPressed(KeyboardConfig.FOURTH_SKILL.get()))
            ksd.pc.getSkillSlot4().ifPresent(skill -> skill.execute(ksd.e));
        else if (Gdx.input.isKeyPressed(KeyboardConfig.FIFTH_SKILL.get()))
            ksd.pc.getSkillSlot5().ifPresent(skill -> skill.execute(ksd.e));

        // check inventory
        else if (Gdx.input.isKeyJustPressed(KeyboardConfig.INVENTORY.get()))
        ksd.ic.setOpen();
        else if (Gdx.input.isKeyJustPressed(KeyboardConfig.INVENTORY_DROP.get()))
        ksd.ic.removeLastItem();
        else if (Gdx.input.isKeyJustPressed(KeyboardConfig.INVENTORY_FIRST.get()))
            ksd.ic.useItem(0);
        else if (Gdx.input.isKeyJustPressed(KeyboardConfig.INVENTORY_SECOND.get()))
            ksd.ic.useItem(1);
        else if (Gdx.input.isKeyJustPressed(KeyboardConfig.INVENTORY_THIRD.get()))
            ksd.ic.useItem(2);
        else if (Gdx.input.isKeyJustPressed(KeyboardConfig.INVENTORY_FOURTH.get()))
            ksd.ic.useItem(3);
        else if (Gdx.input.isKeyJustPressed(KeyboardConfig.INVENTORY_FIFTH.get()))
            ksd.ic.useItem(4);
        else if (Gdx.input.isKeyJustPressed(KeyboardConfig.INVENTORY_SIXTH.get()))
            ksd.ic.useItem(5);
        else if (Gdx.input.isKeyJustPressed(KeyboardConfig.INVENTORY_SEVENTH.get()))
            ksd.ic.useItem(6);
        else if (Gdx.input.isKeyJustPressed(KeyboardConfig.INVENTORY_EIGHTH.get()))
            ksd.ic.useItem(7);
        else if (Gdx.input.isKeyJustPressed(KeyboardConfig.INVENTORY_NINTH.get()))
            ksd.ic.useItem(8);
        else if (Gdx.input.isKeyJustPressed(KeyboardConfig.INVENTORY_FOURTH.get()))
            ksd.ic.useItem(9);
    }

    private KSData buildDataObject(PlayableComponent pc) {
        Entity e = pc.getEntity();

        VelocityComponent vc =
                (VelocityComponent)
                        e.getComponent(VelocityComponent.class)
                                .orElseThrow(PlayerSystem::missingVC);

        InventoryComponent ic =
                (InventoryComponent)
                        e.getComponent(InventoryComponent.class)
                                .orElseThrow(PlayerSystem::missingIC);

        return new KSData(e, pc, vc, ic);
    }

    private static MissingComponentException missingVC() {
        return new MissingComponentException("VelocityComponent");
    }

    private static MissingComponentException missingIC() {
        return new MissingComponentException("InventoryComponent");
    }
}
