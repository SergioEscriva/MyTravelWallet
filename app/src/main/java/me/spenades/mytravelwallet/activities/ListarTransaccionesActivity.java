package me.spenades.mytravelwallet.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.spenades.mytravelwallet.R;
import me.spenades.mytravelwallet.adapters.TransaccionesAdapters;
import me.spenades.mytravelwallet.ayuda.ListaTransaccionesAyuda;
import me.spenades.mytravelwallet.controllers.AyudaAppController;
import me.spenades.mytravelwallet.controllers.MiembroWalletController;
import me.spenades.mytravelwallet.controllers.TransaccionController;
import me.spenades.mytravelwallet.models.Ayuda;
import me.spenades.mytravelwallet.models.Miembro;
import me.spenades.mytravelwallet.models.Transaccion;
import me.spenades.mytravelwallet.utilities.DeudaUtility;
import me.spenades.mytravelwallet.utilities.Operaciones;
import me.spenades.mytravelwallet.utilities.RecyclerTouchListener;

public class ListarTransaccionesActivity extends AppCompatActivity {

    private List<Transaccion> listaDeTransaccions;
    private List<Miembro> listaDeMiembros;
    private RecyclerView recyclerViewTransacciones;
    private TransaccionesAdapters transaccionesAdapters;
    private TransaccionController transaccionController;
    private MiembroWalletController miembroWalletController;
    private FloatingActionButton fabAgregarTransaccion;
    private FloatingActionButton fabResolverDeudas;
    private long walletId;
    private String walletName;
    private String ordenAscendente;
    private TextView tvWalletActivo, tvTotal, tvDeberiaPagar, tvMiembros;
    private TextView tvOrImporte, tvOrCategoria, tvOrPagador, tvOrFecha, tvOrDescripcion;
    private TextView tvOrImporteOr, tvOrCategoriaOr, tvOrPagadorOr, tvOrFechaOr, tvOrDescripcionOr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_transactions);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ayuda();
        Bundle extras = getIntent().getExtras();
        this.walletName = extras.getString("nombreWallet");
        this.walletId = extras.getLong("walletId");
        long usuarioIdActivo = extras.getInt("usuarioIdActivo");
        String usuarioActivo = extras.getString("usuarioActivo");

        // Se muestra sólo la primera vez la Ayuda
        int info = extras.getInt("info");

        // Recuperar datos que enviaron
        if (extras == null) {
            finish();
            return;
        }

        // Definir nuestro controlador
        transaccionController = new TransaccionController(ListarTransaccionesActivity.this);
        miembroWalletController = new MiembroWalletController(ListarTransaccionesActivity.this);

        // Instanciar vistas
        recyclerViewTransacciones = findViewById(R.id.recyclerViewTransacciones);
        fabAgregarTransaccion = findViewById(R.id.fabAgregarTransaccion);
        fabResolverDeudas = findViewById(R.id.fabResolverDeudas);
        tvWalletActivo = findViewById(R.id.tvWalletActivo);
        tvTotal = findViewById(R.id.tvTotal);
        tvDeberiaPagar = findViewById(R.id.tvDeberiaPagar);
        tvMiembros = findViewById(R.id.tvMiembros);
        // Vistas ordenar
        tvOrImporte = findViewById(R.id.tvOrImporte);
        tvOrCategoria = findViewById(R.id.tvOrCategoria);
        tvOrFecha = findViewById(R.id.tvOrFecha);
        tvOrPagador = findViewById(R.id.tvOrPagador);
        tvOrDescripcion = findViewById(R.id.tvOrDescripcion);
        // Punto ordenado
        tvOrImporteOr = findViewById(R.id.tvOrImporteOr);
        tvOrCategoriaOr = findViewById(R.id.tvOrCategoriaOr);
        tvOrFechaOr = findViewById(R.id.tvOrFechaOr);
        tvOrPagadorOr = findViewById(R.id.tvOrPagadorOr);
        tvOrDescripcionOr = findViewById(R.id.tvOrDescripcionOr);

        // Por defecto es una lista vacía,
        listaDeTransaccions = new ArrayList<>();
        transaccionesAdapters = new TransaccionesAdapters(listaDeTransaccions);

        listaDeMiembros = new ArrayList<>();

        // configuramos el recyclerView
        RecyclerView.LayoutManager mLayoutManagerTop =
                new LinearLayoutManager(getApplicationContext());
        recyclerViewTransacciones.setLayoutManager(mLayoutManagerTop);
        recyclerViewTransacciones.setItemAnimator(new DefaultItemAnimator());
        recyclerViewTransacciones.setAdapter(transaccionesAdapters);

        tvWalletActivo.setText(String.format("Wallet %s", this.walletName));

        // Una vez que ya configuramos el RecyclerView le ponemos los datos de la BD
        // y ordenar
        refrescarListas();
        ordenar(1);

        // Listener de Saldar Deudas al pulsar el imorte total
        tvTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListarTransaccionesActivity.this,
                        ResolverDeudaActivity.class);
                intent.putExtra("walletId", String.valueOf(walletId));
                intent.putExtra("info", info);
                startActivity(intent);
            }
        });
        // Para el inicio se pone en modo ascendente
        // Listener Ordenar ascendente y descendente.


        tvOrImporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Recuperamos valo del orden del tooltip que nos está sirviendo de referencia.
                ordenAscendente = String.valueOf(tvOrImporteOr.getTooltipText());
                // si es ascendete, cambiamos el tooltip a descendente.
                if (ordenAscendente.equals("ascendente")) {
                    tvOrImporteOr.setTooltipText("descendente");
                } else {
                    tvOrImporteOr.setTooltipText("ascendente");
                }
                // Ocultamos todos los puntos que especifican orden.
                ocultarNoOrdenados();
                // Mostramos sólo el punto de este orden.
                tvOrImporteOr.setVisibility(View.VISIBLE);
                // llamamos a la función orden.
                ordenar(5);
            }
        });

        tvOrFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Recuperamos valo del orden del tooltip que nos está sirviendo de referencia.
                ordenAscendente = String.valueOf(tvOrFechaOr.getTooltipText());
                // si es ascendete, cambiamos el tooltip a descendente.
                if (ordenAscendente.equals("ascendente")) {
                    tvOrFechaOr.setTooltipText("descendente");
                } else {
                    tvOrFechaOr.setTooltipText("ascendente");
                }
                // Ocultamos todos los puntos que especifican orden.
                ocultarNoOrdenados();
                // Mostramos sólo el punto de este orden.
                tvOrFechaOr.setVisibility(View.VISIBLE);
                // llamamos a la función orden.
                ordenar(1);
            }
        });
        tvOrCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Recuperamos valo del orden del tooltip que nos está sirviendo de referencia.
                ordenAscendente = String.valueOf(tvOrCategoriaOr.getTooltipText());
                // si es ascendete, cambiamos el tooltip a descendente.
                if (ordenAscendente.equals("ascendente")) {
                    tvOrCategoriaOr.setTooltipText("descendente");
                } else {
                    tvOrCategoriaOr.setTooltipText("ascendente");
                }
                // Ocultamos todos los puntos que especifican orden.
                ocultarNoOrdenados();
                // Mostramos sólo el punto de este orden.
                tvOrCategoriaOr.setVisibility(View.VISIBLE);
                // llamamos a la función orden.
                ordenar(3);
            }
        });
        tvOrDescripcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Recuperamos valo del orden del tooltip que nos está sirviendo de referencia.
                ordenAscendente = String.valueOf(tvOrDescripcionOr.getTooltipText());
                // si es ascendete, cambiamos el tooltip a descendente.
                if (ordenAscendente.equals("ascendente")) {
                    tvOrDescripcionOr.setTooltipText("descendente");
                } else {
                    tvOrDescripcionOr.setTooltipText("ascendente");
                }
                // Ocultamos todos los puntos que especifican orden.
                ocultarNoOrdenados();
                // Mostramos sólo el punto de este orden.
                tvOrDescripcionOr.setVisibility(View.VISIBLE);
                // llamamos a la función orden.
                ordenar(2);
            }
        });
        tvOrPagador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Recuperamos valo del orden del tooltip que nos está sirviendo de referencia.
                ordenAscendente = String.valueOf(tvOrPagadorOr.getTooltipText());
                // si es ascendete, cambiamos el tooltip a descendente.
                if (ordenAscendente.equals("ascendente")) {
                    tvOrPagadorOr.setTooltipText("descendente");
                } else {
                    tvOrPagadorOr.setTooltipText("ascendente");
                }
                // Ocultamos todos los puntos que especifican orden.
                ocultarNoOrdenados();
                // Mostramos sólo el punto de este orden.
                tvOrPagadorOr.setVisibility(View.VISIBLE);
                // llamamos a la función orden.
                ordenar(4);
            }
        });

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
                        intent.putExtra("miembrosTransaccion",
                                transaccionSeleccionada.getMiembros());
                        intent.putExtra("fechaTransaccion", transaccionSeleccionada.getFecha());
                        intent.putExtra("categoriaTransaccion", transaccionSeleccionada.getCategoria());
                        intent.putExtra("walletId", String.valueOf(walletId));
                        intent.putExtra("walletName", walletName);
                        intent.putExtra("usuarioActivo", String.valueOf(usuarioActivo));
                        intent.putExtra("usuarioIdActivo", String.valueOf(usuarioIdActivo));
                        intent.putExtra("info", info);
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
                                        refrescarListas();
                                        ordenar(1);
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
                intent.putExtra("info", info);
                startActivity(intent);
            }
        });

        // Créditos
        fabAgregarTransaccion.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(ListarTransaccionesActivity.this)
                        .setTitle("Acerca de")
                        .setMessage("My Travel Wallet, una aplicación fin proyecto DAM para Universae")
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
                intent.putExtra("walletId", String.valueOf(walletId));
                intent.putExtra("info", info);
                startActivity(intent);
            }
        });

        // Créditos
        fabResolverDeudas.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(ListarTransaccionesActivity.this)
                        .setTitle("Acerca de")
                        .setMessage("My Travel Wallet, una aplicación fin proyecto DAM para Universae")
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
        refrescarListas();
        ordenar(1);
    }

    public void refrescarListas() {
        fabResolverDeudas.setVisibility(View.VISIBLE);
        listaDeMiembros = miembroWalletController.obtenerMiembros(walletId);
        //ordenar(1);

    }


    public void resumenTransacciones() {
        Operaciones operaciones = new Operaciones();
        DeudaUtility deudaUtility = new DeudaUtility();
        String totalTransacciones = deudaUtility.sumaTransacciones(listaDeTransaccions,
                listaDeMiembros);
        List<String> siguientePagador = deudaUtility.proximoPagador();
        List<String> miembros = deudaUtility.listaDeMiembros();
        String importeLimpio = operaciones.dosDecimalesStringString(totalTransacciones);
        //String importeTotal = String.format("%s€", importeLimpio);
        String importeTotal = deudaUtility.importeFormateado(totalTransacciones);
        tvTotal.setText(importeTotal);
        tvDeberiaPagar.setText(String.valueOf(siguientePagador.get(1)));
        tvMiembros.setText(String.valueOf(miembros));
    }

    // Ordena la lista tanto de forma ascendente como descendente
    public void ordenar(int orden) {
        listaDeTransaccions = transaccionController.obtenerTransacciones(walletId);
        //int orden = 1;
        // https://stackoverflow.com/questions/45790363/how-to-sort-recyclerview-item-in-android
        Collections.sort(listaDeTransaccions, new Comparator<Transaccion>() {
            @Override
            public int compare(Transaccion lhs, Transaccion rhs) {
                switch (orden) {
                    case 1: //Fecha
                        // Obtiene el valor del orden y ordena según lo obtenido.
                        ordenAscendente = String.valueOf(tvOrFechaOr.getTooltipText());
                        if (ordenAscendente.equals("descendente")) {
                            return rhs.getFecha().compareTo(lhs.getFecha()); //Por Fecha
                        } else {
                            return lhs.getFecha().compareTo(rhs.getFecha()); //Por Fecha
                        }

                    case 2: //Descripción
                        ordenAscendente = String.valueOf(tvOrDescripcionOr.getTooltipText());
                        if (ordenAscendente.equals("ascendente")) {
                            return rhs.getDescripcion().compareTo(lhs.getDescripcion()); //Por Id de transaccion
                        } else {
                            return lhs.getDescripcion().compareTo(rhs.getDescripcion()); //Por Id de transaccion
                        }

                    case 3: //Categoria
                        ordenAscendente = String.valueOf(tvOrCategoriaOr.getTooltipText());
                        if (ordenAscendente.equals("ascendente")) {
                            return rhs.getCategoria().compareTo(lhs.getCategoria()); //categoria
                        } else {
                            return lhs.getCategoria().compareTo(rhs.getCategoria()); //categoria
                        }

                    case 4: //Pagador
                        ordenAscendente = String.valueOf(tvOrPagadorOr.getTooltipText());
                        if (ordenAscendente.equals("ascendente")) {
                            return rhs.getNombrePagador().compareTo(lhs.getNombrePagador()); //Por Nombre Pagador
                        } else {
                            return lhs.getNombrePagador().compareTo(rhs.getNombrePagador()); //Por Nombre Pagador
                        }


                    case 5: //Importe
                        ordenAscendente = String.valueOf(tvOrImporteOr.getTooltipText());
                        if (ordenAscendente.equals("ascendente")) {
                            return Double.compare(Double.parseDouble(rhs.getImporte()), Double.parseDouble(lhs.getImporte())); //Por numero de pagador
                        } else {
                            return Double.compare(Double.parseDouble(lhs.getImporte()), Double.parseDouble(rhs.getImporte())); //Por numero de pagador
                        }
                }
                // return rhs.getFecha().compareTo(lhs.getFecha());
                return 1;
            }
        });
        transaccionesAdapters.setListaDeTransacciones(listaDeTransaccions);
        transaccionesAdapters.notifyDataSetChanged();
        resumenTransacciones();
        if (listaDeTransaccions.size() == 0) {
            fabResolverDeudas.setVisibility(View.INVISIBLE);
        }

    }

    public void ocultarNoOrdenados() {
        tvOrImporteOr.setVisibility(View.INVISIBLE);
        tvOrCategoriaOr.setVisibility(View.INVISIBLE);
        tvOrFechaOr.setVisibility(View.INVISIBLE);
        tvOrPagadorOr.setVisibility(View.INVISIBLE);
        tvOrDescripcionOr.setVisibility(View.INVISIBLE);
    }

    public void ayuda() {
        // Abrir ayuda o no según su visualización previa.
        AyudaAppController ayudaAppController;
        ayudaAppController = new AyudaAppController(ListarTransaccionesActivity.this);
        ArrayList<Ayuda> ayuda = ayudaAppController.obtenerAyuda();
        Ayuda ayudas = ayuda.get(3);

        // Recuperamos de la DB si se ha accedido ya a la ayuda o no.
        if (ayudas.getAyuda() == 1) {
            // Introducimos valor 0 para que no se muestre la ayuda la próxima vez.
            Ayuda ayudasModificado = new Ayuda(0, ayudas.getAyudaNombre(), ayudas.getId());
            ayudaAppController.modificarAyuda(ayudasModificado);
            // Pasar a la actividad
            Intent intent = new Intent(ListarTransaccionesActivity.this,
                    ListaTransaccionesAyuda.class);
            startActivity(intent);
        }
    }

}
