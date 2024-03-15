package me.spenades.mytravelwallet.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.spenades.mytravelwallet.models.Categoria;
import me.spenades.mytravelwallet.models.Participante;
import me.spenades.mytravelwallet.models.Transaccion;

public class Operaciones {

    private static List<Transaccion> listaTransacciones;

    private static List<Participante> listaParticipantes;
    private static List<Participante> listaParticipan;
    private static long walletId;


    public static void main(String[] args) {
        listaTransacciones = new ArrayList<>();
        listaParticipantes = new ArrayList<>();
        listaParticipan = new ArrayList<>();
        walletId = 0l;

    }


    public String sumaTransacciones(List<Transaccion> listaTransacciones,
                                    List<Participante> listaParticipantes) {
        this.listaTransacciones = listaTransacciones;
        this.listaParticipantes = listaParticipantes;
        this.listaParticipan = listaParticipantes;
        float total = 0;
        for (Transaccion i : listaTransacciones) {
            total += Float.valueOf(i.getImporte());
        }

        proximoPagador();
        pagadoPorCadaParticipante();
        return String.valueOf(total);
    }


    public List<String> proximoPagador() {
        Map<Long, Double> listaPagadoresId = pagadoPorCadaParticipante();
        Map<Long, Double> pagadoresIdOrdenados = ordenarTransacciones(listaPagadoresId);

        Set<Long> pagadoresId = pagadoresIdOrdenados.keySet();
        long pagadorFinalId = pagadoresId.iterator().next();
        List<String> siguientePagador = new ArrayList<>();
        for (Participante participantes : listaParticipantes) {
            if (pagadorFinalId == participantes.getUserId()) {
                siguientePagador.add(String.valueOf(pagadorFinalId));
                siguientePagador.add(participantes.getNombre());
            }
        }
        return siguientePagador;
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


    public Map<Long, Double> listaParticipantesACero() {
        //Añade participantes y les pone valor 0.0
        Map<Long, Double> datos = new HashMap<Long, Double>();
        for (Participante participaIdItera : listaParticipantes) {
            long participanteId = participaIdItera.getUserId();
            double importeCero = 0.0;
            datos.put(participanteId, importeCero);
        }
        return datos;
    }


    public Map<Long, Double> pagadoPorCadaParticipante() {
        Map<Long, Double> datos = listaParticipantesACero();
        for (Transaccion transaccion : listaTransacciones) {

            // Creamos un diccionario con lo que ha pagado cada participante cada una de las
            // transacciones
            double importeAnterior = 0;
            double importeTransaccion = 0;
            double importeNuevo = 0;
            long pagadorId = 1;
            for (Participante participa : listaParticipantes) {

                pagadorId = transaccion.getPagadorId();
                importeAnterior = datos.get(pagadorId);
                importeTransaccion = Double.valueOf(transaccion.getImporte().toString());

                // Suma al valor anterior el nuevo valor gastado si lo hay
                importeNuevo = importeAnterior + importeTransaccion;
            }
            datos.put(pagadorId, importeNuevo);
        }
        return datos;
    }


    public double bigDecimal(double numero) {
        if (numero < 0) {
            BigDecimal valor = new BigDecimal(numero);
            BigDecimal resultado = valor.setScale(2, RoundingMode.HALF_DOWN);
            double resultadoSuma = resultado.doubleValue();
            return resultadoSuma;
        }
        return numero;
    }


    public List<String> listaDeMiembros() {
        List<String> lista = new ArrayList<>();

        for (Participante listaCompleta : listaParticipan) {
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

/*
    //https://es.stackoverflow.com/questions/90634/ocultar-teclado-al-lanzar-activity-con-edittext-y-volver-a-mostrarlo/90640
    //Shows the soft keyboard
    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }


    //https://umhandroid.momrach.es/ocultar-el-teclado-virtual/
    // Ocultar el teclado virtual
    private void HideKeyboard(View view) {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


        /*
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


    }
    */

}



