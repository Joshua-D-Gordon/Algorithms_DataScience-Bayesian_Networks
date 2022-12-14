import java.util.ArrayList;

public class Nodes {
        CPTS cpt;
        ArrayList<Nodes> parents = new ArrayList<Nodes>();
        String name;

        ArrayList<String> outcomes = new ArrayList<>();


        public Nodes(String name, ArrayList<String> outcomes){
               this.name = name;
               this.outcomes = outcomes;
        }

        public Nodes(CPTS cpt, String name){
                this.cpt = cpt;
                this.name = name;
        }

        public String toString(){
                return "name of node: " + this.name + " outcomes: " + this.outcomes.toString();
        }



}
