package me.spenades.mytravelwallet.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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


    public String sumaTransacciones(List<Transaccion> listaTransacciones,
                                    List<Miembro> listaMiembros) {
        this.listaTransacciones = listaTransacciones;
        this.listaMiembros = listaMiembros;
        this.listaParticipan = listaMiembros;
        float total = 0;
        for (Transaccion i : listaTransacciones) {
            total += Float.valueOf(i.getImporte());
        }

        proximoPagador();
        pagadoPorCadaMiembro();
        return String.valueOf(total);
    }


    public List<String> proximoPagador() {
        Map<Long, Double> listaPagadoresId = pagadoPorCadaMiembro();
        Map<Long, Double> pagadoresIdOrdenados = ordenarTransacciones(listaPagadoresId);

        Set<Long> pagadoresId = pagadoresIdOrdenados.keySet();
        long pagadorFinalId = pagadoresId.iterator().next();
        List<String> siguientePagador = new ArrayList<>();
        for (Miembro miembros : listaMiembros) {
            if (pagadorFinalId == miembros.getUserId()) {
                siguientePagador.add(String.valueOf(pagadorFinalId));
                siguientePagador.add(miembros.getNombre());
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


    public Map<Long, Double> listaMiembrosACero() {
        //Añade miembros y les pone valor 0.0
        Map<Long, Double> datos = new HashMap<Long, Double>();
        for (Miembro participaIdItera : listaMiembros) {
            long miembroId = participaIdItera.getUserId();
            double importeCero = 0.0;
            datos.put(miembroId, importeCero);
        }
        return datos;
    }


    public Map<Long, Double> pagadoPorCadaMiembro() {
        Map<Long, Double> datos = listaMiembrosACero();
        for (Transaccion transaccion : listaTransacciones) {

            // Creamos un diccionario con lo que ha pagado cada miembro cada una de las
            // transacciones
            double importeAnterior = 0;
            double importeTransaccion = 0;
            double importeNuevo = 0;
            long pagadorId = 1;
            for (Miembro participa : listaMiembros) {

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


    public double bigDecimal1(double numero) {
        if (numero < 0) {
            BigDecimal valor = new BigDecimal(numero);
            BigDecimal resultado = valor.setScale(2, RoundingMode.HALF_DOWN);
            double resultadoSuma = resultado.doubleValue();
            return resultadoSuma;
        }
        return numero;
    }


    // https://es.stackoverflow.com/questions/100147/como-puedo-hacer-para-mostrar-solo-dos-decimales-en-la-operacion-que-sea
    public String dosDecimales(double numero) {
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2); //Define 2 decimales.
        String numeroString = format.format(numero);
        String numeroDecimal = numeroString.replaceAll(",", ".");
        return numeroDecimal;
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

}



