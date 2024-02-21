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

import me.spenades.mywallettravel.controllers.WalletController;
import me.spenades.mywallettravel.modelos.Wallet;


public class ListarWalletsActivity extends AppCompatActivity {
    private List<Wallet> listaDeWallets;

    private RecyclerView recyclerViewWallets;

    private AdaptadorWallets adaptadorWallets;

    private WalletController walletController;

    private FloatingActionButton fabAgregarWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // código es generado automáticamente
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wallets);

        // Definir nuestro controlador
        walletController = new WalletController(ListarWalletsActivity.this);

        // Instanciar vistas
        recyclerViewWallets = findViewById(R.id.recyclerViewWallets) ;
        fabAgregarWallet = findViewById(R.id.fabAgregarWallet);

        // Por defecto es una lista vacía,
        // se la ponemos al adaptador y configuramos el recyclerView
        listaDeWallets = new ArrayList<>();
        adaptadorWallets = new AdaptadorWallets(listaDeWallets);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewWallets.setLayoutManager(mLayoutManager);
        recyclerViewWallets.setItemAnimator(new DefaultItemAnimator());
        recyclerViewWallets.setAdapter(adaptadorWallets);

        // Una vez que ya configuramos el RecyclerView le ponemos los datos de la BD
        refrescarListaDeWallets();
        // Listener de los clicks en la lista WALLET.
        recyclerViewWallets.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewWallets, new RecyclerTouchListener.ClickListener() {
            @Override // Un toque
            public void onClick(View view, int position) {
                 // Pasar a la actividad ListaTransaccionesActivity.java
               Intent intent = new Intent(ListarWalletsActivity.this, ListarTransaccionesActivity.class);
               intent.putExtra("walletId", position);
               startActivity(intent);
            }

            @Override // Un toque largo
            public void onLongClick(View view, int position) {
                final Wallet walletParaEliminar = listaDeWallets.get(position);
                AlertDialog dialog = new AlertDialog
                        .Builder(ListarWalletsActivity.this)
                        .setPositiveButton("Sí, eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                walletController.eliminarWallet(walletParaEliminar);
                                refrescarListaDeWallets();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setTitle("Confirmar")
                        .setMessage("¿Eliminar el Wallet " + walletParaEliminar.getDescripcion() + "?")
                        .create();
                dialog.show();
            }
        }));

        // Listener del FAB
        fabAgregarWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simplemente cambiamos de actividad
                Intent intent = new Intent(ListarWalletsActivity.this, AgregarWalletActivity.class);
                startActivity(intent);
            }
        });

        // Créditos
        fabAgregarWallet.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(ListarWalletsActivity.this)
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
        refrescarListaDeWallets();
    }

    public void refrescarListaDeWallets() {
        if (adaptadorWallets == null) return;
        listaDeWallets = walletController.obtenerWallets();
        adaptadorWallets.setListaDeWallets(listaDeWallets);
        adaptadorWallets.notifyDataSetChanged();

    }

}

