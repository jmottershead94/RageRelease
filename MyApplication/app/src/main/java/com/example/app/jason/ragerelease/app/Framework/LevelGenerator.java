package com.example.app.jason.ragerelease.app.Framework;

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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Win8 on 02/08/2015.
 */
public class LevelGenerator
{
    private static final String TAG = "TKT";
    private static float groundY = 0.0f;
    private int playerImage = 0;
    private int enemyImage = 0;
    //private ScheduledExecutorService service;
    private Timer timer;
    private int interval = 6;                    // 3 seconds.
    private ScheduledExecutorService scheduler;
    private Vector<AnimatedSprite> objects = null;
    private Resources resources = null;
    private Level level = null;

    public LevelGenerator(final Resources gameResources, Level gameLevel, final int gamePlayerImage, final int gameEnemyImage)
    {
        // Setting the local level parameters.
        resources = gameResources;
        playerImage = gamePlayerImage;
        enemyImage = gameEnemyImage;
        //service = Executors.newSingleThreadScheduledExecutor();
        level = gameLevel;
        objects = new Vector<AnimatedSprite>();             // Initialising the vector of level objects.
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void buildLevel()
    {
        // Creating all of the level objects.
        createGround();
        createStaticBackground();
        createAnimatedBackground();
        createPlayer(new Vector2(resources.getScreenWidth() * 0.25f, resources.getScreenHeight() * 0.25f), playerImage);
        //createPlayer(new Vector2(resources.getScreenWidth() * 0.5f, resources.getScreenHeight() * 0.25f), enemyImage);
        createObstacle(new Vector2(resources.getScreenWidth() * 0.95f, resources.getScreenHeight() * 0.45f));

        // This schedules respawning an obstacle once it has gone off screen.
        // Running on a new thread.
        scheduler.scheduleAtFixedRate(new Runnable()
        {
            @Override
            public void run()
            {
                // Loop through all of the level objects.
                for(AnimatedSprite object : objects)
                {
                    // If the object is an obstacle.
                    if(object.getID() == ObjectID.OBSTACLE)
                    {
                        // Respawn the object at the start of the level.
                        object.translateFramework(object.getSpawnLocation());
                    }
                }
            }
        // First taking place at 5 seconds, then executing at a regular interval of 6 seconds afterwards.
        }, interval - 1, interval, TimeUnit.SECONDS);
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

    private void createPlayer(Vector2 position, int image)
    {
        DynamicBody player = new DynamicBody(resources, ObjectID.PLAYER);
        player.bodyInit(position, new Vector2(resources.getScreenWidth() * 0.125f, resources.getScreenWidth() * 0.125f), 0.0f);
        player.setAnimationFrames(6);
        setSprite(image, player);
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

    public void update(float dt)
    {

        // Delay the task.
        //timeHandler.postDelayed(timeRunnable, interval);

        // If the
//        if(timeHandler.postDelayed(timeRunnable, interval))
//        {
//            timeHandler.removeCallbacks(timeRunnable);
//        }
//        else
//        {
//            //timeHandler.postDelayed(timeRunnable, interval);
//        }
        //createObstacle(new Vector2(resources.getScreenWidth() * 0.95f, resources.getScreenHeight() * 0.45f));

//        Timer timer = new Timer();
//        timer.schedule(new TimerTask()
//        {
//            @Override
//            public void run()
//            {
//                createObstacle(new Vector2(resources.getScreenWidth() * 0.95f, resources.getScreenHeight() * 0.45f));
//            }
//        }, 3000, 3000);

//        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
//        service.schedule(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                service.shutdown();
//            }
//        }, 3, TimeUnit.SECONDS);

    }

    // Getters.
    public Vector<AnimatedSprite> getObjects()  { return objects; }
}
