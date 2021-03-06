/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genuini.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import genuini.main.MainGame;
import genuini.screens.GameScreen;

/**
 *
 * @author Adrien
 */
public class TextManager { 
    private BitmapFont bmf;
    private final FreeTypeFontGenerator generator;
    private final FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private String textToDisplay;
    private Vector2 position;
    
    private Array<String> tutorial;
    private int textIndex;
    
    private boolean active;
    
    private float stateTimer;
    private final GameScreen screen;
    private boolean playTutorial;
    private boolean centered;
        
    
    
    public TextManager(GameScreen screen){
         //define the font
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/lobster-two/LobsterTwo-Regular.otf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30; //set the font size: 12px
        parameter.color = Color.BLACK;
        this.screen=screen;
        this.updateText();
        stateTimer=0;
        textIndex=0;
        playTutorial=false;
        textToDisplay="";
        createTutorial();
        active=false;
        centered=true;
    }

    public void setSize(int size){
        parameter.size = size;   
        updateText();
    }
    
    public void setColor(Color c){
        parameter.color = c;
        updateText();
    }
    
    public void setText(String s){
        textToDisplay = s;
        updateText();
    }
    
    private void updateText(){
        bmf = generator.generateFont(parameter);
    }
    
    public void setPosition(Vector2 v){
        position = v;
    }
    
    public void draw(SpriteBatch batch){
        bmf.draw(batch, textToDisplay, position.x, position.y);      
    }
    
    public void update(float delta){
        if(playTutorial && stateTimer>5f){
            if(textIndex<tutorial.size){
                setText(tutorial.get(textIndex));
                textIndex++;
                stateTimer=0;
            }else if(textIndex==tutorial.size){
                stopTutorial();
            }
        }
        float offset=-(textToDisplay.length()*11.5f/2);
        if(textToDisplay.contains("\n")){
            offset/=2;
        }
        
        if(centered){
            //setPosition(new Vector2(screen.getGenuini().getPosition().x+2.7f+offset,screen.getGenuini().getPosition().y+3.3f));
            setPosition(new Vector2(MainGame.V_WIDTH/2+offset ,MainGame.V_HEIGHT-30));
        }        
        stateTimer+=delta;
    }

    private void createTutorial() {
        tutorial = new Array<String>();
        tutorial.add("Welcome, to Genuini's World");
        tutorial.add("Are you ready to live an exciting adventure\n while learning to use Arduino?");
        tutorial.add("Move Genuini by pressing Q,Z,D");
        tutorial.add("Enjoy this demonstration !");
    }
    
    public boolean isActive(){
        return active;
    }
    
    public void activate(){
        active=true;
    }
    
    public void deactivate(){
        active=false;
    }

    public void playTutorial() {
        playTutorial=true;
        activate();
    }
    
    public void stopTutorial() {
        playTutorial=false;
        deactivate();
    }

    public void displayText(String string, int time) {
        activate();
        centered=true;
        setText(string);
        Timer.schedule(new Task(){
            @Override
            public void run() {
                deactivate();
            }
        }, time);
    }
    
    public void displayText(final String string,final int time, int delay) {
        Timer.schedule(new Task(){
            @Override
            public void run() {
                displayText(string, time);
            }
        }, delay);
        
    }
    
    public void displayText(String string, int time, Vector2 position) {
        activate();
        setText(string);
        setPosition(position);
        centered=false;
        Timer.schedule(new Task(){
            @Override
            public void run() {
                deactivate();
            }
        }, time);
    }
    
    public String getHint(){
        String message;
        String lastSkill = screen.getPreferences().getLastSkill();
        if(lastSkill.equals("gravity")){
            message="Press G to change the direction of the gravitional field";
        }else if(lastSkill.equals("doubleJump")){
            message="You can now jump twice !";
        }else if(lastSkill.equals("fireball")){
            message="Press SPACE to throw fireballs";
        }else{
            message = "";
        }
        return message;
    }
}
