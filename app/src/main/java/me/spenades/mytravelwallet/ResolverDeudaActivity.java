package me.spenades.mytravelwallet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.spenades.mytravelwallet.adapters.GastosTotalesAdapters;
import me.spenades.mytravelwallet.adapters.ResolucionesAdapters;
import me.spenades.mytravelwallet.controllers.MiembroController;
import me.spenades.mytravelwallet.controllers.ParticipanController;
import me.spenades.mytravelwallet.controllers.TransaccionController;
import me.spenades.mytravelwallet.models.Miembro;
import me.spenades.mytravelwallet.models.Transaccion;
import me.spenades.mytravelwallet.utilities.Operaciones;
import me.spenades.mytravelwallet.utilities.RecyclerTouchListener;

public class ResolverDeudaActivity extends AppCompatActivity {


    private List<Transaccion> listaDeTransacciones;
    private List<Miembro> listaDeMiembros;
    private List<Miembro> listaDeParticipan;
    private List<List> listaDeSoluciones;
    private ArrayList<String> mostrarResolucion;
    private Map<Long, Double> listaDeGastos;
    private TransaccionController transaccionController;
    private MiembroController miembroController;
    private ParticipanController participanController;
    private ResolucionesAdapters resolucionesAdapters;
    private GastosTotalesAdapters gastosTotalesAdapters;
    private RecyclerView recyclerViewResoluciones, recyclerViewGastosTotales;
    private FrameLayout flInfoDeudas;
    private TextView tvSinDeudas, tvTextoResolucion;
    private long walletId;
    private Map<Long, Double> pagadoPorMiembro;
    private RelativeLayout rlGastosTotales;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolver_deuda);

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        //long usuarioIdActivo = extras.getInt("usuarioIdActivo");
        //String usuarioActivo = extras.getString("usuarioActivo");
        walletId = Long.parseLong(extras.getString("walletId"));

        // Se muestra sólo la primera vez la Ayuda
        int info = extras.getInt("info");
        flInfoDeudas = findViewById(R.id.flInfoDeudas);
        if (info == 1) flInfoDeudas.setVisibility(View.VISIBLE);

        // Si no hay datos (cosa rara) salimos
        if (extras == null) {
            finish();
            return;
        }

        // Definir nuestro controlador
        //walletController = new WalletController(ResolverDeudaActivity.this);
        transaccionController = new TransaccionController(ResolverDeudaActivity.this);
        miembroController = new MiembroController(ResolverDeudaActivity.this);
        participanController = new ParticipanController(ResolverDeudaActivity.this);

        // Instanciamos las vistas
        //tvResolver = findViewById(R.id.tvResolver);
        recyclerViewResoluciones = findViewById(R.id.recyclerViewGastos);
        recyclerViewGastosTotales = findViewById(R.id.recyclerViewGastosTotales);
        tvSinDeudas = findViewById(R.id.tvSinDeudas);
        rlGastosTotales = findViewById(R.id.rlGastosTotales);
        tvTextoResolucion = findViewById(R.id.tvTextoResolucion);

        // Creamos listas vacías.
        listaDeTransacciones = new ArrayList<>();
        listaDeMiembros = new ArrayList<>();
        listaDeParticipan = new ArrayList<>();
        listaDeSoluciones = new ArrayList<>();
        pagadoPorMiembro = new HashMap<>();
        mostrarResolucion = new ArrayList<>();
        listaDeGastos = new HashMap<>();
        resolucionesAdapters = new ResolucionesAdapters(listaDeSoluciones);
        gastosTotalesAdapters = new GastosTotalesAdapters(mostrarResolucion, listaDeMiembros);

        //configuramos el recyclerView
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        recyclerViewResoluciones.setLayoutManager(mLayoutManager);
        recyclerViewResoluciones.setItemAnimator(new DefaultItemAnimator());
        recyclerViewResoluciones.setAdapter(resolucionesAdapters);

        //configuramos el recyclerView
        RecyclerView.LayoutManager mLayoutManager2 =
                new LinearLayoutManager(getApplicationContext());
        recyclerViewGastosTotales.setLayoutManager(mLayoutManager2);
        recyclerViewGastosTotales.setItemAnimator(new DefaultItemAnimator());
        recyclerViewGastosTotales.setAdapter(gastosTotalesAdapters);
        refrescarListas();


        // Listener Temporal Gastos Totales... TODO por implementar

        tvTextoResolucion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                rlGastosTotales.setVisibility(View.VISIBLE);
            }
        });


        // Listener Resolucion deudas, para Resolverlas.
        recyclerViewResoluciones.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewResoluciones,
                new RecyclerTouchListener.ClickListener() {

                    @Override // Un toque Para Saldar Deudas
                    public void onClick(View view, int position) {
                        final List resolucionParaSaldar = listaDeSoluciones.get(position);
                        AlertDialog dialog = new AlertDialog
                                .Builder(ResolverDeudaActivity.this)
                                .setPositiveButton("Sí, está SALDADA", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        eliminarDeuda(resolucionParaSaldar);

                                        //transaccionController.eliminarTransaccion(transaccionParaEliminar);
                                        Toast.makeText(ResolverDeudaActivity.this, "Deuda Saldada. "
                                                , Toast.LENGTH_SHORT).show();
                                        refrescarListas();
                                    }
                                })
                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setTitle("Confirmar")
                                .setMessage("¿Está saldada la deuda de " + resolucionParaSaldar.get(1) + " que tiene con "
                                        + resolucionParaSaldar.get(3) + " de " + resolucionParaSaldar.get(4) + "?")
                                .create();
                        dialog.show();
                    }


                    @Override // Un toque Largo.
                    public void onLongClick(View view, int position) {


                    }


                }) {

            @Override
            public void onClick(View view, int position) {

            }
        });
    }


    public void refrescarListas() {
        listaDeMiembros = miembroController.obtenerMiembros(walletId);
        listaDeTransacciones = transaccionController.obtenerTransacciones(walletId);
        listaDeSoluciones = resolucionDeudaWallet(); //llama a Saldar
        resolucionesAdapters.setListaDeResoluciones(listaDeSoluciones);
        resolucionesAdapters.notifyDataSetChanged();
        if (listaDeSoluciones.size() <= 0) {
            tvSinDeudas.setVisibility(View.VISIBLE);
            recyclerViewResoluciones.setVisibility(View.INVISIBLE);
        }
        listaDeGastos = unificaGastoMiembroWallet();//llama Gasto Total
        mostrarResolucion = operacionesResolucionDeudas(); // TODO Gastos Totales
        gastosTotalesAdapters.setListaDeResoluciones(mostrarResolucion, listaDeMiembros);
        gastosTotalesAdapters.notifyDataSetChanged();


    }


    // Resolución de las deudas TODO sacar a una clase, aún no se
    public ArrayList resolucionDeudaWallet() {

        HashMap<Long, String> usuarioIdNombbre = new HashMap<>();
        for (Miembro miembro : listaDeMiembros) {
            String nombre = miembro.getNombre();
            long userId = miembro.getUserId();
            usuarioIdNombbre.put(userId, nombre);
        }

        //Iniciamos variables
        Map<Long, Double> pagarMiembro = new HashMap<>();
        Map<Long, Double> recibirMiembro = new HashMap<>();
        // Recuperamos deudas por Wallet
        Map deudas = unificaGastoMiembroWallet();

        // Extrae las Keys de las transacciones para los cálculos y rellenamos variables.
        deudas.keySet().forEach((key) -> {
            double pagar = 0L;
            double recibir = 0L;
            long miembroId = Long.parseLong(key.toString());
            long iterarKey = miembroId;
            double cantidadParticipa = Double.valueOf(String.valueOf(deudas.get(iterarKey)));

            // Separamos pagar y recibir en dos listas.
            if (cantidadParticipa >= 0L) {
                double recibirDecimales = dosDecimales(cantidadParticipa);
                recibir = recibirDecimales;
            } else if (cantidadParticipa != 0L) {
                double pagarDecimales = dosDecimales(cantidadParticipa);
                pagar = pagarDecimales;
            }
            pagarMiembro.put(miembroId, pagar);
            recibirMiembro.put(miembroId, recibir);
        });

        // Ordenamos pagos de mayor a menor
        Map<Long, Double> pagarOrdenado = ordenarTransacciones(pagarMiembro);
        Map<Long, Double> recibirOrdenado = ordenarTransacciones(recibirMiembro);

        // Creamos un diccionario para almacenar las soluciones
        ArrayList<ArrayList> soluciones = new ArrayList<>();

        // Iteramos sobre la lista de los que tienen que pagar(pagador)
        for (long pagarId : pagarOrdenado.keySet()) {
            double cantidadAPagar = dosDecimales(pagarOrdenado.get(pagarId));
            long pagador = pagarId;

            // Iteramos sobre la lista de los que tienen que recibir(cobrador)
            while (Math.abs(cantidadAPagar) > 0) {
                for (long recibirId : recibirOrdenado.keySet()) {
                    double cantidadARecibir = dosDecimales(recibirOrdenado.get(recibirId));
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
                        double recibirCalculado =
                                dosDecimales(cantidadARecibir + cantidadAPagar);
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
        ArrayList<ArrayList> solucionesLimpias = eliminarSolucionesCero(soluciones);
        // Rellenar Gastos Totales
        // gastosTotalesResumidos();
        return solucionesLimpias;
    }


    private Map<Long, Double> unificaGastoMiembroWallet() {
        ArrayList<Map> gastoMiembros = gastosMiembrosTransacciones();
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

                });

                return gastosParticianTotalesWallet;
            }
        } catch (Exception e) {
            System.out.println("Oops! ResolverDeuda");
        }
        return gastosParticianTotalesWallet;
    }


    public ArrayList<Map> gastosMiembrosTransacciones() {
        ArrayList<Map> gastoMiembros = new ArrayList<>();
        // iteramos transacciones sacamos a lo que sale cada miembro
        for (Transaccion unaTransaccion : listaDeTransacciones) {

            Map<Long, Double> pagadoPorMiembro = pagadoPorCadaMiembro(unaTransaccion);
            double deudaTotal = Double.valueOf(unaTransaccion.getImporte());
            double aPagarPorMiembro = aPagarPorMiembro(unaTransaccion);

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
                    double saldoDecimales =
                            dosDecimales(dosDecimales(pagado) - dosDecimales(aPagarPorMiembro));
                    saldo = dosDecimales(saldoDecimales);
                    deudas.put(miembroId, saldo);

                    // SI pagado la transacción y está en ella
                } else if (pagado > 0.0 && existeEnListas1 >= 0) {
                    double saldoDecimales =
                            dosDecimales(dosDecimales(pagado) - dosDecimales(aPagarPorMiembro));
                    saldo = dosDecimales(saldoDecimales);
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


    // Suma todos los pagos por miembro
    public Map<Long, Double> pagadoPorCadaMiembro(Transaccion transaccion) {
        long transaccionId = transaccion.getId();
        listaDeParticipan = participanController.obtenerParticipan(transaccionId);

        // Creamos un diccionario con lo que ha pagado cada miembro de esta transacción
        Map<Long, Double> datos = new HashMap<Long, Double>();
        long nombreId = transaccion.getPagadorId();
        double importe = Double.valueOf(transaccion.getImporte().toString());
        for (Miembro unNombre : listaDeMiembros) {
            long nombreIdIndividual = unNombre.getUserId();
            double importeCero = 0.0;
            datos.put(nombreIdIndividual, importeCero);
        }
        datos.put(nombreId, importe);

        return datos;
    }


    public double aPagarPorMiembro(Transaccion transaccion) {

        // Calculamos la deuda total
        int numeroMiembros = listaDeParticipan.size();
        double importeTransaccion = Double.valueOf(transaccion.getImporte());
        double importePorMiembro =
                dosDecimales(dosDecimales(importeTransaccion) / dosDecimales(numeroMiembros + 0.0));

        return importePorMiembro;
    }


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


    public Long eliminarDeuda(List resolucion) {
        String deudaString = resolucion.get(4).toString();
        double deudaDouble = Double.parseDouble(deudaString);
        double deudaDoubleAbs = Math.abs(deudaDouble);
        String deuda = String.valueOf(deudaDoubleAbs);
        long pagador = Long.parseLong(resolucion.get(0).toString());
        String cobrador = resolucion.get(2).toString();
        Operaciones operaciones = new Operaciones();
        String nuevaFecha = operaciones.fechaDeHoy();

        Transaccion transaccionConNuevosCambios = new Transaccion("Saldar Deuda",
                deuda, pagador, cobrador, 2,
                nuevaFecha, walletId);
        long transaccionId = transaccionController.nuevaTransaccion(transaccionConNuevosCambios);
        return transaccionId;
    }


    public ArrayList<String> operacionesResolucionDeudas() {
        String importeTotal = new String();
        ArrayList<ArrayList> gastosTotalesDivididos = new ArrayList<>();
        Map<Long, Double> importePagadoParticipante = transacionesGastosTotales();
        ArrayList<String> miembrosGastos = new ArrayList<>();

        // Iteramos sobre los gastos para extraer que tendría que haber pagado cada miembro.
        for (Long miembroIdGasto : listaDeGastos.keySet()) {
            double importeDeberiaPagarAlWallet = listaDeGastos.get(miembroIdGasto);

            // Limpiamos decimales del importe
            double importeMovimientosWallet = dosDecimales(importeDeberiaPagarAlWallet);

            // Obtenemos de la listaDeGastos los ids, y los iteramos con la listaDeMiembros, para obtener el nombre.
            for (Miembro solucionFinal : listaDeMiembros) {
                long miembroId = solucionFinal.getUserId();
                String miembro = solucionFinal.getNombre();

                // Añadimos los gastos o cobros por participantes.
                if (miembroIdGasto == miembroId) {
                    //String miembro = new String();
                    String importeString = new String();

                    // Rescatamos importe pagado por cada miembro
                    double importeHaPagado = importePagadoParticipante.get(solucionFinal.getUserId());
                    String importeHaPagadoString = String.valueOf(importeHaPagado);

                    // Rescatamos importes a pagar o recibir.
                    String importeFinalDebe = new String();
                    String importeFinalPagado = new String();
                    double importeDoubleLimpio = 0;
                    double importeFinalPagadoLimpio = 0;
                    double gastoRealizado = 0;

                    // Según sea a pagar o recibir se separan para poder mostrarlos.
                    if (importeMovimientosWallet <= 0D) {
                        importeDoubleLimpio = Math.abs(importeMovimientosWallet);
                        String limpiezaNumero = String.valueOf(importeDoubleLimpio);
                        if (importeDoubleLimpio <= 0D) {
                            importeFinalDebe = "\nNo tiene deudas.";
                        } else {
                            importeFinalDebe = "\nDebe al Wallet " + limpiezaNumero + "€";
                        }
                        // Calculamos lo que el gasto total de cada miembro en el Wallet
                        gastoRealizado = importeDoubleLimpio + importeHaPagado;
                    } else {
                        importeFinalPagadoLimpio = importeMovimientosWallet;
                        importeString = String.valueOf(importeFinalPagadoLimpio);
                        importeFinalPagado = "\nEl Wallet le debe " + importeString + "€";

                        // Calculamos lo que el gasto total de cada miembro en el Wallet
                        gastoRealizado = dosDecimales(importeHaPagado - importeMovimientosWallet);
                    }

                    String gastoRealizadoEnWallet = String.valueOf(gastoRealizado);


                    String miembroGastoString =
                            miembro + " ha hecho un Gasto de " + gastoRealizadoEnWallet + "€" + "\nHa pagado " + importeHaPagadoString + "€" + importeFinalDebe + importeFinalPagado;
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

        // Creamos una diccionario con todos los participantes del Wallet, y los ponemos a 0 gastado
        double pagadorImporte = 0.0D;
        for (Miembro solucionFinal : listaDeMiembros) {
            long miembroId = solucionFinal.getUserId();
            gastoTotalpagador.put(miembroId, pagadorImporte);
        }
        // Ahora añadimos lo que realmente ha pagado cada uno.
        for (Transaccion transaccion : listaDeTransacciones) {
            long pagadorId = transaccion.getPagadorId();

            pagadorImporte = Double.parseDouble(transaccion.getImporte());
            gastoTotalpagador.replace(pagadorId, pagadorImporte);

        }
        return gastoTotalpagador;
    }


    public Double dosDecimales(Double importe) {
        Operaciones operaciones = new Operaciones();
        String numeroDosDecimales = operaciones.dosDecimales(importe);
        double numeroLimpio = Double.parseDouble(numeroDosDecimales);
        return numeroLimpio;
    }

}