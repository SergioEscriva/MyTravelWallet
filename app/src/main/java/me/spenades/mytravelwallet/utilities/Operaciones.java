package me.spenades.mytravelwallet.utilities;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.spenades.mytravelwallet.models.Categoria;
import me.spenades.mytravelwallet.models.Miembro;
import me.spenades.mytravelwallet.models.Transaccion;

public class Operaciones {

    private static List<Transaccion> listaTransacciones;

    private static List<Miembro> listaMiembros;
    private static List<Miembro> listaParticipan;
    private static long walletId;


    public static void main(String[] args) {
        listaTransacciones = new ArrayList<>();
        listaMiembros = new ArrayList<>();
        listaParticipan = new ArrayList<>();
        walletId = 0L;

    }


    public Map<Long, Double> ordenarTransacciones(Map<Long, Double> transacciones) {

        // Ordenamos pagos de mayor a menor
        // Java 8 Stream (https://www.techiedelight.com/es/sort-map-by-values-java/)
        //https://www.techiedelight.com/es/sort-map-java-reverse-ordering-keys/
        Map<Long, Double> ordenarDiccionario = new LinkedHashMap<>();

        transacciones.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(entry -> ordenarDiccionario.put(entry.getKey(), entry.getValue()));
        return ordenarDiccionario;
    }


    //https://stackoverflow.com/questions/22609217/rounding-bigdecimal-values-with-2-decimal-places
    public String dosDecimalesDoubleString(double doubleNumeroString) {
        BigDecimal numeroDecimal = new BigDecimal(doubleNumeroString);
        numeroDecimal = numeroDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
        return numeroDecimal.toString();
    }

    public String dosDecimalesStringString(String stringNumeroString) {
        double numero = Double.parseDouble(stringNumeroString);
        BigDecimal numeroDecimal = new BigDecimal(numero);
        numeroDecimal = numeroDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
        return numeroDecimal.toString();
    }

    public Double dosDecimalesDoubleDouble(double doubleNumerodouble) {
        BigDecimal numeroDecimal = new BigDecimal(doubleNumerodouble);
        numeroDecimal = numeroDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
        return numeroDecimal.doubleValue();
    }

    public List<String> listaDeMiembros() {
        List<String> lista = new ArrayList<>();

        for (Miembro listaCompleta : listaParticipan) {
            String nombre = listaCompleta.getNombre();
            lista.add(nombre);
        }
        return lista;
    }


    public String[] listaDeCategoriasString(List<Categoria> listaDeCategorias) {

        // Obtener los datos de la lista
        // String nombreCategoria = categoria.getCategoria();
        // long categoriaId = categoria.getId();


        String[] categorias = new String[listaDeCategorias.size()];
        for (int y = 0; y < listaDeCategorias.size(); y++) {
            categorias[y] = String.valueOf(listaDeCategorias.get(y).getCategoria());
        }
        return categorias;
    }


    public String fechaDeHoy() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        String fecha = day + "/" + month + "/" + year;
        return fecha;
    }

    // validar si es un numero
    public boolean esNumero(String numero) {
        boolean resultado;
        try {
            Double.parseDouble(numero);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
        }
        return resultado;
    }



}



