package com.example.airpeek;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    // Declaración de variables.
    private EditText nameEditText;
    private EditText passwordEditText;
    private EditText password2EditText;
    private EditText emailEditText;
    private EditText birthdateEditText;
    private Spinner countrySpinner;
    private Button registerButton;
    // Cola de solicitudes de Volley
    private RequestQueue requestQueue;
    // Contexto de la actividad
    private final Context context = this;
    private TextView loginPage;
    private ProgressBar pb1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicialización de los elementos de la interfaz
        setContentView(R.layout.activity_register);
        nameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        password2EditText = findViewById(R.id.password2);
        emailEditText = findViewById(R.id.email);
        birthdateEditText = findViewById(R.id.birthdate);
        pb1 = findViewById(R.id.loadingScreen);

        // Configuración del DatePickerDialog para seleccionar la fecha de nacimiento al clickar el campo.
        birthdateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String month, day;
                                // si el mes o el dia es menor a 10 se le añade un 0 delante ("05/03/2000")
                                if (monthOfYear < 10){
                                    month = "0" + (monthOfYear + 1);
                                } else {
                                    month = String.valueOf(monthOfYear + 1);
                                }
                                if (dayOfMonth < 10){
                                    day = "0" + dayOfMonth;
                                } else {
                                    day = String.valueOf(dayOfMonth);
                                }
                                if (year < 2024) {
                                    birthdateEditText.setText(year + "-" + month + "-" + day );
                                } else {
                                    birthdateEditText.setText("");
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        // Configuración del Spinner para seleccionar el país
        countrySpinner =(Spinner) findViewById(R.id.countryselect);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.country_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);
        // Inicialización del botón de registro y del TextView para redirigir a la página de inicio de sesión
        registerButton = findViewById(R.id.register_button);
        loginPage = findViewById(R.id.login_redirect);
        // Inicialización de la cola de solicitudes de Volley
        requestQueue = Volley.newRequestQueue(this);
        // Configuración del OnClickListener para el botón de registro
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = nameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String password2 = password2EditText.getText().toString();
                String email = emailEditText.getText().toString();
                String birthdate = birthdateEditText.getText().toString();
                String country = countrySpinner.getSelectedItem().toString();
                if (validateRegister(username,password,password2, email, birthdate)){
                    pb1.setVisibility(View.VISIBLE); // Alternamos entre la visibilidad de la barra de progresión a nuestra conveniencia.
                    sendRegisterRequest();
                }
            }
        });
        // Configuración del OnClickListener para el TextView que redirige a la página de inicio de sesión

        loginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
    // Método para validar el formulario de registro
    private boolean validateRegister(String username, String password,String password2, String email, String birthdate){
        if (username.isEmpty() || password.isEmpty() || password2.isEmpty() || email.isEmpty()){
            Toast.makeText(this, "Debes rellenar todos los campos!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(password2)){
            passwordEditText.setError("Las contraseñas deben coincidir");
            password2EditText.setError("Las contraseñas deben coincidir");
            return false;
        }
        if (!email.contains("@") || email.length() < 8){
            emailEditText.setError("Formato inválido de email");
            return false;
        }
        if (birthdate.isEmpty()) {
            birthdateEditText.setError("La fecha de nacimiento no es válida ");
            return false;
        }
        return true;
    }

    // Método para enviar la solicitud de registro
    private void sendRegisterRequest() {
        JSONObject requestBody = new JSONObject();
        try {
            // Empaquetamos en requestbody (JSON) los datos del formulario
            requestBody.put("name", nameEditText.getText().toString());
            requestBody.put("password", passwordEditText.getText().toString());
            requestBody.put("email", emailEditText.getText().toString());
            requestBody.put("birthdate", birthdateEditText.getText().toString());
            requestBody.put("country", countrySpinner.getSelectedItem().toString());

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        //Esto inicializa la peticion
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Server.name + "/user",
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pb1.setVisibility(View.GONE); // Alternamos entre la visibilidad de la barra de progresión a nuestra conveniencia.
                        Toast.makeText(context, "Usuario creado", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse == null) {
                            pb1.setVisibility(View.GONE); // Alternamos entre la visibilidad de la barra de progresión a nuestra conveniencia.
                            Toast.makeText(context, "No se pudo establecer la conexión", Toast.LENGTH_LONG).show();
                        } else {
                            pb1.setVisibility(View.GONE); // Alternamos entre la visibilidad de la barra de progresión a nuestra conveniencia.
                            int serverCode = error.networkResponse.statusCode;
                            Toast.makeText(context, "Estado de respuesta: " + serverCode, Toast.LENGTH_LONG).show();
                        }

                    }
                }
        );
        //añade la peticion hecha a la cola de peticiones.
        this.requestQueue.add(request);
    }
}