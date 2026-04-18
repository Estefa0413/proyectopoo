package interfaces;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BasureroContaminante extends Enemigo {

    private Animacion animacionMover;
    private int xImagen;
    private int yImagen;
    private int anchoImagen;
    private int altoImagen;

    private final int ANCHO_RENDER = 95;
    private final int ALTO_RENDER = 95;

    public BasureroContaminante(double x, double y) {
        super(x, y, "basurero", 1.3, 3, 220);
        this.ancho = ANCHO_RENDER;
        this.alto = ALTO_RENDER;
        this.colorBase = Color.DARKGRAY;
        inicializarAnimacion();
        calcularFrame(0);
    }

    public void inicializarAnimacion() {
        Image img = Juego.imagenes.get("basurero");

        int anchoTotal = 950;
        int altoTotal = 350;

        if (img != null) {
            anchoTotal = (int) img.getWidth();
            altoTotal = (int) img.getHeight();
        }

        int anchoFrame = anchoTotal / 3;
        int altoFrame = altoTotal;

        Rectangle coordenadas[] = {
            new Rectangle(0, 0, anchoFrame, altoFrame),
            new Rectangle(anchoFrame, 0, anchoFrame, altoFrame),
            new Rectangle(anchoFrame * 2, 0, anchoFrame, altoFrame)
        };

        animacionMover = new Animacion(0.16, coordenadas);
    }

    public void calcularFrame(double t) {
        Rectangle frameActual = animacionMover.calcularFrameActual(t);
        this.xImagen = (int) frameActual.getX();
        this.yImagen = (int) frameActual.getY();
        this.anchoImagen = (int) frameActual.getWidth();
        this.altoImagen = (int) frameActual.getHeight();
    }

    @Override
    public void moverHaciaJugador(JugadorAnimado jugador) {
        y += velocidad;

        if (Math.abs(jugador.getX() - this.x) > 8) {
            if (jugador.getX() < this.x) {
                this.x -= 0.9;
            } else {
                this.x += 0.9;
            }
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
            graficos.setFill(Color.DARKGRAY);
            graficos.fillRect(x, y, ANCHO_RENDER, ALTO_RENDER);
        }
    }

    @Override
    public Rectangle obtenerRectangulo() {
        return new Rectangle(x + 12, y + 12, 70, 70);
    }
}
