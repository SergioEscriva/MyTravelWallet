package me.spenades.mytravelwallet.utilities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.spenades.mytravelwallet.R;
import me.spenades.mytravelwallet.controllers.ParticipanController;
import me.spenades.mytravelwallet.controllers.ParticipanteController;
import me.spenades.mytravelwallet.controllers.TransaccionController;
import me.spenades.mytravelwallet.controllers.WalletController;
import me.spenades.mytravelwallet.models.Participante;
import me.spenades.mytravelwallet.models.Transaccion;

public class ResolverDeudaActivity extends AppCompatActivity {


    private List<Transaccion> listaDeTransaccions;
    private List<Participante> listaDeParticipantes;
    private List<Participante> listaDeParticipan;
    private TransaccionController transaccionController;
    private ParticipanteController participanteController;
    private ParticipanController participanController;
    private WalletController walletController;
    private Participante participante;


    private long walletId;
    private TextView tvResuelto, tvResuelto2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolver_deuda);

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        long usuarioIdActivo = extras.getInt("usuarioIdActivo");
        String usuarioActivo = extras.getString("usuarioActivo");
        this.walletId = extras.getInt("walletId") + 1;
        // Si no hay datos (cosa rara) salimos
        if (extras == null) {
            finish();
            return;
        }

        // Definir nuestro controlador
        //walletController = new WalletController(ResolverDeudaActivity.this);
        transaccionController = new TransaccionController(ResolverDeudaActivity.this);
        participanteController = new ParticipanteController(ResolverDeudaActivity.this);
        participanController = new ParticipanController(ResolverDeudaActivity.this);

        // Creamos listas vacías.
        listaDeTransaccions = new ArrayList<>();
        listaDeParticipantes = new ArrayList<>();
        listaDeParticipan = new ArrayList<>();
        refrescarListaDeTransacciones();

        // Ahora declaramos las vistas
        tvResuelto = findViewById(R.id.tvResuelto);
        tvResuelto2 = findViewById(R.id.tvResuelto2);
        solucionFinal();
    }


    public ArrayList<Map> gastosParticipantesTransacciones() {
        ArrayList<Map> gastoParticipantes = new ArrayList<>();

        // iteramos transacciones sacamos a lo que sale cada participante
        for (Transaccion unaTransaccion : listaDeTransaccions) {

            Map<Long, Double> pagadoPorParticipante = pagadoPorCadaParticipante(unaTransaccion);
            double deudaTotal = Double.valueOf(unaTransaccion.getImporte());
            double aPagarPorParticipante = aPagarPorParticipante(unaTransaccion);

            // Extraemos lo que ha pagado cada participante
            Map<Long, Double> deudas = new HashMap<Long, Double>();
            for (int n = 0; n < listaDeParticipantes.size(); n++) {
                long participanteId = listaDeParticipantes.get(n).getUserId();
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


    // Suma todos los pagos por participante
    private Map<Long, Double> unificaGastoParticipanteWallet() {
        ArrayList<Map> gastoParticipantes = gastosParticipantesTransacciones();


        Map<Long, Double> gastosParticianTotalesWallet = new HashMap<Long, Double>();

        // Iteramos en busca de las keys
        for (long l = 0; l < gastoParticipantes.get(0).size(); l++) {

            // Extrae las Keys de las transacciones

            gastoParticipantes.get(0).keySet().forEach((key) -> {
                long participanteId = Long.parseLong(key.toString());
                long iterar = participanteId;

                // Suma todas las keys values del mismo Participante
                DoubleSummaryStatistics sumaValoresImporte =
                        (DoubleSummaryStatistics) gastoParticipantes
                                .stream()
                                .collect(Collectors.summarizingDouble(
                                                e -> Double.valueOf(((Map) e).get(iterar).toString())
                                        )

                                );

                double importeTotal = sumaValoresImporte.getSum();
                gastosParticianTotalesWallet.put(iterar, importeTotal);

            });
        }
        return gastosParticianTotalesWallet;
    }


    public Map<Long, Double> pagadoPorCadaParticipante(Transaccion transaccion) {
        long transaccionId = transaccion.getId();
        listaDeParticipan = participanController.obtenerParticipan(transaccionId);

        // Creamos un diccionario con lo que ha pagado cada participante de esta transacción
        Map<Long, Double> datos = new HashMap<Long, Double>();
        long nombreId = transaccion.getPagadorId();
        double importe = Double.valueOf(transaccion.getImporte().toString());
        for (Participante unNombre : listaDeParticipantes) {
            long nombreIdIndividual = unNombre.getUserId();
            double importeCero = 0.0;
            datos.put(nombreIdIndividual, importeCero);
        }
        datos.put(nombreId, importe);
        return datos;
    }


    public double aPagarPorParticipante(Transaccion transaccion) {

        // Calculamos la deuda total
        int numeroParticipantes = listaDeParticipan.size();
        double importeTransaccion = Double.valueOf(transaccion.getImporte());
        //double importePorParticipante = bigDecimal(importeTransaccion / numeroParticipantes);
        double importePorParticipante =
                bigDecimal(bigDecimal(importeTransaccion) / bigDecimal(numeroParticipantes + 0.0));
        return importePorParticipante;
    }


    public ArrayList resolucionDeudaWallet() {
        HashMap<Long, String> usuarioIdNombbre = new HashMap<>();
        for (Participante participante : listaDeParticipantes) {
            String nombre = participante.getNombre();
            long userId = participante.getUserId();
            usuarioIdNombbre.put(userId, nombre);
        }

        //Iniciamos variables
        Map<Long, Double> pagarParticipante = new HashMap<>();
        Map<Long, Double> recibirParticipante = new HashMap<>();
        // Recuperamos deudas por Wallet
        Map deudas = unificaGastoParticipanteWallet();

        // Extrae las Keys de las transacciones para los cálculos y rellenamos variables.
        deudas.keySet().forEach((key) -> {
            double pagar = 0L;
            double recibir = 0L;
            long participanteId = Long.parseLong(key.toString());
            long iterarKey = participanteId;
            double cantidadParticipa = Double.valueOf(String.valueOf(deudas.get(iterarKey)));

            // Separamos pagar y recibir en dos listas.
            if (cantidadParticipa >= 0L) {
                double recibirDecimales = bigDecimal(cantidadParticipa);
                recibir = recibirDecimales;
            } else if (cantidadParticipa != 0L) {
                double pagarDecimales = bigDecimal(cantidadParticipa);
                pagar = pagarDecimales;
            }
            pagarParticipante.put(participanteId, pagar);
            recibirParticipante.put(participanteId, recibir);
        });

        // Ordenamos pagos de mayor a menor
        Map<Long, Double> pagarOrdenado = ordenarTransacciones(pagarParticipante);
        Map<Long, Double> recibirOrdenado = ordenarTransacciones(recibirParticipante);

        // Creamos un diccionario para almacenar las soluciones
        ArrayList<ArrayList> soluciones = new ArrayList<>();
        // Iteramos sobre la lista de los que tienen que pagar
        for (long pagarId : pagarOrdenado.keySet()) {
            double cantidadAPagar = bigDecimal(pagarOrdenado.get(pagarId));
            long pagador = pagarId;


            // Iteramos sobre la lista de los que tienen que recibir
            while (Math.abs(cantidadAPagar) > 0) {
                for (long recibirId : recibirOrdenado.keySet()) {
                    double cantidadARecibir = bigDecimal(recibirOrdenado.get(recibirId));
                    long cobrador = recibirId;

                    // Si el receptor debe recibir más de lo que el pagador tiene que pagar
                    if (cantidadARecibir > Math.abs(cantidadAPagar)) {
                        double cantidadAPagarIni = cantidadAPagar;
                        // El pagador paga la cantidad que debe al receptor
                        ArrayList<String> resoluciones = new ArrayList<>();
                        resoluciones.add(String.valueOf(pagador));
                        resoluciones.add(String.valueOf(usuarioIdNombbre.get(pagador)));
                        resoluciones.add(String.valueOf(cobrador));
                        resoluciones.add(String.valueOf(usuarioIdNombbre.get(cobrador)));
                        resoluciones.add(String.valueOf(cantidadAPagar));
                        soluciones.add(resoluciones);
                        //Actualizamos la cantidad que el receptor tiene que recibir
                        double recibirCalculado =
                                bigDecimal(cantidadARecibir + cantidadAPagar);
                        recibirOrdenado.replace(recibirId, recibirCalculado);

                        // El pagador ya no tiene que pagar nada
                        cantidadAPagar = 0;
                        pagarOrdenado.replace(pagador, cantidadAPagar);

                        break;
                    }
                    // Si el receptor debe recibir menos o lo mismo de lo que el pagador
                    // tiene que pagar
                    if (Math.abs(cantidadAPagar) == 0) {
                        break;
                    }
                    if (Math.abs(cantidadAPagar) > 0) {
                        double cantidadAPagarIni = cantidadAPagar;
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

                        // El receptor ya no tiene que recibir nada
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

        return soluciones;
    }


    //Buscamo el que menos ha pagado
    public void solucionFinal() {
        ArrayList<ArrayList> soluciones = resolucionDeudaWallet();
        String solu = "";
        for (ArrayList solucionFinal : soluciones) {
            double importe = bigDecimal(Math.abs(Double.valueOf(solucionFinal.get(4).toString())));
            if (importe != 0) {

                System.out.println(solucionFinal.get(0) + " le debe " + importe + " a " + solucionFinal.get(1));

                solu += String.valueOf("\n" + solucionFinal.get(1)) + " debe " + importe + "€" +
                        " a " + solucionFinal.get(3) + "\n";
            }
        }
        tvResuelto2.setText(solu);
    }


    public void refrescarListaDeTransacciones() {


        listaDeParticipantes = participanteController.obtenerParticipantes(walletId);
        listaDeTransaccions = transaccionController.obtenerTransacciones(walletId);

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


    // Sumamos con precisión gracias a BigDecimal y redondeamos a 2 decimales.
    //https://oracle-max.com/bigdecimal/
    public double bigDecimal(double numero) {
        BigDecimal valor = new BigDecimal(numero);
        BigDecimal resultado = valor.setScale(2, RoundingMode.HALF_DOWN);
        double resultadoSuma = resultado.doubleValue();
        return resultadoSuma;
    }

}