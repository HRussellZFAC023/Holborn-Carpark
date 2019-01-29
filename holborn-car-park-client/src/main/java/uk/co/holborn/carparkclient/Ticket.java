package uk.co.holborn.carparkclient;

import java.util.Date;

public class Ticket {
    private String _id;
    private Date dateIn;
    private Double price;

    public Ticket(String _id, Date dateIn, Double price) {
        this._id = _id;
        this.dateIn = dateIn;
        this.price = price;
    }
}
