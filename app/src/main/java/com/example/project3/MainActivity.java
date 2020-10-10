package com.example.project3;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project3.databinding.MainActivityBinding;
import com.example.project3.utils.SoftInputUtils;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private final int max_freeUser = 10;    //Maxima cantidad de saludos para el usuario free
    private final int max_char = 20;
    private int counter;                    //Cantidad de saludos actuales para el usuario
    private MainActivityBinding binding;    //Clase ViewBinding para el uso de variables con los atributos de las vistas de la aplicacion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Metodo generador de la actividad con todas sus vistas infladas
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater()); //Objeto tipo ViewBinding para obtener variables de cada vista definida
        setContentView(binding.getRoot());
        setupViews();
    }

    private void setupViews() {
        //Metodo para estructurar las vistas en la actividad cuando se generan. Uso un metodo por cada vista para reorganizar el codigo y para que quede mas limpio
        setupGreetCounter(0);
        setupSwitch();
        setupOptions();
        setupProgressBar();
        setupButton();
        setupTextInput();

        setInitialState();  //Para las vistas de CheckBox, Switch, RadioGroup, y para el icono y el contador de caracteres de los EditText, defino sus valores iniciales al iniciar la app
    }

    private void setInitialState() {
        //El input del nombre recibe el foco
        binding.mainUserNameInput.requestFocus();
        //Estado inicial del CheckBox
        setupCheckbox();
        //Estado inicial del icono
        setIconGender(binding.mainGenderButton1.getId());
        //Estado inicial de la barra de progreso
        setBarProgress(0);
        //Estado inicial del SwitchCompat
        binding.mainSwitch.setChecked(false);
        //Estado inicial del RadioGroup
        binding.mainGenderPickerContainer.clearCheck(); //Limpio las opciones marcadas previamente, aunque no sea necesario durante el inicio de la app
        binding.mainGenderPickerContainer.check(binding.mainGenderButton1.getId()); //Opcion seleccionada por defecto del genero
        //Estado inicial de los contadores de los EditText
        setCharCounter(binding.mainUserNameInput, max_char);
        setCharCounter(binding.mainUserLastNameInput, max_char);
    }

    private void setupTextInput() {
        //Listeners de los EditText para el cambio de color del texto que muestra los caracteres restantes cuando alguno de ellos recibe el foco
        binding.mainUserNameInput.setOnFocusChangeListener((view, hasFocus) -> changeTextColor(binding.mainNameCounter, hasFocus));     //Se crean clases anonimas que implementan la interfaz funcional OnChangeListener que contiene 1 solo metodo, por eso admite el uso de una lambda con una clase anonima
        binding.mainUserLastNameInput.setOnFocusChangeListener((view, hasFocus) -> changeTextColor(binding.mainLastNameCounter, hasFocus));
        //Listener de los EditText para la comprobacion del texto introducido
        binding.mainUserNameInput.addTextChangedListener(new TextWatcher() {    //Crea una clase anonima que iomplementa esta interfaz funcional con estos tres metodos. No se puede usar una lambda porque tiene más de 1 metodo.

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //No se usa
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setCharCounter(binding.mainUserNameInput, binding.mainUserNameInput.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkInput(binding.mainUserNameInput);
            }
        });
        //Lo mismo para el otro EditText para el apellido
        binding.mainUserLastNameInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //No se usa
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setCharCounter(binding.mainUserLastNameInput, binding.mainUserLastNameInput.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkInput(binding.mainUserLastNameInput);
            }
        });
        //Objeto Listener para el boton IME del teclado para que ejecute el mismo metodo que el del boton cuando se pulsa, para mostrar el saludo
        binding.mainUserLastNameInput.setOnEditorActionListener((v, actionId, event) -> {
            showGreet();
            return true;
        });
    }

    private void checkInput(@NotNull EditText input) {
        //Metodo que comprueba el valor que se ha introducido tanto para el nombre como para el apellido

        //Preguntar como compruebo si el contenido del input solo contiene espacios en blanco
        if (input.getText().toString().isEmpty()) {
            input.setError(getString(R.string.main_name_invalid));
        } else {
            input.setError(null);   //El mensaje se borra cuando el valor introducido es valido
        }
    }

    private void changeTextColor(TextView text, boolean hasFocus) {
        //Metodo para cambiar el color del texto contador de caracteres de los EditText cuando estos reciben el foco
        if (hasFocus) {
            text.setTextColor(getResources().getColor(R.color.colorAccent));    //Reciben el mismo color que las vistas cuando estas reciben el foco
        } else {
            text.setTextColor(getResources().getColor(R.color.colorNormal));    //Recupera el color normal cuando las vistas pierden el foco
        }
    }

    private void setupButton() {
        //Estado inicial del texto contador
        setGreetProgress(counter);
        //Objeto Listener para el boton
        binding.mainButton.setOnClickListener(view -> showGreet());
    }

    private void setupProgressBar() {
        //Estado inicial de la barra de progreso
        setBarProgress(counter);
        //Asigno el valor maximo de la barra de progreso
        setBarMax();
    }

    private void setBarMax() {
        //Metodo que asigna el valor maximo de la barra de progreso de los saludos
        binding.mainGreetProgressBar.setMax(max_freeUser);
    }

    private void setupOptions() {
        //Objeto Listener para cambiar el icono al cambiar cualquiera de las opciones de genero
        binding.mainGenderPickerContainer.setOnCheckedChangeListener((radioGroup, radioButton) -> setIconGender(radioButton));
    }

    private void setupCheckbox() {
        //Estado inicial del Checkbox
        binding.mainPoliteCheckbox.setChecked(false);
    }

    private void setupSwitch() {
        //Objeto Listener para el objeto switchCompat de la actividad
        binding.mainSwitch.setOnCheckedChangeListener((switchCompat, state) -> setPremiumLayout(state));
    }

    private void setupGreetCounter(int number) {
        //Metodo para asignar el valor actual del contador de los saludos
        counter = number;
    }

    private void setCharCounter(@NotNull EditText input, int number) {
        //Metodo para modificar la vista que uso como contador de caracteres de los EditText
        if (input.equals(binding.mainUserNameInput)) {
            binding.mainNameCounter.setText(getResources().getQuantityString(R.plurals.input_charCounter, max_char - input.getText().length(), max_char - input.getText().length()));   //Contador del input del nombre
        } else if (input.equals(binding.mainUserLastNameInput)) {
            binding.mainLastNameCounter.setText(getResources().getQuantityString(R.plurals.input_charCounter, max_char - input.getText().length(), max_char - input.getText().length()));   //Contador del input del apellido
        }
    }

    private void setPremiumLayout(boolean state) {
        //Metodo que define las vistas de la actividad.
        if (state) {
            //Si el usuario ha activado la version premium se resetea el contador del boton y el progreso de la barra
            //y desaparece la barra y la frase con el contador del boton
            counter = 0;
            binding.mainGreetProgressBar.setVisibility(View.GONE);
            binding.mainButtonCounter.setVisibility(View.GONE);
            setBarProgress(counter);
            setGreetProgress(counter);
        } else {
            //Si no, se muestra todas las vistas de forma normal
            binding.mainGreetProgressBar.setVisibility(View.VISIBLE);
            binding.mainButtonCounter.setVisibility(View.VISIBLE);
        }
    }

    private void setGreetProgress(int counter) {
        //Metodo para mostrar la frase de progreso con el contador de los saludos para el usuario Free
        binding.mainButtonCounter.setText(getString(R.string.main_button_counter, counter, max_freeUser));;
    }

    private void setIconGender(int selectedGenderOption) {
        //Metodo para seleccionar el Id del icono segun la opcion seleccionada
        if (selectedGenderOption == binding.mainGenderButton1.getId()) {
            binding.mainIconGender.setImageResource(R.drawable.ic_mr);
        } else if (selectedGenderOption == binding.mainGenderButton2.getId()) {
            binding.mainIconGender.setImageResource(R.drawable.ic_mrs);
        } else {    //Obligatoriamente si no se da el caso de la primera ni de la segunda opcion, la unica opcion posible restante es la tercera
            binding.mainIconGender.setImageResource(R.drawable.ic_ms);
        }
    }

    private void setBarProgress(int number) {
        //Asigno al progreso de la barra con el valor recibido por parametro
        binding.mainGreetProgressBar.setProgress(number);
    }

    private void showGreet() {
        //Metodo que recibe las opciones seleccionadas del usuario para mostrar el saludo
        if (!binding.mainUserNameInput.getText().toString().isEmpty() && !binding.mainUserLastNameInput.getText().toString().isEmpty()) {
            //Si el usuario no ha introducido nada en su nombre o en su apellido, o solo ha introducido espacios en blanco en cualquiera de los dos, la aplicacion no hace nada
            if (binding.mainSwitch.isChecked()) {
                //Muestro el saludo en la vista final del layout para el usuario premium
                setGreet(getGender(), binding.mainPoliteCheckbox.isChecked());
            } else {
                //Aumento el contador de los saludos usados
                counter++;
            }
            //Analizo si el contador ha llegado a su limite de saludos
            if (counter > max_freeUser) {
                //Oculto el teclado antes de mostrar el mensaje
                SoftInputUtils.hideSoftKeyboard(binding.mainButton);
                //Si el contador ha superado el limite se pide al usuario que active la version premium
                showTrialEnd();
            } else {
                //Se muestra la barra y la frase con el valor del contador actual
                setBarProgress(counter);
                setGreetProgress(counter);
                //Oculto el teclado antes de mostrar el mensaje por pantalla
                SoftInputUtils.hideSoftKeyboard(binding.mainButton);    //Este metodo recibe una vista para recibir tambien el contexto de la actividad
                setGreet(getGender(), binding.mainPoliteCheckbox.isChecked());
            }
        } else {
            //En el caso de que el valor de alguno de los dos input (o ambos) no sea valido, dicha vista recibe el foco
            //Se comprueba uno a uno
            if (binding.mainUserNameInput.getText().toString().isEmpty()) {
                binding.mainUserNameInput.requestFocus();
            } else {
                binding.mainUserLastNameInput.requestFocus();
            }
        }
    }

    @NotNull
    private String getGender(){
        //Metodo para obtener una cadena con el genero seleccionado de entre las opciones

        //Recojo el id de la opcion seleccionada en el RadioGroup y la comparo con las opciones disponibles
        if (binding.mainGenderPickerContainer.getCheckedRadioButtonId() == binding.mainGenderButton1.getId()) {
            return binding.mainGenderButton1.getText().toString();
        } else if (binding.mainGenderPickerContainer.getCheckedRadioButtonId() == binding.mainGenderButton2.getId()) {
            return binding.mainGenderButton2.getText().toString();
        } else {    //Si no es ni la primera ni la segunda opcion, por obligacion solo queda la tercera
            return binding.mainGenderButton3.getText().toString();
        }
    }

    private void showTrialEnd() {
        //Metodo que muestra por pantalla la indicacion al usuario para que actualice la suscripcion premium
        //binding.mainTextResult.setText(R.string.main_trialEnd);
        Toast.makeText(this, R.string.main_trialEnd, Toast.LENGTH_SHORT).show();    //Se muestra con un mensaje emergente
    }

    private void setGreet(String gender, boolean polite) {
        //Metodo que devuelve el saludo dependiendo de si se ha pedido un saludo formal y de la opcion elegida para el genero
        String greet;
        if (polite) {
            greet = "Good morning " + gender + " " + binding.mainUserNameInput.getText().toString() + " " + binding.mainUserLastNameInput.getText().toString() + ". Pleased to meet you.";
        } else {
            greet = "Hello " + binding.mainUserNameInput.getText().toString() + " " + binding.mainUserLastNameInput.getText().toString() + ". What's up?";
        }

        //binding.mainTextResult.setText(greet);
        Toast.makeText(this, greet, Toast.LENGTH_SHORT).show(); //Muestro el saludo con un mensaje emergente despues de ocultar el teclado
    }
}