
package interfaces;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Item extends ObjetoJuego {

    private String tipo;
    private int valor;

    private int frameActualPuntos = 0;
    private int frameActualBonus = 0;

    private long ultimoCambioFramePuntos = 0;
    private long ultimoCambioFrameBonus = 0;

    public Item(double x, double y, String tipo, int valor) {
        super(x, y, tipo, 1.8);
        this.tipo = tipo;
        this.valor = valor;

        if (tipo.equals("vida")) {
            this.ancho = 85;
            this.alto = 80;
        } else if (tipo.equals("puntos")) {
            this.ancho = 88;
            this.alto = 82;
        } else {
            this.ancho = 88;
            this.alto = 82;
        }
    }

    public String getTipo() {
        return tipo;
    }

    public int getValor() {
        return valor;
    }

    @Override
    public void pintar(GraphicsContext graficos) {
        Image imagenItem = null;

        if (tipo.equals("vida")) {
            imagenItem = Juego.imagenes.get("vida");
        } else if (tipo.equals("puntos")) {
            imagenItem = Juego.imagenes.get("puntos");
        } else if (tipo.equals("bonus")) {
            imagenItem = Juego.imagenes.get("bonus");
        }

        if (imagenItem != null) {
            if (tipo.equals("vida")) {
                pintarSpriteHorizontal(graficos, imagenItem, 0, 3);
            } else if (tipo.equals("puntos")) {
                pintarSprite2x2(graficos, imagenItem, frameActualPuntos);
            } else if (tipo.equals("bonus")) {
                pintarSprite2x2(graficos, imagenItem, frameActualBonus);
            }
        } else {
            if (tipo.equals("vida")) {
                graficos.setFill(Color.HOTPINK);
            } else if (tipo.equals("puntos")) {
                graficos.setFill(Color.ORANGE);
            } else {
                graficos.setFill(Color.LIGHTGREEN);
            }
            graficos.fillOval(x, y, ancho, alto);
        }
    }

    private void pintarSpriteHorizontal(GraphicsContext graficos, Image imagenItem, int frame, int columnas) {
        int anchoFrame = (int) imagenItem.getWidth() / columnas;
        int altoFrame = (int) imagenItem.getHeight();

        int xImagen = frame * anchoFrame;
        int yImagen = 0;

        graficos.drawImage(
            imagenItem,
            xImagen, yImagen, anchoFrame, altoFrame,
            x, y, ancho, alto
        );
    }

    private void pintarSprite2x2(GraphicsContext graficos, Image imagenItem, int frameActual) {
        int columnas = 2;
        int filas = 2;

        int anchoFrame = (int) imagenItem.getWidth() / columnas;
        int altoFrame = (int) imagenItem.getHeight() / filas;

        int columna = frameActual % columnas;
        int fila = frameActual / columnas;

        int xImagen = columna * anchoFrame;
        int yImagen = fila * altoFrame;

        graficos.drawImage(
            imagenItem,
            xImagen, yImagen, anchoFrame, altoFrame,
            x, y, ancho, alto
        );
    }

    @Override
    public void mover() {
        y += velocidad;

        long ahora = System.nanoTime();

        if (tipo.equals("puntos")) {
            if (ahora - ultimoCambioFramePuntos > 180_000_000L) {
                frameActualPuntos = (frameActualPuntos + 1) % 4;
                ultimoCambioFramePuntos = ahora;
            }
        }

        if (tipo.equals("bonus")) {
            if (ahora - ultimoCambioFrameBonus > 200_000_000L) {
                frameActualBonus = (frameActualBonus + 1) % 4;
                ultimoCambioFrameBonus = ahora;
            }
        }

        if (y > Juego.ALTO_VENTANA + 40) {
            activo = false;
        }
    }

    @Override
    public Rectangle obtenerRectangulo() {
        return new Rectangle(x + 6, y + 6, ancho - 12, alto - 12);
    }
}