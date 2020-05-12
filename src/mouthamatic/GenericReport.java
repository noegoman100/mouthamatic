package mouthamatic;

public class GenericReport {
    public String addHeader(){

        return new String("addHeader");
    }
    public String addFooter(){

        return new String("addFooter");
    }
    public String addGuts(){

        return new String("addGuts");
    }
    public String addAll(){

        return addHeader() + addGuts() + addFooter();
    }

}
