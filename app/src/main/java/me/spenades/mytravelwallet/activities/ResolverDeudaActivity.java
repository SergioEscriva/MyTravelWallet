package me.spenades.mytravelwallet.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.spenades.mytravelwallet.R;
import me.spenades.mytravelwallet.adapters.GastosTotalesAdapters;
import me.spenades.mytravelwallet.adapters.ResolucionesAdapters;
import me.spenades.mytravelwallet.ayuda.SaldarDeudasAyuda;
import me.spenades.mytravelwallet.controllers.AyudaAppController;
import me.spenades.mytravelwallet.controllers.MiembroWalletController;
import me.spenades.mytravelwallet.controllers.TransaccionController;
import me.spenades.mytravelwallet.models.Ayuda;
import me.spenades.mytravelwallet.models.Miembro;
import me.spenades.mytravelwallet.models.Transaccion;
import me.spenades.mytravelwallet.utilities.DeudaUtility;
import me.spenades.mytravelwallet.utilities.Operaciones;
import me.spenades.mytravelwallet.utilities.RecyclerTouchListener;

public class ResolverDeudaActivity extends AppCompatActivity {
    private List<Miembro> listaDeMiembros;
    private List<List> listaDeSoluciones;
    private ArrayList<Spanned> mostrarResolucion;
    private TransaccionController transaccionController;
    private MiembroWalletController miembroWalletController;
    private ResolucionesAdapters resolucionesAdapters;
    private GastosTotalesAdapters gastosTotalesAdapters;
    private DeudaUtility deudaUtility;
    private RecyclerView recyclerViewResoluciones;
    private TextView tvSinDeudas;
    private long walletId;
    private RelativeLayout rlGastosTotales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolver_deuda);

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        //long usuarioIdActivo = extras.getInt("usuarioIdActivo");
        //String usuarioActivo = extras.getString("usuarioActivo");
        walletId = extras.getLong("walletId");

        // Se muestra sólo la primera vez la Ayuda
        int info = extras.getInt("info");

        // Definir nuestro controlador
        //walletController = new WalletController(ResolverDeudaActivity.this);
        transaccionController = new TransaccionController(ResolverDeudaActivity.this);
        miembroWalletController = new MiembroWalletController(ResolverDeudaActivity.this);
        deudaUtility = new DeudaUtility();

        // Instanciamos las vistas
        //tvResolver = findViewById(R.id.tvResolver);
        recyclerViewResoluciones = findViewById(R.id.recyclerViewGastos);
        RecyclerView recyclerViewGastosTotales = findViewById(R.id.recyclerViewGastosTotales);
        tvSinDeudas = findViewById(R.id.tvSinDeudas);
        rlGastosTotales = findViewById(R.id.rlGastosTotales);
        TextView tvTextoResolucion = findViewById(R.id.tvTextoResolucion);

        // Creamos listas vacías.
        listaDeMiembros = new ArrayList<>();
        listaDeSoluciones = new ArrayList<>();
        mostrarResolucion = new ArrayList<>();
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
        ayuda();

        tvTextoResolucion.setOnClickListener(v -> rlGastosTotales.setVisibility(View.VISIBLE));

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

        Transaccion transaccionConNuevosCambios = new Transaccion("Deuda Saldada",
                deuda, pagador, cobrador, 2,
                nuevaFecha, walletId);
        return transaccionController.nuevaTransaccion(transaccionConNuevosCambios);
    }

    public void ayuda() {
        // Abrir ayuda o no según su visualización previa.
        AyudaAppController ayudaAppController;
        ayudaAppController = new AyudaAppController(ResolverDeudaActivity.this);
        ArrayList<Ayuda> ayuda = ayudaAppController.obtenerAyuda();
        Ayuda ayudas = ayuda.get(4);

        // Recuperamos de la DB si se ha accedido ya a la ayuda o no.
        if (ayudas.getAyuda() == 1) {
            // Introducimos valor 0 para que no se muestre la ayuda la próxima vez.
            Ayuda ayudasModificado = new Ayuda(0, ayudas.getAyudaNombre(), ayudas.getId());
            ayudaAppController.modificarAyuda(ayudasModificado);
            // Pasar a la actividad
            Intent intent = new Intent(ResolverDeudaActivity.this,
                    SaldarDeudasAyuda.class);
            startActivity(intent);
        }
    }

}