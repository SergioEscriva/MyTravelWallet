package me.spenades.mytravelwallet.utilities;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import me.spenades.mytravelwallet.controllers.UsuarioController;
import me.spenades.mytravelwallet.models.Usuario;


public class UsuarioUtility extends AppCompatActivity {

    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // código es generado automáticamente
        super.onCreate(savedInstanceState);

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        long usuarioIdActivo = extras.getInt("usuarioIdActivo");
        String usuarioActivo = extras.getString("usuarioActivo");
        // Si no hay datos (cosa rara) salimos
        if (extras == null) {
            finish();
            return;
        }

        // Definir nuestro controlador
        walletController = new WalletController(UserUtility.this);
        participanteController = new ParticipanteController(UserUtility.this);
        usuarioController = new UsuarioController(UserUtility.this);

        // Instanciar vistas


    }

     */


    public long nombreAid(String nombre) {
        // Conversión de nombre  a id para DB
        ArrayList<Usuario> nombreIdUsuario;
        UsuarioController usercontrol = new UsuarioController(UsuarioUtility.this);
        nombreIdUsuario = usercontrol.obtenerUsuarioId(nombre);
        long nombreId = nombreIdUsuario.get(0).getId();
        return nombreId;
    }

    public String idAnombre(long nombreId) {
        // Conversión de id a nombre DB
        ArrayList<Usuario> nombreIdUsuario;
        UsuarioController usercontrol = new UsuarioController(UsuarioUtility.this);
        nombreIdUsuario = usercontrol.obtenerUsuarioNombre(nombreId);
        String nombre = nombreIdUsuario.get(0).getNombre();
        return nombre;
    }


}

