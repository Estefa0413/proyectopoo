
package interfaces;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GarzaEnemiga extends Enemigo {

    private Animacion animacionVueloAbajo;
    private int xImagen;
    private int yImagen;
    private int anchoImagen;
    private int altoImagen;

    private final int ANCHO_RENDER = 110;
    private final int ALTO_RENDER = 110;

    public GarzaEnemiga(double x, double y) {
        super(x, y, "garza", 2.7, 1, 150);
        this.ancho = ANCHO_RENDER;
        this.alto = ALTO_RENDER;
        inicializarAnimacion();
        calcularFrame(0);
    }

    public void inicializarAnimacion() {
        Rectangle coordenadas[] = {
            new Rectangle(25, 250, 430, 560),
            new Rectangle(520, 230, 500, 590),
            new Rectangle(1045, 235, 445, 585)
        };

        animacionVueloAbajo = new Animacion(0.12, coordenadas);
    }

    public void calcularFrame(double t) {
        Rectangle frameActual = animacionVueloAbajo.calcularFrameActual(t);
        this.xImagen = (int) frameActual.getX();
        this.yImagen = (int) frameActual.getY();
        this.anchoImagen = (int) frameActual.getWidth();
        this.altoImagen = (int) frameActual.getHeight();
    }

    @Override
    public void moverHaciaJugador(JugadorAnimado jugador) {
      
        y += velocidad;

        if (jugador.getX() + 35 < this.x) {
            this.x -= 0.9;
        } else if (jugador.getX() + 35 > this.x) {
            this.x += 0.9;
        }

        if (y > Juego.ALTO_VENTANA + 120) {
            activo = false;
        }
    }

    @Override
    public void mover() {
        y += velocidad;
        if (y > Juego.ALTO_VENTANA + 120) {
            activo = false;
        }
    }

    @Override
    public void pintar(GraphicsContext graficos) {
        if (Juego.imagenes.get(nombreImagen) != null) {
            graficos.drawImage(
                Juego.imagenes.get(nombreImagen),
                xImagen, yImagen, anchoImagen, altoImagen,
                x, y, ANCHO_RENDER, ALTO_RENDER
            );
        } else {
            graficos.setFill(Color.LIGHTGRAY);
            graficos.fillOval(x, y, ANCHO_RENDER, ALTO_RENDER);
        }
    }

    @Override
    public Rectangle obtenerRectangulo() {
        return new Rectangle(x + 20, y + 18, 70, 70);
    }
}
