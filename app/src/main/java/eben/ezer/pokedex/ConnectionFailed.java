package eben.ezer.pokedex;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.Button;

public class ConnectionFailed extends AppCompatActivity {
    private Button refreshButton;
    private int pokemonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_failed);

        this.pokemonId = getIntent().getIntExtra("POKEMON_ID", -1);

        changStatusBarColor(getWindow(), getResources().getColor(R.color.white, null));
        this.refreshButton = (Button) findViewById(R.id.refresh_button);
        String previousActivity = getIntent().getStringExtra("previous_activity");

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (previousActivity != null) {
                        Class<?> previousClass = Class.forName(previousActivity);
                        Intent retryIntent = new Intent(ConnectionFailed.this, previousClass);
                        retryIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        if(pokemonId != -1)retryIntent.putExtra("POKEMON_ID", pokemonId);
                        startActivity(retryIntent);
                        finish();
                    } else {
                        Intent retryIntent = new Intent(ConnectionFailed.this, MainActivity.class);
                        retryIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(retryIntent);
                        finish();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    Intent retryIntent = new Intent(ConnectionFailed.this, MainActivity.class);
                    retryIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(retryIntent);
                    finish();
                }
            }
        });
    }

    public static void changStatusBarColor(Window window, int couleur) {
        window.setStatusBarColor(couleur);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                controller.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            }
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}