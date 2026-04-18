package interfaces;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PuntajeManager {

    private final String NOMBRE_ARCHIVO = "puntajes.txt";

    public PuntajeManager() {
    try {
        File archivo = new File(NOMBRE_ARCHIVO);
        if (!archivo.exists()) {
            archivo.createNewFile();
        }
    } catch (Exception e) {
        e.printStackTrace();
        }
    }

    private static class Registro {
        String nombre;
        int puntaje;

        Registro(String nombre, int puntaje) {
            this.nombre = nombre;
            this.puntaje = puntaje;
        }
    }

    public boolean clasifica(int puntajeActual) {
        ArrayList<Registro> lista = cargar();
        if (lista.size() < 10) {
            return true;
        }
        int menor = lista.get(lista.size() - 1).puntaje;
        return puntajeActual > menor;
    }

    public void agregarPuntaje(String nombre, int puntaje) {
        ArrayList<Registro> lista = cargar();
        lista.add(new Registro(nombre, puntaje));

        Collections.sort(lista, new Comparator<Registro>() {
            @Override
            public int compare(Registro a, Registro b) {
                return Integer.compare(b.puntaje, a.puntaje);
            }
        });

        while (lista.size() > 10) {
            lista.remove(lista.size() - 1);
        }

        guardar(lista);
    }

    public ArrayList<String> obtenerTop10ComoTexto() {
        ArrayList<Registro> lista = cargar();
        ArrayList<String> lineas = new ArrayList<String>();
        for (int i = 0; i < lista.size(); i++) {
            lineas.add((i + 1) + ". " + lista.get(i).nombre + " - " + lista.get(i).puntaje);
        }
        return lineas;
    }

    private ArrayList<Registro> cargar() {
        ArrayList<Registro> lista = new ArrayList<Registro>();
        File archivo = new File(NOMBRE_ARCHIVO);
        if (!archivo.exists()) {
            return lista;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 2) {
                    lista.add(new Registro(partes[0], Integer.parseInt(partes[1])));
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(lista, new Comparator<Registro>() {
            @Override
            public int compare(Registro a, Registro b) {
                return Integer.compare(b.puntaje, a.puntaje);
            }
        });

        return lista;
    }

    private void guardar(ArrayList<Registro> lista) {
    try {
        BufferedWriter bw = new BufferedWriter(new FileWriter(NOMBRE_ARCHIVO, false));

        for (int i = 0; i < lista.size(); i++) {
            bw.write(lista.get(i).nombre + "," + lista.get(i).puntaje);
            bw.newLine();
        }

        bw.close();
        System.out.println(new File(NOMBRE_ARCHIVO).getAbsolutePath());

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    
}
