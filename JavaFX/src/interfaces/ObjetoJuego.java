package interfaces;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;

public abstract class ObjetoJuego {

    protected double x;
    protected double y;
    protected String nombreImagen;
    protected double velocidad;
    protected int ancho;
    protected int alto;
    protected boolean activo = true;

    public ObjetoJuego(double x, double y, String nombreImagen, double velocidad) {
        this.x = x;
        this.y = y;
        this.nombreImagen = nombreImagen;
        this.velocidad = velocidad;
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public String getNombreImagen() { return nombreImagen; }
    public void setNombreImagen(String nombreImagen) { this.nombreImagen = nombreImagen; }
    public double getVelocidad() { return velocidad; }
    public void setVelocidad(double velocidad) { this.velocidad = velocidad; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Rectangle obtenerRectangulo() {
        return new Rectangle(x, y, ancho, alto);
    }

    public abstract void pintar(GraphicsContext graficos);
    public abstract void mover();
}
