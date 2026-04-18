package interfaces;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Disparo extends ObjetoJuego {

    public Disparo(double x, double y) {
        super(x, y, "disparo", 8);
        this.ancho = 8;
        this.alto = 18;
    }

    @Override
    public void pintar(GraphicsContext graficos) {
        graficos.setFill(Color.DEEPSKYBLUE);
        graficos.fillRoundRect(x, y, ancho, alto, 8, 8);
    }

    @Override
    public void mover() {
        y -= velocidad;
        if (y + alto < 0) {
            activo = false;
        }
    }

    @Override
    public Rectangle obtenerRectangulo() {
        return new Rectangle(x, y, ancho, alto);
    }
}
