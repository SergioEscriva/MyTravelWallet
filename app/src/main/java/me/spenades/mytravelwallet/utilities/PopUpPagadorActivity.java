package me.spenades.mytravelwallet.utilities;


import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import me.spenades.mytravelwallet.R;
import me.spenades.mytravelwallet.activities.AgregarTransaccionActivity;
import me.spenades.mytravelwallet.activities.EditarTransaccionesActivity;
import me.spenades.mytravelwallet.adapters.PagadoresAdapters;
import me.spenades.mytravelwallet.models.Miembro;
import me.spenades.mytravelwallet.models.Usuario;

public class PopUpPagadorActivity extends PopupWindow {

    private static TextView etNombrePagador;
    public String nombrePagador;
    public long pagadorId;
    //public Usuario proximoPagador;
    public double importePagado;
    private RecyclerView recyclerViewPagadores;
    private PagadoresAdapters pagadoresAdapters;


    public void showPopupWindow(final View view, Usuario proximoPagador, List<Miembro> listaDeMiembros, Map<Long, Double> listaImportePagado, String activity) {


        this.nombrePagador = proximoPagador.getNombre();
        this.pagadorId = proximoPagador.getId();
        this.importePagado = importePagado;

        //Crea View con inflater
        LayoutInflater inflater =
                (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);

        View popupView = inflater.inflate(R.layout.activity_pagador, null); // edición
        View activityTransactionView = inflater.inflate(R.layout.activity_add_transaction, null);

        //Especifica tamaño de la ventana
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Hace inactivas los objetos fuera de PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // Localización de la ventana popup
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        //inicializamos los elementos
        recyclerViewPagadores = popupView.findViewById(R.id.recyclerViewGastos);
        etNombrePagador = activityTransactionView.findViewById(R.id.etNombrePagador);
        etNombrePagador.setText(nombrePagador);
        //String pagador = etNombrePagador.getText().toString();

        // listaDeMiembros en el popupView
        pagadoresAdapters = new PagadoresAdapters(listaDeMiembros, listaImportePagado);
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(popupView.getContext());
        recyclerViewPagadores.setLayoutManager(mLayoutManager);
        recyclerViewPagadores.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPagadores.setAdapter(pagadoresAdapters);


        // Seleccionar pagador de la lista
        recyclerViewPagadores.addOnItemTouchListener(new RecyclerTouchListener(popupView.getContext(), recyclerViewPagadores,
                new RecyclerTouchListener.ClickListener() {

                    @Override // Un toque para seleccionar pagador
                    public void onClick(View view, int position) {

                        // Pasar a la actividad editarwallet con el nombre elegido.
                        final Miembro pagadorActivo = listaDeMiembros.get(position);
                        nombrePagador = pagadorActivo.getNombre();
                        pagadorId = pagadorActivo.getUserId();

                        if (activity == "agregar") {

                            //Recuperamos el textview de ActivarTransaccionesActivity y le ponemos el valor.
                            AgregarTransaccionActivity agregarTransaccionesActivity =
                                    new AgregarTransaccionActivity();
                            TextView erNombrePagador = agregarTransaccionesActivity.retornaNombrePagador();
                            TextView erPagadorId = agregarTransaccionesActivity.retornaPagadorId();
                            erPagadorId.setText(String.valueOf(pagadorId));
                            erNombrePagador.setText(nombrePagador);
                            popupWindow.dismiss();
                        } else {

                            // Recuperamos el textview de EditarTransaccionesActivity y le ponemos el valor.
                            EditarTransaccionesActivity editarTransaccionesActivity =
                                    new EditarTransaccionesActivity();
                            TextView erNombrePagador = editarTransaccionesActivity.retornaNombrePagador();
                            TextView erPagadorId = editarTransaccionesActivity.retornaPagadorId();
                            erPagadorId.setText(String.valueOf(pagadorId));
                            erNombrePagador.setText(nombrePagador);
                            popupWindow.dismiss();
                        }
                        popupWindow.dismiss();

                    }


                    @Override
                    public void onLongClick(View view, int position) {
                        popupWindow.dismiss();

                        popupWindow.dismiss();
                    }
                }) {

            @Override
            public void onClick(View view, int position) {
                popupWindow.dismiss();
            }
        });


        // click en parte inactiva de la pantalla popup
        popupView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }

}