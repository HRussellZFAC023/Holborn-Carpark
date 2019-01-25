package uk.co.holborn.carparkclient;

import javafx.collections.ObservableList;

import java.util.ArrayList;

public class DataFetcher<T> {
    ObservableList<T> observableList;
    ArrayList<DataFetcherListener<T>> listeners;
    String url;
    int delayMs;
    boolean interruptFetching = false;
    boolean forceFetched = false;
    Runnable fetchingRunnable = new Runnable() {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if (interruptFetching) Thread.currentThread().interrupt();
                try {
                    if(!forceFetched)fetched(observableList);
                    else{
                        forceFetched = false;
                    }
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    };
    Thread fetchThread = new Thread(fetchingRunnable);

    public DataFetcher(int delayMs){
    this.delayMs = delayMs;
    listeners = new ArrayList<>();
    }

    public void startFetching(){
        interruptFetching = false;
        fetchThread.setDaemon(true);
        fetchThread.start();
    }
    public void interruptFetching(){
    interruptFetching = true;
    }
    public void setDelay(int delayMs){
         this.delayMs = delayMs;
    }
    public void forceFetch(){

        fetched(observableList);
    }
    public void addListener(DataFetcherListener<T> listener){
        listeners.add(listener);
    }
    public void fetched(ObservableList<T> list){
        for (int i = 0; i < listeners.size() ; i++) {
            listeners.get(i).onDataFetched(list);
        }
    }
}
