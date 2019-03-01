import React, { Component } from 'react'
import { Bar } from 'react-chartjs-2';

class ReportSection extends Component {
    constructor(props) {
        super(props);

        this.state = {
            startDate:  props.startDate,
            endDate:    props.endDate,
            tickets:    props.tickets,
            carpark:    props.carpark,
        };

        this.day = 24 * 60 * 60 * 1000;

        this.graphSlots = [];
        this.populateDays = (start_d, end_d) => {
            let all = [];

            let current = start_d.getTime();

            while(current <= end_d.getTime()){
                all.push(new Date(current).getDate() + "/" + (new Date(current).getMonth() + 1));
                current += 7 * this.day;
                this.graphSlots.push(current);

            }

            if(end_d.getTime() > current){
                all.push(end_d.getDate() + "/" + (end_d.getMonth() + 1));
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

            console.log(all)
            return all;
        };
    };

    render(){
        return (
            <section id="to-print" className="collumns">
                <div className="collumn">
                    <div className="has-text-centered">
                        <div className="title is-3">Report</div>
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
        )
    }
}

export default ReportSection;