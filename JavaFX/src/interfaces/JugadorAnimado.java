package interfaces;

import java.util.HashMap;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;

public class JugadorAnimado extends ObjetoJuego {

    private int vidas;
    private HashMap<String, Animacion> animaciones;
    private int xImagen;
    private int yImagen;
    private int anchoImagen;
    private int altoImagen;
    private String animacionActual;
    private int direccion = 1;

    private final int ANCHO_RENDER = 100;
    private final int ALTO_RENDER = 100;

    public JugadorAnimado(double x, double y, String nombreImagen, double velocidad, int vidas, String animacionActual) {
        super(x, y, nombreImagen, velocidad);
        this.vidas = vidas;
        this.animacionActual = animacionActual;
        this.ancho = ANCHO_RENDER;
        this.alto = ALTO_RENDER;
        animaciones = new HashMap<String, Animacion>();
        inicializarAnimaciones();
    }

    public int getVidas() {
        return vidas;
    }

    public void setVidas(int vidas) {
        this.vidas = vidas;
    }

    public String getAnimacionActual() {
        return animacionActual;
    }

    public void setAnimacionActual(String animacionActual) {
        this.animacionActual = animacionActual;
    }

    public int getDireccion() {
        return direccion;
    }

    public void setDireccion(int direccion) {
        this.direccion = direccion;
    }

    public void inicializarAnimaciones() {

        Rectangle coordenadasRemarArriba[] = {
            new Rectangle(50, 55, 190, 190),
            new Rectangle(305, 55, 190, 190),
            new Rectangle(560, 55, 190, 190),
            new Rectangle(305, 55, 190, 190)
        };

        Rectangle coordenadasRemarLado[] = {
            new Rectangle(805, 55, 190, 190),
            new Rectangle(1060, 55, 190, 190),
            new Rectangle(1315, 55, 190, 190),
            new Rectangle(1060, 55, 190, 190)
        };

        Rectangle coordenadasRemarAbajo[] = {
            new Rectangle(805, 355, 190, 190),
            new Rectangle(1060, 355, 190, 190),
            new Rectangle(1315, 355, 190, 190),
            new Rectangle(1060, 355, 190, 190)
        };

        Rectangle coordenadasDescanso[] = {
            new Rectangle(80, 65, 210, 210),
            new Rectangle(335, 65, 210, 210),
            new Rectangle(595, 65, 210, 210),
            new Rectangle(335, 65, 210, 210)
        };

        animaciones.put("remarArriba", new Animacion(0.10, coordenadasRemarArriba));
        animaciones.put("remarLado", new Animacion(0.10, coordenadasRemarLado));
        animaciones.put("remarAbajo", new Animacion(0.10, coordenadasRemarAbajo));
        animaciones.put("descanso", new Animacion(0.20, coordenadasDescanso));
    }

    public void calcularFrame(double t) {
        Rectangle coordenadas = animaciones.get(animacionActual).calcularFrameActual(t);
        this.xImagen = (int) coordenadas.getX();
        this.yImagen = (int) coordenadas.getY();
        this.anchoImagen = (int) coordenadas.getWidth();
        this.altoImagen = (int) coordenadas.getHeight();
    }

    @Override
    public Rectangle obtenerRectangulo() {
        return new Rectangle(x + 15, y + 15, 70, 70);
    }

    @Override
    public void pintar(GraphicsContext graficos) {

        if (animacionActual.equals("remarLado")) {
            if (direccion == 1) {
                graficos.drawImage(
                    Juego.imagenes.get(nombreImagen),
                    xImagen, yImagen, anchoImagen, altoImagen,
                    x, y, ANCHO_RENDER, ALTO_RENDER
                );
            } else {
                graficos.drawImage(
                    Juego.imagenes.get(nombreImagen),
                    xImagen, yImagen, anchoImagen, altoImagen,
                    x + ANCHO_RENDER, y, -ANCHO_RENDER, ALTO_RENDER
                );
            }
        } else {
            graficos.drawImage(
                Juego.imagenes.get(nombreImagen),
                xImagen, yImagen, anchoImagen, altoImagen,
                x, y, ANCHO_RENDER, ALTO_RENDER
            );
        }
    }

    @Override
    public void mover() {
        double dx = 0;
        double dy = 0;

        if (Juego.izquierda) {
            dx -= velocidad;
            direccion = -1;
        }

        if (Juego.derecha) {
            dx += velocidad;
            direccion = 1;
        }

        if (Juego.arriba) {
            dy -= velocidad;
        }

        if (Juego.abajo) {
            dy += velocidad;
        }

        x += dx;
        y += dy;

        if (x < 0)
            x = 0;

        if (x > Juego.ANCHO_VENTANA - ANCHO_RENDER)
            x = Juego.ANCHO_VENTANA - ANCHO_RENDER;

        if (y < 180)
            y = 180;

        if (y > Juego.ALTO_VENTANA - ALTO_RENDER - 10)
            y = Juego.ALTO_VENTANA - ALTO_RENDER - 10;
    }

    public void perderVida() {
        vidas--;
    }
}
