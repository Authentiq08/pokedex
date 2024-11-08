package eben.ezer.pokedex.model;

public class Pokemon {
    private String id;
    private String name;
    private String pic;

    public Pokemon(String id, String name) {
        this.id = id;
        this.name = name;
        this.pic = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/dream-world/"+this.id+".svg";

    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
