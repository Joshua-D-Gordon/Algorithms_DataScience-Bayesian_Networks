
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Ex1 {

    public static void main(String[] args){
            try {
                String path = "input.txt";//path
                readFile(path); //reading file path
            }catch(Exception e){
                e.printStackTrace();
            }
    }

    public static void readFile(String path) throws IOException{
        //creating scanner to read txt file line by line.
        Scanner sc = new Scanner(new File(path));
        //first line is XML file
        String xmlFilePath = sc.nextLine();
        //for creating graph with xml file.
        makeGraph g;
        //creating output file
        Writer wr = new FileWriter("output.txt");

        String currentLine, query;
        int functionToUse;
        while(sc.hasNextLine()){
            //make graph
            g = new makeGraph(xmlFilePath);
            //read next line
            currentLine = sc.nextLine();
            //save function to use
            String f = currentLine.substring(currentLine.length() - 1);
            functionToUse = Integer.parseInt(f);

            //save query
            query = currentLine.substring(0,currentLine.length()-2);

            //check if query exists in cpt tables of nodes in graph
            double p = hasQuery(g,query);
            if(p != -1){// p was diffrent from -1 meaning it exists
                //write the reslut on write file
                wr.write(p+",0,0"+"\r\n");
                continue; // continue in for loop to next line.
            }
            //query does not exist in table. use function {1,2,3} to resolve query
            double[] numsToWrite = new double[3];

            switch (functionToUse){
                case 1:
                    //use function 1
                    //jull join
                    makeGraph GraphduplicatedFone = new makeGraph(g);
                    numsToWrite = functionOne(query, GraphduplicatedFone);
                    wr.write(numsToWrite[0]+","+(int)numsToWrite[1]+","+(int)numsToWrite[2]+"\r\n");
                    break;

                case 2:
                    //use function 2
                    //variable elimination
                    makeGraph GraphduplicatedFtwo = new makeGraph(g);
                    numsToWrite = functionTwo(query, GraphduplicatedFtwo);
                    wr.write(numsToWrite[0]+","+(int)numsToWrite[1]+","+(int)numsToWrite[2]+"\r\n");
                    break;
                case 3:
                    //use function 3
                    //variable elimination choose order
                    //makeGraph GraphduplicatedFThree = new makeGraph(g);
                    //numsToWrite = functionThree(query, GraphduplicatedFThree);
                    break;

            }
        }

        wr.close();

    }
    public static double[] functionOne(String q, makeGraph G) {
        //make list of all graph node names
        G.makename(G.G);
        //make a CSV string of cpt names. | //P(E),P(B),P(A|E,B),P(J|A),P(M|A)
        String str = "";
        for (Nodes n : G.G) {
            str += n.cpt.name + ",";
        }
        str = str.substring(0, str.length() - 1);
        //P(E),P(B),P(A|E,B),P(J|A),P(M|A)

        //split qeury on | to get [[qeury part],[given pat]]
        String[] qeuryArr = q.split(Pattern.quote("|"));
        String qeuryevs = qeuryArr[1].substring(0, qeuryArr[1].length() - 1);
        String qeuryp = qeuryArr[0];

        //creating an array of names from - P(E),P(B),P(A|E,B),P(J|A),P(M|A)
        String[] names1 = str.split(",");
        //creating an array of evidences from - J=T,Q=F,F=v3 .. example
        String[] evs = qeuryevs.split(",");

        //if element in array of names is evidence then format it with its correct given outcome
        // example: if given E=T then for the array names P(E),P(B),P(A|E,B),P(J|A),P(M|A)
        // We produce:P(E=T),P(B),P(A|E=T,B),P(J|A),P(M|A)
        for (int i = 0; i < evs.length; i++) {
            String given = evs[i].substring(0, evs[i].indexOf("="));
            for (int j = 0; j < names1.length; j++) {
                if (names1[j].contains(given)) {
                    names1[j] = names1[j].replaceFirst(given, evs[i]);
                }
            }
        }
        //CONVERTING TO STRING TO FORMAT ARRAY, we later on want to split by CSV but as the queries themselves
        //contain "," we convert the commas we want to split on to "'" and leave the rest as ","
        str = "";
        for (String s : names1) {
            if (!s.contains(")")) {
                str += s+",";
            }
            else{
            str += s + "'";
            }
        }
        str = str.substring(0,str.length()-1);
        //spliting string on "'"
        String[] names = str.split("'");
        //creating array list of array string list to store list of outcomes from non evidence
        ArrayList<ArrayList<String>> arraysofq = new ArrayList<>();
        //array string list to add to arraysofq
        ArrayList<String> outcomes = new ArrayList<>();

        int size = 1;
        int count = 0;
        //looping of nodes in graph
        //adding q at the begining of names
        for(Nodes n: G.G){
            if(qeuryp.contains(n.name)){
                outcomes = n.outcomes;
                size*= n.outcomes.size();
                arraysofq.add(outcomes);
                count++;
            }
        }
        //adding non evs and non q to array of q
        for(Nodes n: G.G){
            //if node is not evidence
            if(!qeuryevs.contains(n.name)&&!qeuryp.contains(n.name)){
                //outcomes is assinged the value of the node outcomes
                outcomes = n.outcomes;
                //size of nested sums is multiplied by the amount of outcomes
                size*= n.outcomes.size();
                // outcomes is added to the array list of array string list to store list of outcomes from non evidence
                arraysofq.add(outcomes);
                count++;
            }
        }
        //ordering the non evidence variables - making the query variable first.
        String[] orders = new String[count];
        int ordersIndex = 1;
        for(Nodes n: G.G){
            if(!qeuryevs.contains(n.name)&&!qeuryp.contains(n.name)){
                orders[ordersIndex] = n.name;
                ordersIndex++;
            } else if (!qeuryevs.contains(n.name)&&qeuryp.contains(n.name)) {
                orders[0] = n.name;
            }

        }
        //creating two 2d array lists: first with outcomes of non evidence the second with the value of
        //the first but multiplied by the times we will need to use it
        //[[T,F],[T,F],[T,F]] && [[T,F,T,F,T,F,T,F],[T,F,T,F,T,F,T,F],[T,F,T,F,T,F,T,F] will give us
        //[[T, T, T, T, F, F, F, F], [T, T, F, F, T, T, F, F], [T, F, T, F, T, F, T, F]]
        ArrayList<ArrayList<String>> sizearr = new ArrayList<>();
        ArrayList<String> sizetoadd = new ArrayList<>();

        for(int i=0; i<arraysofq.size();i++){
            for(int j = 0; j<size/arraysofq.get(i).size(); j++){
                for(int k = 0; k<arraysofq.get(i).size();k++){
                    sizetoadd.add(arraysofq.get(i).get(k));
                }
            }
            sizearr.add(sizetoadd);
            sizetoadd = new ArrayList<>();
        }
        //merge will return [[T, T, T, T, F, F, F, F], [T, T, F, F, T, T, F, F], [T, F, T, F, T, F, T, F]]
        ArrayList<ArrayList<String>> merged = G.mergesort(sizearr,arraysofq);

        //We now have the following
        //names example == {P(E),P(B),P(A|E,B),P(J=T|A),P(M=F|A)}
        // merged example == [[T, T, T, T, F, F, F, F], [T, T, F, F, T, T, F, F], [T, F, T, F, T, F, T, F]]
        // orders example == [B,E,A]

        //counting multipels and adds, and variable helpers
        int countmults = 0;
        int countadds = 0;
        double ansmults = 1;
        double ansadds = 0;

        //making a list of the list of copys of names for each sum
        ArrayList<String[]> namesCopy = new ArrayList<>();
        for(int copys = 0; copys<merged.get(0).size();copys++){
            String[] copy = new String[names.length];
            int copyindex = 0;
            for(String copiedStr:names){
                copy[copyindex] = copiedStr;
                copyindex++;
            }
            namesCopy.add(copy);
        }
        //for the amount of sums we need to do
        for(int i = 0; i<merged.get(0).size();i++){
            //get first copy of names
            names = namesCopy.get(i);
            //for the amount of non evidences
            for(int j = 0; j< orders.length;j++) {
                //for the amount of names
                for (int k = 0; k < names.length; k++) {
                    //if names at current index includes non evidence
                    if(names[k].contains(orders[j])){
                        //split the names at current index on the non evidence variable
                        String[] data = names[k].split(orders[j]);
                        //insert the correct value and make names at index that value by order
                        //THIS IS IMPORTANT as we want our nested sum to sum over all non evidence with all outcomes
                        String mergedatatoadd = merged.get(j).get(i);
                        String appending = data[0] + orders[j]+"="+ mergedatatoadd +data[1];
                        names[k] = appending;
                    }
                }

            }

        }
        //finshed with names copies wich will hold all querys to nest sum over
        String namesizeofoutcomesofQarr = q.split(Pattern.quote("|"))[0];
        String namesizeofoutcomesofQ = namesizeofoutcomesofQarr.substring(namesizeofoutcomesofQarr.indexOf("(")+1,namesizeofoutcomesofQarr.indexOf("="));
        String qvalue = namesizeofoutcomesofQarr.substring(namesizeofoutcomesofQarr.indexOf("=")+1);

        int indexfornormaliztion = 0;
        int sizeofoutcomesofQ = 0;
        int indexofq = 0;
        ArrayList<String> toPrint = new ArrayList<>();
        //getting size of outcomes of qeurynode, and outcomes index.
        for(Nodes n: G.G){
            if(n.name.equals(namesizeofoutcomesofQ)){
                sizeofoutcomesofQ=n.outcomes.size();
                indexofq = n.outcomes.indexOf(qvalue);
                toPrint = n.outcomes;
            }
        }
        //final outcome normalized array initalized.
        double[] normarr = new double[sizeofoutcomesofQ];
        int k = 1;
        //looping over all strings in namesCopy - the list of string arrays containing all possible sum - overs.
        for(String[] strarr: namesCopy){
            //if finished summing over for one outcome of qeury insert the answer to normalized array and reset ansadds for culculating the next outcome variable of qeuery.
            if(indexfornormaliztion == (merged.get(0).size()*k)/(sizeofoutcomesofQ)){
                normarr[k-1] = ansadds;
                k++;
                ansadds = 0;
            }

            indexfornormaliztion++;
            //for each formated Qeury with its correct = sign, get the result and save to d.
            for(String formatedQeury:strarr){
                double d = hasQuery(G,formatedQeury);
                ansmults*=d;
                countmults++; //amount of mults is increased by one.
            }
            countmults-=1;//counting not the first mult
            ansadds+=ansmults; //adding the multiplied values
            countadds++; // adds increased by one
            ansmults = 1;
        }
        normarr[k-1] = ansadds;//adding the last adds
        countadds = countadds - 2;//exclude the first two sums as we want to count the + signs and not the digits added


        double denometanor = 0;// setting the denomenator for normalization
        for(double d: normarr){
            denometanor+=d;
            countadds +=1; // adds increased for normalizing
        }
        countadds--;// coutning + signs not digits added
        for(int i = 0; i<normarr.length;i++){
            normarr[i]/=denometanor; // normalizing
        }

        System.out.println("FUNCTION ONE ENDED ENDED");
        for(int i = 0; i<normarr.length;i++){
            System.out.println("the "+ toPrint.get(i) +" probablity is: "+normarr[i]);
        }
        System.out.println("amount of counts :" + countadds);
        System.out.println("amount of mults : " +countmults);

        double roundans = (double)Math.round(normarr[indexofq] * 100000d) / 100000d;
        System.out.println(roundans);

        double[] ret = {roundans,countadds,countmults};
        System.out.println("function one has ended");
        System.out.println("XX - need to write the return to output file");
        System.out.println();
        return ret;
    }

    public static makeGraph removeirelivantVariables(String q, makeGraph G){
        //make list of all graph node names
        G.makename(G.G);
        //make a CSV string of cpt names. | //P(E),P(B),P(A|E,B),P(J|A),P(M|A)
        String str = "";
        for (Nodes n : G.G) {
            str += n.cpt.name + ",";
        }
        str = str.substring(0, str.length() - 1);
        //P(E),P(B),P(A|E,B),P(J|A),P(M|A)

        //split qeury on | to get [[qeury part],[given pat]]
        String[] qeuryArr = q.split(Pattern.quote("|"));
        String qeuryevs = qeuryArr[1].substring(0, qeuryArr[1].length() - 1);
        String qeuryp = qeuryArr[0];

        //creating an array of names from - P(E),P(B),P(A|E,B),P(J|A),P(M|A)
        String[] names1 = str.split(",");
        //creating an array of evidences from - J=T,Q=F,F=v3 .. example
        String[] evs = qeuryevs.split(",");

        //if element in array of names is evidence then format it with its correct given outcome
        // example: if given E=T then for the array names P(E),P(B),P(A|E,B),P(J|A),P(M|A)
        // We produce:P(E=T),P(B),P(A|E=T,B),P(J|A),P(M|A)
        for (int i = 0; i < evs.length; i++) {
            String given = evs[i].substring(0, evs[i].indexOf("="));
            for (int j = 0; j < names1.length; j++) {
                if (names1[j].contains(given)) {
                    names1[j] = names1[j].replaceFirst(given, evs[i]);
                }
            }
        }
        //CONVERTING TO STRING TO FORMAT ARRAY, we later on want to split by CSV but as the queries themselves
        //contain "," we convert the commas we want to split on to "'" and leave the rest as ","
        str = "";
        for (String s : names1) {
            if (!s.contains(")")) {
                str += s+",";
            }
            else{
                str += s + "'";
            }
        }
        str = str.substring(0,str.length()-1);
        //spliting string on "'"
        String[] names = str.split("'");

        // example: if given E=T then for the array names P(E),P(B),P(A|E,B),P(J|A),P(M|A)
        // We produce:P(E=T),P(B),P(A|E=T,B),P(J|A),P(M|A)

        //if there is a node that is not the parent of evs node or query node then remove
        //evs
        //q Node
        ArrayList<ArrayList<Nodes>> toKeep = new ArrayList<>();
        makeGraph Gcopy = G;
        for(Nodes n: Gcopy.G){
            if(qeuryp.contains(n.name)){//the node is the query node
                ArrayList<Nodes> cleanQList = new ArrayList<>();
                ArrayList<Nodes> QParents = recursivefindparentsofevsorQ(n,cleanQList); // recursevily find parents of node
                QParents.add(n);
                toKeep.add(QParents);// add to nodes to keep
            }
            if(qeuryevs.contains(n.name)){// the node is an evidence node
                ArrayList<Nodes> cleanevsList = new ArrayList<>();
                ArrayList<Nodes> evParents = recursivefindparentsofevsorQ(n,cleanevsList); // recursevily find parents of node
                evParents.add(n);
                toKeep.add(evParents); // add to nodes to keep
            }
        }
        //loop over both lists of nodes to keep and add node once to new list of nodes to keep.
        //this is done as a ev node and q node could have the same parent and we dont want to double count.
        ArrayList<Nodes> nodestokeep = new ArrayList<>();
        for(Nodes n: Gcopy.G){
            for(ArrayList<Nodes> arrNodes :toKeep){
                for(Nodes node: arrNodes){
                    if(n.equals(node)&& !nodestokeep.contains(n)){
                        nodestokeep.add(n);
                    }
                }
            }
        }
        //making new graph and removing nodes not in the nodestokeep list
        makeGraph Gclone = new makeGraph(Gcopy);
        ArrayList<Integer> indexToRemoveNode = new ArrayList<>(); //saving index's to remove
        for(Nodes n: Gclone.G){
            if(!nodestokeep.contains(n)){
                indexToRemoveNode.add(Gclone.G.indexOf(n));
            }
        }

        for(int i = indexToRemoveNode.size()-1; i>=0;i--){
            int toremove = indexToRemoveNode.get(i);
            Gclone.G.remove(toremove); // removing the index's
        }


        //REMOVED ALL NODES THAT ARE ANSTESORS OF AN EVIDENCE NODE OR QUERY NODE

        return Gclone;
    }

    public static ArrayList<Nodes> recursivefindparentsofevsorQ(Nodes n, ArrayList<Nodes> evsList){
        //recursive stop case when node has no parents - return the list
        if(n.parents.size() == 0){
            return evsList;
        }
        //if the list dose not contain the parent - add the parent to list
        for(int i = 0; i<n.parents.size();i++){
            if(!evsList.contains(n.parents.get(i))){
                evsList.add(n.parents.get(i));
            }
        }
        //for all parents - get their parents and add to list
        for(int j = 0; j<n.parents.size();j++){
            recursivefindparentsofevsorQ(n.parents.get(j),evsList);
        }
        //return list
        return evsList;
    }

    public static makeGraph removeOnevaluedNodes(String q, makeGraph G){

        //split qeury on | to get [[qeury part],[given pat]]
        String[] qeuryArr = q.split(Pattern.quote("|"));
        String qeuryevs = qeuryArr[1].substring(0, qeuryArr[1].length() - 1);
        String qeuryp = qeuryArr[0];

        //array of index's to remove
        ArrayList<Integer> indextoremove = new ArrayList<>();
        makeGraph Gcopy = new makeGraph(G);
        for(Nodes n: Gcopy.G){

            if(qeuryevs.contains(n.name)&&n.parents.size()==0){// the node is an evidence node and has no parents
                //save index to remove this node
                indextoremove.add(G.G.indexOf(n));
            }
        }

        for(int index: indextoremove){
            Gcopy.G.remove(index);//remove nodes from graph
        }

        //REMOVING NODES THAT HAVE BEEN REMOVED FROM PARENTS LIST IN GRAPH
        //Adding NODE NAMES values THAT HAVE BEEN REMOVED to CPT NAME with value
        //REMOVING NODE NAMES THAT HAVE BEEN REMOVED FROM CPT NAME

        //name of nodes in the graph
        makeGraph Gclone = Gcopy;
        ArrayList<String> currentNames = new ArrayList<>();
        for(Nodes n: Gclone.G){
            currentNames.add(n.name);
        }

        //for nodes in the graph
        ArrayList<Integer> indexnodetoremove = new ArrayList<>();
        indextoremove = new ArrayList<>();
        for(Nodes n: Gclone.G){
                //for parents in current node
                for (Nodes p : n.parents) {
                    //if parents name not in graph
                    if (!currentNames.contains(p.name)) {
                        indexnodetoremove.add(Gclone.G.indexOf(n));//save index to remove
                        indextoremove.add(n.parents.indexOf(p.name));
                    }
                }

        }
        for(int i = 0; i<indexnodetoremove.size();i++) {
            Nodes n = Gclone.G.get(indexnodetoremove.get(i)); // remove
            n.parents.remove(indextoremove.get(i));
        }

        //remove from cpt name
        for(Nodes n: Gclone.G){
            String cptname = n.cpt.name;
            if(cptname.contains("|")){// has parents and could contain a parent value removed
                String[] cptnameArr = cptname.split(Pattern.quote("|"));
                String givenNames = cptnameArr[1].substring(0,cptnameArr[1].length()-1);
                // E,A,B ect..
                String replacecptName = cptnameArr[0]+"|";
                cptnameArr = givenNames.split(",");
                for(String st: cptnameArr){
                    if(currentNames.contains(st)){
                        replacecptName+=st+",";
                    }else{
                        String valueofremoved = "";
                        valueofremoved = qeuryp.substring(qeuryp.indexOf("="));
                        replacecptName+=st+valueofremoved+",";
                    }
                }
                replacecptName = replacecptName.substring(0,replacecptName.length()-1)+")";
                n.cpt.name = replacecptName;
            }
        }

        //removing from cpt tables
        //creating an array of evidences from - J=T,Q=F,F=v3 .. example
        String[] evs = qeuryevs.split(",");
        //lopping over the strings of evidence
        for(String st: evs){
            String givenStr = st.substring(0,st.indexOf("="));
            String outcomeOfGiven = st.substring(st.indexOf("=")+1);

            for(Nodes n: Gclone.G){
                CPTS cpt = n.cpt;
                if(cpt.header.contains(givenStr)){//the header contains evidence
                    int indexOfEv = cpt.header.indexOf(givenStr); // index in header and of dependencie table
                    //index's of torf table to remove
                    ArrayList<Integer> indexsToRemove = new ArrayList<>();
                    int index = 0;
                    for(String stringOfdep: cpt.dependeciesTable.get(indexOfEv)){
                        if(!stringOfdep.equals(outcomeOfGiven)){
                            indexsToRemove.add(index);
                        }
                        index++;
                    }
                    //header
                    cpt.header.remove(indexOfEv);//remove from header
                    int indexHead = 0;
                    //reformat strings in header
                    for(String headerStr: cpt.header){
                        if(headerStr.contains(givenStr)){
                            if(headerStr.contains(","+givenStr+",")){
                                cpt.header.set(indexHead,headerStr.replace(givenStr+",",""));
                            } else if (headerStr.contains(givenStr+",")) {
                                cpt.header.set(indexHead,headerStr.replace(givenStr+",",""));
                            } else if (headerStr.contains(","+givenStr)) {
                                String replace = headerStr.replace(","+givenStr,"");
                                cpt.header.set(indexHead,replace);
                            }
                        }
                        indexHead++;
                    }

                    //dependencie table
                    cpt.dependeciesTable.remove(indexOfEv);// remove the column of eveidence
                    for(int i = 0; i< indexsToRemove.size();i++){
                        int ind = indexsToRemove.get(indexsToRemove.size() - i -1);
                        for(ArrayList<String> arrst: cpt.dependeciesTable){
                            arrst.remove(ind);
                        }
                    }

                    //torf table
                    int indexfortorf = 0;

                    ArrayList<ArrayList<Double>> newtorf = new ArrayList<>(); // new truthtable
                    for(int i = 0; i<cpt.TorFarrandPs.length;i++){
                        if(indexfortorf==indexsToRemove.size()){
                            indexfortorf = 0;
                        }
                        ArrayList<Double> temp = new ArrayList<>();
                        for(double d: cpt.TorFarrandPs[i]){
                            temp.add(d);
                        }
                        if(i!=indexsToRemove.get(indexfortorf)){
                        newtorf.add(temp);
                        }else{
                            indexfortorf++;
                        }

                    }
                    //final toreplace truthtable.
                    double[][] toreplace = new double[cpt.TorFarrandPs.length - indexsToRemove.size()][cpt.TorFarrandPs[0].length];
                    for(int i = 0; i<toreplace.length;i++){
                        for(int j= 0 ; j< toreplace[0].length;j++){
                            toreplace[i][j] = newtorf.get(i).get(j);
                        }
                    }
                    cpt.TorFarrandPs = toreplace; // make cpt's truth table the new truth table

                }
            }
        }

        //DELETED COLUMNS REMOVED FROM CPT TABLE

        //removing single valued nodes
        int indextoremovesinglevalue = -1; //false
        int helper = 0;
        for(Nodes n: Gclone.G){
            if(n.cpt.header.size()==1&&n.cpt.TorFarrandPs[0].length==1){
                indextoremovesinglevalue=helper;
            }
            helper++;
        }
        if(indextoremovesinglevalue!=-1){ //if true
            Gclone.G.remove(indextoremovesinglevalue); //remove single valued node in graph
        }

        //return graph
        return Gclone;
    }

    public static CPTS joinFactors(ArrayList<CPTS> cptList, String chosenHideen, makeGraph G){
        //stop case for recursive function - if list contains only one cpt return the cpt
        if(cptList.size() == 1){
            return cptList.get(0);
        }
        //finding max rows for factor two and other for factor one
        int max =0;
        int indexmax = 0;
        int counterhelper = 0;
        for(CPTS cpts: cptList){
            if(cpts.TorFarrandPs.length>max){
                max = cpts.TorFarrandPs.length;
                indexmax=counterhelper;
            }
            counterhelper++;
        }

        int rowsizeOne = max;
        int rowsizeTwo = max;

        //getting first factors to join - factor one and two
        CPTS factorOne = cptList.get(indexmax), factorTwo = cptList.get(indexmax);
        //finding smallest factor rows and assigning factorOne to that factor
        for(CPTS cpt: cptList){
            if (cpt.TorFarrandPs.length < rowsizeOne) {
                rowsizeOne = cpt.TorFarrandPs.length;
                factorOne = cpt;
            } else if (cpt.TorFarrandPs.length <= rowsizeOne) {
                rowsizeOne = cpt.TorFarrandPs.length;
                factorOne = cpt;
            }
        }
        //finding smallest rows that is not already factorOne and assigning factorTwo to that factor
        for(CPTS cpt: cptList){
            if (cpt.TorFarrandPs.length < rowsizeTwo && !cpt.equals(factorOne)) {
                rowsizeTwo = cpt.TorFarrandPs.length;
                factorTwo = cpt;
            } else if (cpt.TorFarrandPs.length <= rowsizeTwo && !cpt.equals(factorOne)) {
                rowsizeTwo = cpt.TorFarrandPs.length;
                factorTwo = cpt;
            }
        }
        //joining factors
        //checking if complexjoin needed - // case A2, b2=t , b2=f && b2, b3, c2 = v1
        // else non-complex join
        boolean complex = false;
        String appenedf2 = "";
        for(int i = 0; i<factorTwo.header.size()-1;i++){
            appenedf2+=factorTwo.header.get(i);// making a string of factor two header
        }
        for(int i = 0; i<factorOne.header.size()-1;i++){
            if(!appenedf2.contains(factorOne.header.get(i))){ // if factor two header string dose not contain a factor one header element then this is a coplex join
                complex = true;
            }
        }
        if(complex){//its a complex join example - A, B, C, P(....) && A,B,D,P(......)
            int mults = 0;//mults to add at the end
            appenedf2 = "";
            /*
            making the new factor in the following order: common elements in both factors first, non common elements from factor
            one, then non common factor elemonets from factor two:

            Example: f1 - A, B, C && f2 - D, B, C = f3 - B, C, A, D.

             */
            // new header, new dependecie tables
            ArrayList<String> newhead = new ArrayList<>();
            ArrayList<String> tail = new ArrayList<>();
            ArrayList<ArrayList<String>> fordep = new ArrayList<>();
            ArrayList<ArrayList<String>> fordeptail = new ArrayList<>();
            ArrayList<Integer> indexsToRemovef1 = new ArrayList<>();
            ArrayList<Integer> indexsToRemovef2 = new ArrayList<>();

            for(int i = 0; i<factorTwo.header.size()-1;i++){
                appenedf2+=factorTwo.header.get(i);//getting factor two header elements
            }

            for(int i = 0; i<factorOne.header.size()-1;i++){
                if(!appenedf2.contains(factorOne.header.get(i))){//if factor one element is not a common element
                    tail.add(factorOne.header.get(i));//add to tail
                    fordeptail.add(factorOne.dependeciesTable.get(i));//add to dependencie tail
                }else{
                    newhead.add(factorOne.header.get(i)); // else its a common element
                    fordep.add(factorOne.dependeciesTable.get(i));//add to head
                    indexsToRemovef1.add(i);//save index
                }
            }
            for(int i = 0; i<factorTwo.header.size()-1;i++){
                if(!tail.contains(factorTwo.header.get(i))&&!newhead.contains(factorTwo.header.get(i))){//not a common element factor two
                    tail.add(factorTwo.header.get(i));//add to tail
                    fordeptail.add(factorTwo.dependeciesTable.get(i));// add deps to tail

                }else{
                    indexsToRemovef2.add(i);
                }
            }
            for(String s:tail){//adding tail elements to new header
                newhead.add(s);
            }
            for(ArrayList<String> s: fordeptail){//adding dependencie tail element to dependencies.
                fordep.add(s);
            }
            // cleaning dependecies for merge function
            ArrayList<ArrayList<String>> finaldeps = new ArrayList<>();
            for(int i = 0; i< fordep.size();i++){
                ArrayList<String> temp = new ArrayList<>();
                for(int j = 0; j< fordep.get(i).size();j++){
                    if(!temp.contains(fordep.get(i).get(j))){
                        temp.add(fordep.get(i).get(j));
                    }
                }
                finaldeps.add(temp);
            }
            int tomultby = 1;//length of dependecie table for merge function
            for(ArrayList<String> s: finaldeps){
                tomultby*=s.size();
            }
            //creating second list of dependeciesfor merge function
            ArrayList<ArrayList<String>> secondmergepart = new ArrayList<>();
            for(int i = 0; i< finaldeps.size();i++){
                ArrayList<String> temp = new ArrayList<>();
                for(int k = 0; k<tomultby/ finaldeps.get(i).size();k++){
                    for(int j = 0; j< finaldeps.get(i).size();j++){
                        temp.add(finaldeps.get(i).get(j));
                    }
                }
                secondmergepart.add(temp);
            }
            // getting new dependecie table from mergesort function
            fordep = G.mergesort(secondmergepart,finaldeps);
            //new truth-table for joined factors
            double[][] newtorf = new double[fordep.get(0).size()][1];

            String st="";
            String st2 = "";
            int indexhelper = 0;
            // CURRENTLY HAVE THE NEW HEADER, DEPENDENCIE TABLE AND EMPTY TRUTH-TABLE
            //filling new truth-table
            /*
            for each column in the NEW dependencie table make a row from the index's in the same line called st.
            example first st = T, T, T.
            for each column in factorone dependencie table make a row from the common index's in the same line called commonpartf1 one.
            st2 = commonpartf1 + non commons from header f1
            for each column in factortwo dependencie table make a row from the cmmon index's in the same line called commonpartf2
            if commonpartf1 = common part f2 we have a match
            add to st2 all the non commons from factor two
            if st2 = st we have a full match and multiply the correct values to the new truth table.

            this is done as we ordered the new dependencie table by: commons, non common f1, non commons f2.
             */
            for (int i = 0; i < fordep.get(0).size(); i++) {
                for (int j = 0; j < fordep.size(); j++) {
                    st += fordep.get(j).get(i);// string in new dependencie table first row
                }

                String commonpartf1 = "";
                String commonpartf2 = "";

                for (int k = 0; k < factorOne.dependeciesTable.get(0).size(); k++) {
                    for (int m = 0; m < indexsToRemovef1.size(); m++) {
                        commonpartf1 += factorOne.dependeciesTable.get(indexsToRemovef1.get(m)).get(k);//adding factor one deps table common cols
                    }//common part in factor1 header and factor 2 header
                    st2 = commonpartf1;
                    for (int m = 0; m < factorOne.dependeciesTable.size(); m++) {
                        if (!indexsToRemovef1.contains(m)) {
                            st2 += factorOne.dependeciesTable.get(m).get(k);//common part plus factorone non common part in order
                        }
                    }
                    String ancor = st2;//saved ancor for looping

                    for (int m = 0; m < factorTwo.dependeciesTable.get(0).size(); m++) {
                        commonpartf2="";
                        for (int u = 0; u < indexsToRemovef2.size(); u++) {
                            commonpartf2 += factorTwo.dependeciesTable.get(indexsToRemovef2.get(u)).get(m);//common part factor two
                        }

                        if (commonpartf2.equals(commonpartf1)) {//if same common parts

                            for (int g = 0; g < factorTwo.dependeciesTable.size(); g++) {
                                if (!indexsToRemovef2.contains(g)) {
                                    st2 += factorTwo.dependeciesTable.get(g).get(m);//common part plus factorone non common part plus factortwo non common part
                                }
                            }

                            if (st2.equals(st)) {//we have a full match;
                                newtorf[indexhelper][0] = factorOne.TorFarrandPs[k][0] * factorTwo.TorFarrandPs[m][0];
                                mults++;
                                indexhelper++;

                            }

                        }
                        st2=ancor;
                    }
                    commonpartf1 = "";
                }
                st = "";
            }

            newhead.add("P(....)");
            factorTwo.header = newhead;
            factorTwo.dependeciesTable = fordep;
            factorTwo.TorFarrandPs = newtorf;

            //adding mults to factor two, if factor one had mults add it aswell
            if(factorOne.multactioncount==0){
                factorTwo.multactioncount+=mults;
                factorTwo.addactioncount+=factorOne.addactioncount;
            }else{
                factorTwo.multactioncount+=factorOne.multactioncount;
                factorTwo.multactioncount+=mults;
                factorTwo.addactioncount+=factorOne.addactioncount;
            }
            System.out.println("current mults is :");
            System.out.println(factorTwo.multactioncount);
            //removing FACTORONE from cpt list and node from graph
            int indextoremove = 0;
            int indexhelpler = 0;
            for(Nodes n: G.G){
                if(n.cpt.equals(factorOne)){
                    indextoremove = indexhelpler;
                }
                indexhelpler++;
            }
            G.G.remove(indextoremove);

            int index = 0;
            for(int i = 0; i< cptList.size();i++){
                if(cptList.get(i).equals(factorOne)){
                    index = i;
                }
            }
            cptList.remove(index);
            //return joinfactors function with cpt list with factorone removed, choseen hidden and Graph
            return joinFactors(cptList,chosenHideen,G);


        }else{//normal join example = A, P(....) && E, B, A, P(...)
            int mults = 0;//mults to add later
            //eliminating non common elements from factor 2
            ArrayList<Integer> indexstokeepf2depstable = new ArrayList<>();
            for(int i = 0; i<factorOne.header.size()-1;i++){
                int index = factorTwo.header.indexOf(factorOne.header.get(i));
                indexstokeepf2depstable.add(index);
            }
            ArrayList<ArrayList<String>> factorTwodepsremoved = new ArrayList<>();
            for(int i: indexstokeepf2depstable){
                factorTwodepsremoved.add(factorTwo.dependeciesTable.get(i));
            }

            /*
            for dependecie table in factorone , make first row as the string = st;
            for dependecies in factortworemoved make a string of all elements = st2
            if st = st2 we have a match , and multiply the correct values to new truth-table
             */

            String st="", st2 = "";
            for (int i = 0; i < factorOne.dependeciesTable.get(0).size(); i++) {
                for (int j = 0; j < factorOne.dependeciesTable.size(); j++) {
                    st += factorOne.dependeciesTable.get(j).get(i);// row at factorone dependencie table
                }


                for (int k = 0; k < factorTwodepsremoved.get(0).size(); k++) {
                    for (int m = 0; m < factorTwodepsremoved.size(); m++) {
                        st2 += factorTwodepsremoved.get(m).get(k);// row at factortworemoved table
                    }

                    if (st2.equals(st)) {//we have a match
                            factorTwo.TorFarrandPs[k][0]*=factorOne.TorFarrandPs[i][0];//multiply the correct values to new truth-table
                            mults++;//mults increases by one
                    }

                    st2 = "";
                }

                    st = "";
            }

            //adding mults to factortwo, if factorOne had mults add it aswell.
            if(factorOne.multactioncount==0){
                factorTwo.multactioncount+=mults;
                factorTwo.addactioncount+=factorOne.addactioncount;
            }else{
                factorTwo.multactioncount+=factorOne.multactioncount;
                factorTwo.multactioncount+=mults;
                factorTwo.addactioncount+=factorOne.addactioncount;
            }

            //removing factorone from cptlist and from graph
            int indextoremove = 0;
            int indexhelpler = 0;
            for(Nodes n: G.G){
                if(n.cpt.equals(factorOne)){
                    indextoremove = indexhelpler;
                }
                indexhelpler++;
            }
            G.G.remove(indextoremove);

            int index = 0;
            for(int i = 0; i< cptList.size();i++){
                if(cptList.get(i).equals(factorOne)){
                    index = i;
                }
            }
            cptList.remove(index);
            //return join factors with new cptlist without factorone , choosen hidden and Graph
            return joinFactors(cptList,chosenHideen,G);

        }

    }

    public static CPTS eliminateFactor(CPTS cpt,String toeliminate, makeGraph G){
        int adds = 0;
        if(cpt.dependeciesTable.size()==1){//this is eliminating on itsself and should be removed next round by onevalued factors
            double[][] newtorf = new double[1][1];
            double sum = 0;
            for(double[] d: cpt.TorFarrandPs){
                for(double doub: d){
                    sum+= doub;
                    adds++;
                }
            }
            adds-=2;//dont want to count the digits but + signs
            newtorf[0][0] = sum;
            cpt.TorFarrandPs = newtorf;
            cpt.addactioncount+=adds;

            return cpt;
        }
        //adding addactioncount
        int addactioncount = adds;

        // elimination on B and cpt headeer is - example - [E,B, P(....)]
        int reducesizemult= 0;
        //finding size to reduce cpt table by
        int in = cpt.header.indexOf(toeliminate);
        ArrayList<String> helpercount = new ArrayList<>();
        for(int i = 0; i<cpt.dependeciesTable.get(in).size();i++){
            if(!helpercount.contains(cpt.dependeciesTable.get(in).get(i))){
                helpercount.add(cpt.dependeciesTable.get(in).get(i));
                reducesizemult++;//counting the amount of outcomes for hidden
            }
        }

        //making new dep table
        int mergefunctmult = 1;
        ArrayList<ArrayList<String>> formergefunc = new ArrayList<>();
        for(int i = 0; i<cpt.dependeciesTable.size();i++){
            ArrayList<String> temphelper = new ArrayList<>();
            for(int j = 0; j<cpt.dependeciesTable.get(0).size();j++){
                if(!temphelper.contains(cpt.dependeciesTable.get(i).get(j))){
                    temphelper.add(cpt.dependeciesTable.get(i).get(j));
                }
            }
            formergefunc.add(temphelper);
        }
        for(ArrayList<String> starr: formergefunc){
            mergefunctmult*=starr.size();
        }
        //reducing size and removing values at eliminated hidden
        mergefunctmult/=reducesizemult; //   = original size / sizeof outcomes of hidden
        formergefunc.remove(in);//remove at index of choosen hidden

        //making array list of array string lists for mergesort function
        ArrayList<ArrayList<String>> formergefuncsecondpart = new ArrayList<>();
        ArrayList<String> helper = new ArrayList<>();
        for(int i = 0; i<formergefunc.size();i++){
            for(int k = 0; k< mergefunctmult/formergefunc.get(i).size();k++) {
                for (int j = 0; j < formergefunc.get(i).size(); j++) {

                    helper.add(formergefunc.get(i).get(j));

                }
            }
            formergefuncsecondpart.add(helper);
            helper = new ArrayList<>();
        }
        //new dependecie table
        ArrayList<ArrayList<String>> depsmerged = G.mergesort(formergefuncsecondpart,formergefunc);

        addactioncount-=cpt.TorFarrandPs.length/reducesizemult; // dont want to count the first adds to the array
        //new truth table
        double[][] newtorftable = new double[cpt.TorFarrandPs.length/reducesizemult][cpt.TorFarrandPs[0].length];
        //removing hidden from header and dependencie table
        cpt.dependeciesTable.remove(cpt.header.indexOf(toeliminate));
        cpt.header.remove(toeliminate);
        /*
        for each row , make st = string of the row at each index of new dependencie table.
        for each row in original factor make st2 = string of the row at each index of original dependencie table.
        if st = st2 we have a match, insert to new truth table multiple of the correct values
         */
        String st = "";
        String st2 = "";
        double total = 0;
        int indexrunner = 0;

        for (int i = 0; i < depsmerged.get(0).size(); i++) {
            for (int j = 0; j < depsmerged.size(); j++) {
                st += depsmerged.get(j).get(i);
            }

            for (int k = 0; k < cpt.dependeciesTable.get(0).size(); k++) {
                for (int m = 0; m < cpt.dependeciesTable.size(); m++) {
                    st2 += cpt.dependeciesTable.get(m).get(k);
                }

                if (st2.equals(st)) {
                    total += cpt.TorFarrandPs[k][0];
                    addactioncount++;
                }
                st2 = "";
            }
            newtorftable[indexrunner][cpt.TorFarrandPs[0].length - 1] = total;
            total = 0;
            indexrunner++;
            st = "";
        }

        cpt.dependeciesTable = depsmerged;
        cpt.TorFarrandPs = newtorftable;
        cpt.addactioncount+= addactioncount;

        //return eliminated factor
        return cpt;


    }

    public static makeGraph initilizeEvidence(String q, makeGraph G){
        //arrString or qeary part
        String[] qeuryArr = q.split(Pattern.quote("|"));
        //string of the qeury part and eveidence part
        String qeuryevs = qeuryArr[1].substring(0, qeuryArr[1].length() - 1);
        //arrString of evidences
        String[] evs = qeuryevs.split(",");

        int evoutcomeindexheader = 0;
        int torfindextokeep= 0;
        int indexofnode = 0;

        for(String ev: evs){//for string in eveidences

            String name = ev.substring(0, ev.indexOf("="));//evidence name

            String evoutcome = ev.substring(ev.indexOf("=")+1);//outcome

            for(Nodes n: G.G){//for nodes in graph
                if(n.name.equals(name)){//this is an evidence node
                    int i = 0;
                    for(String st: n.cpt.header){
                        if(st.contains(evoutcome)){
                            evoutcomeindexheader = i;//index to insert outcome
                        }
                        i++;
                    }
                    if(n.cpt.dependeciesTable.size()==0){
                        torfindextokeep = evoutcomeindexheader;
                    }else {
                        torfindextokeep = evoutcomeindexheader - n.cpt.dependeciesTable.size();
                    }
                    indexofnode = G.G.indexOf(n);//indexof node
                }
            }
            //get the node
            Nodes n = G.G.get(indexofnode);
            //new header
            ArrayList<String> newhead = new ArrayList<>();
            for(int i = 0; i< n.cpt.dependeciesTable.size();i++){
                newhead.add(n.cpt.header.get(i));//adding to new header
            }
            for(int i = n.cpt.dependeciesTable.size(); i<n.cpt.header.size();i++){
                if(i==evoutcomeindexheader){
                    newhead.add(n.cpt.header.get(i));//adding to new header
                }
            }
            n.cpt.header = newhead;//changing node cpt header to new header
            //new truth-table
            double[][] newtorf = new double[n.cpt.TorFarrandPs.length][1];
            for(int i = 0; i<n.cpt.TorFarrandPs.length;i++){
                    for(int j = 0; j<n.cpt.TorFarrandPs[0].length;j++){
                        if(j==torfindextokeep){//if index is tokeep
                            newtorf[i][0]=n.cpt.TorFarrandPs[i][j]; //newtorf table add those values
                        }
                    }
                }
                n.cpt.TorFarrandPs = newtorf;
            }
        //return updated Graph with inisilized evidences
        return G;
    }

    public static makeGraph removeonevaluedfactor(makeGraph G){

        int indextoremove = -1;
        int indexhelper = 0;
        int multcounts = 0, addcounts = 0;
        //for nodes in graph
        for(Nodes n: G.G){
            if(n.cpt.TorFarrandPs.length==1&&n.cpt.TorFarrandPs[0].length==1){//if truth-table on valued
                indextoremove = indexhelper;//save index for removal
                multcounts = n.cpt.multactioncount;//save multcounts
                addcounts = n.cpt.addactioncount;//save addocunts
            }
            indexhelper++;
        }

        if(indextoremove!=-1){//if there is a index to remove
            G.G.remove(indextoremove); //remove the index
        }
        //Add the addds and mults to the first element in the graphs mults and counts, as we dont want to lose this info
        G.G.get(0).cpt.addactioncount+=addcounts;
        G.G.get(0).cpt.multactioncount+=multcounts;
        //return graph
        return G;
    }
    public static double[] functionTwo(String q, makeGraph G){
        //declaring Graph
        makeGraph Graph;
        Graph = new makeGraph(removeirelivantVariables(q,G));//remove irelivant variables
        Graph = initilizeEvidence(q,Graph);//initilize evidences
        removeOnevaluedNodes(q,Graph);//remove one value nodes function
        //making a list of hidden nodes
        String[] qeuryArr = q.split(Pattern.quote("|"));
        String qeuryevs = qeuryArr[1].substring(0, qeuryArr[1].length() - 1);//string of eviedence part of qeury
        String qeuryp = qeuryArr[0];//string of qeury part of qeury
        //saving index of out come of Q
        String nameofQ = qeuryp.substring(qeuryp.indexOf("(")+1,qeuryp.indexOf("="));//name of qeury
        String outcomeofQ = qeuryp.substring(qeuryp.indexOf("=")+1);//name of out come of query
        int indexforend = 0;//index for end of function
        //for nodes in graph
        for(Nodes n: G.G){
            if(n.name.equals(nameofQ)){//if the qeury node
                for(int i = 0; i< n.outcomes.size();i++){//for outcomes
                    if(n.outcomes.get(i).equals(outcomeofQ)){//if the outcome
                        indexforend = i;//save out come index
                    }
                }
            }
        }
        //new list of hiddens
        ArrayList<String> hiddens = new ArrayList<>();
        for(Nodes n: Graph.G){
            if(!qeuryevs.contains(n.name)&&!qeuryp.contains(n.name)){//this is a hidden node as its not Q or evidence
                hiddens.add(n.name);
            }
        }
        //Sorting for ABC
        Collections.sort(hiddens);
        //new factor graph = Current graph
        makeGraph factorgraph = new makeGraph(Graph);
        //format graph node cpts to factors
        factorgraph.factorformat(factorgraph);
        //while hiddens list is not emepty
        while(hiddens.size()>0){
            //choose the first hidden in list
            String choosenhiden = hiddens.get(0);
            //list of factors containing choosen
            ArrayList<CPTS> cptlist = new ArrayList<>();
            for(Nodes n: G.G){
                if(n.cpt.header.contains(choosenhiden)){//if factor header contains hidden add to list
                    cptlist.add(n.cpt);
                }
            }
            //newcpt = joined all factors containing hidden
            CPTS newcpt = joinFactors(cptlist,choosenhiden,G);
            //function eliminate out chosenHidden - returns the cpt eliminated on choosen hidden
            eliminateFactor(newcpt,choosenhiden,G);
            //remove one valued factors (if no one valued will return the same)
            removeonevaluedfactor(G);
            //remove choosenhidden from list of hiddens
            hiddens.remove(choosenhiden);

        }

        String formatedq  = qeuryp.substring(qeuryp.indexOf("(")+1,qeuryp.indexOf("="));//the qeury name
        CPTS finalcpt = null;//finall cpt

        //if graph size is bigger than one join all remaining factors
        if(G.G.size()>1){
            ArrayList<CPTS> cptlist = new ArrayList<>();//new factor list
            for(Nodes n : G.G){
                cptlist.add(n.cpt);//add all to list
            }
            finalcpt = joinFactors(cptlist,formatedq,G);//join all factors
        }else{//graph has one final factor
            finalcpt = G.G.get(0).cpt;//finalfactor = single graph node
        }
        //Arraylist of truth-table of finalfactor
        ArrayList<Double> one = new ArrayList<>();
        for (int i = 0; i < finalcpt.TorFarrandPs.length; i++) {
            for (int j = 0; j < finalcpt.TorFarrandPs[0].length; j++) {
                one.add(finalcpt.TorFarrandPs[i][j]);
            }
        }

        int normalizedadds = -1;//dont want to count the digits , rather the + signs
        double sum = 0;// denmenator for normalization
        for (int i = 0; i < one.size(); i++) {
            sum += one.get(i);//adding denominator for normalization
            normalizedadds++;//incressing normalized adds by 1
        }
        //final normalized truth-table
        ArrayList<Double> normalized = new ArrayList<>();
        //for all truth-table values
        for (int i = 0; i < one.size(); i++) {
            double ans = one.get(i);//the value
            ans = ans / sum;//the normalized value
            normalized.add(ans);//add the normalied value
        }

        finalcpt.addactioncount+=normalizedadds;//adding normalizing addcounts to addocunts
        System.out.println();
        System.out.println("FUNCTION 2 FINISHED");
        System.out.println(normalized);
        System.out.println("amount of mults is");
        System.out.println(finalcpt.multactioncount);
        System.out.println("amouts of adds is ");
        System.out.println(finalcpt.addactioncount);
        System.out.println("the probablity for"+ nameofQ + " " + "is");
        System.out.println(normalized.get(indexforend));
        System.out.println("FINSIHEDDDDDDDDDDDDDD FUNCTION TWO 22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222");
        System.out.println();
        System.out.println();
        double roundans = (double)Math.round(normalized.get(indexforend) * 100000d) / 100000d;

        double[] ret = {roundans,finalcpt.addactioncount,finalcpt.multactioncount};
        return ret;
    }

    public static ArrayList<String> sortedforfunctionThree(String q,ArrayList<String> hiddens, makeGraph G){
        //we want no complex joins


        return null;
    }


    public static double[] functionThree(String q, makeGraph G){
        //reorder cptlist for optimal outcome

        //SAME AS FUNCTION2

        //Sorting for ABC
        //Collections.sort(hiddens); // SORT HIDDENS NEW FUNCTION
        //NEED TO SORT FOR OPTIMAL HIDDENS

        //SAME AS FUNCTION2


        double[] ret = {0.0,0.0,0.0};
        return ret;
    }

    public static String[] formatQeury(String query){
        //cleaning query to match cpt header
        String queryCopy = query;//copy of qeury

        String[] formatQueryarr = queryCopy.split(Pattern.quote("|"));//spliting
        String evidencStr = formatQueryarr[1];//eviedence part
        String head = formatQueryarr[0]+"|";//qeury part
        String ev = "";
        String[] evidenceeqauls = evidencStr.split(",");

        for(String st: evidenceeqauls){
            String[] temp = st.split("=");
            head+=temp[0]+",";//evidence name
            ev+=temp[1]+",";//evidence outcome
        }
        ev = ev.substring(0,ev.length()-2);
        head = head.substring(0,head.length()-1)+")";

        String[] toReturn = {head,ev};//return formated header and eviedences

        return toReturn;
    }

    public static double hasQuery(makeGraph g, String query){
        //nodewithcpt of qeury inisilized with no match
        Nodes nodeWithCpt = new Nodes(null,"no match");
        //clean query to match cpt names
        if(!query.contains(Pattern.quote("|"))){//if no givens no parents
            for (int i = 0; i<g.G.size();i++) {
                if(g.G.get(i).cpt.header.contains(query)){
                    nodeWithCpt = g.G.get(i);//the node with qeury is found
                    String[] singlenode = query.split("=");//split
                    String formatted = singlenode[1].substring(0,singlenode[1].length()-1);//outcome of qeury

                    int indexOfnodewithnogives = nodeWithCpt.outcomes.indexOf(formatted);//index of outcome in truth-table

                    return nodeWithCpt.cpt.TorFarrandPs[0][indexOfnodewithnogives];//return the value at the index
                }
            }
        }
        //format query
        String[] formated = formatQeury(query);
        String formatedquery = formated[0];//formatted qeury

        boolean flag = false;//check if query matchs cpt name return -1 if no match
        for (int i = 0; i<g.G.size();i++) {
            if(g.G.get(i).cpt.header.contains(formatedquery)){
                nodeWithCpt = g.G.get(i);
                flag = true;//matchs
            }
        }
        if(flag == false){
            return -1;//no match return -1.0
        }

        int headerIndexP = nodeWithCpt.cpt.header.indexOf(formatedquery) - nodeWithCpt.parents.size();
        //array of eveidence values
        String evs = formated[1];
        //cpt of node with matching name dependencie table
        ArrayList<ArrayList<String>> cptArray= nodeWithCpt.cpt.dependeciesTable;

        for(int j = 0; j<cptArray.get(0).size();j++) {
            String compare = "";
            for (int i = 0; i < cptArray.size(); i++) {

                compare += cptArray.get(i).get(j) + ",";
            }
            compare = compare.substring(0, compare.length() - 1);//removing last comma
            if (compare.equals(evs)) {//if comparestring = evs
                return nodeWithCpt.cpt.TorFarrandPs[j][headerIndexP];//return the value at truth-table
            }
        }
        return -1;
    }

}


