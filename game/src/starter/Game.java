package starter;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static logging.LoggerConfig.initBaseLogger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import configuration.Configuration;
import configuration.KeyboardConfig;
import controller.AbstractController;
import controller.SystemController;
import ecs.Quests.BossmonsterQuest;
import ecs.Quests.LevelQuest;
import ecs.Quests.Quest;
import ecs.components.*;
import ecs.components.ai.AIComponent;
import ecs.components.ai.*;
import ecs.components.HealthComponent;
import ecs.components.MissingComponentException;
import ecs.components.PositionComponent;
import ecs.entities.*;
import ecs.entities.Imp;
import ecs.items.ItemData;
import ecs.systems.*;

import graphic.DungeonCamera;
import graphic.Painter;
import graphic.hud.LockpickingGame;
import graphic.hud.QuestMenus.ActiveQuestMenu;
import graphic.hud.GameOverMenu;
import graphic.hud.PauseMenu;
import graphic.hud.QuestMenus.QuestMenu;
import graphic.textures.TextureHandler;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import level.IOnLevelLoader;
import level.LevelAPI;
import level.elements.ILevel;
import level.elements.tile.Tile;
import level.generator.IGenerator;
import level.generator.postGeneration.WallGenerator;
import level.generator.randomwalk.RandomWalkGenerator;
import level.tools.LevelSize;
import tools.Constants;
import tools.Point;

/**
 * The heart of the framework. From here all strings are pulled.
 */
public class Game extends ScreenAdapter implements IOnLevelLoader {

    private final LevelSize LEVELSIZE = LevelSize.MEDIUM;

    /**
     * The batch is necessary to draw ALL the stuff. Every object that uses draw need to know the
     * batch.
     */
    protected SpriteBatch batch;

    /**
     * Contains all Controller of the Dungeon
     */
    protected List<AbstractController<?>> controller;

    public static DungeonCamera camera;
    /**
     * Draws objects
     */
    protected Painter painter;

    protected LevelAPI levelAPI;
    /**
     * Generates the level
     */
    protected IGenerator generator;

    private boolean doSetup = true;
    private static boolean paused = false;

    /**
     * A handler for managing asset paths
     */
    private static TextureHandler handler;

    /**
     * All entities that are currently active in the dungeon
     */
    private static final Set<Entity> entities = new HashSet<>();
    /**
     * All entities to be removed from the dungeon in the next frame
     */
    private static final Set<Entity> entitiesToRemove = new HashSet<>();
    /**
     * All entities to be added from the dungeon in the next frame
     */
    private static final Set<Entity> entitiesToAdd = new HashSet<>();

    /**
     * List of all Systems in the ECS
     */
    public static SystemController systems;

    public static ILevel currentLevel;
    public static int levelCounter = 0;

    public static boolean dragonExists = false;

    private static PauseMenu<Actor> pauseMenu;
    public static GameOverMenu<Actor> gameOverMenu;

    public static QuestMenu<Actor> questMenu;
    public static ActiveQuestMenu<Actor> activeQuestMenu;
    public static LockpickingGame<Actor> lockpickingGame;

    private static boolean hasOngoingQuest = false;
    private static Entity hero;
    private Logger gameLogger;
    public Questmaster questmaster;
    private boolean hasShownQuestMenuThisLevel = false;

    public static void main(String[] args) {
        // start the game
        try {
            Configuration.loadAndGetConfiguration("dungeon_config.json", KeyboardConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DesktopLauncher.run(new Game());
    }

    public static int getCurrentLevelCounter() {
        return levelCounter;
    }

    /**
     * Main game loop. Redraws the dungeon and calls the own implementation (beginFrame, endFrame
     * and onLevelLoad).
     *
     * @param delta Time since last loop.
     */
    @Override
    public void render(float delta) {
        if (doSetup) setup();
        batch.setProjectionMatrix(camera.combined);
        frame();
        clearScreen();
        levelAPI.update();
        controller.forEach(AbstractController::update);
        camera.update();
    }

    /**
     * Called once at the beginning of the game.
     */
    protected void setup() {
        doSetup = false;
        /*
         * THIS EXCEPTION HANDLING IS A TEMPORARY WORKAROUND !
         *
         * <p>The TextureHandler can throw an exception when it is first created. This exception
         * (IOEception) must be handled somewhere. Normally we want to pass exceptions to the method
         * caller. This approach is (atm) not possible in the libgdx render method because Java does
         * not allow extending method signatures derived from a class. We should try to make clean
         * code out of this workaround later.
         *
         * <p>Please see also discussions at:<br>
         * - https://github.com/Programmiermethoden/Dungeon/pull/560<br>
         * - https://github.com/Programmiermethoden/Dungeon/issues/587<br>
         */

        handler = TextureHandler.getInstance();

        controller = new ArrayList<>();
        setupCameras();
        painter = new Painter(batch, camera);
        generator = new RandomWalkGenerator();
        levelAPI = new LevelAPI(batch, painter, generator, this);
        initBaseLogger();
        gameLogger = Logger.getLogger(this.getClass().getName());
        systems = new SystemController();
        controller.add(systems);
        pauseMenu = new PauseMenu<>();
        gameOverMenu = new GameOverMenu<>();
        questMenu = new QuestMenu<>();
        activeQuestMenu = new ActiveQuestMenu<>();
        lockpickingGame = new LockpickingGame<>();
        controller.add(activeQuestMenu);
        controller.add(pauseMenu);
        controller.add(gameOverMenu);
        controller.add(questMenu);
        controller.add(lockpickingGame);
        hero = new Hero();

        //manageQuestMenus();


        levelAPI = new LevelAPI(batch, painter, new WallGenerator(new RandomWalkGenerator()), this);
        levelAPI.loadLevel(LEVELSIZE);
        createSystems();
    }

    /**
     * Called at the beginning of each frame. Before the controllers call <code>update</code>.
     */
    protected void frame() {
        setCameraFocus();
        manageEntitiesSets();
        getHero().ifPresent(this::loadNextLevelIfEntityIsOnEndTile);
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) togglePause();
        if (gameOverMenu.isMenuOpen) manageGameOverMenuInputs();
        manageQuestMenus();
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) saveGame("game/saves/save.txt");
        if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) loadGame("game/saves/save.txt");
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
        // temp
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)){
            lockpickingGame.startLockpickingGame(null);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            Logger logger = Logger.getLogger("Health");
            Game.getHero().stream()
                .flatMap(e -> e.getComponent(HealthComponent.class).stream())
                .map(HealthComponent.class::cast)
                .forEach(healthComponent -> {
                    logger.info("Hero-Health:" + healthComponent.getCurrentHealthpoints());
                });
        }
    }

    private void manageGameOverMenuInputs() {
        //check Inputs while gameOverMenu is active
        if (Gdx.input.isKeyPressed(Input.Keys.K)) {
            gameLogger.info("game has endet");
            Gdx.app.exit();
            //remove all Entities and place a new hero
        } else if (Gdx.input.isKeyPressed(Input.Keys.L)) {
            Hero hero = new Hero();
            Set<Entity> allEntities = Game.getEntities();
            Iterator<Entity> entityIterator = allEntities.iterator();
            while (entityIterator.hasNext()) {
                Game.removeEntity(entityIterator.next());
            }
            Game.setHero(hero);
            levelCounter = 0;
            gameLogger.info("restart");
            gameOverMenu.hideEndMenu();
        }
    }

    /**
     * Manages the Questmaster input and the Questmenu Input.
     */
    private void manageQuestMenus() {
        if (questmaster.hasInteracted && !hasOngoingQuest) {
            if (!hasShownQuestMenuThisLevel) {
                questMenu.showQuestMenu();
                hasShownQuestMenuThisLevel = true;
                questMenu.isMenuOpen = true;
                systems.forEach(ECS_System::toggleRun);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.H) && questMenu.isMenuOpen) {
                gameLogger.info("Hero accepted the Quest");
                questMenu.isMenuOpen = false;
                questMenu.hideQuestMenu();
                systems.forEach(ECS_System::toggleRun);
                createQuest();


            } else if (Gdx.input.isKeyPressed(Input.Keys.J) && questMenu.isMenuOpen) {
                gameLogger.info("Hero rejected the Quest");
                questMenu.hideQuestMenu();
                questMenu.isMenuOpen = false;
                systems.forEach(ECS_System::toggleRun);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.G) && hasOngoingQuest) {
            toggleActiveQuestMenu();
        }
    }

    /**
     * Called when the Quest Menu is opened.
     * Opens a hud with the Quest information
     */
    public static void toggleActiveQuestMenu() {
        paused = !paused;
        if (systems != null) {
            systems.forEach(ECS_System::toggleRun);
        }
        if (pauseMenu != null) {
            if (paused) activeQuestMenu.showActiveQuestMenu();
            else activeQuestMenu.hideActiveQuestMenu();
        }
    }

    /**
     * Called after a Quest has been accepted.
     * Creates a new Random Quest
     */
    private void createQuest() {
        if (!hasOngoingQuest) {
            if (Math.random() > 0.5) {
                new LevelQuest("Levelmaster", " complete 5 more Levels ");
                activeQuestMenu.setScreenTextQuest("  Levelmaster", "  complete 5 \n more Levels ");
            } else {
                new BossmonsterQuest("Flawless", "defeat the Dragon without even a tiny scratch!");
                activeQuestMenu.setScreenTextQuest("  Flawless", "defeat the Dragon \n without  even a  \n tiny scratch!");
            }
        }
        hasOngoingQuest = true;
    }

    @Override
    public void onLevelLoad() {
        currentLevel = levelAPI.getCurrentLevel();
        levelCounter++;
        gameLogger.info("Level " + levelCounter + " loaded");
        hasShownQuestMenuThisLevel = false;

        if (levelCounter % 10 == 0) {
            DragonP1.createNewDragonP1();
            dragonExists = true;
        } else {
            if (levelCounter % 2 == 0) {
                // Ghost ghost = Ghost.createNewGhost();
                // Gravestone.createNewGravestone(ghost);
            }
            questmaster = Questmaster.createNewQuestmaster();
            //createQuest();

            Mimic_Chest_Trap.createNewMimicChest();
            SlowTrap.createSlowTrap();
            Imp.createNewImp();
            Slime.createNewSlime();
            Chort.createNewChort();
            //If the player has a sword in the inventory, it wont be added again
            InventoryComponent ic = (InventoryComponent) getHero().get().getComponent(InventoryComponent.class).get();
            if(!ic.hasItemOfType("Sword")) addEntity(new SwordItem());
            addEntity(new HealthPotion());
            addEntity(new BombItem());
            addEntity(new BackpackItem());
        }

        entities.clear();
        getHero().ifPresent(this::placeOnLevelStart);
        //add 50 xp to the hero upon entering a level
        Game.getHero().map(h -> (Hero) h).ifPresent(h -> h.addXP(50));
    }

    private void manageEntitiesSets() {
        entities.removeAll(entitiesToRemove);
        entities.addAll(entitiesToAdd);
        for (Entity entity : entitiesToRemove) {
            gameLogger.info("Entity '" + entity.getClass().getSimpleName() + "' was deleted.");
        }
        for (Entity entity : entitiesToAdd) {
            gameLogger.info("Entity '" + entity.getClass().getSimpleName() + "' was added.");
        }
        entitiesToRemove.clear();
        entitiesToAdd.clear();
    }

    private void setCameraFocus() {
        if (getHero().isPresent()) {
            PositionComponent pc =
                (PositionComponent)
                    getHero()
                        .get()
                        .getComponent(PositionComponent.class)
                        .orElseThrow(
                            () ->
                                new MissingComponentException(
                                    "PositionComponent"));
            camera.setFocusPoint(pc.getPosition());

        } else camera.setFocusPoint(new Point(0, 0));
    }

    private void loadNextLevelIfEntityIsOnEndTile(Entity hero) {
        if (isOnEndTile(hero) && !dragonExists) levelAPI.loadLevel(LEVELSIZE);
    }

    private boolean isOnEndTile(Entity entity) {
        PositionComponent pc =
            (PositionComponent)
                entity.getComponent(PositionComponent.class)
                    .orElseThrow(
                        () -> new MissingComponentException("PositionComponent"));
        Tile currentTile = currentLevel.getTileAt(pc.getPosition().toCoordinate());
        return currentTile.equals(currentLevel.getEndTile());
    }

    private void placeOnLevelStart(Entity hero) {
        entities.add(hero);
        PositionComponent pc =
            (PositionComponent)
                hero.getComponent(PositionComponent.class)
                    .orElseThrow(
                        () -> new MissingComponentException("PositionComponent"));
        pc.setPosition(currentLevel.getStartTile().getCoordinate().toPoint());

        // Reset hero's velocity
        Game.getHero().stream()
            .flatMap(e -> e.getComponent(VelocityComponent.class).stream())
            .map(VelocityComponent.class::cast)
            .forEach(VelocityComponent -> {
                VelocityComponent.setXVelocity(0.3f);
                VelocityComponent.setYVelocity(0.3f);
            });
    }

    public static TextureHandler getHandler() {
        return handler;
    }

    /**
     * Toggle between pause and run
     */
    public static void togglePause() {
        paused = !paused;
        if (systems != null) {
            systems.forEach(ECS_System::toggleRun);
        }
        if (pauseMenu != null) {
            if (paused) pauseMenu.showMenu();
            else pauseMenu.hideMenu();
        }
    }

    /**
     * Given entity will be added to the game in the next frame
     *
     * @param entity will be added to the game next frame
     */
    public static void addEntity(Entity entity) {
        entitiesToAdd.add(entity);
    }

    /**
     * Given entity will be removed from the game in the next frame
     *
     * @param entity will be removed from the game next frame
     */
    public static void removeEntity(Entity entity) {
        entitiesToRemove.add(entity);
    }

    /**
     * @return Set with all entities currently in game
     */
    public static Set<Entity> getEntities() {
        return entities;
    }

    /**
     * @return Set with all entities that will be added to the game next frame
     */
    public static Set<Entity> getEntitiesToAdd() {
        return entitiesToAdd;
    }

    /**
     * @return Set with all entities that will be removed from the game next frame
     */
    public static Set<Entity> getEntitiesToRemove() {
        return entitiesToRemove;
    }

    /**
     * @return the player character, can be null if not initialized
     */
    public static Optional<Entity> getHero() {
        return Optional.ofNullable(hero);
    }

    /**
     * set the reference of the playable character careful: old hero will not be removed from the
     * game
     *
     * @param hero new reference of hero
     */
    public static void setHero(Entity hero) {
        Game.hero = hero;
    }

    /**
     * @return true if the game is paused, otherwise false
     */
    public static boolean isPaused() {
        return paused;
    }

    /** changes the boolean "paused" in the game */
    public static void togglePaused() {
        paused = !paused;
    }

    public void setSpriteBatch(SpriteBatch batch) {
        this.batch = batch;
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
    }

    private void setupCameras() {
        camera = new DungeonCamera(null, Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        camera.zoom = Constants.DEFAULT_ZOOM_FACTOR;

        // See also:
        // https://stackoverflow.com/questions/52011592/libgdx-set-ortho-camera
    }

    public static void setHasOngoingQuest(boolean value) {
        hasOngoingQuest = value;
    }



    private void createSystems() {
        new VelocitySystem();
        new DrawSystem(painter);
        new PlayerSystem();
        new AISystem();
        new CollisionSystem();
        new HealthSystem();
        new XPSystem();
        new SkillSystem();
        new ProjectileSystem();
        new ManaSystem();
        new QuestSystem();
    }

    public static void setDragonExistsFalse() {
        dragonExists = false;
    }

    /**
     * save the Level and all Entities with their Components in a file
     *
     * @param saveFile the file being written to
     */
    public void saveGame(String saveFile) {
        gameLogger.info("Game saving started.");
        togglePause();
        try (FileOutputStream fos = new FileOutputStream(saveFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            // remove AIComponent before serialization
            entities.stream().filter(entity -> entity.getComponent(AIComponent.class)
                .isPresent()).forEach(entity -> entity.removeComponent(AIComponent.class));
            // remove Ghosts HitboxComponent before serialization, as it contains an idle-AI
            entities.stream().filter(entity -> entity instanceof Ghost).map(Ghost.class::cast)
                .forEach(ghost -> ghost.removeComponent(HitboxComponent.class));
            // write relevant data to oos
            oos.writeObject(dragonExists);
            oos.writeObject(hasOngoingQuest);
            oos.writeObject(levelCounter);
            oos.writeObject(currentLevel);
            oos.writeObject(hero);
            oos.writeObject(entities);
            oos.writeObject(Quest.questList);
            oos.close();
            // re-add AIComponents to Entities
            entities.stream().filter(entity -> entity instanceof Monster).map(Monster.class::cast)
                .forEach(Monster::setupAIComponent);
            entities.stream().filter(entity -> entity instanceof NPC).map(NPC.class::cast)
                .forEach(NPC::setupAIComponent);
            // re-add Ghosts HitboxComponent
            entities.stream().filter(entity -> entity instanceof Ghost).map(Ghost.class::cast)
                .forEach(Ghost::setupHitboxComponent);
            gameLogger.info("Game saved successfully.");
        } catch (IOException ex) {
            gameLogger.severe("Game could not be saved.");
            ex.printStackTrace();
        }
        togglePause();
    }

    /**
     * read the Level and Entities from a file
     *
     * @param saveFile the file being read from
     */
    public void loadGame(String saveFile) {
        gameLogger.info("Game loading started.");
        togglePause();
        try (FileInputStream fis = new FileInputStream(saveFile);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            // read objects from file
            boolean newDragonExists = (boolean) ois.readObject();
            boolean newHasOngoingQuest = (boolean) ois.readObject();
            int newLevelCounter = (int) ois.readObject();
            ILevel newCurrentLevel = (ILevel) ois.readObject();
            Hero newHero = (Hero) ois.readObject();
            Set<Entity> newEntities = (HashSet<Entity>) ois.readObject();
            ArrayList<Quest> newQuests = (ArrayList<Quest>) ois.readObject();
            ois.close();
            // add objects to game
            dragonExists = newDragonExists;
            hasOngoingQuest = newHasOngoingQuest;
            levelCounter = newLevelCounter;
            hero = newHero;
            hero.setupLogger();
            Quest.questList = newQuests;
            // set up transient values
            for (Entity entity : newEntities) {
                if (entity instanceof Ghost) ((Ghost) entity).setupHitboxComponent();
                entity.setupLogger();
                entity.setupAIComponent();
            }
            // remove old entities before adding new ones
            entitiesToRemove.addAll(entities);
            entitiesToAdd.addAll(newEntities);
            // set the level
            currentLevel = newCurrentLevel;
            levelAPI.setCurrentLevel(newCurrentLevel);
            gameLogger.info("Game loaded successfully.");
        } catch (IOException | ClassNotFoundException ex) {
            gameLogger.severe("File: "+saveFile+" could not be loaded.");
            ex.printStackTrace();
        }
        togglePause();
    }
}
