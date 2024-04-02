package me.spenades.mytravelwallet.utilities;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import me.spenades.mytravelwallet.models.Miembro;
import me.spenades.mytravelwallet.models.Transaccion;

public class DeudaUtility extends AppCompatActivity {

    private static List<Transaccion> listaDeTransacciones;

    private static List<Miembro> listaDeMiembros;
    private static List<Miembro> listaDeParticipan;
    private static Map<Long, Double> listaDeGastos;
    private static long walletId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listaDeTransacciones = new ArrayList<>();
        listaDeMiembros = new ArrayList<>();
        listaDeParticipan = new ArrayList<>();
        listaDeGastos = new HashMap<>();
        walletId = 0L;

    }


    // Esta es la suma que aparece en el resumen.
    // INICIA TODAS LAS VARIABLES NECESARIAS PARA OPERAR EN ESTA CLASE
    public String sumaTransacciones(List<Transaccion> listaDeTransacciones,
                                    List<Miembro> listaDeMiembros) {
        DeudaUtility.listaDeTransacciones = listaDeTransacciones;
        DeudaUtility.listaDeMiembros = listaDeMiembros;
        listaDeParticipan = listaDeMiembros;
        double total = 0.0;
        for (Transaccion transaccion : listaDeTransacciones) {
            double totalLimpiar = Double.valueOf(transaccion.getImporte());
            total += Double.valueOf(totalLimpiar);
        }
        proximoPagador();
        pagadoPorCadaMiembro();
        return String.valueOf(total);
    }


    //#1
    public ArrayList resolucionDeudaWallet() {
        Operaciones operaciones = new Operaciones();
        // Recuperamos deudas por Wallet
        HashMap<Long, String> usuarioIdNombbre = new HashMap<>();
        for (Miembro miembro : listaDeMiembros) {
            String nombre = miembro.getNombre();
            long userId = miembro.getUserId();
            usuarioIdNombbre.put(userId, nombre);
        }

        //Iniciamos variables
        Map<Long, Double> pagarMiembro = new HashMap<>();
        Map<Long, Double> recibirMiembro = new HashMap<>();
        ArrayList<Map> gastoMiembros = gastosMiembrosTransacciones(listaDeTransacciones);
        Map<Long, Double> deudas = unificaGastoMiembroWallet(gastoMiembros); //#2

        // Extrae las Keys de las transacciones para los cálculos y rellenamos variables.
        deudas.keySet().forEach((key) -> {
            double pagar = 0L;
            double recibir = 0L;
            long miembroId = Long.parseLong(key.toString());
            long iterarKey = miembroId;
            double cantidadParticipa = Double.valueOf(String.valueOf(deudas.get(iterarKey)));

            // Separamos pagar y recibir en dos listas.
            if (cantidadParticipa >= 0L) {
                double recibirDecimales = operaciones.dosDecimalesDoubleDouble(cantidadParticipa);
                recibir = recibirDecimales;
            } else if (cantidadParticipa != 0L) {
                double pagarDecimales = operaciones.dosDecimalesDoubleDouble(cantidadParticipa);
                pagar = pagarDecimales;
            }
            pagarMiembro.put(miembroId, pagar);
            recibirMiembro.put(miembroId, recibir);
        });

        // Ordenamos pagos de mayor a menor
        Map<Long, Double> pagarOrdenado = ordenarTransacciones(pagarMiembro); //#6
        Map<Long, Double> recibirOrdenado = ordenarTransacciones(recibirMiembro); //#6

        // Creamos un diccionario para almacenar las soluciones
        ArrayList<ArrayList> soluciones = new ArrayList<>();

        // Iteramos sobre la lista de los que tienen que pagar(pagador)
        for (long pagarId : pagarOrdenado.keySet()) {
            double cantidadAPagar = operaciones.dosDecimalesDoubleDouble(pagarOrdenado.get(pagarId));
            long pagador = pagarId;

            // Iteramos sobre la lista de los que tienen que recibir(cobrador)
            while (Math.abs(cantidadAPagar) > 0) {
                for (long recibirId : recibirOrdenado.keySet()) {
                    double cantidadARecibirLimpiar = recibirOrdenado.get(recibirId);
                    double cantidadARecibir = operaciones.dosDecimalesDoubleDouble(cantidadARecibirLimpiar);
                    long cobrador = recibirId;

                    // Si el cobrador debe recibir más de lo que el pagador tiene que pagar
                    if (cantidadARecibir > Math.abs(cantidadAPagar)) {
                        double cantidadAPagarIni = cantidadAPagar;

                        // El pagador paga la cantidad que debe al cobrador
                        ArrayList<String> resoluciones = new ArrayList<>();
                        resoluciones.add(String.valueOf(pagador));
                        resoluciones.add(String.valueOf(usuarioIdNombbre.get(pagador)));
                        resoluciones.add(String.valueOf(cobrador));
                        resoluciones.add(String.valueOf(usuarioIdNombbre.get(cobrador)));
                        resoluciones.add(String.valueOf(cantidadAPagar));
                        soluciones.add(resoluciones);

                        //Actualizamos la cantidad que el cobrador tiene que recibir
                        double recibirCalculadoLimpiar = cantidadARecibir + cantidadAPagar;
                        double recibirCalculado = operaciones.dosDecimalesDoubleDouble(recibirCalculadoLimpiar);
                        recibirOrdenado.replace(recibirId, recibirCalculado);

                        // El pagador ya no tiene que pagar nada
                        cantidadAPagar = 0;
                        pagarOrdenado.replace(pagador, cantidadAPagar);
                        break;
                    }
                    // Si el cobrador debe recibir menos o lo mismo de lo que el pagador
                    // tiene que pagar
                    if (Math.abs(cantidadAPagar) == 0) {
                        break;
                    }
                    if (Math.abs(cantidadAPagar) > 0) {
                        //double cantidadAPagarIni = cantidadAPagar;

                        // El pagador paga la cantidad que el receptor tiene que recibir
                        ArrayList<String> resoluciones = new ArrayList<>();
                        resoluciones.add(String.valueOf(pagador));
                        resoluciones.add(String.valueOf(usuarioIdNombbre.get(pagador)));
                        resoluciones.add(String.valueOf(cobrador));
                        resoluciones.add(String.valueOf(usuarioIdNombbre.get(cobrador)));
                        resoluciones.add(String.valueOf(Math.abs(cantidadARecibir)));
                        soluciones.add(resoluciones);

                        // Actualizamos la cantidad que el pagador tiene que pagar
                        cantidadAPagar += cantidadARecibir;

                        // El cobrador ya no tiene que recibir nada
                        recibirOrdenado.remove(cobrador, cantidadARecibir);

                        pagarOrdenado.replace(pagador, cantidadAPagar);
                        break;
                    }
                }
                if (recibirOrdenado.size() == 0) {
                    break;

                }
            }

        }
        ArrayList<ArrayList> solucionesLimpias = eliminarSolucionesCero(soluciones); //#7
        // Rellenar Gastos Totales
        System.out.println(solucionesLimpias);
        return solucionesLimpias;
    }


    //#2
    public Map<Long, Double> unificaGastoMiembroWallet(ArrayList<Map> gastoMiembros) {
        //ArrayList<Map> gastoMiembros = gastosMiembrosTransacciones(); //#3
        Map<Long, Double> gastosParticianTotalesWallet = new HashMap<Long, Double>();
        try {
            // Iteramos en busca de las keys
            for (long l = 0; l < gastoMiembros.get(0).size(); l++) {

                // Extrae las Keys de las transacciones

                gastoMiembros.get(0).keySet().forEach((key) -> {
                    long miembroId = Long.parseLong(key.toString());
                    long iterar = miembroId;

                    // Suma todas las keys values del mismo Miembro
                    DoubleSummaryStatistics sumaValoresImporte =
                            (DoubleSummaryStatistics) gastoMiembros
                                    .stream()
                                    .collect(Collectors.summarizingDouble(
                                                    e -> Double.valueOf(((Map) e).get(iterar).toString())
                                            )
                                    );

                    double importeTotal = sumaValoresImporte.getSum();
                    gastosParticianTotalesWallet.put(iterar, importeTotal);
                    listaDeGastos = gastosParticianTotalesWallet;

                });

            }
        } catch (Exception e) {
        }
        return gastosParticianTotalesWallet;
    }


    //#3 que debería pagar cada miembro
    public ArrayList<Map> gastosMiembrosTransacciones(List<Transaccion> listaDeTransaccionesNew) {
        Operaciones operaciones = new Operaciones();
        ArrayList<Map> gastoMiembros = new ArrayList<>();

        // iteramos transacciones sacamos a lo que sale cada miembro
        for (Transaccion unaTransaccion : listaDeTransaccionesNew) {

            Map<Long, Double> pagadoPorMiembro = pagadoPorCadaMiembro(unaTransaccion); //#4
            Transaccion deudaTotal = unaTransaccion;
            Double aPagarPorMiembro = aPagarPorMiembro(unaTransaccion); //#5

            // Extraemos lo que ha pagado cada miembro
            Map<Long, Double> deudas = new HashMap<Long, Double>();
            for (int n = 0; n < listaDeMiembros.size(); n++) {
                long miembroId = listaDeMiembros.get(n).getUserId();
                double pagado = pagadoPorMiembro.get(miembroId);

                // segun se haya pagado o no una cantidad se resta
                // Comprueba si existe en la lista de miembros y asigna importes, los demás
                // a cero
                String listado = unaTransaccion.getMiembros();
                String miembroIdInt = String.valueOf(miembroId);
                int existeEnListas1 = listado.indexOf(miembroIdInt);

                double saldo = pagado;
                // NO ha pagado esta transacción pero está en ella
                if (pagado == 0.0 && existeEnListas1 >= 0) {
                    double saldoDecimales = pagado - aPagarPorMiembro;
                    saldo = operaciones.dosDecimalesDoubleDouble(saldoDecimales);
                    deudas.put(miembroId, saldo);

                    // SI pagado la transacción y está en ella
                } else if (pagado > 0.0 && existeEnListas1 >= 0) {
                    double saldoDecimales = pagado - aPagarPorMiembro;
                    saldo = operaciones.dosDecimalesDoubleDouble(saldoDecimales);
                    deudas.put(miembroId, saldo);

                    // No está en la transacción
                } else {
                    saldo = pagado;

                }
                deudas.put(miembroId, saldo);
            }

            gastoMiembros.add(deudas);
        }

        return gastoMiembros;
    }


    //#4 Suma todos los pagos por miembro
    public Map<Long, Double> pagadoPorCadaMiembro(Transaccion transaccion) {
        long transaccionId = transaccion.getId();

        // Creamos un diccionario con lo que ha pagado cada miembro de esta transacción
        Map<Long, Double> datos = new HashMap<Long, Double>();
        long nombreId = transaccion.getPagadorId();
        double importe = Double.valueOf(transaccion.getImporte());
        for (Miembro unNombre : listaDeMiembros) {
            long nombreIdIndividual = unNombre.getUserId();
            double importeCero = 0.0;
            datos.put(nombreIdIndividual, importeCero);
        }
        datos.put(nombreId, importe);

        return datos;
    }


    //#5
    public double aPagarPorMiembro(Transaccion transaccion) {
        Operaciones operaciones = new Operaciones();
        // Calculamos la deuda total
        double numeroMiembros = listaDeParticipan.size();
        double importeTransaccion = Double.valueOf(transaccion.getImporte());
        double importePorMiembroLimpiar = importeTransaccion / (numeroMiembros + 0);
        double importePorMiembro = operaciones.dosDecimalesDoubleDouble(importePorMiembroLimpiar);
        return importePorMiembro;
    }


    //#6
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


    //#7
    public ArrayList<ArrayList> eliminarSolucionesCero(ArrayList<ArrayList> soluciones) {
        ArrayList<ArrayList> solucionesLimpias = new ArrayList<>();

        // Iteramos y eliminamos los que tienen que pagar 0 €
        for (ArrayList solucion : soluciones) {
            String importe = solucion.get(4).toString();
            double importeLong = Double.parseDouble(importe);
            if (importeLong != 0D) {
                solucionesLimpias.add(solucion);
            }
        }
        return solucionesLimpias;
    }

    // Compone parte de la pantalla de Gastos Totales.
    public ArrayList<Spanned> operacionesResolucionDeudas() {
        Operaciones operaciones = new Operaciones();
        ArrayList<ArrayList> gastosTotalesDivididos = new ArrayList<>();

        // Creamos nuevas listas pero sin los gastos que se han pagado para uno mismo.
        Map<Long, Double> importePagadoParticipante = transacionesGastosTotales();
        List<Transaccion> listaDeTransaccionesSinPropio = listaDeTransaccionesSinPropio();
        ArrayList<Map> gastoMiembros = gastosMiembrosTransacciones(listaDeTransaccionesSinPropio);
        Map<Long, Double> listaDeGastosSinPropio = unificaGastoMiembroWallet(gastoMiembros);
        ArrayList<Spanned> miembrosGastos = new ArrayList<>();

        // Iteramos sobre los gastos para extraer que tendría que haber pagado cada miembro.
        // donde no estára incluido lo que se haya pagado sólo a si mismo.
        for (Long miembroIdGasto : listaDeGastosSinPropio.keySet()) {
            double importeDeberiaPagarAlWallet = listaDeGastosSinPropio.get(miembroIdGasto);


            // Limpiamos decimales del importe
            double importeMovimientosWallet = operaciones.dosDecimalesDoubleDouble(importeDeberiaPagarAlWallet);

            // Obtenemos de la listaDeGastos los ids, y los iteramos con la listaDeMiembros, para obtener el nombre.
            for (Miembro solucionFinal : listaDeMiembros) {
                long miembroId = solucionFinal.getUserId();
                String miembro = solucionFinal.getNombre();

                // Añadimos los gastos o cobros por participantes.
                if (miembroIdGasto == miembroId) {

                    // if (importeDeberiaPagarAlWallet == miembroId) {
                    //String miembro = new String();
                    String importeString = "";

                    // Rescatamos importe pagado por cada miembro
                    double importeHaPagado = importePagadoParticipante.get(solucionFinal.getUserId());
                    String importeHaPagadoString = operaciones.dosDecimalesDoubleString(importeHaPagado);

                    // Rescatamos importes a pagar o recibir.
                    String importeFinalDebe = "";
                    String importeFinalPagado = "";
                    double importeDoubleLimpio = 0;
                    double importeFinalPagadoLimpio = 0;
                    double gastoRealizado = 0;

                    // Según sea a pagar o recibir se separan para poder mostrarlos.
                    if (importeMovimientosWallet <= 0D) {
                        importeDoubleLimpio = Math.abs(importeMovimientosWallet);
                        String limpiezaNumero = String.valueOf(importeDoubleLimpio);
                        if (importeDoubleLimpio <= 0D) {
                            importeFinalDebe = "No tiene deudas.";
                        } else {

                            importeFinalDebe = "<b>Debe </b><FONT COLOR=#E91E63>" + limpiezaNumero + "€</FONT>";
                        }
                        // Calculamos lo que el gasto total de cada miembro en el Wallet
                        double gastoRealizadoLimpiar = importeDoubleLimpio + importeHaPagado;
                        gastoRealizado = operaciones.dosDecimalesDoubleDouble(gastoRealizadoLimpiar);
                    } else {
                        importeFinalPagadoLimpio = importeMovimientosWallet;
                        importeString = String.valueOf(importeFinalPagadoLimpio);
                        importeFinalPagado = "<b>Le deben </b><FONT COLOR=#1ED63A>" + importeString + "€</FONT>";

                        // Calculamos lo que ha gasto total de cada miembro en el Wallet
                        double gastoRealizadoLimpiar = importeHaPagado - importeMovimientosWallet;
                        gastoRealizado = operaciones.dosDecimalesDoubleDouble(gastoRealizadoLimpiar);
                    }
                    String gastoRealizadoEnWallet = String.valueOf(Math.abs(gastoRealizado));

                    Spanned miembroGastoString = Html.fromHtml(
                            "<strong>" + miembro + "</strong> adeuda <b>" + gastoRealizadoEnWallet + "€</b><br><i>     Ha pagado " + importeHaPagadoString + "€</i><br>" + importeFinalDebe + importeFinalPagado);
                    miembrosGastos.add(miembroGastoString);
                    ArrayList<String> gastosTotales = new ArrayList<>();
                    gastosTotales.add(miembro);
                    gastosTotales.add(importeString);
                    gastosTotalesDivididos.add(gastosTotales);
                    //}
                }

            }
        }

        return miembrosGastos;
    }


    // Gastos que ha realizado cada participante en las transacciones del wallet
    public Map<Long, Double> transacionesGastosTotales() {
        Map<Long, Double> gastoTotalpagador = new HashMap<>();

        //lista de miembrps sin formato
        listaDeMiembros();

        // Creamos una diccionario con todos los participantes del Wallet, y los ponemos a 0 gastado
        double pagadorImporte = 0.0D;
        long pagadorId = 0;
        for (Miembro solucionFinal : listaDeMiembros) {
            long miembroId = solucionFinal.getUserId();
            gastoTotalpagador.put(miembroId, pagadorImporte);
        }
        // Ahora añadimos lo que realmente ha pagado cada uno.

        for (Transaccion transaccion : listaDeTransacciones) {
            pagadorId = transaccion.getPagadorId();
            double importePrevio = gastoTotalpagador.get(pagadorId);
            importePrevio += Double.parseDouble(transaccion.getImporte());
            gastoTotalpagador.replace(pagadorId, importePrevio);
        }
        return gastoTotalpagador;
    }


    public List<String> listaDeMiembros() {
        List<String> lista = new ArrayList<>();

        for (Miembro listaCompleta : listaDeParticipan) {
            String nombre = listaCompleta.getNombre();
            lista.add(nombre);
        }
        return lista;
    }


    public List<String> proximoPagador() {
        Map<Long, Double> listaPagadoresId = pagadoPorCadaMiembro();
        Map<Long, Double> pagadoresIdOrdenados = ordenarTransacciones(listaPagadoresId);

        Set<Long> pagadoresId = pagadoresIdOrdenados.keySet();
        long pagadorFinalId = pagadoresId.iterator().next();
        List<String> siguientePagador = new ArrayList<>();
        for (Miembro miembros : listaDeMiembros) {
            if (pagadorFinalId == miembros.getUserId()) {
                siguientePagador.add(String.valueOf(pagadorFinalId));
                siguientePagador.add(miembros.getNombre());
            }
        }
        return siguientePagador;
    }


    public Map<Long, Double> listaMiembrosACero() {
        //Añade miembros y les pone valor 0.0
        Map<Long, Double> datos = new HashMap<Long, Double>();
        for (Miembro participaIdItera : listaDeMiembros) {
            long miembroId = participaIdItera.getUserId();
            double importeCero = 0.0;
            datos.put(miembroId, importeCero);
        }
        return datos;
    }


    public Map<Long, Double> pagadoPorCadaMiembro() {
        Map<Long, Double> datos = listaMiembrosACero();
        for (Transaccion transaccion : listaDeTransacciones) {

            // Creamos un diccionario con lo que ha pagado cada miembro cada una de las
            // transacciones
            double importeAnterior = 0;
            double importeTransaccion = 0;
            double importeNuevo = 0;
            long pagadorId = 1;
            for (Miembro participa : listaDeMiembros) {

                pagadorId = transaccion.getPagadorId();
                importeAnterior = datos.get(pagadorId);
                importeTransaccion = Double.valueOf(transaccion.getImporte());

                // Suma al valor anterior el nuevo valor gastado si lo hay
                importeNuevo = importeAnterior + importeTransaccion;
            }
            datos.put(pagadorId, importeNuevo);
        }
        return datos;
    }

    // sin los Propios pagos sólo a si mismo para Gastos totales


    // lista de transacciones sin los Propios pagos sólo a si mismo para Gastos totales
    public List<Transaccion> listaDeTransaccionesSinPropio() {
        List<Transaccion> listaDeTransaccionesSinPropio = new ArrayList<>();

        //iteramos sobre los gastos y si es el pagador y participante el mismo no se añade.
        for (Transaccion transaccion : listaDeTransacciones) {
            long pagadorId = transaccion.getPagadorId();
            String listaParticipantes = transaccion.getMiembros();
            if (listaParticipantes.length() == 1 && listaParticipantes.contains(String.valueOf(pagadorId))) {

            } else {
                listaDeTransaccionesSinPropio.add(transaccion);
            }
        }
        return listaDeTransaccionesSinPropio;
    }


    // Formatea e importe para mostrarlo según España.
    //https://javiergarciaescobedo.es/programacion-en-java/29-trucos/113-formato-de-numeros-monedas-y-porcentajes2
    public String importeFormateado(String importeLimpiar) {
        double importe = Double.valueOf(importeLimpiar);
        NumberFormat formatoImporte = NumberFormat.getCurrencyInstance();
        //Si se desea forzar el formato español:
        formatoImporte = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));
        return formatoImporte.format(importe);
    }

}



