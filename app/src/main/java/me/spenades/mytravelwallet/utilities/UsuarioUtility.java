package me.spenades.mytravelwallet.utilities;



import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import me.spenades.mytravelwallet.controllers.UsuarioAppController;
import me.spenades.mytravelwallet.models.Usuario;


public class UsuarioUtility extends AppCompatActivity {

    public long nombreAid(String nombre) {
        // Conversión de nombre  a id para DB
        ArrayList<Usuario> nombreIdUsuario;
        UsuarioAppController usercontrol = new UsuarioAppController(UsuarioUtility.this);
        nombreIdUsuario = usercontrol.obtenerUsuarioId(nombre);
        long nombreId = nombreIdUsuario.get(0).getId();
        return nombreId;
    }


    public String idAnombre(long nombreId) {
        // Conversión de id a nombre DB
        ArrayList<Usuario> nombreIdUsuario;
        UsuarioAppController usercontrol = new UsuarioAppController(UsuarioUtility.this);
        nombreIdUsuario = usercontrol.obtenerUsuarioNombre(nombreId);
        String nombre = nombreIdUsuario.get(0).getNombre();
        return nombre;
    }




}

