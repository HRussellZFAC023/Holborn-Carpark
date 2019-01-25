import javafx.collections.ObservableList;

public interface  DataFetcherListener<T>{
    public void onDataFetched(ObservableList<T> updatedList);

}
