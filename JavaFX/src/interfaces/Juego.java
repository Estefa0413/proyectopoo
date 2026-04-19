package interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;


import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.application.Platform;

public class Juego extends Application {

    public static final int ANCHO_VENTANA = 700;
    public static final int ALTO_VENTANA = 500;
    public static final int TAM_TILE = 64;

    private GraphicsContext graficos;
    private Group root;
    private Scene escena;
    private Canvas lienzo;

    private JugadorAnimado jugador;
    private ArrayList<Disparo> disparos;
    private ArrayList<Enemigo> enemigos;
    private ArrayList<Obstaculo> obstaculos;
    private ArrayList<Item> items;

    private PuntajeManager puntajeManager;
    private Random random;
    private AnimationTimer timer;

    public static boolean arriba;
    public static boolean abajo;
    public static boolean izquierda;
    public static boolean derecha;
    public static HashMap<String, Image> imagenes;

    private int[][] mapa;
    private double offsetMapaY = 0;
    private double velocidadScroll = 1.6;

    private int puntaje = 0;
    private int distancia = 0;
    private boolean jugando = true;
    private boolean finMostrado = false;

    private long ultimoSpawnEnemigo = 0;
    private long ultimoDisparo = 0;
    private int siguienteOleada = 1;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage ventana) {
        inicializarComponentes();
        cargarImagenes();
        inicializarMapa();
        inicializarOleadas();
        gestionEventos();

        ventana.setScene(escena);
        ventana.setTitle("Capibara del Pantano");
        ventana.show();
        lienzo.requestFocus();

        cicloJuego();
    }

    public void inicializarComponentes() {
        imagenes = new HashMap<String, Image>();
        disparos = new ArrayList<Disparo>();
        enemigos = new ArrayList<Enemigo>();
        obstaculos = new ArrayList<Obstaculo>();
        items = new ArrayList<Item>();
        puntajeManager = new PuntajeManager();
        random = new Random();

        root = new Group();
        escena = new Scene(root, ANCHO_VENTANA, ALTO_VENTANA);
        lienzo = new Canvas(ANCHO_VENTANA, ALTO_VENTANA);
        graficos = lienzo.getGraphicsContext2D();
        root.getChildren().add(lienzo);

        jugador = new JugadorAnimado(ANCHO_VENTANA / 2.0 - 50, 360, "capibaraanimado", 4.2, 3, "descanso");
    }
    public void cargarImagenes() {
        cargarImagen("capibaraanimado", "/interfaces/capibaraanimado.png");
        cargarImagen("babosa", "/interfaces/babosa.png");
        cargarImagen("garza", "/interfaces/garza.png");
        cargarImagen("basurero", "/interfaces/basurero.png");
        cargarImagen("piedra", "/interfaces/piedra.png");
        cargarImagen("tronco", "/interfaces/tronco.png");
        cargarImagen("vida", "/interfaces/corazon.png");
        cargarImagen("puntos", "/interfaces/naranja.png");
        cargarImagen("bonus", "/interfaces/hoja.png");
        cargarImagen("fondo", "/interfaces/fondo.png");
    }

    private void cargarImagen(String llave, String ruta) {
        try {
            Image img = new Image(getClass().getResourceAsStream(ruta));
            imagenes.put(llave, img);
        } catch (Exception e) {
            System.out.println("No se pudo cargar: " + ruta);
        }
    }

    public void inicializarMapa() {
        mapa = new int[40][11];
        for (int i = 0; i < mapa.length; i++) {
            for (int j = 0; j < mapa[i].length; j++) {
                if (j == 0 || j == mapa[i].length - 1) {
                    mapa[i][j] = 1;
                } else if ((i + j) % 7 == 0) {
                    mapa[i][j] = 2;
                } else {
                    mapa[i][j] = 0;
                }
            }
        }
    }

    public void inicializarOleadas() {
    enemigos.add(new BabosaToxica(120, -80));
    enemigos.add(new BabosaToxica(510, -220));

    obstaculos.add(new Obstaculo(260, -150, "tronco"));
    obstaculos.add(new Obstaculo(420, -320, "piedra"));

    items.add(new Item(340, -260, "vida", 1));
    items.add(new Item(180, -420, "puntos", 100));
    items.add(new Item(500, -520, "bonus", 200));
}

    public void cicloJuego() {
    long tiempoInicial = System.nanoTime();

    timer = new AnimationTimer() {
        @Override
        public void handle(long tiempoActual) {
            double t = (tiempoActual - tiempoInicial) / 1000000000.0;

            if (jugando) {
                actualizarEstado(t);
                pintar();
            } else {
                pintar();

                if (!finMostrado) {
                    finMostrado = true;
                    stop();

                    Platform.runLater(() -> {
                        mostrarFinJuego();
                    });
                }
            }
        }
    };

    timer.start();
}

    public void actualizarEstado(double t) {
    offsetMapaY += velocidadScroll;
    distancia += velocidadScroll;

    jugador.calcularFrame(t);
    jugador.mover();

    if (izquierda || derecha) {
        jugador.setAnimacionActual("remarLado");
    } else if (arriba) {
        jugador.setAnimacionActual("remarArriba");
    } else if (abajo) {
        jugador.setAnimacionActual("remarAbajo");
    } else {
        jugador.setAnimacionActual("descanso");
    }

        for (Enemigo e : enemigos) {
        if (e instanceof GarzaEnemiga) {
            ((GarzaEnemiga) e).calcularFrame(t);
        } else if (e instanceof BabosaToxica) {
            ((BabosaToxica) e).calcularFrame(t);
        } else if (e instanceof BasureroContaminante) {
            ((BasureroContaminante) e).calcularFrame(t);
        }

        for (Obstaculo obstaculo : obstaculos) {
            obstaculo.mover();
        }
    }

    generarOleadasPlanificadas();
    generarObstaculosDinamicos();
    moverListas();
    verificarColisiones();
    limpiarListas();

    if (jugador.getVidas() <= 0) {
        jugando = false;
    }
}

    private void generarOleadasPlanificadas() {
    if (siguienteOleada == 1 && distancia > 700) {
        enemigos.add(new GarzaEnemiga(100, -80));
        enemigos.add(new GarzaEnemiga(520, -160));

        items.add(new Item(320, -240, "puntos", 150));
        siguienteOleada++;
    }

    if (siguienteOleada == 2 && distancia > 1400) {
        enemigos.add(new BasureroContaminante(320, -120));
        obstaculos.add(new Obstaculo(180, -140, "tronco"));
        obstaculos.add(new Obstaculo(470, -230, "piedra"));

        items.add(new Item(240, -260, "bonus", 200));
        siguienteOleada++;
    }

    if (siguienteOleada == 3 && distancia > 2200) {
        enemigos.add(new BabosaToxica(120, -80));
        enemigos.add(new GarzaEnemiga(320, -160));
        enemigos.add(new BasureroContaminante(520, -260));

        items.add(new Item(350, -320, "vida", 1));
        siguienteOleada++;
    }
}

    private void generarObstaculosDinamicos() {
    if (distancia % 500 < velocidadScroll) {
        double x = 80 + random.nextInt(ANCHO_VENTANA - 160);
        String tipo = random.nextBoolean() ? "piedra" : "tronco";
        obstaculos.add(new Obstaculo(x, -70, tipo));
    }

    if (distancia % 900 < velocidadScroll) {
        double x = 80 + random.nextInt(ANCHO_VENTANA - 160);
        items.add(new Item(x, -40, "puntos", 100));
    }

    if (distancia % 1400 < velocidadScroll) {
        double x = 80 + random.nextInt(ANCHO_VENTANA - 160);
        items.add(new Item(x, -40, "bonus", 200));
    }

    if (distancia % 2200 < velocidadScroll) {
        double x = 80 + random.nextInt(ANCHO_VENTANA - 160);
        items.add(new Item(x, -40, "vida", 1));
    }

    long ahora = System.nanoTime();

    if (ahora - ultimoSpawnEnemigo > 2_200_000_000L) {
        int tipoEnemigo = random.nextInt(3);
        double x = 60 + random.nextInt(ANCHO_VENTANA - 120);

        if (tipoEnemigo == 0) {
            enemigos.add(new BabosaToxica(x, -100));
        } else if (tipoEnemigo == 1) {
            enemigos.add(new GarzaEnemiga(x, -130));
        } else {
            enemigos.add(new BasureroContaminante(x, -120));
        }

        ultimoSpawnEnemigo = ahora;
    }
}

    private void moverListas() {
        for (Disparo d : disparos) d.mover();
        for (Enemigo e : enemigos) e.moverHaciaJugador(jugador);
        for (Obstaculo o : obstaculos) o.mover();
        for (Item i : items) i.mover();
    }

    private void verificarColisiones() {
    for (int i = 0; i < obstaculos.size(); i++) {
        Obstaculo o = obstaculos.get(i);
        if (o.isActivo() && rectIntersects(jugador.obtenerRectangulo(), o.obtenerRectangulo())) {
            jugador.perderVida();
            o.setActivo(false);
        }
    }

    for (int i = 0; i < items.size(); i++) {
        Item item = items.get(i);
        if (item.isActivo() && rectIntersects(jugador.obtenerRectangulo(), item.obtenerRectangulo())) {
            if (item.getTipo().equals("vida")) {
                jugador.setVidas(jugador.getVidas() + item.getValor());
            } else if (item.getTipo().equals("puntos")) {
                puntaje += item.getValor();
            } else if (item.getTipo().equals("bonus")) {
                puntaje += item.getValor();
            }
            item.setActivo(false);
        }
    }

    for (int i = 0; i < enemigos.size(); i++) {
        Enemigo enemigo = enemigos.get(i);

        if (enemigo.isActivo() && rectIntersects(jugador.obtenerRectangulo(), enemigo.obtenerRectangulo())) {
            jugador.perderVida();
            enemigo.setActivo(false);
        }

        for (int j = 0; j < disparos.size(); j++) {
            Disparo d = disparos.get(j);

            if (d.isActivo() && enemigo.isActivo()
                    && rectIntersects(d.obtenerRectangulo(), enemigo.obtenerRectangulo())) {

                enemigo.recibirDanio(1);
                d.setActivo(false);

                if (!enemigo.isActivo()) {
                    puntaje += enemigo.getPuntos();

                    if (random.nextDouble() < 0.20) {
                        items.add(new Item(enemigo.getX(), enemigo.getY(), "puntos", 80));
                    }
                }
            }
        }
    }
}

    private void limpiarListas() {
        Iterator<Disparo> itD = disparos.iterator();
        while (itD.hasNext()) if (!itD.next().isActivo()) itD.remove();
        Iterator<Enemigo> itE = enemigos.iterator();
        while (itE.hasNext()) if (!itE.next().isActivo()) itE.remove();
        Iterator<Obstaculo> itO = obstaculos.iterator();
        while (itO.hasNext()) if (!itO.next().isActivo()) itO.remove();
        Iterator<Item> itI = items.iterator();
        while (itI.hasNext()) if (!itI.next().isActivo()) itI.remove();
    }

    public void pintar() {
    graficos.clearRect(0, 0, ANCHO_VENTANA, ALTO_VENTANA);

    if (imagenes.get("fondo") != null) {
        graficos.drawImage(imagenes.get("fondo"), 0, 0, ANCHO_VENTANA, ALTO_VENTANA);
    } else {
        pintarMapa();
    }

    for (int i = 0; i < obstaculos.size(); i++) {
        obstaculos.get(i).pintar(graficos);
    }

    for (int i = 0; i < items.size(); i++) {
        items.get(i).pintar(graficos);
    }

    for (int i = 0; i < enemigos.size(); i++) {
        enemigos.get(i).pintar(graficos);
    }

    for (int i = 0; i < disparos.size(); i++) {
        disparos.get(i).pintar(graficos);
    }

    jugador.pintar(graficos);
    pintarHUD();
    }

    private void pintarMapa() {
        double desplazamiento = offsetMapaY % TAM_TILE;
        int filaInicial = (int) (offsetMapaY / TAM_TILE);

        for (int filaPantalla = 0; filaPantalla <= ALTO_VENTANA / TAM_TILE + 1; filaPantalla++) {
            int filaMapa = (filaInicial + filaPantalla) % mapa.length;
            double y = filaPantalla * TAM_TILE - desplazamiento;

            for (int col = 0; col < mapa[filaMapa].length; col++) {
                double x = col * TAM_TILE;
                int tipo = mapa[filaMapa][col];

                if (tipo == 0) {
                    graficos.setFill(Color.web("#8ACB5A"));
                } else if (tipo == 1) {
                    graficos.setFill(Color.web("#7A5C3E"));
                } else {
                    graficos.setFill(Color.web("#69BEEB"));
                }
                graficos.fillRect(x, y, TAM_TILE, TAM_TILE);

                if (tipo == 0) {
                    graficos.setFill(Color.web("#6DAA43"));
                    graficos.fillOval(x + 8, y + 12, 10, 6);
                    graficos.fillOval(x + 32, y + 30, 8, 5);
                } else if (tipo == 2) {
                    graficos.setStroke(Color.web("#BFEFFF"));
                    graficos.strokeLine(x + 10, y + 18, x + 24, y + 18);
                    graficos.strokeLine(x + 34, y + 36, x + 48, y + 36);
                }
            }
        }
    }

    private void pintarHUD() {
    graficos.setFill(Color.rgb(0, 0, 0, 0.45));
    graficos.fillRoundRect(10, 10, 220, 80, 15, 15);

    graficos.setFill(Color.WHITE);
    graficos.fillText("Vidas: " + jugador.getVidas(), 20, 30);
    graficos.fillText("Puntaje: " + puntaje, 20, 52);
    graficos.fillText("Distancia: " + distancia, 20, 74);
    }

    public void gestionEventos() {
    escena.setOnKeyPressed((KeyEvent evento) -> {
        switch (evento.getCode().toString()) {
            case "RIGHT":
                derecha = true;
                jugador.setDireccion(1);
                jugador.setAnimacionActual("remarLado");
                break;

            case "LEFT":
                izquierda = true;
                jugador.setDireccion(-1);
                jugador.setAnimacionActual("remarLado");
                break;

            case "UP":
                arriba = true;
                jugador.setAnimacionActual("remarArriba");
                break;

            case "DOWN":
                abajo = true;
                jugador.setAnimacionActual("remarAbajo");
                break;

            case "SPACE":
                disparar();
                break;
        }
    });

    escena.setOnKeyReleased((KeyEvent evento) -> {
        switch (evento.getCode().toString()) {
            case "RIGHT":
                derecha = false;
                break;

            case "LEFT":
                izquierda = false;
                break;

            case "UP":
                arriba = false;
                break;

            case "DOWN":
                abajo = false;
                break;
        }

        if (derecha || izquierda) {
            jugador.setAnimacionActual("remarLado");
        } else if (arriba) {
            jugador.setAnimacionActual("remarArriba");
        } else if (abajo) {
            jugador.setAnimacionActual("remarAbajo");
        } else {
            jugador.setAnimacionActual("descanso");
        }
    });
}

    private void disparar() {
        long ahora = System.nanoTime();
        if (ahora - ultimoDisparo < 230_000_000L) return;
        ultimoDisparo = ahora;
        double xDisparo = jugador.getX() + 32;
        double yDisparo = jugador.getY() - 8;
        disparos.add(new Disparo(xDisparo, yDisparo));
    }

    private boolean rectIntersects(javafx.scene.shape.Rectangle a, javafx.scene.shape.Rectangle b) {
        return a.getBoundsInLocal().intersects(b.getBoundsInLocal());
    }

    private void mostrarFinJuego() {
        if (puntajeManager.clasifica(puntaje)) {
            TextInputDialog dialogo = new TextInputDialog("Jugador");
            dialogo.setTitle("Nuevo Puntaje");
            dialogo.setHeaderText("¡Entraste al top 10!");
            dialogo.setContentText("Escribe tu nombre:");
            String nombre = dialogo.showAndWait().orElse("Jugador");
            if (nombre.trim().isEmpty()) nombre = "Jugador";
            puntajeManager.agregarPuntaje(nombre, puntaje);
        }

        ArrayList<String> top = puntajeManager.obtenerTop10ComoTexto();
        StringBuilder textoTop = new StringBuilder();
        for (String s : top) textoTop.append(s).append("\n");

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Fin del juego");
        alerta.setHeaderText("Capibara del Pantano");
        alerta.setContentText("Puntaje final: " + puntaje + "\n\nTop 10:\n" + textoTop);
        alerta.showAndWait();
    }
}
