package me.spenades.mytravelwallet;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import me.spenades.mytravelwallet.adapters.PagadoresAdapters;
import me.spenades.mytravelwallet.models.Participante;

public class PopUpClassPagador extends PopupWindow {

    private static TextView etPagadorId, etNombrePagador;
    public String nombrePagador;
    public String pagadorId;
    //PopupWindow display method
    private RecyclerView recyclerViewPagadores;
    private PagadoresAdapters pagadoresAdapters;
    private Participante participante;
    private List<Participante> listaDeParticipantes;
    private Button buttonEdit;
    private TextView pagadorEle;
    private Context context;

    public void showPopupWindow(final View view, List<Participante> listaDeParticipantes) {
        //Create a View object yourself through inflater


        this.nombrePagador = nombrePagador;
        this.pagadorId = pagadorId;

        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);

        View popupView = inflater.inflate(R.layout.activity_pagador, null);


        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        //Initialize the elements of our window, install the handler
        recyclerViewPagadores = popupView.findViewById(R.id.recyclerViewPagadores);

        EditarTransaccionesActivity obj = new EditarTransaccionesActivity();

        // listaDeParticipantes en el popupView
        pagadoresAdapters = new PagadoresAdapters(listaDeParticipantes);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(popupView.getContext()); //.getApplicationContext());
        recyclerViewPagadores.setLayoutManager(mLayoutManager);
        recyclerViewPagadores.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPagadores.setAdapter(pagadoresAdapters);
        //setContentView(view);


        // Seleccionar pagador de la lista

        recyclerViewPagadores.addOnItemTouchListener(new RecyclerTouchListener(popupView.getContext(), recyclerViewPagadores, new RecyclerTouchListener.ClickListener() {
            @Override // Un toque para seleccionar pagador
            public void onClick(View view, int position) {

                // Pasar a la actividad editarwallet con el nombre elegido.
                final Participante pagadorActivo = listaDeParticipantes.get(position);
                nombrePagador = pagadorActivo.getNombre();
                pagadorId = String.valueOf(pagadorActivo.getUserId());

                // Recuperamos el textview de EditarTransaccionesActivity y le ponemos el valor.
                EditarTransaccionesActivity editarTransaccionesActivity = new EditarTransaccionesActivity();
                TextView erNombrePagador = editarTransaccionesActivity.retornaNombrePagador();
                TextView erPagadorId = editarTransaccionesActivity.retornaPagadorId();
                erPagadorId.setText(pagadorId);
                erNombrePagador.setText(nombrePagador);
                popupWindow.dismiss();
            }

            @Override
            public void onLongClick(View view, int position) {
                popupWindow.dismiss();

            }
        }));

        /*
        // botón general del popup
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nombrePagador = pagadorEle.getText().toString();
                //collectInput(nombrePagador);
                popupWindow.dismiss();
                Toast.makeText(view.getContext(), "Cerrado, popup action button", Toast.LENGTH_SHORT).show();

            }

    });
    */
        /*
        // clickven parte inactiva de la pantalla popup

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });

         */

    }


}