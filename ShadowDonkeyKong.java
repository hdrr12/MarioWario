import bagel.*;

import java.util.ArrayList;
import java.util.Properties;
import bagel.*;
import bagel.util.*;
import bagel.map.*;

public class ShadowDonkeyKong extends AbstractGame {
    private enum gameState {HOME, PLAYING, WIN, LOSE}
    private gameState state = gameState.HOME;

    //game entities
    private Mario mario;
    private DonkeyKong donkey;
    private ArrayList<Platform> platforms;
    private ArrayList<Ladder> ladders;
    private ArrayList<Barrel> barrels;
    private ArrayList<Hammer> hammers; // changed to list cos of multiple hammers
    //private ArrayList<Blaster> blasters;
    //private ArrayList<Monkey> monkeys;
    //private ArrayList<Projectile> projectiles;
    private Image background;

    // game properties
    private int score = 0;
    private int currentFrame = 0;
    private int maxFrames;

    // ui elements
    private Font titleFont;
    private Font gameFont;
    private Font scoreFont;
    private Font endFont;

    Properties gameProps;
    Properties messageProps;

    public ShadowDonkeyKong(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                messageProps.getProperty("home.title"));

        this.gameProps = gameProps;
        this.messageProps = messageProps;
        // these two will make everything ready to be runned in update methods
        loadProperties();
    }

    private void loadProperties(){
        //initialize fonts
        //since font is same for all
        String fontPath = gameProps.getProperty("font");

        //get font sizes
        titleFont = new Font(fontPath, Integer.parseInt(gameProps.getProperty("home.title.fontSize")));
        gameFont = new Font(fontPath, Integer.parseInt(gameProps.getProperty("gamePlay.score.fontSize")));
        scoreFont = new Font(fontPath, Integer.parseInt(gameProps.getProperty("gameEnd.scores.fontSize")));
        endFont = new Font(fontPath, Integer.parseInt(gameProps.getProperty("gameEnd.status.fontSize")));

        //load bg image
        background = new Image(gameProps.getProperty("backgroundImage"));

        //set max frames
        maxFrames = Integer.parseInt(gameProps.getProperty("gamePlay.maxFrames"));
    }

    private void loadLevel(int levelNum){
        // reset game state
        score = 0;
        currentFrame = 0;

        // Clear previous level's entities
        platforms = new ArrayList<>();
        ladders = new ArrayList<>();
        barrels = new ArrayList<>();
        hammers = new ArrayList<>();
        //monkeys = new ArrayList<>();
        //blasters = new ArrayList<>();
        //projectiles = new ArrayList<>();

        String levelPref = "level" + levelNum;

        //load platform based on level
        String platformStrLev = gameProps.getProperty("platforms." + levelPref);
        String[] platformsStr = platformStrLev.split(";");
        for (String platform : platformsStr){
            String[] coords = platform.split(",");
            platforms.add(new Platform(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])));
        }

        //create mario based on level
        String marioCoordsLev = gameProps.getProperty("mario." + levelPref);
        String[] marioCoords = marioCoordsLev.split(",");
        int marioX = Integer.parseInt(marioCoords[0]);
        int marioY = Integer.parseInt(marioCoords[1]);
        mario = new Mario(marioX, marioY);

        // Create Donkey
        String donkeyCoordsLev = gameProps.getProperty("donkey." + levelPref);
        String[] donkeyCoords = donkeyCoordsLev.split(",");
        int donkeyX = Integer.parseInt(donkeyCoords[0]);
        int donkeyY = Integer.parseInt(donkeyCoords[1]);
        donkey = new DonkeyKong(donkeyX, donkeyY);

        // create hammer
        String hammerCountLev = gameProps.getProperty("hammer." + levelPref + ".count");
        int hammerCount = Integer.parseInt(hammerCountLev.trim());
        for(int i = 1; i<=hammerCount; i++){
            String hammerCoordsKey = "hammer." + levelPref + "." + i;
            String hammerCoordsVal = gameProps.getProperty(hammerCoordsKey);
            String[] coords = hammerCoordsVal.split(",");
            hammers.add(new Hammer(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])));
        }

        //Create ladders
        String ladderCountStr = gameProps.getProperty("ladder." + levelPref + ".count");
        int ladderCount = Integer.parseInt(ladderCountStr);
        for(int i = 1; i <= ladderCount; i++){
            String ladderCoordsKey = "ladder." + levelPref + "." + i;
            String ladderCoordsVal = gameProps.getProperty(ladderCoordsKey);
            String[] coords = ladderCoordsVal.split(",");
            ladders.add(new Ladder(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])));
        }

        // Create barrels
        String barrelCountStr = gameProps.getProperty("barrel." + levelPref + ".count");
        int barrelCount = Integer.parseInt(barrelCountStr);
        for(int i = 1; i <= barrelCount; i++){
            String barrelCoordsKey = "ladder." + levelPref + "." + i;
            String barrelCoordsVal = gameProps.getProperty(barrelCoordsKey);
            String[] coords = barrelCoordsVal.split(",");
            barrels.add(new Barrel(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])));
        }

        if (levelNum == 2){
            // load Blasters
            // load normal monkeys
            // load intelligent monkeys
        }



    }

    @Override
    protected void update(Input input) {
        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }

        //update gaem based on state
        switch(state){
            case HOME:
                homeScreen(input);
                break;
            case PLAYING:
                updateGameplay(input);
                break;
            case WIN, LOSE:
                gameoverScreen(input);
                break;
        }

    }

    private void homeScreen(Input input){
        //draw bg
        background.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);

        //draw title
        String title = messageProps.getProperty("home.title");
        int titleY =  Integer.parseInt(gameProps.getProperty("home.title.y"));
        double titleWidth = titleFont.getWidth(title);
        titleFont.drawString(title, (Window.getWidth() - titleWidth) / 2, titleY);

        //draw prompt
        String prompt = messageProps.getProperty("home.prompt");
        double promptY = Double.parseDouble(gameProps.getProperty("home.prompt.y"));
        double promptWidth = gameFont.getWidth(prompt);
        gameFont.drawString(prompt, (Window.getWidth() - promptWidth) / 2, promptY);

        //check enter to start game
        if(input.wasPressed(Keys.ENTER)){
            state = gameState.PLAYING;
        }

    }

    private void updateGameplay(Input input){
        currentFrame++;
        double timeLeft = (maxFrames - currentFrame) / 60.0;

        //draw bg
        background.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

        for (Platform platform : platforms) {
            platform.draw();
        }

        // draw ladders
        for(Ladder ladder: ladders){
            ladder.draw();
        }

        // draw platforms
        donkey.update(platforms);
        donkey.draw();

        //draw hammer
        hammer.update(mario);
        hammer.draw();

        //draw mario
        mario.update(input, platforms, ladders);
        mario.draw();



        //update and draw barrels
        ArrayList<Barrel> barrelHammerHit = new ArrayList<>();
        for(Barrel barrel : barrels){
            barrel.update(platforms);
            barrel.draw();

            //check for collisions
            if (barrel.getBox().intersects(mario.getBox())){
                if(mario.hasHammer()){
                    //hit barrel with hammer
                    barrelHammerHit.add(barrel);
                    score += 100;
                } else{
                    //mario touch barrel no hammer
                    state = gameState.LOSE;
                }
            }

            if (mario.jumpedOver(barrel)){
                score += 30;
            }

        }
        barrels.removeAll(barrelHammerHit);


        // Check if Mario reached Donkey Kong win condition with hammer
        if((mario.getBox().intersects(donkey.getBox())) && mario.hasHammer()){
            state = gameState.WIN;
        }else if (mario.getBox().intersects(donkey.getBox())){
            state = gameState.LOSE;
        }

        // Check if time ran out
        if (timeLeft <= 0) {
            state = gameState.LOSE;
        }

        // Draw score and time
        int scoreX = Integer.parseInt(gameProps.getProperty("gamePlay.score.x"));
        int scoreY = Integer.parseInt(gameProps.getProperty("gamePlay.score.y"));
        gameFont.drawString("SCORE " + score, scoreX, scoreY);
        gameFont.drawString("TIME LEFT " + (int)timeLeft, scoreX, scoreY + 30);

    }

    private void gameoverScreen(Input input){
       background.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

       //draw end msg
        String message;
        if(state == gameState.WIN){
            message = messageProps.getProperty("gameEnd.won");
        }else{
            message = messageProps.getProperty("gameEnd.lost");
        }

        //draw message
        double statusY = Double.parseDouble(gameProps.getProperty("gameEnd.status.y"));
        double messageWidth = endFont.getWidth(message);
        endFont.drawString(message, (Window.getWidth() - messageWidth) / 2, statusY);

        // Calculate final score including time bonus
        int timeBonus = ((maxFrames - currentFrame) / 60) * 3;

        if (timeBonus < 0) {
            timeBonus = 0;
        }
        int finalScore;
        if (state == gameState.WIN) {
            finalScore = score + timeBonus;
        } else {
            finalScore = score;
        }

        //draw final score
        String scoreMsg = messageProps.getProperty("gameEnd.score") + ": " + finalScore;
        double scoresY = Double.parseDouble(gameProps.getProperty("gameEnd.scores.y"));
        double scoreWidth = scoreFont.getWidth(scoreMsg);
        scoreFont.drawString(scoreMsg, (Window.getWidth() - scoreWidth) / 2, scoresY);

        // draw continue msg
        String continuePrompt = messageProps.getProperty("gameEnd.continue");
        double promptWidth = gameFont.getWidth(continuePrompt);
        gameFont.drawString(continuePrompt, (Window.getWidth() - promptWidth) / 2, Window.getHeight() - 100);

        // Handle restart
        if (input.wasPressed(Keys.SPACE)) {
            state = gameState.HOME;
            setupGame();
        }

    }

    public static void main(String[] args) {
        Properties gameProps = IOUtils.readPropertiesFile("res/app.properties");
        Properties messageProps = IOUtils.readPropertiesFile("res/message_en.properties");
        ShadowDonkeyKong game = new ShadowDonkeyKong(gameProps, messageProps);
        game.run();
    }


}
