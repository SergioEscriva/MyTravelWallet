package me.spenades.mytravelwallet.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.spenades.mytravelwallet.SQLiteDB.AyudanteBaseDeDatos;
import me.spenades.mytravelwallet.models.Participante;
import me.spenades.mytravelwallet.models.Transaccion;
import me.spenades.mytravelwallet.models.Wallet;


public class Operaciones {

    private static List<Transaccion> listaTransacciones;

    private static List<Participante> listaParticipantes;
    private static List<Participante> listaParticipan;
    private static long walletId;
    private Wallet wallet;
    private AyudanteBaseDeDatos ayudanteBaseDeDatos;


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


    public List<String> sumaTransaccionesWallet(Long walletId, List<Wallet> listaDeWallets) {
        List<String> importeFinalWallet = new ArrayList<>();

        for (Wallet walletActivo : listaDeWallets) {
            for (Transaccion listaTransacciones : listaTransacciones) {
                listaTransacciones.getImporte();
                importeFinalWallet.add(listaTransacciones.getImporte());
            }
        }
        return importeFinalWallet;
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
                //long participanteId = participa.getUserId();
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


    public double aPagarPorParticipante(Transaccion transaccion) {

        // Calculamos la deuda total
        int numeroParticipantes = listaParticipan.size();
        double importeTransaccion = Double.valueOf(transaccion.getImporte());
        //double importePorParticipante = bigDecimal(importeTransaccion / numeroParticipantes);
        double importePorParticipante =
                bigDecimal(bigDecimal(importeTransaccion) / bigDecimal(numeroParticipantes + 0.0));
        return importePorParticipante;
    }


    // Se suma que ha pagado cada participante
    public ArrayList<Map> gastosParticipantesTransacciones() {
        ArrayList<Map> gastoParticipantes = new ArrayList<>();

        // iteramos transacciones sacamos a lo que sale cada participante
        for (Transaccion unaTransaccion : listaTransacciones) {

            Map<Long, Double> pagadoPorParticipante = pagadoPorCadaParticipante();
            double deudaTotal = Double.valueOf(unaTransaccion.getImporte());
            double aPagarPorParticipante = aPagarPorParticipante(unaTransaccion);

            // Extraemos lo que ha pagado cada participante
            Map<Long, Double> deudas = new HashMap<Long, Double>();
            for (int n = 0; n < listaParticipantes.size(); n++) {
                long participanteId = listaParticipantes.get(n).getUserId();
                double pagado = pagadoPorParticipante.get(participanteId);

                // segun se haya pagado o no una cantidad se resta
                // Comprueba si existe en la lista de participantes y asigna importes, los demás
                // a cero
                String listado = unaTransaccion.getParticipantes();
                String participanteIdInt = String.valueOf(participanteId);
                int existeEnListas1 = listado.indexOf(participanteIdInt);

                double saldo = pagado;
                // NO ha pagado esta transacción pero está en ella
                if (pagado == 0.0 && existeEnListas1 >= 0) {
                    double saldoDecimales =
                            bigDecimal(bigDecimal(pagado) - bigDecimal(aPagarPorParticipante));
                    saldo = bigDecimal(saldoDecimales);
                    deudas.put(participanteId, saldo);

                    // SI pagado la transacción y está en ella
                } else if (pagado > 0.0 && existeEnListas1 >= 0) {
                    double saldoDecimales =
                            bigDecimal(bigDecimal(pagado) - bigDecimal(aPagarPorParticipante));
                    saldo = bigDecimal(saldoDecimales);
                    deudas.put(participanteId, saldo);

                    // No está en la transacción
                } else {
                    saldo = pagado;

                }
                deudas.put(participanteId, saldo);
            }
            gastoParticipantes.add(deudas);
        }
        return gastoParticipantes;
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


     /*
    public void fecha() {

        GregorianCalendar dd = new GregorianCalendar();
        //SI NO SETEO LA FECHA EL CONSTRUCTOR POR DEFECTO
        //LE PONE LA FECHA ACTUAL
        dd.set(GregorianCalendar.DATE, 15);
        dd.set(GregorianCalendar.MONTH, 11);
        dd.set(GregorianCalendar.YEAR, 1980);
        //EL MES SE CUENTA DE 0 A 11, DICIEMBRE ES EL 11 ETC.
        System.out.println(dd.getTime());
        System.out.println("día:" + dd.get(GregorianCalendar.DATE));
        System.out.println("mes:" + (dd.get(GregorianCalendar.MONTH) + 1));
        System.out.println("año:" + dd.get(GregorianCalendar.YEAR));


        //Fijate que el mes se cuenta de 0 a 11 y hay que sumar 1
    }

      */


}

