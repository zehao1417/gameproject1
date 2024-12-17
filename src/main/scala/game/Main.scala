package game

import game.pages.menu
import scalafx.application.JFXApp


object Main extends JFXApp {

  // Override the start method to set up the main menu after the stage is initialized
  stage = new JFXApp.PrimaryStage {
    title = "Space Shooters"
  }

  // Initialize the main menu after the stage is ready
  menu.startMenu(stage)
}
