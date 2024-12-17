package game.pages

import scalafx.animation.AnimationTimer
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

import scala.collection.mutable

class Enemy(rootPane: Pane, startX: Double, direction: Double, enemyBullets: mutable.Buffer[Rectangle], spawn: Boolean){
  // Load the enemy image
  private val enemyImage = new Image(getClass.getResourceAsStream("/Images/enemies.png"))

  // Create an ImageView for the enemy
  private val enemyView = new ImageView(enemyImage) {
    fitWidth = 50
    fitHeight = 50
    translateX = startX
    translateY = 50 // Start near the top of the screen
  }

  // enemy properties
  private val speed: Double = 1.5
  val bulletSpeed: Double = 4.0
  var lastShotTime: Long = 0
  private var alive: Boolean = true // track if the enemy is still alive

  // adds enemy to the pane
  rootPane.children.add(enemyView)

  // function to move the enemy horizontally
  def move(): Unit = {
    enemyView.translateX.value += direction * speed

    // check if enemy is inside the screen
    if (enemyView.translateX.value < -enemyView.fitWidth() || enemyView.translateX.value > rootPane.width.value) {
      alive = false
      rootPane.children.remove(enemyView)
    }
  }

  // function to shoot bullets for enemies
  def shoot(): Unit = {
    val currentTime = System.nanoTime()

    // allow shooting of bullet every second
    if (currentTime - lastShotTime >= 1e9.toLong) {
      val bullet = new Rectangle {
        width = 5
        height = 10
        fill = Color.Red
        translateX = enemyView.translateX.value + enemyView.fitWidth() / 2 - width.value / 2
        translateY = enemyView.translateY.value + enemyView.fitHeight()
      }

      rootPane.children.add(bullet)
      enemyBullets += bullet

      var bulletTimer: AnimationTimer = null // Declare the type explicitly

      bulletTimer = AnimationTimer { _ =>
        bullet.translateY.value += bulletSpeed

        // Check for collision with the player
        if (bullet.translateY.value > rootPane.height.value) {
          rootPane.children.remove(bullet)
          bulletTimer.stop()
        }
      }

      bulletTimer.start()
      lastShotTime = currentTime
    }
  }

  private def checkHitByBullet(bullet: Rectangle): Boolean = {
    val bulletBounds = bullet.boundsInParent()
    val enemyBounds = enemyView.boundsInParent()

    if (bulletBounds.intersects(enemyBounds)) {
      // enemy is hit, remove enemy from the screen
      alive = false
      rootPane.children.remove(enemyView)
      true
    } else {
      // enemy was not hit
      false
    }
  }

  // to check if the enemy is still alive
  def isAlive: Boolean = alive

  def update(bullets: mutable.Buffer[Rectangle]): Unit = {
    move()
    shoot()

    // check if bullet hit the enemy
    bullets.foreach { bullet =>
      if (checkHitByBullet(bullet)) {
        // If hit, remove the bullet
        rootPane.children.remove(bullet)
      }
    }
  }


}
