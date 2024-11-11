package eben.ezer.pokedex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PictureDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.caverock.androidsvg.SVG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import java.util.Iterator;

import eben.ezer.pokedex.customclass.ProgressBarAnimation;
import pl.droidsonroids.gif.GifImageView;

public class DetailActivity extends AppCompatActivity {
    private Context context;
    private int pokemonId;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private TextView mPokemonName;
    private ImageView mPokemonImage;
    private ImageView mBackToHome;
    private ImageView mPreviousItem;
    private ImageView mNextItem;
    private TextView mPokemonID;
    private LinearLayout mLayoutType;
    private TextView mPokemonWeight;
    private TextView mPokemonHeight;

    private LinearLayout mPokemonAbilitiesLayout;

    private LinearLayout mLayoutNavigation;
    private LinearLayout mStatsLayoutLoading;
    private LinearLayout mLayoutName;
    private LinearLayout mStatLayout;
    private TextView mPokemonDescription;

    private TextView mHPTextView;
    private TextView mATKTextView;
    private TextView mDEFTextView;
    private TextView mSATKTextView;
    private TextView mSDEFTextView;
    private TextView dSPDTextView;

    private TextView mHPTextFinal;
    private TextView mATKTextFinal;
    private TextView mDEFTextFinal;
    private TextView mSATKTextFinal;
    private TextView mSDEFTextFinal;
    private TextView dSPDTextFinal;

    private ProgressBar mHPProgressBar;
    private ProgressBar mATKProgressBar;
    private ProgressBar mDEFProgressBar;
    private ProgressBar mSATKProgressBar;
    private ProgressBar mSDEFProgressBar;
    private ProgressBar dSPDProgressBar;
    private ProgressBar mProgressImage;
    private ProgressBar mLoadingSpinnerStat;

    private ConstraintLayout detailMainlayout;

    private GifImageView waitingGif;

    private HashMap<String, Integer> colorMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_detail);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        changStatusBarColor(getWindow(), getResources().getColor(R.color.white, null));

        this.pokemonId = getIntent().getIntExtra("POKEMON_ID", -1);
        init();
        fetchData(pokemonId);
    }

    public void init(){
        this.context = DetailActivity.this;
        this.detailMainlayout = (ConstraintLayout) findViewById(R.id.detailMainLayout);
        this.colorMap= new HashMap<>();

        colorMap.put("normal", context.getResources().getColor(R.color.normal));
        colorMap.put("fire", context.getResources().getColor(R.color.fire));
        colorMap.put("water", context.getResources().getColor(R.color.water));
        colorMap.put("electric", context.getResources().getColor(R.color.electric));
        colorMap.put("grass", context.getResources().getColor(R.color.grass));
        colorMap.put("ice", context.getResources().getColor(R.color.ice));
        colorMap.put("fighting", context.getResources().getColor(R.color.fighting));
        colorMap.put("poison", context.getResources().getColor(R.color.poison));
        colorMap.put("ground", context.getResources().getColor(R.color.ground));
        colorMap.put("flying", context.getResources().getColor(R.color.flying));
        colorMap.put("psychic", context.getResources().getColor(R.color.psychic));
        colorMap.put("bug", context.getResources().getColor(R.color.bug));
        colorMap.put("rock", context.getResources().getColor(R.color.rock));
        colorMap.put("ghost", context.getResources().getColor(R.color.ghost));
        colorMap.put("dragon", context.getResources().getColor(R.color.dragon));
        colorMap.put("dark", context.getResources().getColor(R.color.dark));
        colorMap.put("steel", context.getResources().getColor(R.color.steel));
        colorMap.put("fairy", context.getResources().getColor(R.color.fairy));
        //colorMap.put("dark", context.getResources().getColor(R.color.dark));

        mBackToHome = (ImageView) findViewById(R.id.dBackToHome);
        mPreviousItem = (ImageView) findViewById(R.id.previousItem);
        mNextItem = (ImageView) findViewById(R.id.nextItem);
        mStatsLayoutLoading = (LinearLayout) findViewById(R.id.dStatsLayoutLoading);
        mProgressImage =  (ProgressBar)findViewById(R.id.loadingSpinner);
        mLoadingSpinnerStat = (ProgressBar)findViewById(R.id.loadingSpinnerStat);
        waitingGif = (GifImageView) findViewById(R.id.waitingGif);


        changAllColor(context.getResources().getColor(R.color.white));
        //Onclick Listener

        mBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPreviousItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousItem();
            }
        });

        mNextItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { nextItem();}
        });
    }

    public void previousItem(){
        if(this.pokemonId > 1){
            this.pokemonId --;
            disableNavigationButtons();
            clearAll();
            loadingStatDetail(mStatLayout, mStatsLayoutLoading, mProgressImage);
            fetchData(pokemonId);
        } else {
            showToast(getString(R.string.no_previous_pokemon));
        }
    }

    public void nextItem(){
        if(this.pokemonId < MainActivity.TOTAL_POKEMON){
            this.pokemonId ++;
            disableNavigationButtons();
            clearAll();
            loadingStatDetail(mStatLayout, mStatsLayoutLoading, mProgressImage);
            fetchData(pokemonId);
        } else {
            showToast(getString(R.string.no_next_pokemon));
        }
    }

    public void showToast(String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void drawSvg(ImageView imageView, ProgressBar progressBar) {
        // Afficher le spinner avant le chargement
        if(progressBar != null && progressBar.getVisibility() == View.GONE){
            progressBar.setVisibility(View.VISIBLE);
        }

        new Thread(() -> {
            try {
                // Charger l'URL du SVG
                URL url = new URL("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/dream-world/" + this.pokemonId + ".svg");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();

                // Utiliser AndroidSVG pour charger le fichier SVG
                SVG svg = SVG.getFromInputStream(inputStream);
                PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());

                // Mettre à jour l'ImageView et masquer le spinner sur le thread principal
                ((Activity) context).runOnUiThread(() -> {
                    // Cacher le spinner et afficher le drawable
                    if(progressBar != null)progressBar.setVisibility(View.GONE);
                    imageView.setImageDrawable(drawable);
                });

            } catch (Exception e) {
                e.printStackTrace();
                // Masquer le spinner en cas d'erreur
                ((Activity) context).runOnUiThread(() -> progressBar.setVisibility(View.GONE));
            }
        }).start();
    }


    public String getDescription (JSONObject dataSpecies) throws JSONException {
        JSONArray data = null;
        String desc = "";
        try {
            data = dataSpecies.getJSONArray("flavor_text_entries");
            for(int i = 0; i < data.length(); i++){
                if(data.getJSONObject(i).getJSONObject("language").getString("name").equals("en")){
                    desc = data.getJSONObject(i).getString("flavor_text").replaceAll("\f", " ").replace("\n", " ");
                    return desc;
                }
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }finally {
            return desc;
        }
    }

    public void fetchData(int pokemonId){

        final String pokeIDString = String.valueOf(pokemonId);
        requestQueue = Volley.newRequestQueue(context);

        Log.d("DEBUGP", "https://pokeapi.co/api/v2/pokemon/" + pokeIDString);

        stringRequest = new StringRequest(Request.Method.GET, "https://pokeapi.co/api/v2/pokemon/" + pokeIDString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    fetchDataSpecies(pokeIDString, jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //enableNavigationButtons();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //showToast("API call error");
                showConnectionFailed();
                enableNavigationButtons();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }
        };
        requestQueue.add(stringRequest);

    }

    public void fetchDataSpecies(String pokemonID, JSONObject jsonObject){
        requestQueue = Volley.newRequestQueue(context);

        stringRequest = new StringRequest(Request.Method.GET, "https://pokeapi.co/api/v2/pokemon-species/" + pokemonID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject dataSpecies = new JSONObject(response);
                    showData(jsonObject, dataSpecies);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    enableNavigationButtons();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //showToast("API call error");
                showConnectionFailed();
                enableNavigationButtons();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }
        };
        requestQueue.add(stringRequest);
    }

    public void showData(JSONObject data, JSONObject dataSpecies){

        mPokemonName =  (TextView)findViewById(R.id.dPokemonName);
        mPokemonID =  (TextView)findViewById(R.id.dPokemonID);
        mPokemonImage =  (ImageView)findViewById(R.id.dPokemonImage);
        mLayoutType =  (LinearLayout)findViewById(R.id.dLayoutType);
        mStatLayout = (LinearLayout)findViewById(R.id.dStatsLayout);
        mPokemonWeight =  (TextView)findViewById(R.id.dPokemonWeight);
        mPokemonHeight =  (TextView)findViewById(R.id.dPokemonHeight);
        mPokemonAbilitiesLayout =  (LinearLayout)findViewById(R.id.dPokemonAbilitiesLayout);
        mPokemonDescription =  (TextView)findViewById(R.id.dPokemonDescription);
        mHPProgressBar =  (ProgressBar)findViewById(R.id.dHPProgressBar);
        mATKProgressBar =  (ProgressBar)findViewById(R.id.dATKProgressBar);
        mDEFProgressBar =  (ProgressBar)findViewById(R.id.dDEFProgressBar);
        mSATKProgressBar =  (ProgressBar)findViewById(R.id.dSATKProgressBar);
        mSDEFProgressBar =  (ProgressBar)findViewById(R.id.dSDEFProgressBar);
        dSPDProgressBar =  (ProgressBar)findViewById(R.id.dSPDProgressBar);

        mHPTextView = (TextView)findViewById(R.id.dPokemonHP);
        mATKTextView = (TextView)findViewById(R.id.dPokemonATK);
        mDEFTextView = (TextView)findViewById(R.id.dPokemonDEF);
        mSATKTextView = (TextView)findViewById(R.id.dPokemonSATK);
        mSDEFTextView = (TextView)findViewById(R.id.dPokemonSDEF);
        dSPDTextView = (TextView)findViewById(R.id.dPokemonSPD);

        mLayoutNavigation = (LinearLayout) findViewById(R.id.dLayoutNavigation);
        mLayoutName= (LinearLayout) findViewById(R.id.dLayoutName);


        //Extracting data
        try {
            String pokemonName =  data.getString("name");
            int id = data.getInt("id");
            String pokemonID =  data.getString("id");

            double weight = data.getDouble("weight")/10;
            double height = data.getDouble("height")/10;

            String pokemonWeight =  String.valueOf(weight) + "kg";
            String pokemonHeight =  String.valueOf(height)+ "m";


            JSONArray typesArray = data.getJSONArray("types");
            String type = data.getJSONArray("types").getJSONObject(0).getJSONObject("type").getString("name");
            changeTypesBackgroundBorderColor(colorMap.get(type));


            for(int i = 0; i < typesArray.length(); i++){
                JSONObject js = typesArray.getJSONObject(i);
                TextView textView = new TextView(context);
                textView.setText(js.getJSONObject("type").getString("name"));
                textView.setTextColor(getResources().getColor(R.color.dark));
                //textView.setTextColor(getResources().getColor(R.color.white));

                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

                int marginInDp = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                params.setMargins(marginInDp, marginInDp, marginInDp, marginInDp);
                textView.setLayoutParams(params);

                textView.setBackground(ContextCompat.getDrawable(context, R.drawable.types_background));


                mLayoutType.addView(textView);
            }

            JSONArray abilitiesArray = data.getJSONArray("abilities");

            for(int i = 0; i < abilitiesArray.length(); i++){
                JSONObject js = abilitiesArray.getJSONObject(i);

                TextView textView = new TextView(context);
                textView.setText(js.getJSONObject("ability").getString("name"));
                textView.setTextColor(getResources().getColor(R.color.dark));
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

                int marginInDp = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                params.setMargins(marginInDp, marginInDp, marginInDp, marginInDp);
                textView.setLayoutParams(params);

                mPokemonAbilitiesLayout.addView(textView);
            }

            //affichage
            mPokemonName.setText(pokemonName);
            mPokemonID.setText("#" + pokemonID);
            drawSvg(mPokemonImage, mProgressImage);
            mPokemonWeight.setText(pokemonWeight);
            mPokemonHeight.setText(pokemonHeight);
            mPokemonDescription.setText(getDescription(dataSpecies));

            //Progress Bars
            //JSONObject pokemonStats = data.getJSONObject("stats");
            JSONArray statsArray = data.getJSONArray("stats");

            for(int i = 0; i < statsArray.length(); i++){
                switch (statsArray.getJSONObject(i).getJSONObject("stat").getString("name")){
                    case "hp":
                        ProgressBarAnimation animHp = new ProgressBarAnimation(mHPProgressBar, 0, (float)statsArray.getJSONObject(i).getDouble("base_stat"));
                        animHp.setDuration(1000);
                        mHPProgressBar.startAnimation(animHp);
                        mHPTextView.setText(String.format("%03d", statsArray.getJSONObject(i).getInt("base_stat")));
                        break;

                    case "attack":
                        ProgressBarAnimation animAttack = new ProgressBarAnimation(mATKProgressBar, 0, (float)statsArray.getJSONObject(i).getDouble("base_stat"));
                        animAttack.setDuration(1000);
                        mATKProgressBar.startAnimation(animAttack);
                        mATKTextView.setText(String.format("%03d", statsArray.getJSONObject(i).getInt("base_stat")));
                        break;

                    case "defense":
                        ProgressBarAnimation animDefense = new ProgressBarAnimation(mDEFProgressBar, 0, (float)statsArray.getJSONObject(i).getDouble("base_stat"));
                        animDefense.setDuration(1000);
                        mDEFProgressBar.startAnimation(animDefense);
                        mDEFTextView.setText(String.format("%03d", statsArray.getJSONObject(i).getInt("base_stat")));
                        break;

                    case "special-attack":
                        ProgressBarAnimation animSpecialAttack = new ProgressBarAnimation(mSATKProgressBar, 0, (float)statsArray.getJSONObject(i).getDouble("base_stat"));
                        animSpecialAttack.setDuration(1000);
                        mSATKProgressBar.startAnimation(animSpecialAttack);
                        mSATKTextView.setText(String.format("%03d", statsArray.getJSONObject(i).getInt("base_stat")));
                        break;

                    case "special-defense":
                        ProgressBarAnimation animSpecialDefense = new ProgressBarAnimation(mSDEFProgressBar, 0, (float)statsArray.getJSONObject(i).getDouble("base_stat"));
                        animSpecialDefense.setDuration(1000);
                        mSDEFProgressBar.startAnimation(animSpecialDefense);
                        mSDEFTextView.setText(String.format("%03d", statsArray.getJSONObject(i).getInt("base_stat")));
                        break;

                    case "speed":
                        ProgressBarAnimation animSpeed = new ProgressBarAnimation(dSPDProgressBar, 0, (float)statsArray.getJSONObject(i).getDouble("base_stat"));
                        animSpeed.setDuration(1000);
                        dSPDProgressBar.startAnimation(animSpeed);
                        dSPDTextView.setText(String.format("%03d", statsArray.getJSONObject(i).getInt("base_stat")));
                        break;
                }
            }

            mLayoutName.setVisibility(View.VISIBLE);

            mLayoutNavigation.setVisibility(View.VISIBLE);

            mStatLayout.setVisibility(View.VISIBLE);

            waitingGif.setVisibility(View.GONE);

            //SetColor
            if(colorMap.get(type) != null){
                changAllColor(colorMap.get(type));
            }else {
                changAllColor(context.getResources().getColor(R.color.normal));
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void changeBackgroundColor(int color) {
        LayerDrawable background = (LayerDrawable) getResources().getDrawable(R.drawable.detail_background_color);

        // Accéder au fond par son ID
        Drawable backgroundColorLayer = background.findDrawableByLayerId(R.id.background_color_item);

        // Appliquer la couleur spécifiée uniquement au fond
        backgroundColorLayer.setTint(color);

        detailMainlayout.setBackground(background);
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

    public void changeTextColor(int color){
        mHPTextFinal = (TextView) findViewById(R.id.dTextHP);
        mATKTextFinal = (TextView) findViewById(R.id.dTextATK);
        mDEFTextFinal = (TextView) findViewById(R.id.dTextDEF);
        mSATKTextFinal = (TextView) findViewById(R.id.dTextSATK);
        mSDEFTextFinal = (TextView) findViewById(R.id.dTextSDEF);
        dSPDTextFinal = (TextView) findViewById(R.id.dTextSPD);

        mHPTextFinal.setTextColor(color);
        mATKTextFinal.setTextColor(color);
        mDEFTextFinal.setTextColor(color);
        mSATKTextFinal.setTextColor(color);
        mSDEFTextFinal.setTextColor(color);
        dSPDTextFinal.setTextColor(color);
    }

    public void changeSpinnerColor(int color){
        if (mLoadingSpinnerStat != null) {
            mLoadingSpinnerStat.setIndeterminateTintList(ColorStateList.valueOf(color));
        }
    }


    private void changeTypesBackgroundBorderColor(int color) {
        GradientDrawable drawable = (GradientDrawable) getResources().getDrawable(R.drawable.types_background);

        int strokeWidth = (int) getResources().getDisplayMetrics().density * 2;
        drawable.setStroke(strokeWidth, color);
        //drawable.setColor(color);
    }

    public void changAllColor(int color){
        changeBackgroundColor(color);
        changStatusBarColor(getWindow(), color);
        changeTextColor(color);
        changeSpinnerColor(color);
    }

    public void clearAll(){
        if(mPokemonImage != null){
            mPokemonImage.setImageDrawable(null);
        }
        if(mPokemonAbilitiesLayout != null){
            mPokemonAbilitiesLayout.removeAllViews();
        }
        if(mLayoutType != null){
            mLayoutType.removeAllViews();
        }
    }

    public void loadingStatDetail(LinearLayout mStatLayout, LinearLayout mStatsLayoutLoading, ProgressBar progressBar){
        if(progressBar != null ){
            progressBar.setVisibility(View.VISIBLE);
        }
       if(mStatLayout != null)mStatLayout.setVisibility(View.GONE);
       if(mStatsLayoutLoading != null)mStatsLayoutLoading.setVisibility(View.VISIBLE);

    }

    private void disableNavigationButtons() {
        if(mPreviousItem != null){
            mPreviousItem.setEnabled(false);
        }
        if(mNextItem != null){
            mNextItem.setEnabled(false);
        }
    }

    private void enableNavigationButtons() {
        if(mPreviousItem != null){
            mPreviousItem.setEnabled(true);
        }
        if(mNextItem != null){
            mNextItem.setEnabled(true);
        }
    }

    public void showConnectionFailed(){
        Intent errorIntent = new Intent(DetailActivity.this, ConnectionFailed.class);
        errorIntent.putExtra("previous_activity", DetailActivity.class.getName());
        errorIntent.putExtra("POKEMON_ID", this.pokemonId);
        startActivity(errorIntent);
    }

}
