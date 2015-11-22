package com.example.app.jason.ragerelease.app.Framework;

import android.app.Service;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.app.jason.ragerelease.app.Framework.Maths.Vector2;
import com.example.app.jason.ragerelease.R;
import com.example.app.jason.ragerelease.app.Framework.Physics.DynamicBody;
import com.example.app.jason.ragerelease.app.Framework.Physics.StaticBody;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Win8 on 02/08/2015.
 */
public class LevelGenerator
{
    private static final String PREFS_NAME = "MyPrefsFile";                     // Where the options will be saved to, whether they are true or false.
    private static final String TAG = "TKT";
    private static float groundY = 0.0f;
    private int playerImage = 0;
    private int companionImage = 0;
    private int interval = 10;                           // 3 seconds.
    private Timer timer = null;
    private ScheduledExecutorService scheduler = null;
    private Bitmap cameraImage = null;
    private Vector<AnimatedSprite> objects = null;
    private Resources resources = null;
    private Level level = null;
    private boolean optionOneChecked = false, optionTwoChecked = false;
    private ScheduledFuture<?> future = null;

    public LevelGenerator(final Resources gameResources, Level gameLevel, final int gamePlayerImage, final int gameCompanionImage)
    {
        // Setting the local level parameters.
        resources = gameResources;
        playerImage = gamePlayerImage;
        companionImage = gameCompanionImage;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        level = gameLevel;
        objects = new Vector<AnimatedSprite>();             // Initialising the vector of level objects.

        // Load in options here...
        // Accessing saved options.
        SharedPreferences gameSettings = resources.getActivity().getSharedPreferences(PREFS_NAME, resources.getActivity().MODE_PRIVATE);
        optionOneChecked = gameSettings.getBoolean("moptionOneCheckedStatus", false);
        optionTwoChecked = gameSettings.getBoolean("moptionTwoCheckedStatus", false);
    }

    public void buildLevel(int numberOfCharacters, int numberOfObstacles)
    {
        // Creating all of the level objects.
        createGround();
        createStaticBackground();
        createAnimatedBackground();
        createPlayer(new Vector2(resources.getScreenWidth() * 0.45f, resources.getScreenHeight() * 0.25f), playerImage, ObjectID.CHARACTERONE);
        createObstacle(new Vector2(resources.getScreenWidth() * 0.95f, resources.getScreenHeight() * 0.45f));

        // If there should be more than one player character.
        if(numberOfCharacters > 1)
        {
            createPlayer(new Vector2(resources.getScreenWidth() * 0.15f, resources.getScreenHeight() * 0.25f), companionImage, ObjectID.CHARACTERTWO);
        }

        // Calculating a random interval for the obstacles to spawn in with.
//        int minimumSeconds = 5;
//        int maximumSeconds = 9;
//        Random intervalRandom = new Random();
//        interval = intervalRandom.nextInt() * (maximumSeconds - minimumSeconds) + minimumSeconds;

        // If there should be more than one obstacle.
        if(numberOfObstacles > 1)
        {
            Vector2 minimumAndMaximumXPosition = new Vector2(resources.getScreenWidth() * 0.75f, resources.getScreenWidth() * 0.95f); // 0.001 is the minimum animation ticks, 0.005 is the maximum animation ticks.
            Vector2 minimumAndMaximumYPosition = new Vector2(resources.getScreenHeight() * 0.125f, resources.getScreenWidth() * 0.45f);

            Random positionRandom = new Random();
            float xPosition = positionRandom.nextFloat() * (minimumAndMaximumXPosition.getY() - minimumAndMaximumXPosition.getX()) + minimumAndMaximumXPosition.getX();

            positionRandom = new Random();
            float yPosition = positionRandom.nextFloat() * (minimumAndMaximumYPosition.getY() - minimumAndMaximumYPosition.getX()) + minimumAndMaximumYPosition.getX();

            createObstacle(new Vector2(xPosition, yPosition));
        }

        // This schedules respawning an obstacle once it has gone off screen.
        // Running on a new thread.
        future = scheduler.scheduleAtFixedRate(new Runnable()
        {
            @Override
            public void run()
            {
                if(!level.player.isPaused())
                {
                    // Loop through all of the level objects.
                    for (AnimatedSprite object : objects)
                    {
                        // If the object is an obstacle.
                        if (object.getID() == ObjectID.OBSTACLE)
                        {
                            object.translateFramework(object.getSpawnLocation());
                        }
                    }
                }
            }
            // First taking place at 5 seconds, then executing at a regular interval of 6 seconds afterwards.
        }, interval - 1, interval, TimeUnit.SECONDS);

        //level.player.distanceText.bringToFront();
    }

    private void createGround()
    {
        StaticBody groundFloor = new StaticBody(resources, ObjectID.GROUND);
        groundY = resources.getScreenHeight() - 190.0f;
        groundFloor.bodyInit(new Vector2(0.0f, groundY), new Vector2(resources.getScreenWidth(), 80.0f), 0.0f);
        groundFloor.setTexture(R.drawable.sprite_sheet, new Vector2(0.0f, 0.0f), new Vector2(0.125f, 0.0625f));
        objects.add(groundFloor);
    }

    private void createStaticBackground()
    {
        AnimatedSprite background = new AnimatedSprite(resources, ObjectID.SPRITE);
        background.init(new Vector2(0.0f, 0.0f), new Vector2(resources.getScreenWidth(), groundY), 0.0f);
        background.setTexture(R.drawable.night_sky, new Vector2(0.0f, 0.0f), new Vector2(1.0f, 1.0f));
        objects.add(background);
    }

    private void createAnimatedBackground()
    {
        AnimatedSprite animatedBackground = new AnimatedSprite(resources, ObjectID.ANIMATEDSPRITE);
        animatedBackground.init(new Vector2(0.0f, resources.getScreenHeight() - 230.0f), new Vector2(resources.getScreenWidth(), 80.0f), 0.0f);
        animatedBackground.setTexture(R.drawable.sprite_sheet, new Vector2(0.0f, 0.0625f), new Vector2(0.125f, 0.0625f));
        animatedBackground.setAnimationFrames(8);
        objects.add(animatedBackground);
    }

    private void setSprite(int image, AnimatedSprite sprite)
    {
        float textureWidth = 1.0f / 7.0f;
        float textureHeight = 1.0f / 3.0f;

        if(image == R.drawable.p1_front)
        {
            sprite.setTexture(R.drawable.p1_spritesheet, new Vector2(0.0f, 0.0f), new Vector2(textureWidth, textureHeight));
        }
        else if(image == R.drawable.p2_front)
        {
            sprite.setTexture(R.drawable.p2_spritesheet, new Vector2(0.0f, 0.0f), new Vector2(textureWidth, textureHeight));
        }
        else if(image == R.drawable.p3_front)
        {
            sprite.setTexture(R.drawable.p3_spritesheet, new Vector2(0.0f, 0.0f), new Vector2(textureWidth, textureHeight));
        }
        else if(image == R.drawable.p4_front)
        {
            sprite.setTexture(R.drawable.p4_spritesheet, new Vector2(0.0f, 0.0f), new Vector2(textureWidth, textureHeight));
        }
        else if(image == R.drawable.p5_front)
        {
            sprite.setTexture(R.drawable.p5_spritesheet, new Vector2(0.0f, 0.0f), new Vector2(textureWidth, textureHeight));
        }
        else if(image == R.drawable.p6_front)
        {
            sprite.setTexture(R.drawable.p6_spritesheet, new Vector2(0.0f, 0.0f), new Vector2(textureWidth, textureHeight));
        }
        else if(image == R.drawable.p7_front)
        {
            sprite.setTexture(R.drawable.p7_spritesheet, new Vector2(0.0f, 0.0f), new Vector2(textureWidth, textureHeight));
        }
        else if(image == R.drawable.p8_front)
        {
            sprite.setTexture(R.drawable.p8_spritesheet, new Vector2(0.0f, 0.0f), new Vector2(textureWidth, textureHeight));
        }
    }

    private void setImage(AnimatedSprite object)
    {
        // Getting access to the camera properties.
        CameraHandler cameraHandler = new CameraHandler(resources);

        // Storing the last taken image in the gallery into a bitmap.
        cameraImage = cameraHandler.getLastPicture();

        // Setting the camera image for the bitmap used on the sprite.
        object.setCameraImage(cameraImage);
    }

    private void createPlayer(Vector2 position, int image, final int id)
    {
        DynamicBody player = new DynamicBody(resources, id);

        player.bodyInit(position, new Vector2(resources.getScreenWidth() * 0.125f, resources.getScreenWidth() * 0.125f), 0.0f);
        setSprite(image, player);
        player.setAnimationFrames(6);

        // Used with camera code.
//        if(optionOneChecked)
//        {
//            // Rotate the image so that it is the right way round.
//            player.bodyInit(position, new Vector2(resources.getScreenWidth() * 0.125f, resources.getScreenWidth() * 0.125f), 270.0f);
//            setImage(player);
//        }
//        else
//        {
//            player.bodyInit(position, new Vector2(resources.getScreenWidth() * 0.125f, resources.getScreenWidth() * 0.125f), 0.0f);
//            setSprite(image, player);
//            player.setAnimationFrames(6);
//        }

        objects.add(player);
    }

    private void createObstacle(Vector2 position)
    {
        final StaticBody obstacle = new StaticBody(resources, ObjectID.OBSTACLE);
        obstacle.bodyInit(position, new Vector2(resources.getScreenWidth() * 0.125f, resources.getScreenWidth() * 0.125f), 0.0f);
        obstacle.setTexture(R.drawable.box_explosive, new Vector2(0.0f, 0.0f), new Vector2(1.0f, 1.0f));
        objects.add(obstacle);
    }

    public void addToView()
    {
        if (resources.getBackground() != null)
        {
            for (AnimatedSprite object : getObjects())
            {
                if (object != null)
                {
                    resources.getBackground().addView(object);
                }
            }
        }
    }

    public void clearLevel()
    {
        future.cancel(true);

        // If there are objects in the vector.
        if(!objects.isEmpty())
        {
            // Iterate through each of the objects in the level.
            for(AnimatedSprite object : getObjects())
            {
                // If there is an object.
                if(object != null)
                {
                    // Removing all of the sprites from the screen.
                    // If the object does not have a texture, but is using standard colours.
                    if(object.colour != null)
                    {
                        // Set the alpha value to 0 and RGB to 0.
                        object.setColour(0, 0, 0, 0);
                    }
                    // Otherwise, the object has a texture.
                    else if(object.image != null)
                    {
                        // Set the texture to be transparent.
                        object.removeTexture();
                    }

                    if(object.body != null)
                    {
                        // Should destroy the body after the level.
                        object.body.destroyFixture(object.body.getFixtureList());
                        resources.getWorld().destroyBody(object.body);
                    }
                }
            }

            // Remove all of the objects from the list.
            objects.clear();
        }
    }

    // Getters.
    public Vector<AnimatedSprite> getObjects()  { return objects; }
}
