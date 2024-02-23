package me.spenades.mywallettravel;

import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.spenades.mywallettravel.adapters.AdaptadorUsuarios;
import me.spenades.mywallettravel.controllers.UsuarioController;
import me.spenades.mywallettravel.models.Usuario;

public class MainActivity extends AppCompatActivity {
    private UsuarioController usuarioController;
    private List<Usuario> listaDeUsuarios;
    private AdaptadorUsuarios adaptadorUsuarios;
    private Button btnEmpezar;
    private EditText etNombrePropietario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Definir nuestro controlador
        usuarioController = new UsuarioController(MainActivity.this);

        // Recuperamos lista de usuarios
        listaDeUsuarios = new ArrayList<>();
        adaptadorUsuarios = new AdaptadorUsuarios(listaDeUsuarios);
        // Se envía el wallet 0 para poder recuperar todos los usuarios
        listaDeUsuarios = usuarioController.obtenerUsuarios(0);
        adaptadorUsuarios.setListaDeUsuarios(listaDeUsuarios);
        int cantidadUsuarios = adaptadorUsuarios.getItemCount();

        System.out.println(adaptadorUsuarios.getItemCount());

        //final Usuario usuarioActivo = listaDeUsuarios.get(1);

        // Si la lista está vacía se insta a añadir usuario Propietario
        if (cantidadUsuarios == 0){

            // Instanciamos vistas
            etNombrePropietario = findViewById(R.id.etNombrePropietario);
            btnEmpezar = findViewById(R.id.btnEmpezar);

            btnEmpezar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Resetear errores
                    etNombrePropietario.setError(null);
                    String nombrePropietario = etNombrePropietario.getText().toString();

                    if ("".equals(nombrePropietario)) {
                        etNombrePropietario.setError("Escribe tu Nombre");
                        etNombrePropietario.requestFocus();
                        return;
                    }

                    System.out.println(nombrePropietario);
                    Usuario nuevoPropietario = new Usuario(nombrePropietario, nombrePropietario);
                    System.out.println(nuevoPropietario);
                    long id = usuarioController.nuevoUsuario(nuevoPropietario);
                    if (id == -1) {
                        // De alguna manera ocurrió un error
                        Toast.makeText(MainActivity.this, "Error al guardar. Intenta de nuevo", Toast.LENGTH_SHORT).show();
                    } else {

                        Intent intent = new Intent(MainActivity.this, ListarWalletsActivity.class);
                        startActivity(intent);
                    }
                }

            });
        }else {
            //Simplemente cambiamos de actividad

            Intent intent = new Intent(MainActivity.this, ListarWalletsActivity.class);
            startActivity(intent);
        }
    }

}

