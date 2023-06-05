package ecs.components.skill;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import ecs.components.PositionComponent;
import ecs.components.VelocityComponent;
import starter.Game;
import tools.Point;
import ecs.components.MissingComponentException;
import ecs.entities.Entity;

public class SkillTools {

    /**
     * calculates the last position in range regardless of aimed position
     *
     * @param startPoint position to start the calculation
     * @param aimPoint point to aim for
     * @param range range from start to
     * @return last position in range if you follow the directon from startPoint to aimPoint
     */
    public static Point calculateLastPositionInRange(
            Point startPoint, Point aimPoint, float range) {

        // calculate distance from startPoint to aimPoint
        float dx = aimPoint.x - startPoint.x;
        float dy = aimPoint.y - startPoint.y;

        // vector from startPoint to aimPoint
        Vector2 scv = new Vector2(dx, dy);

        // normalize the vector (length of 1)
        scv.nor();

        // resize the vector to the length of the range
        scv.scl(range);

        return new Point(startPoint.x + scv.x, startPoint.y + scv.y);
    }

    public static Point calculateVelocity(Point start, Point goal, float speed) {
        float x1 = start.x;
        float y1 = start.y;
        float x2 = goal.x;
        float y2 = goal.y;

        float dx = x2 - x1;
        float dy = y2 - y1;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float velocityX = dx / distance * speed;
        float velocityY = dy / distance * speed;
        return new Point(velocityX, velocityY);
    }

    /**
     * causes knockback to target entity
     * @param cause
     * @param target
     */
    public static void causeKnockBack(Entity cause, Entity target) {
        // The the velocity component of the target entity
        // The Method getUnitDirectionalVector returns a vector between two points, as a point.
        // That vector has the right direction for the knockback and
        // is then used to set the velocity of the target entity
        target.getComponent(VelocityComponent.class)
        .ifPresent(vlc -> {
                ((VelocityComponent) vlc).setCurrentXVelocity(
                    // Set the x velocity to the x value of the vector between the target and the cause
                        Point.getUnitDirectionalVector(
                                ((PositionComponent) target
                                        .getComponent(PositionComponent.class)
                                        .get())
                                        .getPosition(),
                                ((ProjectileComponent) cause
                                        .getComponent(ProjectileComponent.class)
                                        .get())
                                        .getStartPosition()).x
                                );
                ((VelocityComponent) vlc).setCurrentYVelocity(
                    // Set the y velocity to the y value of the vector between the target and the cause
                        Point.getUnitDirectionalVector(
                                ((PositionComponent) target
                                        .getComponent(PositionComponent.class)
                                        .get())
                                        .getPosition(),
                                ((ProjectileComponent) cause
                                        .getComponent(ProjectileComponent.class)
                                        .get())
                                        .getStartPosition()).y
                                );
        });
    }

    public static void causeKnockBack(Entity cause, Entity target, float knockbackFactor) {
        // The the velocity component of the target entity
        // The Method getUnitDirectionalVector returns a vector between two points, as a point.
        // That vector has the right direction for the knockback and
        // is then used to set the velocity of the target entity
        target.getComponent(VelocityComponent.class)
        .ifPresent(vlc -> {
                ((VelocityComponent) vlc).setCurrentXVelocity(
                    // Set the x velocity to the x value of the vector between the target and the cause
                        Point.getUnitDirectionalVector(
                                ((PositionComponent) target
                                        .getComponent(PositionComponent.class)
                                        .get())
                                        .getPosition(),
                                ((ProjectileComponent) cause
                                        .getComponent(ProjectileComponent.class)
                                        .get())
                                        .getStartPosition()).x
                                // Multiply the x value of the vector by the knockbackFactor
                                * knockbackFactor);
                ((VelocityComponent) vlc).setCurrentYVelocity(
                    // Set the y velocity to the y value of the vector between the target and the cause
                        Point.getUnitDirectionalVector(
                                ((PositionComponent) target
                                        .getComponent(PositionComponent.class)
                                        .get())
                                        .getPosition(),
                                ((ProjectileComponent) cause
                                        .getComponent(ProjectileComponent.class)
                                        .get())
                                        .getStartPosition()).y
                                // Multiply the y value of the vector by the knockbackFactor
                                * knockbackFactor);
        });
    }

    /**
     * gets the current cursor position as Point
     *
     * @return mouse cursor position as Point
     */
    public static Point getCursorPositionAsPoint() {
        Vector3 mousePosition =
                Game.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        return new Point(mousePosition.x, mousePosition.y);
    }

    public static Point getHeroPositionAsPoint() {
        Entity hero = Game.getHero().get();
        PositionComponent positionComponent =
                (PositionComponent)
                        hero.getComponent(PositionComponent.class)
                                .orElseThrow(() -> new MissingComponentException("PositionComponent"));
        System.out.println(positionComponent.getPosition());
        return positionComponent.getPosition();
    }
}
