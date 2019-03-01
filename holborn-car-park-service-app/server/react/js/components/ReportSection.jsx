import React, { Component } from 'react'
import { Bar } from 'react-chartjs-2';
import { Scrollbars } from 'react-custom-scrollbars';


class ReportSection extends Component {
    constructor(props) {
        super(props);

        this.state = {
            startDate:  props.startDate,
            endDate:    props.endDate,
            tickets:    props.tickets,
            carpark:    props.carpark,
            username:   props.username
        };

        this.day = 24 * 60 * 60 * 1000;
        this.visibleHeight = window.innerHeight - document.getElementById('nav-bar').scrollHeight - document.getElementById('report-settings').scrollHeight - 50;

        this.graphSlots = [];
        this.populateDays = (start_d, end_d) => {
            this.graphSlots = [];
            let all = [];

            let current = start_d.getTime();

            while(current <= end_d.getTime()){
                if(new Date(current).getDate() > 9 && (new Date(current).getMonth() + 1) > 9) {
                    all.push(new Date(current).getDate() + "/" + (new Date(current).getMonth() + 1));
                }
                else if(new Date(current).getDate() <= 9 && (new Date(current).getMonth() + 1) > 9){
                    all.push("0" + new Date(current).getDate() + "/" + (new Date(current).getMonth() + 1));
                }
                else if(new Date(current).getDate() > 9 && (new Date(current).getMonth() + 1) <= 9){
                    all.push(new Date(current).getDate() + "/" + "0" + (new Date(current).getMonth() + 1));
                }
                else if(new Date(current).getDate() <= 9 && (new Date(current).getMonth() + 1) <= 9){
                    all.push("0" + new Date(current).getDate() + "/" + "0" + (new Date(current).getMonth() + 1));
                }
                current += 7 * this.day;
                this.graphSlots.push(current);

            }

            if(end_d.getTime() > current) {
                if (end_d.getDate() > 9 && end_d.getMonth() > 9) {
                    all.push(end_d.getDate() + "/" + (end_d.getMonth() + 1));
                }
                else if (end_d.getDate() <= 9 && end_d.getMonth() > 9) {
                    all.push("0" + end_d.getDate() + "/" + (end_d.getMonth() + 1));
                }
                else if (end_d.getDate() > 9 && end_d.getMonth() <= 9) {
                    all.push(end_d.getDate() + "/" + "0" + (end_d.getMonth() + 1));
                }
                else if (end_d.getDate() <= 9 && end_d.getMonth() <= 9) {
                    all.push("0" + end_d.getDate() + "/" + "0" + (end_d.getMonth() + 1));
                }
                this.graphSlots.push(end_d.getTime());
            }

            return all;
        };

        this.ticketsPerWeek = () => {
            let all = new Array (10).fill(0);

            for(let i = 0; i < this.state.tickets.length; ++i){
                for(let j = 0; j < this.graphSlots.length; ++j) {
                    if (new Date(this.state.tickets[i].date_in).getTime() <= this.graphSlots[j]) {
                        all[j]++;
                        break;
                    }
                }
            }

            return all;
        };

        this.formatDate = (date) => {
            let monthNames = [
                "January", "February", "March",
                "April", "May", "June", "July",
                "August", "September", "October",
                "November", "December"
            ];

            let day = date.getDate();
            let monthIndex = date.getMonth();
            let year = date.getFullYear();

            return day + ' ' + monthNames[monthIndex] + ' ' + year;
        }
    };

    static getDerivedStateFromProps(props, state) {
        if(props.redraw === true){
            return {
                startDate:  props.startDate,
                endDate:    props.endDate,
                tickets:    props.tickets,
                carpark:    props.carpark,
            }
        }

        // Return null to indicate no change to state.
        return null;
    }

    render(){
        let revenue = 0;

        for(let i = 0; i < this.state.tickets.length; ++i){
            if(this.state.tickets[i].amount_paid) {
                revenue += this.state.tickets[i].amount_paid;
            }
        }

        return (
            <Scrollbars
                style={{height: this.visibleHeight }}
                autoHide
                autoHideTimeout={500}
            >
                <section id="to-print">
                    <div className="box has-text-grey-dark has-text-weight-semi-bold is-size-5">
                        <div className="has-text-centered">
                            <div className="title is-2" style={{marginBottom: "50px", textDecoration: "underline"}}>
                                Car Park Report
                            </div>
                        </div>

                        <div className="columns">
                            <div className="column left">
                                <div className="tile" style={{marginBottom: "20px"}}>
                                    Requested by: {this.state.username}
                                </div>
                                <div className="tile">
                                    Fot time period: {this.formatDate(this.state.startDate) + " - " + this.formatDate(this.state.endDate)}
                                </div>
                            </div>
                            <div className="column right">
                                <div className="tile right-float-stack" style={{marginBottom: "20px"}}>
                                    Date: {this.formatDate(new Date())}
                                </div>
                                <div className="tile right-float-stack">
                                    For car park: {this.state.carpark.name}
                                </div>
                            </div>
                        </div>

                        <div className="horizontal-line"> </div>

                        <div className="has-text-centered">
                            <div className="title is-3 has-text-grey-dark" style={{marginTop: "20px"}}>
                                Overview
                            </div>
                        </div>

                        <div className="columns">

                            <div className="column left">
                                <div className="tile" style={{marginBottom: "20px"}}>
                                    Total cars: {this.state.tickets.length}
                                </div>
                            </div>

                            <div className="column right">
                                <div className="tile right-float-stack"  style={{marginBottom: "20px"}}>
                                    Total revenue: £{revenue}
                                </div>
                            </div>
                        </div>

                        <div className="tile">
                            <Bar data={
                                {
                                    labels: this.populateDays(new Date(this.state.startDate), new Date(this.state.endDate)),
                                    datasets: [{
                                        label: "№ of cars",
                                        backgroundColor: 'rgb(255, 99, 132)',
                                        borderColor: 'rgb(255, 99, 132)',
                                        data: this.ticketsPerWeek(),
                                    }]
                                }
                            }
                            />
                        </div>
                    </div>
                </section>
            </Scrollbars>
        )
    }
}

export default ReportSection;