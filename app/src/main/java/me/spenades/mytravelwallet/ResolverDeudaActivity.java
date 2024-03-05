package me.spenades.mytravelwallet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.spenades.mytravelwallet.controllers.ParticipanteController;
import me.spenades.mytravelwallet.controllers.TransaccionController;
import me.spenades.mytravelwallet.controllers.WalletController;
import me.spenades.mytravelwallet.models.Participante;
import me.spenades.mytravelwallet.models.Transaccion;

public class ResolverDeudaActivity extends AppCompatActivity {

    private List<Transaccion> listaDeTransaccions;
    private List<Participante> listaDeParticipantes;

    private TransaccionController transaccionController;
    private ParticipanteController participanteController;

    private WalletController walletController;
    private long walletId;

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

        listaDeTransaccions = new ArrayList<>();
        listaDeParticipantes = new ArrayList<>();
        refrescarListaDeTransacciones();
        transaccionesDivision();

    }

    //// TODO
    public void transaccionesDivision() {

        Map<Long, Double> gastoParticipante = new HashMap<Long, Double>();
        ArrayList<Map> gastoParticipantes = new ArrayList<>();
        List resultadoDeudaParticipantes = new ArrayList();
        System.out.println(listaDeTransaccions);
        System.out.println(listaDeParticipantes);
        // iteramos transacciones
        for (Transaccion iterarTransaccion : listaDeTransaccions) {
            Map<Long, Double> unaTransaccion = pagadoPorCadaParticipante(iterarTransaccion);
            double deudaTotal = Double.valueOf(iterarTransaccion.getImporte());
            double aPagarPorParticipante = deudasPorParticipante(iterarTransaccion);
            Map<Long, Double> deudas = new HashMap<Long, Double>();

            // sacamos lo que debe cada uno de la transacción
            for (Participante participanteTransaccion : listaDeParticipantes) {
                long participanteId = participanteTransaccion.getUserId();
                double pagado = unaTransaccion.get(participanteId);

                // segun se haya pagado o no una cantidad se resta
                deudas.put(participanteId, deudaTotal);
                if (pagado <= 0) {

                    double saldo = pagado - aPagarPorParticipante;
                    deudas.put(participanteId, saldo);
                }
            }
            gastoParticipantes.add(deudas);

            // Sumamos los gastos totales de cada participante.
            for (int i = 1; i <= listaDeTransaccions.size(); i++) {
                System.out.println("lksks" + gastoParticipantes.get(0));
                long participante = (long) (i);
                DoubleSummaryStatistics statistics = (DoubleSummaryStatistics) gastoParticipantes
                        .stream()
                        .collect(
                                Collectors.summarizingDouble(
                                        e -> Double.valueOf(((Map) e).get(participante).toString())
                                )
                        );
                double importeTotal = statistics.getSum();
                gastoParticipante.put(participante, importeTotal);


                 /*
                 sumaDeudas = (participante, statistics);
                long nombreIdIndividual = unNombre.getUserId();
                double importeCero = 0.0;
                datos.put(nombreIdIndividual, importeCero);

                datos.put(nombreId, importe);
                //gastoParticipantes.add(sumaDeudas);

                  */

            }

        }
        resultadoDeudaParticipantes.add(gastoParticipante);

        System.out.println(" Array " + resultadoDeudaParticipantes);


        //DoubleSummaryStatistics sumcc = (DoubleSummaryStatistics) list.stream().collect(Collectors.summingDouble(e -> Integer.valueOf(((Map) e).get("sum").toString())));


    }

    // TODO FALTARÁ ITERAR LOS PARTICIPANTES QUE PARTICIPAN
    public Map<Long, Double> pagadoPorCadaParticipante(Transaccion transaccion) {

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

    public double deudasPorParticipante(Transaccion transaccion) {
        // Calculamos la deuda total
        int numeroParticipantes = listaDeParticipantes.size();
        double importeTransaccion = Double.valueOf(transaccion.getImporte());
        double importePorParticipante = importeTransaccion / numeroParticipantes;
        return importePorParticipante;
    }

    public void refrescarListaDeTransacciones() {
        listaDeTransaccions = transaccionController.obtenerTransacciones(walletId);
        listaDeParticipantes = participanteController.obtenerParticipantes(walletId);
        System.out.println(listaDeTransaccions);

    }
}
