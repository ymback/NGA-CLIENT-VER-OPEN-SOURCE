package sp.phone.interfaces;

public interface OnNearbyLoadComplete {
    void onProgresUpdate(int progress, int total);

    void OnComplete(String result);
}
