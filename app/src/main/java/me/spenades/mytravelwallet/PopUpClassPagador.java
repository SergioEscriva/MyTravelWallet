package me.spenades.mytravelwallet;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.List;

import me.spenades.mytravelwallet.adapters.PagadoresAdapters;
import me.spenades.mytravelwallet.models.Participante;

public class PopUpClassPagador extends PopupWindow {

    //PopupWindow display method
    private RecyclerView recyclerViewPagadores;
    private PagadoresAdapters pagadoresAdapters;
    private Participante participante;
    private List<Participante> listaDeParticipantes;
    private Button buttonEdit;
    private Context context;

    public void showPopupWindow(final View view, List<Participante> listaDeParticipantes) {
        //Create a View object yourself through inflater


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


        // recyclerview tamaño fijo y linear layout
        //recyclerViewPagadores.setHasFixedSize(true);
        //recyclerViewPagadores.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        //recyclerViewPagadores.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));


        //listaDeParticipantes = new ArrayList<>();
        pagadoresAdapters = new PagadoresAdapters(listaDeParticipantes);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(popupView.getContext()); //.getApplicationContext());
        recyclerViewPagadores.setLayoutManager(mLayoutManager);
        recyclerViewPagadores.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPagadores.setAdapter(pagadoresAdapters);
        setContentView(view);


        /*
        // Adaptador pagadores
        listaDeParticipantes = new ArrayList<>();
        pagadoresAdapters = new PagadoresAdapters(listaDeParticipantes);

        // configuramos el recyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewPagadores.setLayoutManager(mLayoutManager);
        recyclerViewPagadores.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPagadores.setAdapter(pagadoresAdapters);

        // Refrescamos
        refrescarListaDeWallets();

        // TextView test2 = popupView.findViewById(R.id.titleText);
        //test2.setText(R.string.textTitle);
        //Inicializamos  Popup y el botón.


        buttonEdit = popupView.findViewById(R.id.btn
        )

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println();
                //final Participante pagadorActivo = listaDeParticipantes.get(position);
                //String pagador = pagadorActivo.getNombre();
                //long pagadorId = pagadorActivo.getUserId();
                //nuevoPagador = String.valueOf(pagadorId);
                //As an example, display the message
                Toast.makeText(view.getContext(), "Wow, popup action button", Toast.LENGTH_SHORT).show();
                ;

            }
        });

        */
        //Handler for clicking on the inactive zone of the window

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void refrescarListaDeWallets() {
        if (pagadoresAdapters == null) return;
        pagadoresAdapters.setListaDeParticipantes(listaDeParticipantes);
        pagadoresAdapters.notifyDataSetChanged();

    }

}