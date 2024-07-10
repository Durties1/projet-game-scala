import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scala.util.Random

final case class Rond(x: Double, y: Double, dx: Double, dy: Double, color: Color, rayon: Double) {
  val circle = new Circle {
    centerX = x
    centerY = y
    radius = rayon
    fill = color
  }

  def move(screenWidth: Double, screenHeight: Double): Rond = {
    val newDx = if (x < 0 || x > screenWidth) -dx else dx
    val newDy = if (y < 0 || y > screenHeight) -dy else dy
    val newX = x + newDx
    val newY = y + newDy
    this.copy(x = newX, y = newY, dx = newDx, dy = newDy)
  }

  def collidesWith(other: Rond): Boolean = {
    val dx = other.x - x
    val dy = other.y - y
    val distance = Math.sqrt(dx * dx + dy * dy)
    distance < circle.radius.value + other.circle.radius.value
  }

  def handleCollision(other: Rond, isPlayer: Boolean): Rond = {
    if (collidesWith(other)) {
      val dx = other.x - x
      val dy = other.y - y
      val angle = Math.atan2(dy, dx)
      val newDx = -Math.cos(angle)
      val newDy = -Math.sin(angle)
      this.copy(dx = newDx, dy = newDy)
    } else {
      this
    }
  }

  def collidesWithAny(others: Seq[Rond]): Boolean = {
    others.exists(this.collidesWith)
  }
}