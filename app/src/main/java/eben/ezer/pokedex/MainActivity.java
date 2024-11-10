package eben.ezer.pokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
//import android.widget.SearchView;
import androidx.appcompat.widget.SearchView;

import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eben.ezer.pokedex.adapter.PokemonListAdapter;
import eben.ezer.pokedex.interfacex.RecyclerViewInterface;
import eben.ezer.pokedex.model.Pokemon;


public class MainActivity extends AppCompatActivity implements RecyclerViewInterface {

   public static final int TOTAL_POKEMON = 400;
   private RecyclerView recyclerView;
   private PokemonListAdapter adapter;
   private SearchView searchView;
   private Context context;
   private RequestQueue requestQueue;
   private List<Pokemon> pokemonList = new ArrayList<>();
   private StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        requestJsonData();
        changStatusBarColor(getWindow(), getResources().getColor(R.color.redBack, null));
        //customNavigationBar(getWindow());
        //changNavigationBarColor(getWindow(), Color.RED);

    }

    public void init(){
        this.recyclerView = findViewById(R.id.pokemonRecyclerView);
        this.context = MainActivity.this;
        this.searchView = findViewById(R.id.mSearchView);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

    }

    public void requestJsonData(){
        requestQueue = Volley.newRequestQueue(context);
        stringRequest = new StringRequest(Request.Method.GET, "https://pokeapi.co/api/v2/pokemon?limit="+ String.valueOf(MainActivity.TOTAL_POKEMON), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //Log.d("onResponse:", response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    fetchTheData(jsonArray);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //showToast("API call error");

                Intent errorIntent = new Intent(MainActivity.this, ConnectionFailed.class);
                errorIntent.putExtra("previous_activity", MainActivity.class.getName());
                startActivity(errorIntent);

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }
        };
        requestQueue.add(stringRequest);
    }

    private void fetchTheData(JSONArray jsonArray) throws JSONException {
        for (int i =0; i< jsonArray.length(); i++){
            try {
                JSONObject pokemon = jsonArray.getJSONObject(i);
                //System.out.println("Heyo");
                //System.out.println(pokemon);
                String pokemonID = pokemon.getString("url").split("/")[6];
                pokemonList.add(new Pokemon(pokemonID, pokemon.getString("name")));
            }catch(Exception e){
                e.printStackTrace();
                //showToast("Pokemon Detail Error");
            }
        }

        adapter = new PokemonListAdapter(pokemonList, context, this);
        // Utiliser un GridLayoutManager avec 2 colonnes
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        //recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    public void showToast(String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void changStatusBarColor(Window window, int couleur) {
        window.setStatusBarColor(couleur);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                controller.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            }
        } else {
            // Pour les anciennes versions
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public static void changNavigationBarColor(Window window, int couleur) {
        window.setNavigationBarColor(couleur);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                controller.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
            }
        } else {
            // Pour les anciennes versions
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    public static void customNavigationBar(Window window) {
        // Rendre la barre de navigation transparente
        window.setNavigationBarColor(android.graphics.Color.TRANSPARENT);

        // Cacher la barre de navigation (mode immersif)
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // API 30 (Android 11) et plus
            window.setDecorFitsSystemWindows(false);
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            // Pour les versions Android inférieures à 11
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        }*/
    }

    private void filterList(String text) {
        List<Pokemon> filteredList = new ArrayList<>();
        for(Pokemon pokemon : pokemonList){
            if(pokemon.getName().toLowerCase().contains(text.toLowerCase()) ||
                    String.valueOf(pokemon.getId()).contains(text)){
                filteredList.add(pokemon);
            }
        }

        if(filteredList.isEmpty()){
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }else{
            adapter.setFilteredList(filteredList);
        }
    }


    @Override
    public void onItemClick(int pokemonID) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("POKEMON_ID", pokemonID);
        startActivity(intent);
    }
}