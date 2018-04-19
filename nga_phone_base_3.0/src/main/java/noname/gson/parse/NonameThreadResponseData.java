package noname.gson.parse;

public class NonameThreadResponseData {
    public int page;
    public int totalpage;
    public int totalthreads;
    public NonameThreadBody[] threads;

    public void set_threads(NonameThreadBody[] threads) {
        this.threads = threads;
    }
}