import java.util.ArrayList;
import java.util.Arrays;

public class CPTS {
    String name;
    ArrayList<String> header;
    double[][] TorFarrandPs;
    ArrayList<ArrayList<String>> dependeciesTable;
    int addactioncount = 0;
    int multactioncount = 0;

    /***
     * constructor for CPT
     * @param
     * @param TorFarrandPs
     */
    public CPTS(ArrayList<String> header,double TorFarrandPs[][] ){
        this.header = header;
        this.TorFarrandPs = TorFarrandPs;
        this.dependeciesTable = new ArrayList<ArrayList<String>>(0);
    }

    public CPTS(ArrayList<String> header, ArrayList<ArrayList<String>> dependensieTable, double TorFarrandPs[][]){
        this.header = header;
        this.dependeciesTable = dependensieTable;
        this.TorFarrandPs = TorFarrandPs;
    }

    public String toString(){
        return "Header: "+ this.header.toString() + "\n" + "dependencys: "+ this.dependeciesTable.toString() +"\n"+ Arrays.deepToString(this.TorFarrandPs);
    }

}
