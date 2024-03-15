package me.spenades.mytravelwallet;

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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.spenades.mytravelwallet.adapters.TransaccionesAdapters;
import me.spenades.mytravelwallet.controllers.ParticipanteController;
import me.spenades.mytravelwallet.controllers.TransaccionController;
import me.spenades.mytravelwallet.models.Participante;
import me.spenades.mytravelwallet.models.Transaccion;
import me.spenades.mytravelwallet.utilities.Operaciones;
import me.spenades.mytravelwallet.utilities.RecyclerTouchListener;
import me.spenades.mytravelwallet.utilities.ResolverDeudaActivity;
import me.spenades.mytravelwallet.utilities.UsuarioUtility;


public class ListarTransaccionesActivity extends AppCompatActivity {

    private List<Transaccion> listaDeTransaccions;
    private List<Participante> listaDeParticipantes;
    private RecyclerView recyclerViewTransacciones;
    // private FrameLayout frameLResumen;
    private TransaccionesAdapters transaccionesAdapters;
    private TransaccionController transaccionController;
    private ParticipanteController participanteController;
    private UsuarioUtility usuarioUtility;
    private FloatingActionButton fabAgregarTransaccion;
    private FloatingActionButton fabResolverDeudas;
    private long walletId;
    private String walletName;
    private TextView tvWalletActivo, tvTotal, tvDeberiaPagar, tvMiembros;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.activity_main_transactions);
        this.walletName = extras.getString("nombreWallet");
        this.walletId = extras.getLong("walletId");
        long usuarioIdActivo = extras.getInt("usuarioIdActivo");
        String usuarioActivo = extras.getString("usuarioActivo");

        // Recuperar datos que enviaron
        if (extras == null) {
            finish();
            return;
        }


        // Definir nuestro controlador
        transaccionController = new TransaccionController(ListarTransaccionesActivity.this);
        participanteController = new ParticipanteController(ListarTransaccionesActivity.this);


        // Instanciar vistas
        recyclerViewTransacciones = findViewById(R.id.recyclerViewTransacciones);
        fabAgregarTransaccion = findViewById(R.id.fabAgregarTransaccion);
        fabResolverDeudas = findViewById(R.id.fabResolverDeudas);
        tvWalletActivo = findViewById(R.id.tvWalletActivo);
        tvTotal = findViewById(R.id.tvTotal);
        tvDeberiaPagar = findViewById(R.id.tvDeberiaPagar);
        tvMiembros = findViewById(R.id.tvMiembros);

        // Por defecto es una lista vacía,
        listaDeTransaccions = new ArrayList<>();
        transaccionesAdapters = new TransaccionesAdapters(listaDeTransaccions);
        listaDeParticipantes = new ArrayList<>();

        // se la ponemos al adaptador y configuramos el recyclerView

        RecyclerView.LayoutManager mLayoutManagerTop =
                new LinearLayoutManager(getApplicationContext());
        recyclerViewTransacciones.setLayoutManager(mLayoutManagerTop);
        recyclerViewTransacciones.setItemAnimator(new DefaultItemAnimator());
        recyclerViewTransacciones.setAdapter(transaccionesAdapters);

        tvWalletActivo.setText("Wallet " + this.walletName);

        // Una vez que ya configuramos el RecyclerView le ponemos los datos de la BD
        refrescarListaDeTransacciones();

        Operaciones objOperaciones = new Operaciones();
        String totalTransacciones = objOperaciones.sumaTransacciones(listaDeTransaccions,
                listaDeParticipantes);
        List<String> siguientePagador = objOperaciones.proximoPagador();
        List<String> miembros = objOperaciones.listaDeMiembros();
        String importeTotal = totalTransacciones + "€";
        tvTotal.setText(importeTotal);
        tvDeberiaPagar.setText(String.valueOf(siguientePagador.get(1)));
        tvMiembros.setText(String.valueOf(miembros));

        // Listener de los clicks en la lista TRANSACCIONES
        recyclerViewTransacciones.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewTransacciones,
                new RecyclerTouchListener.ClickListener() {

                    @Override // Un toque Editar
                    public void onClick(View view, int position) {

                        // Pasar a la actividad EditarTransaccionesActivity.java
                        Transaccion transaccionSeleccionada = listaDeTransaccions.get(position);
                        String transaccionId = String.valueOf(transaccionSeleccionada.getId());

                        Intent intent = new Intent(ListarTransaccionesActivity.this,
                                EditarTransaccionesActivity.class);
                        intent.putExtra("transaccionId", transaccionId);
                        intent.putExtra("descripcionTransaccion", transaccionSeleccionada.getDescripcion());
                        intent.putExtra("importeTransaccion", transaccionSeleccionada.getImporte());
                        intent.putExtra("nombrePagadorTransaccion",
                                transaccionSeleccionada.getNombrePagador());
                        intent.putExtra("pagadorIdTransaccion", transaccionSeleccionada.getPagadorId());
                        intent.putExtra("participantesTransaccion",
                                transaccionSeleccionada.getParticipantes());
                        intent.putExtra("fechaTransaccion", transaccionSeleccionada.getFecha());
                        intent.putExtra("categoriaTransaccion", transaccionSeleccionada.getCategoria());
                        intent.putExtra("walletId", String.valueOf(walletId));
                        intent.putExtra("walletName", walletName);
                        intent.putExtra("usuarioActivo", String.valueOf(usuarioActivo));
                        intent.putExtra("usuarioIdActivo", String.valueOf(usuarioIdActivo));
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
                }) {

            @Override
            public void onClick(View view, int position) {

            }
        });

        // Listener del Añadir transacción
        fabAgregarTransaccion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // //Añadir transacción Nueva
                Intent intent = new Intent(ListarTransaccionesActivity.this,
                        AgregarTransaccionActivity.class);
                intent.putExtra("walletId", String.valueOf(walletId));
                intent.putExtra("walletName", walletName);
                intent.putExtra("usuarioActivo", String.valueOf(usuarioActivo));
                intent.putExtra("usuarioIdActivo", String.valueOf(usuarioIdActivo));
                startActivity(intent);
            }
        });

        // Créditos
        fabAgregarTransaccion.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(ListarTransaccionesActivity.this)
                        .setTitle("Acerca de")
                        .setMessage("Wallet Travel Universae\n\nIcons www.flaticon.com, y " +
                                "plantilla código de www.parzibyte.me")
                        .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogo, int which) {
                                dialogo.dismiss();
                            }
                        })
                        .setPositiveButton("Sitio web", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intentNavegador = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://universae.com"));
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
                Intent intent = new Intent(ListarTransaccionesActivity.this,
                        ResolverDeudaActivity.class);
                intent.putExtra("walletId", walletId);
                startActivity(intent);
            }
        });

        // Créditos
        fabResolverDeudas.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(ListarTransaccionesActivity.this)
                        .setTitle("Acerca de")
                        .setMessage("My Wallet Travel, una aplicación fin proyecto DAM para Universae")
                        .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogo, int which) {
                                dialogo.dismiss();
                            }
                        })
                        .setPositiveButton("Sitio web", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intentNavegador = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://universae.com"));
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
        listaDeParticipantes = participanteController.obtenerParticipantes(walletId);
        listaDeTransaccions = transaccionController.obtenerTransacciones(walletId);
        transaccionesAdapters.setListaDeTransacciones(listaDeTransaccions);
        transaccionesAdapters.notifyDataSetChanged();
    }


}
