
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class makeGraph {
    ArrayList<Nodes> G;//Array lists of nodes
    boolean hasMadeQeury;//has made qeury
    String qeury;//qeury

    public String format(String str){
        return str.replaceAll("    ", "");//if not formated correctly xml file, 5 spaces instead of tab
    }

    public void makename(ArrayList<Nodes> Graph){
        //for nodes in graph
        for(Nodes n:Graph){
           String st ="P(";
           if(n.parents.size()==0){//if no parents
               n.cpt.name = st+n.name+")";//make name like P(A)
           } else {//else has parents
               st+=n.name+"|";
               for(int i = 0; i< n.parents.size();i++){//add parent+,
                   String name = n.parents.get(i).name;
                   st = st + name+",";
               }
               st=st.substring(0,st.length()-1)+")";//remove last comma
               n.cpt.name = st;//make name like P(A|B,E,V)
           }
        }
    }

    public makeGraph factorformat(makeGraph G){
        //for node in graph
        for(Nodes n:G.G){
            //making new factor formats for cpts
            //new header
            ArrayList<String> newhead = new ArrayList<>();
            //new dependencie table
            ArrayList<ArrayList<String>> newdeps;
            //new truth-table
            double[][] newtorf = new double[n.cpt.TorFarrandPs[0].length*n.cpt.TorFarrandPs.length][1];

            String torfheaderpart = "";
            String sub;
            ArrayList<String> torfdepspart = new ArrayList<>();

            for(int i = 0; i<n.cpt.header.size();i++){
                if(!n.cpt.header.get(i).contains("=")){
                    newhead.add(n.cpt.header.get(i));//adding to new head elements without = sign
                }else{//has an = sign
                    if(n.cpt.header.get(i).contains("|")){//if contains given
                        sub = n.cpt.header.get(i).substring(n.cpt.header.get(i).indexOf("=")+1,n.cpt.header.get(i).indexOf("|"));
                    }else{//no given sign
                        sub = n.cpt.header.get(i).substring(n.cpt.header.get(i).indexOf("=")+1,n.cpt.header.get(i).indexOf(")"));
                    }
                    torfdepspart.add(sub);//addsubstring
                    torfheaderpart = n.cpt.header.get(i).substring(n.cpt.header.get(i).indexOf("(")+1,n.cpt.header.get(i).indexOf("="));//add to string header part
                }
            }
            newhead.add(torfheaderpart);//beggining of header part : A, B, E
            newhead.add("P("+torfheaderpart+")");//end of header part P(E|A,B,F)
            //finished making factor header

            //making new truth-table table from old truth-table
            int indexhelper = 0;
            for(int i = 0; i<n.cpt.TorFarrandPs.length;i++){
                for(int j = 0; j<n.cpt.TorFarrandPs[0].length;j++){
                    newtorf[indexhelper][0] = n.cpt.TorFarrandPs[i][j];
                    indexhelper++;
                }
            }
            // finished making factor t or false table formatted correctly

            //making new dependencie table for formated factor.
            //first arraylist for mergesort function
            ArrayList<ArrayList<String>> formergefunc = new ArrayList<>();
            for(int i = 0; i<n.cpt.dependeciesTable.size();i++){
                ArrayList<String> temp = new ArrayList<>();
                for(int j = 0; j<n.cpt.dependeciesTable.get(i).size();j++){
                    if(!temp.contains(n.cpt.dependeciesTable.get(i).get(j))){
                        temp.add(n.cpt.dependeciesTable.get(i).get(j));
                    }
                }
                formergefunc.add(temp);//adding all beggining of header values dependencies
            }
            formergefunc.add(torfdepspart);//adding end of header values dependecies

            //if size is 1 remove from list before mergesort function (evidence will have only one value)
            ArrayList<Integer> toremove = new ArrayList<>();//arraylist of index's to remove
            indexhelper = 0;
            for(ArrayList<String> s: formergefunc){
                if(s.size() == 1){
                    toremove.add(indexhelper);//remove index added to list
                }
                indexhelper++;
            }
            for(int i: toremove){
                formergefunc.remove(i);//remove
                newhead.remove(i);//remove from new header
            }
            //getting sizes for mergesort function
            int size = 1;
            for(ArrayList<String> s: formergefunc){
                size*=s.size();
            }
            //for mergesort function secondpart array list
            ArrayList<ArrayList<String>> formergesecondpart = new ArrayList<>();

            for(int i = 0; i< formergefunc.size();i++) {
                ArrayList<String> temp = new ArrayList<>();
                for (int k = 0; k < size / formergefunc.get(i).size(); k++) {
                    for (int j = 0; j < formergefunc.get(i).size(); j++) {
                        temp.add(formergefunc.get(i).get(j));
                    }
                }
                formergesecondpart.add(temp);//adding to mformergefunctionsecondpart

            }
            //finall new dependencie table from mergesort funiton
            newdeps =  G.mergesort(formergesecondpart,formergefunc);
            //changing cpt: header, dependencie and table, truth-table to new header, dependencie and table
            n.cpt.header = newhead;
            n.cpt.dependeciesTable = newdeps;
            n.cpt.TorFarrandPs = newtorf;

        }

        //return formatted cpts to factors Graph
        return G;
    }

    public ArrayList<ArrayList<String>> mergesort(ArrayList<ArrayList<String>> arr, ArrayList<ArrayList<String>> outcomesArr) {
        //2d string array sorted to return
        ArrayList<ArrayList<String>> toreturn = new ArrayList<>();
        //adds a 2d array of outcomes per col
        ArrayList<ArrayList<String>> outcomes = outcomesArr;
        //outcome example: [[T,F],[T,F],[v1,v2,v3]]
        //arr exmaple = 12: [[T,F,T,F,T,F,T,F,T,F,T,F],[T,F,T,F,T,F,T,F,T,F,T,F],[v1,v2,v3,v1,v2,v3,v1,v2,v3,v1,v2,v3]]
        //toreturn example: [[T,T,T,T,T,T,F,F,F,F,F,F],[T,T,T,F,F,F,T,T,T,F,F,F],[v1,v2,v3,v1,v2,v3,v1,v2,v3,v1,v2,v3]]

        int u = 1;
        //for outcomes
        for(int i = 0; i< outcomes.size();i++){
            //new array string list
            ArrayList<String> sorted = new ArrayList<>();
            //for outcome size
            for(int j = 0; j<outcomes.get(i).size();j++){
                //copy = finaldependencie column size / outcomes of that variable
                int copys = arr.get(0).size()/outcomes.get(i).size();
                copys/=u;
                //for copys amount of times
                for(int t = 0; t<copys; t++){
                    sorted.add(outcomes.get(i).get(j));//add outcomes
                }
            }
            toreturn.add(sorted);
            u*=outcomes.get(i).size();
        }

        //current toReturn cols example: 12: [[T,T,T,T,T,T,F,F,F,F,F,F],[T,T,T,F,F,F],[v1,v2,v3]]
        //Fixing to: [[T,T,T,T,T,T,F,F,F,F,F,F],[T,T,T,F,F,F,T,T,T,F,F,F],[v1,v2,v3,v1,v2,v3,v1,v2,v3,v1,v2,v3]]

        ArrayList<ArrayList<String>> cumsum = new ArrayList<>();
        int count = 1;
        int outcomeIndex = 0;
        //for arrya in current to return
        for(ArrayList<String> strarr: toreturn){
            //new array string list
            ArrayList<String> cumsumStr = new ArrayList<>();
            for(int i = 0; i<count; i++){
                for(String st: strarr){
                    cumsumStr.add(st);//add to cumsumStr
                }
            }
            count*=outcomes.get(outcomeIndex).size();//count = the next amount for next variable
            outcomeIndex++;//next index in outcomes
            cumsum.add(cumsumStr);//adding cumsumStr to cumsum
        }
        //returns ordered new dependencie table
        return cumsum;
    }

    public makeGraph(makeGraph Gcopy){
        this.G = Gcopy.G;
        this.hasMadeQeury = Gcopy.hasMadeQeury;
        this.qeury = Gcopy.qeury;
    }

    public makeGraph(String xmlFile) throws FileNotFoundException {
        //Making graph form xml file
        File file = new File(xmlFile);

        Scanner sc = new Scanner(file);

        String line;

        //array of linked nodes - our graph
        ArrayList<Nodes> arrNodenames = new ArrayList<>();

        //ancor points in xml document
        String outcome = "<OUTCOME>",variableOpen = "<VARIABLE>",variableClose = "</VARIABLE>", name = "<NAME>", def = "<DEFINITION>", forInDef = "<FOR>", table = "<TABLE>", defEnd = "</DEFINITION>", given = "<GIVEN>";
        String nodeName = "";
        int cptCount = 0;
        ArrayList<String> outcomes= new ArrayList<>();
        while(sc.hasNextLine()){
            //new line scanned
            line = format(sc.nextLine());

            //if line contains Variable opening tag
            if(line.contains(variableOpen)) {//while section of variable not closed
                while (!line.contains(variableClose)) {
                    //next line
                    line = format(sc.nextLine());
                    //if line contains name
                    if (line.contains(name)) {
                        // new Arraylist of outcomes for node
                        outcomes= new ArrayList<>();
                        //value of Str at <"Name">Str<"</NAME">
                        String subName = line;
                        subName = subName.substring(name.length()+1, subName.length()-name.length()-1);
                        nodeName = subName;

                        //outcomes for Node
                    } else if (line.contains(outcome)) {//if line contains outcome
                        String subOutcome;
                        subOutcome = line.substring(outcome.length()+1,line.length()-outcome.length()-1);
                        // adds outcome to outcomes array
                        outcomes.add(subOutcome);
                    }
                }
                //finished Variable adds nodes to array
                arrNodenames.add(new Nodes(nodeName, outcomes));
            }

            //finished node names and outcomes , starting to get data for cpts

            if(line.contains(def)) {
                //value of char at <"FOR">Str<"</FOR">
                String ch = " ";
                String cptName = "P(";
                double doubleChar = 0;
                ArrayList<String> parentNodeName = new ArrayList<>();
                ArrayList<Integer> asciiNames = new ArrayList<>();
                boolean hasParents = false;
                int parentsCount = 0;
                //while definition nested xml not ended
                while(!line.contains(defEnd)){
                    line = format(sc.nextLine());
                    //value of char at <"FOR">Str<"</FOR">
                    if(line.contains(forInDef)){
                        String subName;
                        subName = line.substring(forInDef.length()+1, line.length()-forInDef.length()-1);
                        ch = subName;
                        //adds char to cptName string = P(Char|
                        cptName+=ch+"|";

                    }
                    //if node has a parent
                    if(line.contains(given)){
                        hasParents = true;
                        parentsCount++;
                        ch = line.substring(given.length()+1,line.length()-given.length()-1);
                        for (Nodes n : arrNodenames) {
                            if (n.name.equals(ch)) {
                                arrNodenames.get(cptCount).parents.add(n);
                            }
                        }

                        String parentSubName;
                        parentSubName = line.substring(given.length()+1,line.length()-given.length()-1);
                        //adds parent to cptName string = P(X| Parent
                        cptName+=parentSubName+",";

                    } else if (line.contains(table) && hasParents == false) { // node has no parents
                        //cpt name changed to = P(X)
                        cptName = cptName.substring(0,cptName.length()-2);
                        cptName+=")";
                        //substring of line without <table> and <table/>
                        if(line.contains("</TABLE>")){

                            line = line.substring(table.length()+1,line.length()-table.length()-1);

                        } else if (!line.contains("</TABLE>")) {
                            String templine = line.substring(table.length()+1);
                            line = format(sc.nextLine());
                            while(!line.contains("</TABEL>")){
                                templine+= line;
                                line = format(sc.nextLine());
                            }
                            templine+= line.substring(0,line.length()-table.length()-1);
                            line = templine;
                        }

                        //array of strings of the numbers split but spacebar
                        String[] nums = line.split(" ");
                        //the numbers in double format
                        double[] TorFarrP = new double[nums.length];
                        for(int i = 0; i< nums.length; i++){
                            String tempnum = nums[i];
                            TorFarrP[i] = Double.parseDouble(tempnum);
                        }
                        //data structure for cpt of nodes with no parents
                        int rows = 1, cols;
                        cols = arrNodenames.get(cptCount).outcomes.size();
                        double[][] tableStructureCPT = new double[rows][cols];
                        //insert table to cpt double structure
                        for(int i = 0; i<cols; i++){
                            tableStructureCPT[0][i] = TorFarrP[i];
                        }
                        //make header
                        ArrayList<String> header = new ArrayList<>();
                        String outcomeName, ps = "P(";
                        for(int i = 0; i<cols; i++){
                            outcomeName = arrNodenames.get(cptCount).outcomes.get(i);
                            ps+=arrNodenames.get(cptCount).name+"="+outcomeName+")";
                            header.add(ps);
                            ps="P(";
                        }

                        CPTS cpt = new CPTS(header, tableStructureCPT);
                        arrNodenames.get(cptCount).cpt = cpt;


                    }else if (line.contains(table) && hasParents == true){ // node has parents
                        //cpt name changed to P(X| Parent1 Parent2 ...)
                        cptName = cptName.substring(0,cptName.length()-1);
                        cptName+=")";

                        //make header
                        ArrayList<String> header = new ArrayList<>();
                        String outcomeName, ps = "P(";
                        //dependencies looks like E,A,B
                        String dependencies = "";

                        int rows = 1;
                        int colsDependencies = 0;

                        for(int i = 0; i<arrNodenames.get(cptCount).parents.size();i++){
                            String parentName = arrNodenames.get(cptCount).parents.get(i).name;
                            header.add(parentName);
                            dependencies+=parentName+",";
                            rows*= arrNodenames.get(cptCount).parents.get(i).outcomes.size();
                            colsDependencies++;
                        }
                        //dependencies string = E,A,B
                        dependencies = dependencies.substring(0, dependencies.length()-1);

                        ArrayList<String> subHeaderdeps = header;
                        int cols = arrNodenames.get(cptCount).outcomes.size();
                        for(int i = 0; i<cols; i++){
                            outcomeName = arrNodenames.get(cptCount).outcomes.get(i);
                            ps+=arrNodenames.get(cptCount).name+"="+outcomeName+"|"+dependencies+")";
                            header.add(ps);
                            ps="P(";
                        }

                        //data structure dependencies for cpt of nodes with parents
                        //rows = multipiclatiom of all outcomes of parents
                        //cols = dependencies;
                        //ArrayList<ArrayList<String>> dependenciesTableCPT = new ArrayList<ArrayList<String>>();
                        //insert dependencies
                        /*
                        [[a2 b0 **P(c=T) P(c=F)],
                         [T  v1 **  0.05  0.95 ],
                         [F  v1 **  ...    ... ],
                         [T  v2 **  ...    ... ],
                         [F  v2 **  ...    ... ],
                         [T  v3 **  ...    ... ],
                         [F  v3 **  ...    ... ]]
                         */
                        ArrayList<ArrayList<String>> dependscols = new ArrayList<>();
                        ArrayList<ArrayList<String>> outcomesArr = new ArrayList<>();
                        for(String str: subHeaderdeps){
                            for(Nodes n: arrNodenames){
                                if(n.name.equals(str)){
                                    ArrayList<ArrayList<String>> beforeProssced = new ArrayList<>();
                                    outcomesArr.add(n.outcomes);
                                    for(int i = 0;i<rows/n.outcomes.size();i++){
                                        beforeProssced.add(n.outcomes);
                                    }
                                    ArrayList<String> afterProssced = new ArrayList<>();
                                    for(ArrayList<String> arrStr: beforeProssced){
                                        for(String string: arrStr){
                                            afterProssced.add(string);
                                        }
                                    }
                                    dependscols.add(afterProssced);
                                }
                            }
                        }
                        //new dependencie table
                        dependscols = mergesort(dependscols,outcomesArr);

                        //insert values to cpt table double structure
                        //current node outcome sizes: P(E=0|...),P(E=1|...),P(E=2|...) = 3 , P(E=T|...),P(E=F|...) = 2
                        int currentNodeOutcomes = arrNodenames.get(cptCount).outcomes.size();
                        double[][] dataStructureCPT = new double[rows][currentNodeOutcomes];
                        //insert table data to structure


                        //substring of line without <table> and <table/>
                        if(line.contains("</TABLE>")){
                            line = line.substring(table.length()+1,line.length()-table.length()-1);
                        } else if (!line.contains("</TABLE>")) {
                            String templine = line.substring(table.length()+1);
                            line = format(sc.nextLine());
                            while(!line.contains("</TABEL>")){
                                templine+= line;
                                line = format(sc.nextLine());
                            }
                            templine+= line.substring(0,line.length()-table.length()-1);
                            line = templine;
                        }

                        //array of strings of the numbers split but spacebar
                        String[] nums = line.split(" ");


                        int numsIndex = 0;
                        for(int i = 0; i< rows;i++){
                            for(int j=0; j< currentNodeOutcomes; j++){
                                dataStructureCPT[i][j] = Double.parseDouble(nums[numsIndex]);
                                numsIndex++;
                            }
                        }

                        CPTS cpt = new CPTS(header,dependscols ,dataStructureCPT);
                        arrNodenames.get(cptCount).cpt = cpt;

                    }


                }
                //next node in graph array
                cptCount++;

            }
        }

        this.G = arrNodenames;
        this.hasMadeQeury = false;
        this.qeury = "";
    }


}
