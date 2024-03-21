package me.spenades.mytravelwallet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.spenades.mytravelwallet.adapters.GastosTotalesAdapters;
import me.spenades.mytravelwallet.adapters.ResolucionesAdapters;
import me.spenades.mytravelwallet.controllers.MiembroWalletController;
import me.spenades.mytravelwallet.controllers.ParticipaTransaccionController;
import me.spenades.mytravelwallet.controllers.TransaccionController;
import me.spenades.mytravelwallet.models.Miembro;
import me.spenades.mytravelwallet.models.Transaccion;
import me.spenades.mytravelwallet.utilities.DeudaUtility;
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
    private MiembroWalletController miembroWalletController;
    private ParticipaTransaccionController participaTransaccionController;
    private ResolucionesAdapters resolucionesAdapters;
    private GastosTotalesAdapters gastosTotalesAdapters;
    private DeudaUtility deudaUtility;
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
        miembroWalletController = new MiembroWalletController(ResolverDeudaActivity.this);
        participaTransaccionController = new ParticipaTransaccionController(ResolverDeudaActivity.this);

        deudaUtility = new DeudaUtility();

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
                                        listaDeSoluciones.remove(position);
                                        resolucionesAdapters.setListaDeResoluciones(listaDeSoluciones);
                                        resolucionesAdapters.notifyDataSetChanged();
                                        eliminarDeuda(resolucionParaSaldar);

                                        //transaccionController.eliminarTransaccion(transaccionParaEliminar);
                                        Toast.makeText(ResolverDeudaActivity.this, "Deuda Saldada. "
                                                , Toast.LENGTH_SHORT).show();
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
        listaDeMiembros = miembroWalletController.obtenerMiembros(walletId);
        listaDeTransacciones = transaccionController.obtenerTransacciones(walletId);
        listaDeSoluciones = deudaUtility.resolucionDeudaWallet(); //llama a Saldar
        resolucionesAdapters.setListaDeResoluciones(listaDeSoluciones);
        resolucionesAdapters.notifyDataSetChanged();
        if (listaDeSoluciones.size() <= 0) {
            tvSinDeudas.setVisibility(View.VISIBLE);
            recyclerViewResoluciones.setVisibility(View.INVISIBLE);
        }
        mostrarResolucion = deudaUtility.operacionesResolucionDeudas(); //Gastos Totales
        gastosTotalesAdapters.setListaDeResoluciones(mostrarResolucion, listaDeMiembros);
        gastosTotalesAdapters.notifyDataSetChanged();
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

}