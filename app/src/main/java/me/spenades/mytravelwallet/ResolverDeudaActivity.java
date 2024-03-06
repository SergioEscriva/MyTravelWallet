package me.spenades.mytravelwallet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private TextView tvResuelto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolver_deuda);
        System.out.println("Empiezaaaaaaaaaaaaaaaaaaaa");
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
                    saldo = redondearDosDecimales(saldoDecimales);
                    deudas.put(participanteId, saldo);
                    System.out.println(participanteId + " primero " + saldo + " xCabeza " + aPagarPorParticipante);

                    // SI pagado la transacción y está en ella
                } else if (pagado > 0.0 && existeEnListas1 >= 0) {
                    double saldoDecimales = pagado - aPagarPorParticipante;
                    saldo = redondearDosDecimales(saldoDecimales);
                    deudas.put(participanteId, saldo);
                    System.out.println(participanteId + " segundo " + saldo + " xCabeza " + aPagarPorParticipante);

                    // No está en la transacción
                } else {
                    saldo = pagado;
                    System.out.println(participanteId + " tercero " + pagado + " No participa");
                }
                deudas.put(participanteId, saldo);
            }
            gastoParticipantes.add(deudas);
        }
        // System.out.println("1" + gastoParticipantes);
        tvResuelto.setText(gastoParticipantes.toString());
        return gastoParticipantes;
    }

    private ArrayList<Map> unificaGastoParticipanteWallet() {
        ArrayList<Map> gastoParticipantes = gastosParticipantesTransacciones();

        //System.out.println("2" + gastoParticipantes);
        Map<Long, Double> gastosParticianTotalesWallet = new HashMap<Long, Double>();
        ArrayList<Map> resultadoDeudaParticipantes = new ArrayList();

        // Iteramos en busca de las keys
        for (long l = 0; l < gastoParticipantes.get(0).size(); l++) {

            // Extrae las Keys de las transacciones

            gastoParticipantes.get(0).keySet().forEach((key) -> {
                long participanteId = Long.parseLong(key.toString());
                long iterar = participanteId;

                // Suma todas las keys values
                DoubleSummaryStatistics sumaValoresImporte =
                        (DoubleSummaryStatistics) gastoParticipantes
                                .stream()
                                .collect(
                                        Collectors.summarizingDouble(
                                                e -> Double.valueOf(((Map) e).get(iterar).toString())
                                        )
                                );

                double importeTotal = sumaValoresImporte.getSum();
                gastosParticianTotalesWallet.put(iterar, importeTotal);

            });

        }
        resultadoDeudaParticipantes.add(gastosParticianTotalesWallet);

        System.out.println("Final:: " + resultadoDeudaParticipantes);

        return resultadoDeudaParticipantes;
    }

    //TODo Bien
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
        System.out.println("UUUUUU " + datos);
        return datos;
    }

    // Todo Bien
    public double aPagarPorParticipante(Transaccion transaccion) {

        // Calculamos la deuda total
        int numeroParticipantes = listaDeParticipan.size();
        double importeTransaccion = Double.valueOf(transaccion.getImporte());
        double importePorParticipante = importeTransaccion / numeroParticipantes;
        return importePorParticipante;
    }

    public void resolucionDeudaWallet() {
        System.out.println("resolución");
        gastosParticipantesTransacciones();
        //Iniciamos variables
        ArrayList<Map> aPagar = new ArrayList<>();
        ArrayList<Map> aRecibir = new ArrayList<>();
        Map pagarParticipante = new HashMap<>();
        Map recibirParticipante = new HashMap<>();

        // Recuperamos deudas por Wallet
        ArrayList<Map> deudas = gastosParticipantesTransacciones();

        // Iteramos sobre cada paticipante y separamos lo que le deben y lo que debe.
        for (int i = 0; i < deudas.size(); i++) {

            int deudaIterador = i;
            // Extrae las Keys de las transacciones
            deudas.get(i).keySet().forEach((key) -> {
                double pagar = 0;
                double recibir = 0;
                long participanteId = Long.parseLong(key.toString());
                long iterarKey = participanteId;
                double participante =
                        Double.valueOf(deudas.get(deudaIterador).get(iterarKey).toString());

                // Separamos
                if (participante > 0) {
                    pagar = participante;
                } else if (participante < 0) {
                    recibir = participante;
                }
                pagarParticipante.put(participanteId, pagar);
                recibirParticipante.put(participanteId, recibir);
            });
            aPagar.add(pagarParticipante);
            aRecibir.add(recibirParticipante);
            System.out.println("Pagar " + aPagar);
            System.out.println("Recibir " + aRecibir);
        }


        ;
        /**/
        //List deudasMap = deudas.  .get(0);
        // for (int i = 0; i <= deudasMap.size(); i++) {
        //pagar.get(1);
        //if (pagar )

        // }

        //List pagar =


    }

    public double redondearDosDecimales(double numero) {

        double numero2decimales = Math.round(numero * 100.0) / 100.0;
        return numero2decimales;
    }


    public void refrescarListaDeTransacciones() {
        listaDeTransaccions = transaccionController.obtenerTransacciones(walletId);
        listaDeParticipantes = participanteController.obtenerParticipantes(walletId);
        //listaDeParticipan = participanController.obtenerParticipan(transaccionId);


    }
}
