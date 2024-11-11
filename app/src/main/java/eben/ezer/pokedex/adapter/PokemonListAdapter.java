package eben.ezer.pokedex.adapter;

import android.content.Context;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import eben.ezer.pokedex.R;
import eben.ezer.pokedex.interfacex.RecyclerViewInterface;
import eben.ezer.pokedex.model.Pokemon;

import java.net.URL;
import java.io.InputStream;
import java.net.HttpURLConnection;
import com.caverock.androidsvg.SVG;
import android.graphics.drawable.PictureDrawable;


public class PokemonListAdapter extends RecyclerView.Adapter<PokemonListAdapter.PokemonListAdapterHolder>{
    private final RecyclerViewInterface mRecyclerViewInterface;

    private List<Pokemon> mPokemonList;
    private final Context mContext;

    // Constructeur pour passer la liste de Pokémon et le contexte
    public PokemonListAdapter(List<Pokemon> pokemonList, Context context, RecyclerViewInterface recyclerViewInterface) {
        this.mPokemonList = pokemonList;
        this.mContext = context;
        this.mRecyclerViewInterface = recyclerViewInterface;
    }

    public List<Pokemon> getPokemonList() {
        return mPokemonList;
    }


    public void setFilteredList(List<Pokemon> filteredList){
        this.mPokemonList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PokemonListAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pokemon_item, parent, false);
        return new PokemonListAdapterHolder(view, mRecyclerViewInterface, this);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonListAdapterHolder holder, int position) {
        holder.pImageView.setImageDrawable(null);
        holder.pLoadingSpin.setVisibility(View.VISIBLE);

        // Capturer la position actuelle
        int currentPosition = position;
        Pokemon currentPokemon = mPokemonList.get(currentPosition);

        holder.pId.setText("#"+currentPokemon.getId());
        holder.pName.setText("#"+currentPokemon.getName());



        new Thread(() -> {

            try {
                // Charger l'URL du SVG
                URL url = new URL((currentPokemon.getPic()));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();

                // Utiliser AndroidSVG pour charger le fichier SVG
                SVG svg = SVG.getFromInputStream(inputStream);
                PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());

                // Mettre à jour l'ImageView sur le thread principal
                ((Activity) mContext).runOnUiThread(() -> {
                    if (holder.getAdapterPosition() == currentPosition) {
                        holder.pLoadingSpin.setVisibility(View.GONE);
                        holder.pImageView.setImageDrawable(drawable);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    @Override
    public int getItemCount() {
        return mPokemonList.size();
    }

    public static class PokemonListAdapterHolder extends RecyclerView.ViewHolder{

        private TextView pId, pName;
        private ImageView pImageView;
        private ProgressBar pLoadingSpin;
        private PokemonListAdapter adapter;


        public PokemonListAdapterHolder(@NonNull View itemView, RecyclerViewInterface mRecyclerViewInterface, PokemonListAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            pId = itemView.findViewById(R.id.pokemonId);
            pImageView = itemView.findViewById(R.id.pokemonImage);
            pName = itemView.findViewById(R.id.pokemonName);
            pLoadingSpin = itemView.findViewById(R.id.LloadingSpinner);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mRecyclerViewInterface != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            int pokemonId = Integer.parseInt(adapter.getPokemonList().get(position).getId());
                            mRecyclerViewInterface.onItemClick(pokemonId);
                        }
                    }
                }
            });

        }
    }
}
