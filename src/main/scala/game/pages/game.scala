package game.pages

import scalafx.animation.AnimationTimer
import scalafx.scene.Scene
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.stage.Stage

import scala.collection.mutable
import scala.util.Random

object game {
  def startGame(stage: Stage): Scene = {
    val rootPane = new Pane()

    val scene = new Scene(rootPane, 800, 600) {
      fill = Color.Black
    }

    val backgroundImage: ImageView = new ImageView(new Image(getClass.getResourceAsStream("/Images/background.jpg"))) {
      preserveRatio = false
    }

    // adds background image
    rootPane.children.add(backgroundImage)

    // allows the background to be set accordingly to the screen size
    backgroundImage.fitWidth = scene.getWidth
    backgroundImage.fitHeight = scene.getHeight

    // makes background fit to application screen size
    stage.setOnShown { _ =>
      backgroundImage.fitWidth = scene.getWidth
      backgroundImage.fitHeight = scene.getHeight
    }

    // start of game
    val startTime = System.nanoTime()
    var score: Int = 0

    // creates the player
    val player = new Player(rootPane, scene, stage, startTime)
    player.playerSpaceship.translateX = (scene.width.value - player.playerSpaceship.fitWidth()) / 2
    player.playerSpaceship.translateY = scene.height.value - player.playerSpaceship.fitHeight() - 10

    // to keep track of enemy's bullet
    val enemyBullet: mutable.Buffer[Rectangle] = mutable.Buffer()

    // creates sets of enemies
    val enemies: mutable.Buffer[Enemy] = mutable.Buffer()

    // to keep track of player's bullet
    val bullets: mutable.Buffer[Rectangle] = player.activeBullet
    
    // spawns enemies up to a limit of five on the screen at one time
    var lastEnemySpawnTime: Long = 0
    val enemySpawnInterval: Long = 1e9.toLong

    var gameLoop: AnimationTimer = null

    // timer for game updates
    gameLoop = AnimationTimer { currentTime =>
      // check if player is dead
      if (player.isDead) {
        gameLoop.stop()
        val endTime = System.nanoTime()
        val timePlayed = (endTime - startTime) / 1e9 // Convert to seconds

        // Direct to end screen
        endScreen.showEndScreen(stage, score, timePlayed)
      } else {
        // remove any enemies that are eliminated or off-screen
        val eliminatedEnemies = enemies.filterNot(_.isAlive)
        score += eliminatedEnemies.size*100 // update score based on eliminated enemies
        enemies --= eliminatedEnemies

        // spawns new enemies if there are less than 5 on the screen
        val currentTime = System.nanoTime()
        while (enemies.size < 8 && currentTime - lastEnemySpawnTime >= enemySpawnInterval) {
          val spawnFromLeft = Random.nextBoolean()
          val spawnX = if (spawnFromLeft) -50 else scene.width.value + 50 // spawns enemy outside of the screen
          val enemyDirection = if (spawnFromLeft) 1.0 else -1.0 // 1 for right, -1 for left
          enemies += new Enemy(rootPane, spawnX, enemyDirection, enemyBullet, spawnFromLeft)
          lastEnemySpawnTime = currentTime
        }

        // updates all enemy and player status (for movement, bullet collisions, ...)
        player.checkCollisionWithEnemyBullets(enemyBullet)
        enemies.foreach(_.update(bullets))
      }
    }

    gameLoop.start()

    player.handleKeyEvents(scene)
    stage.scene = scene
    scene

  }
}