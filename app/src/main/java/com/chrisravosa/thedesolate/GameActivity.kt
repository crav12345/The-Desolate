package com.chrisravosa.thedesolate

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chrisravosa.thedesolate.R.id.progressBarVitality
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// Tag used for debug logs.
const val TAG = "GameActivity"

// Defines matrix the world generates onto.
const val WORLD_SIZE = 20

// Used when player finds resources to heal vitality.
const val RESOURCES_BONUS = 33

class GameActivity : AppCompatActivity() {
    // Narrative text values for room descriptions.
    private val roomDescriptions = arrayOf(
        R.string.room_description1,
        R.string.room_description2,
        R.string.room_description3,
        R.string.room_description4,
        R.string.room_description5,
        R.string.room_description6,
        R.string.room_description7,
        R.string.room_description8,
        R.string.room_description9,
        R.string.room_description10,
        R.string.room_description11,
        R.string.room_description12,
        R.string.room_description13,
        R.string.room_description14,
        R.string.room_description15,
        R.string.room_description16,
        R.string.room_description17,
        R.string.room_description18,
        R.string.room_description19,
        R.string.room_description20,
    )

    // Narrative text values for blocked paths.
    private val blockedDescriptions = arrayOf(
        R.string.room_blocked1,
        R.string.room_blocked2,
        R.string.room_blocked3,
        R.string.room_blocked4
    )

    // Objects used for collecting game data.
    private val gameWorld = WorldObject()
    private val player = PlayerObject()

    // Handles day counter and vitality threads.
    private val executorService = Executors.newSingleThreadScheduledExecutor()

    // Metrics
    private var roomsPlaced = 1
    private var playerDisplayX = 0
    private var playerDisplayY = 0
    private var daysPassed = 1
    private var roomsVisited = 1

    // Used to animate survivor sprite
    private lateinit var survivorIdleAnimation: AnimationDrawable

    /** Called as activity is started */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Reset metrics values.
        roomsPlaced = 0
        playerDisplayX = 0
        playerDisplayY = 0
        daysPassed = 1
        roomsVisited = 1

        // Set HUD views to starting values.
        applicationContext.resources.getString(R.string.game_start)
            .also { findViewById<TextView>(R.id.textNarrative).text = it }
        findViewById<TextView>(R.id.textDays).text =
            applicationContext.resources.getString(R.string.day, daysPassed)
        findViewById<ImageView>(R.id.imageLocation).setBackgroundResource(
            R.drawable.amron_crate
        )
        findViewById<TextView>(R.id.textCoordinates).text =
            applicationContext.resources.getString(
                R.string.coordinates, playerDisplayX, playerDisplayY
            )
        findViewById<ProgressBar>(progressBarVitality).progress = 100

        // Generate game world.
        val worldMap = generateWorld(WORLD_SIZE)
        gameWorld.map = worldMap
        gameWorld.numRooms = roomsPlaced
    }

    /** Called as activity becomes visible to user */
    override fun onStart() {
        super.onStart()

        // An animation_list can't be called in onCreate(), so add the
        // 8-bit-bounce to our player character here.
        findViewById<ImageView>(R.id.characterImage).apply {
            setBackgroundResource(R.drawable.survivor_idle_right)
            survivorIdleAnimation = background as AnimationDrawable
        }
        survivorIdleAnimation.start()
    }

    /** Called when the user presses the start button */
    fun startGame(view: View) {
        // Delete the start button, we don't need it anymore.
        findViewById<Button>(R.id.buttonStart).visibility = View.GONE

        // Set narrative text to home description.
        findViewById<TextView>(R.id.textNarrative).text =
            applicationContext.resources.getString(R.string.room_description0)

        // Enable user controls.
        findViewById<Button>(R.id.buttonNorth).isEnabled = true
        findViewById<Button>(R.id.buttonEast).isEnabled = true
        findViewById<Button>(R.id.buttonSouth).isEnabled = true
        findViewById<Button>(R.id.buttonWest).isEnabled = true
        findViewById<Button>(R.id.buttonSearch).isEnabled = true

        // Put player at center of map.
        player.y = gameWorld.map.size / 2
        player.x = gameWorld.map.size / 2

        // Handlers to interface with threads which handle days and health.
        val timeThreadHandler = Handler(mainLooper)
        val vitalityThreadHandler = Handler(mainLooper)

        // Start incrementing days at intervals of 30 seconds.
        executorService.scheduleAtFixedRate(
            {
                try {
                    timeThreadHandler.post { nextDay() }
                } catch (e: Exception) {
                    Log.e(TAG, "ERROR: $e")
                }
            }, 30, 30, TimeUnit.SECONDS
        )

        // Start decrementing player vitality once per second.
        executorService.scheduleAtFixedRate(
            {
                try {
                    vitalityThreadHandler.post { lowerVitality() }
                } catch (e: Exception) {
                    Log.e(TAG, "ERROR: $e")
                }
            }, 1, 1, TimeUnit.SECONDS)
    }

    /** Called whenever the player presses the 'search' button */
    fun search(view: View) {
        // Check whether this RoomObject instance has resources.
        if(gameWorld.map[player.y][player.x].hasResources) {
            // Remove the resources from the room.
            gameWorld.map[player.y][player.x].hasResources = false

            // Notify the player that they've found some resources.
            findViewById<TextView>(R.id.textNarrative).text =
                applicationContext.resources.getString(
                    R.string.resources_found
                )

            // Locate the vitality bar and update it.
            val vitalityBar = findViewById<ProgressBar>(
                progressBarVitality
            )
            var progress = vitalityBar.progress
            progress += RESOURCES_BONUS
            vitalityBar.progress = progress
        }
        // Otherwise, notify the player that they've found nothing.
        else {
            findViewById<TextView>(R.id.textNarrative).text =
                applicationContext.resources.getString(
                    R.string.resources_not_found
                )
        }
    }

    /** Called by the timeThreadHandler to increment days */
    private fun nextDay() {
        // Increment days passed and update the HUD.
        daysPassed++
        findViewById<TextView>(R.id.textDays).text =
            applicationContext.resources.getString(
                R.string.day, daysPassed
            )
    }

    /** Called by the vitalityThreadHandler to decrement vitality */
    private fun lowerVitality() {
        // Locate the vitality bar and update it.
        val vitalityProgressBar =
            findViewById<ProgressBar>(progressBarVitality)
        var progress = vitalityProgressBar.progress
        progress--

        // If the value of the vitality bar is 0 or less, the game ends.
        if (progress <= 0) {
            // Shutdown other threads.
            executorService.shutdown()
            if (!executorService.awaitTermination(
                    100, TimeUnit.MICROSECONDS
                )
            ) {
                // Log to the console if the threads aren't stopping.
                Log.d(
                    TAG,
                    "Still waiting for executor service to shut down..."
                )
            }

            // Record player score
            val score = daysPassed * roomsVisited

            // Start the EnterScore activity and pass score to it so the player
            // knows how they did.
            val intent = Intent(this, EnterScore::class.java)
            intent.putExtra("score", score)
            intent.putExtra("daysSurvived", daysPassed)
            intent.putExtra("areasVisited", roomsVisited)
            startActivity(intent)
        }
        // Otherwise, simply update the vitality bar with a new value.
        else {
            vitalityProgressBar.progress = progress
        }
    }

    /** Called when player presses 'Go North' button */
    fun goNorth(view: View) {
        // Check if the room has an open door in this direction.
        if (gameWorld.map[player.y][player.x].doors[0]) {
            // Update the player's coordinates accordingly.
            player.y--
            playerDisplayY++

            // Flag this room as having had the player there.
            gameWorld.map[player.y][player.x].hasBeenVisited = true

            // Increment number of rooms visited.
            roomsVisited++

            // Update text to describe the next room.
            findViewById<TextView>(R.id.textNarrative).text =
                gameWorld.map[player.y][player.x].roomDescription

            // Update display for player coordinates.
            findViewById<TextView>(R.id.textCoordinates).text =
                applicationContext.resources.getString(
                    R.string.coordinates, playerDisplayX, playerDisplayY
                )
        }
        // Otherwise, notify the player that this direction is blocked.
        else {
            val description = (blockedDescriptions.indices).random()
            findViewById<TextView>(R.id.textNarrative).text =
                applicationContext.resources.getString(
                    blockedDescriptions[description]
                )
        }
    }

    /** Called when player presses 'Go East' button */
    fun goEast(view: View) {
        // Check if the room has an open door in this direction.
        if (gameWorld.map[player.y][player.x].doors[1]) {
            // Update the player's coordinates accordingly.
            player.x++
            playerDisplayX++

            // Flag this room as having had the player there.
            gameWorld.map[player.y][player.x].hasBeenVisited = true

            // Increment number of rooms visited.
            roomsVisited++

            // Update text to describe the next room.
            findViewById<TextView>(R.id.textNarrative).text =
                gameWorld.map[player.y][player.x].roomDescription

            // Update display for player coordinates.
            findViewById<TextView>(R.id.textCoordinates).text =
                applicationContext.resources.getString(
                    R.string.coordinates, playerDisplayX, playerDisplayY
                )
        }
        // Otherwise, notify the player that this direction is blocked.
        else {
            val description = (blockedDescriptions.indices).random()
            findViewById<TextView>(R.id.textNarrative).text =
                applicationContext.resources.getString(
                    blockedDescriptions[description]
                )
        }
    }

    /** Called when player presses 'Go South' button */
    fun goSouth(view: View) {
        // Check if the room has an open door in this direction.
        if (gameWorld.map[player.y][player.x].doors[2]) {
            // Update the player's coordinates accordingly.
            player.y++
            playerDisplayY--

            // Flag this room as having had the player there.
            gameWorld.map[player.y][player.x].hasBeenVisited = true

            // Increment number of rooms visited.
            roomsVisited++

            // Update text to describe the next room.
            findViewById<TextView>(R.id.textNarrative).text =
                gameWorld.map[player.y][player.x].roomDescription

            // Update display for player coordinates.
            findViewById<TextView>(R.id.textCoordinates).text =
                applicationContext.resources.getString(
                    R.string.coordinates, playerDisplayX, playerDisplayY
                )
        }
        // Otherwise, notify the player that this direction is blocked.
        else {
            val description = (blockedDescriptions.indices).random()
            findViewById<TextView>(R.id.textNarrative).text =
                applicationContext.resources.getString(
                    blockedDescriptions[description]
                )
        }
    }

    /** Called when player presses 'Go West' button */
    fun goWest(view: View) {
        // Check if the room has an open door in this direction.
        if (gameWorld.map[player.y][player.x].doors[3]) {
            // Update the player's coordinates accordingly.
            player.x--
            playerDisplayX--

            // Flag this room as having had the player there.
            gameWorld.map[player.y][player.x].hasBeenVisited = true

            // Increment number of rooms visited.
            roomsVisited++

            // Update text to describe the next room.
            findViewById<TextView>(R.id.textNarrative).text =
                gameWorld.map[player.y][player.x].roomDescription

            // Update display for player coordinates.
            findViewById<TextView>(R.id.textCoordinates).text =
                applicationContext.resources.getString(
                    R.string.coordinates, playerDisplayX, playerDisplayY
                )
        }
        // Otherwise, notify the player that this direction is blocked.
        else {
            val description = (blockedDescriptions.indices).random()
            findViewById<TextView>(R.id.textNarrative).text =
                applicationContext.resources.getString(
                    blockedDescriptions[description]
                )
        }
    }

    /** Procedurally generates world before game start */
    private fun generateWorld(n: Int): Array<Array<RoomObject>> {
        // This matrix stores our map as it is created.
        val worldMap = Array(n) { Array(n) {RoomObject()}}

        // Place a room at the center of the matrix.
        worldMap[n/2][n/2] = RoomObject(
            arrayOf(true,true,true,true),
            n/2,
            n/2,
            true,
            applicationContext.resources.getString(R.string.room_description0),
            hasBeenVisited = true,
            hasResources = false
        )

        // Place rooms at every location surrounding the center room.
        worldMap[n/2 + 1][n/2] = RoomObject(
            arrayOf(true, false, false, false),
            n/2 + 1,
            n/2,
            false,
            "",
            hasBeenVisited = false,
            hasResources = false
        )
        worldMap[n/2 - 1][n/2] = RoomObject(
            arrayOf(false, false, true, false),
            n/2 - 1,
            n/2,
            false,
            "",
            hasBeenVisited = false,
            hasResources = false
        )
        worldMap[n/2][n/2 + 1] = RoomObject(
            arrayOf(false, false, false, true),
            n/2,
            n/2 + 1,
            false,
            "",
            hasBeenVisited = false,
            hasResources = false
        )
        worldMap[n/2][n/2 - 1] = RoomObject(
            arrayOf(false, true, false, false),
            n/2,
            n/2 - 1,
            false,
            "",
            hasBeenVisited = false,
            hasResources = false
        )

        // Snake a path off of every room around center.
        snakeAPath(worldMap[n/2 + 1][n/2], worldMap)
        snakeAPath(worldMap[n/2 - 1][n/2], worldMap)
        snakeAPath(worldMap[n/2][n/2 + 1], worldMap)
        snakeAPath(worldMap[n/2][n/2 - 1], worldMap)

        // Send our new map back.
        return worldMap
    }

    /** Recursively carves paths between rooms given a starting point */
    private fun snakeAPath(start: RoomObject, map: Array<Array<RoomObject>>) {
        // Increment the number of rooms.
        roomsPlaced++

        // There's a 25% chance of resources spawning here.
        val rand = (0..100).random()
        if (rand >= 75)
            start.hasResources = true

        // Add a flag so this room doesn't get paths twice if another
        // path happens to meet it.
        start.hasBeenPathed = true

        // Randomly determine this room's appearance.
        val description = (roomDescriptions.indices).random()
        start.roomDescription = applicationContext.resources.getString(
            roomDescriptions[description]
        )

        // Iterate through each wall of the room to determine whether to add
        // a path out in that direction. Digits represent cardinal directions
        // where 0 = north, 1 = east, 2 = south, and 3 = west.
        for (i in 0 until start.doors.size) {
            // Safety check to make sure there isn't already an opening here.
            if (!start.doors[i]) {
                // Determines whether a door can go here.
                var canPlace = false

                // Check if we're at the end of the map in the given direction.
                // If we aren't we can place a room.
                if (i == 0) {
                    if (start.y > 0)
                        canPlace = true
                }
                if (i == 1) {
                    if (start.x < map.size - 1)
                        canPlace = true
                }
                if (i == 2) {
                    if (start.y < map.size - 1)
                        canPlace = true
                }
                if (i == 3) {
                    if (start.x > 0)
                        canPlace = true
                }

                // Now that we can, see if we want to by flipping a coin.
                // This coin flip means adjacent rooms won't always connect
                // which adds some maze walls to the game world.
                if (canPlace && (0..1).random() == 1) {
                    // Open the door in this direction.
                    start.doors[i] = true

                    // Tell the room in front of this one that there is now a
                    // path here and its door must open.
                    if (i == 0) {
                        // Going north, so open adjacent room's southern door.
                        map[start.y - 1][start.x].doors[2] = true

                        // Apply coordinates to adjacent room.
                        map[start.y - 1][start.x].x = start.x
                        map[start.y - 1][start.x].y = start.y - 1

                        // If the adjacent room hasn't already gone through
                        // this method, make a recursive call to extend paths
                        // out of it.
                        if (!map[start.y - 1][start.x].hasBeenPathed)
                            snakeAPath(map[start.y - 1][start.x], map)
                    }
                    if (i == 1) {
                        // Going east, so open adjacent room's western door.
                        map[start.y][start.x + 1].doors[3] = true

                        // Apply coordinates to adjacent room.
                        map[start.y][start.x + 1].x = start.x + 1
                        map[start.y][start.x + 1].y = start.y

                        // If the adjacent room hasn't already gone through
                        // this method, make a recursive call to extend paths
                        // out of it.
                        if (!map[start.y][start.x + 1].hasBeenPathed)
                            snakeAPath(map[start.y][start.x + 1], map)
                    }
                    if (i == 2) {
                        // Going south, so open adjacent room's northern door.
                        map[start.y + 1][start.x].doors[0] = true

                        // Apply coordinates to adjacent room.
                        map[start.y + 1][start.x].x = start.x
                        map[start.y + 1][start.x].y = start.y + 1

                        // If the adjacent room hasn't already gone through
                        // this method, make a recursive call to extend paths
                        // out of it.
                        if (!map[start.y + 1][start.x].hasBeenPathed)
                            snakeAPath(map[start.y + 1][start.x], map)
                    }
                    if (i == 3) {
                        // Going west, so open adjacent room's eastern door.
                        map[start.y][start.x - 1].doors[1] = true

                        // Apply coordinates to adjacent room.
                        map[start.y][start.x - 1].x = start.x - 1
                        map[start.y][start.x - 1].y = start.y

                        // If the adjacent room hasn't already gone through
                        // this method, make a recursive call to extend paths
                        // out of it.
                        if (!map[start.y][start.x - 1].hasBeenPathed)
                            snakeAPath(map[start.y][start.x - 1], map)
                    }
                }
            }
        }
    }
}