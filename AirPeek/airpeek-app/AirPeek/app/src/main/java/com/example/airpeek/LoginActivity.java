package com.example.airpeek;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    // Definición de variables.
    private EditText userEmail;
    private EditText userPassword;
    private Button accessButton;
    private TextView registerRedirect;
    private Context context = this;
    private RequestQueue requestQueue;
    private ProgressBar pb1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicialización de los elementos de la interfaz de usuario
        userEmail = findViewById(R.id.email);
        userPassword = findViewById(R.id.passwd);
        accessButton = findViewById(R.id.access);
        registerRedirect = findViewById(R.id.register_redirect);
        pb1 = findViewById(R.id.loadingScreen);
        // Inicialización de la cola de solicitud de Volley
        requestQueue = Volley.newRequestQueue(this);
        // Configuración del listener del botón de acceso
        accessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString();
                String password = userPassword.getText().toString();
                // Validación del correo electrónico y la contraseña antes de intentar iniciar sesión
                if (validateLogin(email, password)){
                    pb1.setVisibility(View.VISIBLE); // Alternamos entre la visibilidad de la barra de progresión a nuestra conveniencia.
                    loginUser(email, password);
                }
            }
        });
        // Configuración del listener del texto de redirección al registro
        registerRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicio de la actividad de registro
                Intent intent = new Intent(context, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    // Método para validar el correo electrónico y la contraseña
    private boolean validateLogin(String email, String password){
        if (!email.contains("@") || email.length() < 8){
            userEmail.setError("Formato inválido de email");
            return false;
        }
        return true;
    }
    // Método para iniciar sesión del usuario
    private void loginUser(String email, String password) {
        // Creación del cuerpo de la solicitud
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        // Creación de la solicitud
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Server.name + "/user/session",
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejo de la respuesta exitosa
                        String receivedToken;
                        try {
                            receivedToken = response.getString("SessionToken");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        // Mostrar el token recibido
                        Toast.makeText(context, "Token: " + receivedToken, Toast.LENGTH_SHORT).show();
                        // Inicio de la actividad principal
                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);
                        // Almacenamiento del main de usuario y el token en las preferencias compartidas.
                        SharedPreferences preferences = context.getSharedPreferences("AIRPEEK_APP_PREFS", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("VALID_EMAIL", email);
                        editor.putString("VALID_TOKEN", receivedToken);
                        editor.commit();
                        pb1.setVisibility(View.GONE); // Alternamos entre la visibilidad de la barra de progresión a nuestra conveniencia.
                        // Finalización de la actividad de inicio de sesión
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Manejo de errores de la solicitud
                        if (error.networkResponse == null) {
                            pb1.setVisibility(View.GONE); // Alternamos entre la visibilidad de la barra de progresión a nuestra conveniencia.
                            Toast.makeText(context, "La conexión no se ha establecido", Toast.LENGTH_LONG).show();
                        } else {
                            pb1.setVisibility(View.GONE); // Alternamos entre la visibilidad de la barra de progresión a nuestra conveniencia.
                            int serverCode = error.networkResponse.statusCode;
                            Toast.makeText(context, "Estado de respuesta "+serverCode, Toast.LENGTH_LONG).show();
                        }
                        error.printStackTrace();
                    }
                }
        );
        // Añadir la solicitud a la cola de solicitudes
        this.requestQueue.add(request);
    }
}
