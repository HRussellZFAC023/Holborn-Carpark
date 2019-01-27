package uk.co.holborn.carparkclient;

import javafx.collections.ObservableList;

public interface  DataFetcherListener<T>{
    public void onDataFetched(ObservableList<T> updatedList);

}
