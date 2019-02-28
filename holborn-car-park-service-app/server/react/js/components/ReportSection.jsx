import React, { Component } from 'react'
import { Bar } from 'react-chartjs-2';

class ReportSection extends Component {
    constructor(props) {
        super(props);

    }

    render() {
        return (
            <section className="collumns">
                <div className="collumn">
                    <div className="has-text-centered">
                        <div className="title is-3">Report</div>
                    </div>
                    <div className="tile">
                        <Bar data={
                            {
                                labels: ["January", "February", "March", "April", "May", "June", "July"],
                                datasets: [{
                                    label: "No* cars",
                                    backgroundColor: 'rgb(255, 99, 132)',
                                    borderColor: 'rgb(255, 99, 132)',
                                    data: [0, 10, 5, 2, 20, 30, 45],
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