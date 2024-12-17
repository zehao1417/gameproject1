package game.pages

import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import scalafx.stage.Stage

object endScreen {
  def showEndScreen(stage: Stage, score: Int, timePlayed: Double): Unit = {
    val scoreLabel = new Label(s"Your Score: $score") {
      font = Font(24)
      textFill = Color.White
    }

    val timeLabel = new Label(f"Time Played: ${timePlayed}%.2f seconds") {
      font = Font(24)
      textFill = Color.White
    }

    val endLabel = new Label("Game Over") {
      font = Font(36)
      textFill = Color.Red
    }

    val backButton = new Button("Back to Main Menu") {
      font = Font(18)
      onAction = _ => {
        // Return to the main menu when clicked
        menu.startMenu(stage)
      }
    }

    val layout = new VBox(20, endLabel, scoreLabel, timeLabel, backButton) {
      style = "-fx-background-color: black;"
      alignment = scalafx.geometry.Pos.Center
      prefWidth = 800
      prefHeight = 600
    }

    val endScene = new Scene(800, 600) {
      root = layout
    }

    stage.scene = endScene
    stage.show()
  }
}
