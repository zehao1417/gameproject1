package game.pages

import javafx.scene.input.{KeyCode, KeyEvent}
import scalafx.animation.AnimationTimer
import scalafx.scene.Scene
import scalafx.scene.control.ProgressBar
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.stage.Stage

import scala.collection.mutable

class Player(rootPane: Pane, scene: Scene, stage: Stage, startTime: Long) {

  // to load the player's spaceship image
  private val spaceshipImage = new Image(getClass.getResourceAsStream("/Images/player.png"))

  val playerSpaceship: ImageView = new ImageView(spaceshipImage) {
    fitWidth = 50
    fitHeight = 50
  }

  rootPane.children.add(playerSpaceship)

  // player position and movement variables
  private var playerX: Double = 200
  private val playerY: Double = 550
  private val playerSpeed = 5.0
  private var isMovingLeft: Boolean = false
  private var isMovingRight: Boolean = false
  private var isShooting: Boolean = false // to track if space-bar is held down
  var score: Int = 0

  // bullet properties
  val bulletSpeed = 7.0
  var lastShotTime: Long = 0
  var activeBullet: mutable.Buffer[Rectangle] = mutable.Buffer()


  // function to move the player left
  private def moveLeft(): Unit = {
    if (playerX > 0) {
      playerX -= playerSpeed
      playerSpaceship.translateX = playerX
    }
  }

  // function to move the player right
  private def moveRight(): Unit = {
    if (playerX < rootPane.width.value - playerSpaceship.fitWidth()) {
      playerX += playerSpeed
      playerSpaceship.translateX = playerX
    }
  }

  // function to shoot a bullet
  def shoot(): Unit = {
    val currentTime = System.nanoTime()
    // delay between each shots
    if (activeBullet.isEmpty && (currentTime - lastShotTime) >= 1e8.toLong) {
      val bullet = new Rectangle {
        width = 5
        height = 10
        fill = Color.Red
        translateX = playerX + playerSpaceship.fitWidth() / 2 - width.value / 2
        translateY = playerY - height.value
      }

      rootPane.children.add(bullet)
      activeBullet += bullet
      lastShotTime = currentTime

      var bulletTimer: AnimationTimer = null

      bulletTimer = AnimationTimer { _ =>
        bullet.translateY.value -= bulletSpeed
        if (bullet.translateY.value < 0) {
          rootPane.children.remove(bullet)
          activeBullet -= bullet
          bulletTimer.stop()
        }
      }

      bulletTimer.start()
    }
  }

  // function to handle continuous movement, when holding A or D for example
  private def handleMovement(): Unit = {
    if (isMovingLeft) moveLeft()
    if (isMovingRight) moveRight()
  }

  // function to handle key events
  def handleKeyEvents(scene: Scene): Unit = {
    scene.addEventHandler(KeyEvent.KEY_PRESSED, (event: KeyEvent) => {
      event.getCode match {
        case KeyCode.A => isMovingLeft = true
        case KeyCode.D => isMovingRight = true
        case KeyCode.SPACE => isShooting = true
        case KeyCode.SPACE => shoot()
        case _ => // so that "A", "D", and spacebar are the only controls
      }
    })

    scene.addEventHandler(KeyEvent.KEY_RELEASED, (event: KeyEvent) => {
      event.getCode match {
        case KeyCode.A => isMovingLeft = false
        case KeyCode.D => isMovingRight = false
        case KeyCode.SPACE => isShooting = false
        case _ =>
      }
    })
  }

  // timer to continuously handle movement while keys are pressed
  AnimationTimer { _ =>
    handleMovement()
    if (isShooting) shoot() // constantly shoots bullet if spacebar is held down
  }.start()

  // adds the player hp as the progress bar
  private var playerHP: Int = 3
  private val hpBar = new ProgressBar {
    progress = 1.0
    prefWidth = 200
    style = "-fx-accent: red;" // set the color of the progress bar to red
  }

  // adds the hp bar to the top-right of the screen
  rootPane.children.add(hpBar)
  hpBar.translateX = 20
  hpBar.translateY = 20

  def isDead: Boolean = playerHP <= 0

  def checkCollisionWithEnemyBullets(enemyBullet: mutable.Buffer[Rectangle]): Unit = {
    enemyBullet.foreach { bullet =>
      if (bullet != null) {
      val bulletBounds = bullet.boundsInParent()
      val playerBounds = playerSpaceship.boundsInParent()

      if (bulletBounds.intersects(playerBounds)) {
        playerHit()
        rootPane.children.remove(bullet)
        enemyBullet -= bullet
        }
      }
    }
  }

  // function to handle when the player is hit by an enemy bullet
  private def playerHit(): Unit = {
    if (playerHP > 0) {
      playerHP -= 1
      hpBar.progress = playerHP.toDouble / 3
    }

    // check if the player is out of HP
    if (playerHP <= 0) {
      // Display the end screen when the player dies
      val endTime = System.nanoTime()
      val timePlayed = (endTime - startTime) / 1e9 // Calculate time played in seconds
      endScreen.showEndScreen(stage, score, timePlayed)
    }
  }

}
