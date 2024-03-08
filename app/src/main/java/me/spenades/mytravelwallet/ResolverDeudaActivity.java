package me.spenades.mytravelwallet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

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

    private long walletId;
    private TextView tvResuelto, tvResuelto2;


    static <Long, Double extends Comparable<? super Double>> SortedSet<Map.Entry<Long, Double>> entriesSortedByValuesMasMenos(Map<Long, Double> map) {
        Map<Long, Double> partiPagarYa = new HashMap<>();

        SortedSet<Map.Entry<Long, Double>> sortedEntries =
                new TreeSet<Map.Entry<Long, Double>>(new Comparator<Map.Entry<Long, Double>>() {

                    @Override
                    public int compare(Map.Entry<Long, Double> e2, Map.Entry<Long, Double> e1) {
                        int res = e1.getValue().compareTo(e2.getValue());
                        return res != 0 ? res : 1;
                    }
                });


        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }


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

        // Ahora declaramos las vistas

        tvResuelto = findViewById(R.id.tvResuelto);
        tvResuelto2 = findViewById(R.id.tvResuelto2);

        // Creamos listas vacías.
        listaDeTransaccions = new ArrayList<>();
        listaDeParticipantes = new ArrayList<>();
        listaDeParticipan = new ArrayList<>();
        refrescarListaDeTransacciones();
        resolucionDeudaWallet();

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
                double pagado = pagadoPorParticipante.get(participanteId);//

                // segun se haya pagado o no una cantidad se resta
                // Comprueba si existe en la lista de participantes y asigna importes, los demás
                // a cero
                String listado = unaTransaccion.getParticipantes();
                String participanteIdInt = String.valueOf(participanteId);
                int existeEnListas1 = listado.indexOf(participanteIdInt);

                double saldo = pagado;
                // NO ha pagado esta transacción pero está en ella
                if (pagado == 0.0 && existeEnListas1 >= 0) {
                    double saldoDecimales = pagado - aPagarPorParticipante;
                    saldo = numeroDosDecimales(saldoDecimales);
                    deudas.put(participanteId, saldo);

                    // SI pagado la transacción y está en ella
                } else if (pagado > 0.0 && existeEnListas1 >= 0) {
                    double saldoDecimales = pagado - aPagarPorParticipante;
                    saldo = numeroDosDecimales(saldoDecimales);
                    deudas.put(participanteId, saldo);

                    // No está en la transacción
                } else {
                    saldo = pagado;

                }
                deudas.put(participanteId, saldo);
            }
            gastoParticipantes.add(deudas);
        }

        tvResuelto.setText(gastoParticipantes.toString());

        return gastoParticipantes;
    }


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
        double importePorParticipante = importeTransaccion / numeroParticipantes;
        return importePorParticipante;
    }


    public double numeroDosDecimales(double numero) {
        // dos decimales y número absoluto.
        double numero2decimales = Math.round(numero * 100.0) / 100.0;
        //double numeroLimpio = Math.abs(numero2decimales);
        return numero2decimales;
    }


    public void refrescarListaDeTransacciones() {
        listaDeTransaccions = transaccionController.obtenerTransacciones(walletId);
        listaDeParticipantes = participanteController.obtenerParticipantes(walletId);
        //listaDeParticipan = participanController.obtenerParticipan(transaccionId);


    }


    public void resolucionDeudaWallet() {


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
                double pagarDecimales = cantidadParticipa;
                pagar = pagarDecimales;
            } else if (cantidadParticipa != 0L) {
                double recibirDecimales = cantidadParticipa;
                recibir = recibirDecimales;
            }
            pagarParticipante.put(participanteId, pagar);
            recibirParticipante.put(participanteId, recibir);
        });

        // Ordenamos pagos de mayor a menor
        // Java 8 Stream (https://www.techiedelight.com/es/sort-map-by-values-java/)
        //https://www.techiedelight.com/es/sort-map-java-reverse-ordering-keys/
        Map<Long, Double> sortedMapRecibir = new LinkedHashMap<>();

        // Ordenamos recibir de menos a mayor.
        // Java 8 Stream (https://www.techiedelight.com/es/sort-map-by-values-java/)
        Map<Long, Double> sortedMapPagar = new LinkedHashMap<>();

        recibirParticipante.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(entry -> sortedMapPagar.put(entry.getKey(), entry.getValue()));

        pagarParticipante.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(entry -> sortedMapRecibir.put(entry.getKey(), entry.getValue()))
        ;


        System.out.println("Recibir " + sortedMapRecibir);
        System.out.println("Pagar " + sortedMapPagar);

        // Creamos un diccionario para almacenar las soluciones
        ArrayList<ArrayList> soluciones = new ArrayList<>();

        // Iteramos sobre la lista de los que tienen que pagar
        for (long pagarId : sortedMapPagar.keySet()) {
            double cantidadAPagar = sortedMapPagar.get(pagarId);
            long pagadorId = pagarId;

            // Iteramos sobre la lista de los que tienen que recibir
            while (Math.abs(cantidadAPagar) > 0) {
                for (long recibirId : sortedMapRecibir.keySet()) {
                    double cantidadARecibir = sortedMapRecibir.get(recibirId);
                    System.out.println("num " + cantidadAPagar + " abs " + Math.abs
                            (cantidadAPagar) + " num " + cantidadAPagar);
                    System.out.println(cantidadARecibir > Math.abs(cantidadAPagar));
                    long cobrador = recibirId;
                    // Si el receptor debe recibir más de lo que el pagador tiene que pagar
                    if (cantidadARecibir > Math.abs(cantidadAPagar)) {
                        double cantidadAPagarIni = cantidadAPagar;
                        // El pagador paga la cantidad que debe al receptor
                        ArrayList<String> pagadorAcobrador = new ArrayList<>();
                        pagadorAcobrador.add(String.valueOf(pagadorId));
                        pagadorAcobrador.add(String.valueOf(cobrador));
                        pagadorAcobrador.add(String.valueOf(cantidadAPagar));
                        soluciones.add(pagadorAcobrador);
                        //Actualizamos la cantidad que el receptor tiene que recibir
                        //sortedMapRecibir.put(recibirId, (cantidadARecibir - cantidadAPagar));
                        double recibirCalculado = cantidadARecibir + cantidadAPagar;
                        sortedMapRecibir.replace(recibirId, recibirCalculado);
                        System.out.println("Actualizacion " + sortedMapRecibir);
                        System.out.println("Final Beta " + soluciones);

                    }
                }
            }
        }


        System.out.println("Ordenado Pagar" + sortedMapPagar);
        System.out.println("Ordenado Recibir" + sortedMapRecibir);
        tvResuelto2.setText(sortedMapPagar.toString() + " " + sortedMapRecibir.toString());
    }

}