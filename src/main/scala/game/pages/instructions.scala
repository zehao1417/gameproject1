package game.pages

import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.stage.Stage

object instructions {
  def showInstructions(stage: Stage): Unit = {
    val rules = new Label(
      """|Game Rules:
         |1. Use "A" and "D" to move your spaceship left and right.
         |2. Press spacebar to shoot bullets.
         |3. Destroy enemy spaceships while avoiding the incoming bullets to win the game.
         |4. The game ends when all the enemy spaceships are destroyed.""".stripMargin
    ) {
      style = "-fx-font-size: 18px; -fx-text-fill: white;"
      wrapText = true
    }

    val backButton = new Button("Back to Menu") {
      onAction = _ => menu.startMenu(stage) // goes back to main menu
    }

    val setup = new VBox(20, rules, backButton) {
      alignment = Pos.Center
      padding = scalafx.geometry.Insets(20)
    }

    val backgroundImage = new ImageView(new Image(getClass.getResourceAsStream("/Images/background.jpg"))) {
      preserveRatio = false
    }

    val instructionsPage = new StackPane {
      children = Seq(backgroundImage, setup)
      alignment = Pos.Center
    }

    // creates the scene and binds the background image size to the scene size
    val instructionsScene = new Scene(800, 500) {
      root = instructionsPage
      backgroundImage.fitWidth <== this.width
      backgroundImage.fitHeight <== this.height
    }

    stage.scene = instructionsScene
  }
}
