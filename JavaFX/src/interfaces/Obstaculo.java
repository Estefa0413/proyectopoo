package interfaces;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Obstaculo extends ObjetoJuego {

    private String tipo;

    public Obstaculo(double x, double y, String tipo) {
        super(x, y, tipo, 2.2);
        this.tipo = tipo;

        if (tipo.equals("tronco")) {
            this.ancho = 90;
            this.alto = 30;
        } else if (tipo.equals("roca")) {
            this.ancho = 46;
            this.alto = 46;
        } else {
            this.ancho = 60;
            this.alto = 40;
        }
    }

    @Override
    public void pintar(GraphicsContext graficos) {
        if (tipo.equals("tronco")) {
            graficos.setFill(Color.SADDLEBROWN);
            graficos.fillRoundRect(x, y, ancho, alto, 20, 20);
        } else if (tipo.equals("roca")) {
            graficos.setFill(Color.GRAY);
            graficos.fillOval(x, y, ancho, alto);
        } else {
            graficos.setFill(Color.DARKOLIVEGREEN);
            graficos.fillRoundRect(x, y, ancho, alto, 12, 12);
        }
    }

    @Override
    public void mover() {
        y += velocidad;
        if (y > Juego.ALTO_VENTANA + 80) {
            activo = false;
        }
    }

    @Override
    public Rectangle obtenerRectangulo() {
        return new Rectangle(x + 4, y + 4, ancho - 8, alto - 8);
    }
}
