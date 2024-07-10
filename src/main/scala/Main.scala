import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.animation.AnimationTimer
import scalafx.scene.input.MouseEvent
import scala.util.Random
import scalafx.scene.paint.Color
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.stage.Screen
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox
import scalafx.geometry.Pos
import scalafx.scene.text.Text
import scalafx.scene.layout.Pane

object Main extends JFXApp3 {
  val random = new Random()

  override def start(): Unit = {
    val (screenWidth, screenHeight) = (Screen.primary.visualBounds.width, Screen.primary.visualBounds.height)

    val playButton = new Button("Jouer")
    val quitButton = new Button("Quitter")
    val restartButton = new Button("Restart")
    val menu = new VBox(playButton, quitButton)
    menu.alignment = Pos.Center
    menu.spacing = 10

    val gamePane = new Pane()
    val gameScene = new Scene(gamePane, screenWidth, screenHeight)
    val menuScene = new Scene(menu, screenWidth, screenHeight)

    stage = new PrimaryStage {
      title = "Ronds"
      width = screenWidth
      height = screenHeight
      scene = menuScene
    }

    quitButton.onAction = _ => System.exit(0)

    playButton.onAction = _ => {
      stage.scene = gameScene
      startGame()
    }

    restartButton.onAction = _ => {
      stage.scene = gameScene
      startGame()
    }

    def startGame(): Unit = {
      var ronds = (for (_ <- 1 to 50) yield {
        Rond(
          random.nextDouble() * screenWidth,
          random.nextDouble() * screenHeight,
          random.nextDouble() * 4 - 2,
          random.nextDouble() * 4 - 2,
          Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)),
          10
        )
      }).toList

      var player = Rond(screenWidth / 2, screenHeight / 2, 0, 0, Color.Blue, 20)

      gamePane.children = (ronds :+ player).map(_.circle)

      stage.scene.value.onMouseMoved = (me: MouseEvent) => {
        player.circle.centerX.value = me.x
        player.circle.centerY.value = me.y
      }

      var timer: AnimationTimer = null
      timer = AnimationTimer(_ => {
        ronds = ronds.map(_.move(screenWidth, screenHeight))
        for (rond <- ronds) {
          player = player.handleCollision(rond, isPlayer = true)
          ronds = ronds.map(_.handleCollision(rond, isPlayer = false))
        }
        gamePane.children = (ronds :+ player).map(_.circle)

        if (player.collidesWithAny(ronds)) {
          timer.stop()
          val gameOverText = new Text("Game Over")
          gameOverText.layoutX = screenWidth / 2
          gameOverText.layoutY = screenHeight / 2
          gamePane.children = List(gameOverText, restartButton, quitButton)
        }
      })

      timer.start()
    }
  }
}