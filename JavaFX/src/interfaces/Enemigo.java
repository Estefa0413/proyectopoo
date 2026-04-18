package interfaces;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public abstract class Enemigo extends ObjetoJuego {

    protected int vidas;
    protected int puntos;
    protected Color colorBase;

    public Enemigo(double x, double y, String nombreImagen, double velocidad, int vidas, int puntos) {
        super(x, y, nombreImagen, velocidad);
        this.vidas = vidas;
        this.puntos = puntos;
        this.ancho = 52;
        this.alto = 52;
        this.colorBase = Color.DARKSEAGREEN;
    }

    public int getVidas() { return vidas; }
    public int getPuntos() { return puntos; }

    public void recibirDanio(int d) {
        vidas -= d;
        if (vidas <= 0) {
            activo = false;
        }
    }

    public abstract void moverHaciaJugador(JugadorAnimado jugador);

    @Override
    public void mover() {
        y += velocidad;
        if (y > Juego.ALTO_VENTANA + 60) {
            activo = false;
        }
    }

    @Override
    public void pintar(GraphicsContext graficos) {
        graficos.setFill(colorBase);
        graficos.fillOval(x, y, ancho, alto);
        graficos.setStroke(Color.BLACK);
        graficos.strokeOval(x, y, ancho, alto);
    }

    @Override
    public Rectangle obtenerRectangulo() {
        return new Rectangle(x + 6, y + 6, ancho - 12, alto - 12);
    }
}
