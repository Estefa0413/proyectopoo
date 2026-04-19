
package interfaces;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

public class Obstaculo extends ObjetoJuego {
    
    private String tipo; 
    
    public Obstaculo(double x, double y, String tipo) {
        super(x, y, tipo, 0.0005);  
        this.tipo = tipo;
        this.ancho = 80;  
        this.alto = 80;  
    }

    public void pintar(GraphicsContext graficos) {
        Image imagen = null;
        if (tipo.equals("piedra")) {
            imagen = Juego.imagenes.get("piedra");
        } else if (tipo.equals("tronco")) {
            imagen = Juego.imagenes.get("tronco");
        }
        if (imagen != null) {
            graficos.drawImage(imagen, x, y, ancho, alto); 
        } else {
            graficos.setFill(javafx.scene.paint.Color.BROWN);
            graficos.fillRect(x, y, ancho, alto);
        }
    }
    
    @Override
    public Rectangle obtenerRectangulo() {
        return new Rectangle(x, y, ancho, alto);
    }

    @Override
    public void mover() {
        y += 2;

        if (y > Juego.ALTO_VENTANA + 300){
            y = -600;
        }
    }

}
