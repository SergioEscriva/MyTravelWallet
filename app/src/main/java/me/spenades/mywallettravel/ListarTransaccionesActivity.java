package me.spenades.mywallettravel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.spenades.mywallettravel.adapters.AdaptadorResumen;
import me.spenades.mywallettravel.adapters.AdaptadorTransacciones;
import me.spenades.mywallettravel.controllers.TransaccionController;
import me.spenades.mywallettravel.models.Transaccion;


public class ListarTransaccionesActivity extends AppCompatActivity {
    private List<Transaccion> listaDeTransaccions;
    private RecyclerView recyclerViewTransacciones;
    private RecyclerView recyclerViewResumen;
    private AdaptadorTransacciones adaptadorTransacciones;
    private AdaptadorResumen adaptadorResumen;
    private TransaccionController transaccionController;
    private FloatingActionButton fabAgregarTransaccion;
    private FloatingActionButton fabResolverDeudas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // código es generado automáticamente
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_transactions);

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        // Si no hay datos (cosa rara) salimos
        if (extras == null) {
            finish();
            return;
        }


        // Recuperamos WalletId Activo
        long walletIdSelected = extras.getInt("walletId");
        String walletNameSelected = extras.getString("walletName");

        // Definir nuestro controlador
        transaccionController = new TransaccionController(ListarTransaccionesActivity.this);

        // Instanciar vistas
        recyclerViewResumen = findViewById(R.id.recyclerViewResumen);
        recyclerViewTransacciones = findViewById(R.id.recyclerViewTransacciones) ;
        fabAgregarTransaccion = findViewById(R.id.fabAgregarTransaccion);
        fabResolverDeudas = findViewById(R.id.fabResolverDeudas);

        // Por defecto es una lista vacía,


        listaDeTransaccions = new ArrayList<>();
        adaptadorTransacciones = new AdaptadorTransacciones(listaDeTransaccions);

        // se la ponemos al adaptador y configuramos el recyclerView

        RecyclerView.LayoutManager mLayoutManagerTop = new LinearLayoutManager(getApplicationContext());
        recyclerViewTransacciones.setLayoutManager(mLayoutManagerTop);
        recyclerViewTransacciones.setItemAnimator(new DefaultItemAnimator());
        recyclerViewTransacciones.setAdapter(adaptadorTransacciones);

        RecyclerView.LayoutManager mLayoutManagerBottom = new LinearLayoutManager(getApplicationContext());
        adaptadorResumen = new AdaptadorResumen(listaDeTransaccions, walletNameSelected);
        recyclerViewResumen.setLayoutManager(mLayoutManagerBottom);
        recyclerViewResumen.setItemAnimator(new DefaultItemAnimator());
        recyclerViewResumen.setAdapter(adaptadorResumen);


        // Una vez que ya configuramos el RecyclerView le ponemos los datos de la BD
        refrescarListaDeTransacciones();

        // Listener de los clicks en la lista TRANSACCIONES
        recyclerViewTransacciones.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewTransacciones, new RecyclerTouchListener.ClickListener() {
            @Override // Un toque
            public void onClick(View view, int position) {
                // Pasar a la actividad EditarTransaccionesActivity.java
                Transaccion transaccionSeleccionada = listaDeTransaccions.get(position);
                Intent intent = new Intent(ListarTransaccionesActivity.this, EditarTransaccionesActivity.class);
                intent.putExtra("idTransaccion", transaccionSeleccionada.getId());
                intent.putExtra("descripcionTransaccion", transaccionSeleccionada.getDescripcion());
                intent.putExtra("importeTransaccion", transaccionSeleccionada.getImporte());
                intent.putExtra("pagadorTransaccion", transaccionSeleccionada.getPagador());
                intent.putExtra("participantesTransaccion", transaccionSeleccionada.getParticipantes());
                intent.putExtra("categoriaTransaccion", transaccionSeleccionada.getCategoria());
                intent.putExtra("fechaTransaccion", transaccionSeleccionada.getFecha());
                intent.putExtra("walletId", walletIdSelected);
                startActivity(intent);
            }

            @Override // Un toque largo
            public void onLongClick(View view, int position) {
                final Transaccion transaccionParaEliminar = listaDeTransaccions.get(position);
                AlertDialog dialog = new AlertDialog
                        .Builder(ListarTransaccionesActivity.this)
                        .setPositiveButton("Sí, eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                transaccionController.eliminarTransaccion(transaccionParaEliminar);
                                refrescarListaDeTransacciones();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setTitle("Confirmar")
                        .setMessage("¿Eliminar a la transacción " + transaccionParaEliminar.getDescripcion() + "?")
                        .create();
                dialog.show();
            }
        }));

        // Listener del FABTransacciones
        fabAgregarTransaccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simplemente cambiamos de actividad
                Intent intent = new Intent(ListarTransaccionesActivity.this, AgregarTransaccionActivity.class);
                intent.putExtra("walletId", walletIdSelected);
                startActivity(intent);
            }
        });

        // Créditos
        fabAgregarTransaccion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(ListarTransaccionesActivity.this)
                        .setTitle("Acerca de")
                        .setMessage("Wallet Travel Universae\n\nIcons www.flaticon.com, y plantilla código de www.parzibyte.me")
                        .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogo, int which) {
                                dialogo.dismiss();
                            }
                        })
                        .setPositiveButton("Sitio web", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intentNavegador = new Intent(Intent.ACTION_VIEW, Uri.parse("https://universae.com"));
                                startActivity(intentNavegador);
                            }
                        })
                        .create()
                        .show();
                return false;
            }
        });




    // Listener del FABResolverDeudas
        fabResolverDeudas.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Simplemente cambiamos de actividad
            Intent intent = new Intent(ListarTransaccionesActivity.this, ResolverDeudaActivity.class);
            intent.putExtra("walletId", walletIdSelected);
            startActivity(intent);
        }
    });

    // Créditos
        fabResolverDeudas.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            new AlertDialog.Builder(ListarTransaccionesActivity.this)
                    .setTitle("Acerca de")
                    .setMessage("Wallet Travel Universae\n\nIcons www.flaticon.com, y plantilla código de www.parzibyte.me")
                    .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogo, int which) {
                            dialogo.dismiss();
                        }
                    })
                    .setPositiveButton("Sitio web", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intentNavegador = new Intent(Intent.ACTION_VIEW, Uri.parse("https://universae.com"));
                            startActivity(intentNavegador);
                        }
                    })
                    .create()
                    .show();
            return false;
        }
    });

    }


    @Override
    protected void onResume() {
        super.onResume();
        refrescarListaDeTransacciones();
    }

    public void refrescarListaDeTransacciones() {
        if (adaptadorTransacciones == null) return;
        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        // Si no hay datos (cosa rara) salimos
        if (extras == null) {
            finish();
            return;
        }

        // Recuperamos WalletId Activo
        int walletIdSelected = extras.getInt("walletId");
        String walletNameSelected = extras.getString("walletName");

        listaDeTransaccions = transaccionController.obtenerTransacciones(walletIdSelected);
        adaptadorTransacciones.setListaDeTransacciones(listaDeTransaccions);
        adaptadorTransacciones.notifyDataSetChanged();
        adaptadorResumen.setListaDeTransacciones(listaDeTransaccions, walletNameSelected);
        adaptadorResumen.notifyDataSetChanged();

    }

}
