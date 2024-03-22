package me.spenades.mytravelwallet.utilities;

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
        walletId = 0l;

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


    // https://es.stackoverflow.com/questions/100147/como-puedo-hacer-para-mostrar-solo-dos-decimales-en-la-operacion-que-sea
    public String dosDecimalesDoubleString(double doubleNumeroString) {
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2); //Define 2 decimales.
        String numeroString = format.format(doubleNumeroString);
        String numeroDecimal = numeroString.replaceAll(",", "."); //cambia la coma por un punto
        return numeroDecimal;
    }

    public String dosDecimalesStringString(String stringNumeroString) {
        double numero = Double.parseDouble(stringNumeroString);
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2); //Define 2 decimales.
        String numeroString = format.format(numero);
        String numeroDecimal = numeroString.replaceAll(",", ".");
        return numeroDecimal;
    }

    public Double dosDecimalesDoubleDouble(double doubleNumerodouble) {
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2); //Define 2 decimales.
        String numeroString = format.format(doubleNumerodouble);
        String numeroDecimal = numeroString.replaceAll(",", ".");
        double numeroDecimalDouble = Double.parseDouble(numeroDecimal);
        return numeroDecimalDouble;
    }

    public Double dosDecimalesStringDouble(String stringNumeroDouble) {
        double numero = Double.parseDouble(stringNumeroDouble);
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2); //Define 2 decimales.
        String numeroString = format.format(numero);
        String numeroDecimal = numeroString.replaceAll(",", ".");
        double numeroDecimalDouble = Double.parseDouble(numeroDecimal);
        return numeroDecimalDouble;
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
/*
    // Cierra el teclado
    // https://umhandroid.momrach.es/ocultar-el-teclado-virtual/
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // Abre el teclado
    // https://umhandroid.momrach.es/ocultar-el-teclado-virtual/
    private void visibleKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 1);
    }

 */
}



