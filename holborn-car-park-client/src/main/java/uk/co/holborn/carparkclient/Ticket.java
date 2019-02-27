package uk.co.holborn.carparkclient;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Class simulating a ticket object
 *
 * @author Vlad Alboiu
 * @version 1.0
 */
public class Ticket {
    private String _id;
    private Date date_in;
    private Date date_out;
    private BigDecimal price;
    private double duration;
    private int duration_paying_for;
    private BigDecimal amountPaid;

    public Ticket() {
        price = BigDecimal.ZERO;
        amountPaid = BigDecimal.ZERO;
    }

    public int getDuration_paying_for() {
        return duration_paying_for;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public void setDuration_paying_for(int duration_paying_for) {
        this.duration_paying_for = duration_paying_for;
    }

    public Ticket(String _id, Date date_in, BigDecimal price) {
        this._id = _id;
        this.date_in = date_in;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "_id='" + _id + '\'' +
                ", date_in=" + date_in +
                ", date_out=" + date_out +
                ", price=" + price +
                ", duration=" + duration +
                '}';
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Date getDate_in() {
        return date_in;
    }

    public void setDate_in(Date date_in) {
        this.date_in = date_in;
    }

    public Date getDate_out() {
        return date_out;
    }

    public void setDate_out(Date date_check_out) {
        this.date_out = date_check_out;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}
