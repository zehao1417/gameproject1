package game.pages

import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, Text}
import scalafx.stage.Stage

object menu {

  // function to create and display the main menu scene
  def startMenu(stage: Stage): Unit = {
    stage.title = "Main Menu"
    stage.resizable = false
    stage.scene = createMainMenu(stage)
    stage.show() // calls the stage
  }

  // function to create the main menu scene
  private def createMainMenu(stage: Stage): Scene = {
    val title = new Text("Space Shooters") {
      font = Font(36)
      fill = Color.White
    }

    val startButton = new Button("Start") {
      minWidth = 100
      onAction = _ => stage.scene = game.startGame(stage)  // starts the game when clicked
    }

    val instructionsButton = new Button("Instructions") { //
      minWidth = 100
      onAction = _ => instructions.showInstructions(stage) // shows instructions when clicked
    }

    // aligns the buttons and the title together in the middle
    val setup = new VBox(20, title, startButton, instructionsButton) {
      alignment = Pos.Center
    }

    val backgroundImage = new ImageView(new Image(getClass.getResourceAsStream("/Images/background.jpg"))) {
      preserveRatio = false
    }

    val mainMenu = new StackPane {
      children = Seq(backgroundImage, setup)
      alignment = Pos.Center
    }

    // creates main menu with background
    val scene = new Scene(800, 500) {
      root = mainMenu

      backgroundImage.fitWidth <== this.width
      backgroundImage.fitHeight <== this.height
    }

    scene

  }
}
