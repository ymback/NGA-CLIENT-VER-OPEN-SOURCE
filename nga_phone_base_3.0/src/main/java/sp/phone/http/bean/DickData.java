package sp.phone.http.bean;

public class DickData {
    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int gettId() {
        return tId;
    }

    public void settId(int tId) {
        this.tId = tId;
    }

    public int getpId() {
        return pId;
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public int getSeedOffset() {
        return seedOffset;
    }

    public void setSeedOffset(int seedOffset) {
        this.seedOffset = seedOffset;
    }

    public double getRndSeed() {
        return rndSeed;
    }

    public void setRndSeed(double rndSeed) {
        this.rndSeed = rndSeed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArgsId() {
        return argsId;
    }

    public void setArgsId(String argsId) {
        this.argsId = argsId;
    }

    public double getSeed(){
        return seed;
    }

    public void setSeed(double seed){
        this.seed = seed;
    }

    private String txt;
    private int authorId;
    private int tId;
    private int pId;
    private int seedOffset;
    private double rndSeed;
    private String id;
    private String argsId;
    private double seed;
}