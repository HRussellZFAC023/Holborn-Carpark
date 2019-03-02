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
    private BigDecimal amountInTicketMachine;
    private BigDecimal change;
    private boolean paid;
    private int discount;
    private boolean valid;
    private boolean paidOnReceived;
    private boolean receivedFromSmartcard;

    /**
     * A ticket constructor
     */
    public Ticket() {
        price = BigDecimal.ZERO;
        amountInTicketMachine = BigDecimal.ZERO;
        change = BigDecimal.ZERO;
        paid = false;
        discount = 0;
        paidOnReceived = false;
        valid = false;
        receivedFromSmartcard = false;
    }

    // getters and setters

    public boolean isReceivedFromSmartcard() {
        return receivedFromSmartcard;
    }

    public void setReceivedFromSmartcard(boolean receivedFromSmartcard) {
        this.receivedFromSmartcard = receivedFromSmartcard;
    }

    public boolean isPaidOnReceived() {
        return paidOnReceived;
    }

    public void setPaidOnReceived(boolean paidOnReceived) {
        this.paidOnReceived = paidOnReceived;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public BigDecimal getChange() {
        return change;
    }

    public void setChange(BigDecimal change) {
        this.change = change;
    }

    public int getDuration_paying_for() {
        return duration_paying_for;
    }

    public BigDecimal getAmountInTicketMachine() {
        return amountInTicketMachine;
    }

    public void setAmountInTicketMachine(BigDecimal amountInTicketMachine) {
        this.amountInTicketMachine = amountInTicketMachine;
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
                ", duration_paying_for=" + duration_paying_for +
                ", amountInTicketMachine=" + amountInTicketMachine +
                ", change=" + change +
                ", paid=" + paid +
                ", discount=" + discount +
                ", valid=" + valid +
                ", paidOnReceived=" + paidOnReceived +
                ", receivedFromSmartcard=" + receivedFromSmartcard +
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
