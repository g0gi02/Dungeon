package ecs.entities;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.AnimationComponent;
import ecs.components.PositionComponent;
import ecs.components.VelocityComponent;
import ecs.components.skill.*;
import ecs.components.xp.XPComponent;
import graphic.Animation;
import starter.Game;

import java.util.List;

/**
 * The Hero is the player character. It's entity in the ECS. This class helps to setup the hero with
 * all its components and attributes .
 */
public class Hero extends Entity {

    private final float meleeCollDown = 0.2f;
    private final float fireballCoolDown = 5f;
    private final float icestreamSkillCoolDown = 5/30f;
    private final float speedBoostSkillCoolDown = 6f;
    private final float meleeManaCost = 0f;
    private final float fireballManaCost = 6f;
    private final float icestreamManaCost = 1f;
    private final float speedBoostSkillManaCost = 10f;
    private float manaRegenRate = 1/30f;
    private float maxMana = 999f;
    private final float xSpeed = 0.3f;
    private final float ySpeed = 0.3f;
    private int previousLevel = 0;
    private final int MAX_HEALTH = 100;
    private final String GET_PATH_TO_KNIGHT_DEATH = "animation";

    private static final List<String> missingTexture = List.of("animation/missingTexture.png");
    private final String pathToKnightHit = "knight/hit";
    private final String pathToIdleLeft = "knight/idleLeft";
    private final String pathToIdleRight = "knight/idleRight";
    private final String pathToRunLeft = "knight/runLeft";
    private final String pathToRunRight = "knight/runRight";
    private Skill meleeSkill;
    private Skill firstSkill;
    private Skill secondSkill;
    private Skill thirdSkill;
    private Skill boomerangSkill;
    private Skill bouncingBallSkill;
    private SkillComponent sc;
    private PlayableComponent pc;
    private ManaComponent mc;

    /** Entity with Components */
    public Hero() {
        super();
        new PositionComponent(this);
        setupVelocityComponent();
        setupAnimationComponent();
        setupHitboxComponent();
        setupMeleeSkill();
        setupFireballSkill();
        setupSpeedBoostSkill();
        setupIceSkill();
        setupBouncingBallSkill();
        setupBoomerangSkill();
        this.pc = new PlayableComponent(this);
        this.sc = new SkillComponent(this);
        this.mc = new ManaComponent(this);
        new XPComponent(this);
        setupManaComponent();
        setupHealthComponent();

        unlockFourthSkill();
        unlockFifthSkill();
        unlockMeleeSkill();
    }

    private void setupHealthComponent() {
        Animation knightHit = AnimationBuilder.buildAnimation(pathToKnightHit);
        Animation knightDeath = AnimationBuilder.buildAnimation(GET_PATH_TO_KNIGHT_DEATH);

        new HealthComponent(this,
                            MAX_HEALTH,
                            e -> {Game.gameOverMenu.showEndMenu();
                                Game.levelCounter = 0;},
                            knightHit, knightDeath);
    }

    private void setupVelocityComponent() {
        Animation moveRight = AnimationBuilder.buildAnimation(pathToRunRight);
        Animation moveLeft = AnimationBuilder.buildAnimation(pathToRunLeft);
        new VelocityComponent(this, xSpeed, ySpeed, moveLeft, moveRight);
    }

    private void setupAnimationComponent() {
        Animation idleRight = AnimationBuilder.buildAnimation(pathToIdleRight);
        Animation idleLeft = AnimationBuilder.buildAnimation(pathToIdleLeft);
        new AnimationComponent(this, idleLeft, idleRight);
    }

    private void setupMeleeSkill () {
        meleeSkill =
            new Skill(
                new SwordSlash(SkillTools::getCursorPositionAsPoint, 1f),
                meleeCollDown, meleeManaCost);
    }

    private void setupFireballSkill() {
        firstSkill =
            new Skill(
                new FireballSkill(SkillTools::getCursorPositionAsPoint),
                fireballCoolDown, fireballManaCost);
    }

    private void setupSpeedBoostSkill() {
        secondSkill =
            new Skill(
                new SpeedboostSkill(1.5f), speedBoostSkillCoolDown, speedBoostSkillManaCost);
    }

    private void setupIceSkill() {
        thirdSkill =
            new Skill(
                new IcestreamSkill(SkillTools::getCursorPositionAsPoint),
                icestreamSkillCoolDown, icestreamManaCost);
    }

    private void setupBoomerangSkill() {
        boomerangSkill =
            new Skill(
                new BoomerangSkill(SkillTools::getCursorPositionAsPoint), 6f, 0f);
    }

    private void setupBouncingBallSkill() {
        bouncingBallSkill =
            new Skill(
                new BouncingBallSkill(SkillTools::getCursorPositionAsPoint), 6f, 0f);
    }

    private void setupHitboxComponent() {
        new HitboxComponent(
                this,
                (you, other, direction) -> System.out.println("heroCollisionEnter"),
                (you, other, direction) -> System.out.println("heroCollisionLeave"));
    }

    private void setupManaComponent() {
        this.mc.setMaxMana(this.maxMana);
        this.mc.setManaRegenRate(this.manaRegenRate);
    }

    private void unlockMeleeSkill() {
        this.pc.setMeleeSkill(meleeSkill);
        this.sc.addSkill(meleeSkill);
    }
    
    // Fireball skill
    private void unlockFirstSkill() {
        this.pc.setSkillSlot1(firstSkill);
        this.sc.addSkill(firstSkill);
    }

    // Speed boost skill
    private void unlockSecondSkill() {
        this.pc.setSkillSlot2(secondSkill);
        this.sc.addSkill(secondSkill);
    }

    // Ice stream skill
    private void unlockThirdSkill() {
        this.pc.setSkillSlot3(thirdSkill);
        this.sc.addSkill(thirdSkill);
    }

    // Boomrang skill is unlocked by default
    private void unlockFourthSkill() {
        this.pc.setSkillSlot4(boomerangSkill);
        this.sc.addSkill(boomerangSkill);
    }

    // Bouncing ball skill is unlocked by default
    private void unlockFifthSkill() {
        this.pc.setSkillSlot5(bouncingBallSkill);
        this.sc.addSkill(bouncingBallSkill);
    }

    /**
     * add xp to the heros XPComponent, and unlocks Skills at set levels
     *
     * @param xp amount of xp to add
     */
    public void addXP(long xp) {
        Game.getHero().map(Hero.class::cast)
            .ifPresent(h -> h.getComponent(XPComponent.class)
                .map(c -> {
                    XPComponent xpc = (XPComponent) c;
                    xpc.addXP(xp);
                    if (this.pc.getSkillSlot1().isEmpty() && xpc.getCurrentLevel() > 0) unlockFirstSkill();
                    if (this.pc.getSkillSlot2().isEmpty() && xpc.getCurrentLevel() > 1) unlockSecondSkill();
                    if (this.pc.getSkillSlot3().isEmpty() && xpc.getCurrentLevel() > 2) unlockThirdSkill();
                    if (xpc.getCurrentLevel()>this.previousLevel) {
                        this.mc.setMaxMana(this.mc.getMaxMana()+2f);
                        this.previousLevel++;
                    }
                    System.out.println("current lvl: "+(xpc.getCurrentLevel() + 1) +
                        " xp to next lvl: " + xpc.getXPToNextLevel() +
                        " maxMana: "+this.mc.getMaxMana());
                    return null;
                })
            );
    }
}
