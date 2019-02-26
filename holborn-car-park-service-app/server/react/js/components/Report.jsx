import React, { Component } from 'react'
import DatePicker from "react-datepicker";
import { Bar } from 'react-chartjs-2';
import { Doughnut } from 'react-chartjs-2';

import "react-datepicker/dist/react-datepicker.css";

//fixme fetch this data from api in componentDidMount()
const carParksData = ["Egham", "Staines", "Windsor", "Mayfair", "Holborn"];

class Report extends Component {
    constructor(props) {
        super(props);
        this.state = {
            carpark: carParksData[0],
            startDate: null
        };
        this.handleChange = (date) =>{
            this.setState({
                startDate: date
            });
        }
    }

    render() {
        return (
            <section>
                <div className="columns">
                    <div className="column">
                        <div className="card">
                            <header className="card-header">
                                <p className="card-header-title">
                                    Select Carpark
                                </p>

                                <div className="dropdown is-hoverable">
                                    <div className="dropdown-trigger">
                                        <a className="card-header-icon" aria-haspopup="true"
                                            aria-controls="dropdown-menu4">
                                            <span className="icon is-small">
                                                <i className="fas fa-angle-down" aria-hidden="true" />
                                            </span>
                                        </a>
                                    </div>
                                    <div className="dropdown-menu" id="dropdown-menu4" role="menu">
                                        <div className="dropdown-content">
                                            <div className="dropdown-item">
                                                <ul>
                                                    {carParksData.map(function (cp) {
                                                        return <li /*onClick={this.setState({carpark: cp})}*/>{cp}</li>
                                                    })}
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </header>
                            <div className="card-content">
                                <div className="content">
                                    {this.state.carpark}
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="column">
                        <div className="card">
                            <header className="card-header">
                                <p className="card-header-title">
                                    Time
                                </p>

                                <div className="dropdown is-hoverable">
                                    <div className="dropdown-trigger">
                                        <a className="card-header-icon" aria-haspopup="true"
                                            aria-controls="dropdown-menu4">
                                            <span className="icon is-small">
                                                <i className="fas fa-angle-down" aria-hidden="true" />
                                            </span>
                                        </a>
                                    </div>
                                    <div className="dropdown-menu" id="dropdown-menu4" role="menu">
                                        <div className="dropdown-content">
                                            <div className="dropdown-item">
                                                <ul>
                                                    <li>possible time</li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                            </header>
                            <div className="card-content">
                                <div className="content">
                                    <p style={{ display: "inline" }}>From:&nbsp;&nbsp;</p>
                                    <DatePicker
                                        selected={this.state.startDate}
                                        onChange={this.handleChange}
                                        showTimeSelect
                                        timeFormat="HH:mm"
                                        timeIntervals={60}
                                        dateFormat="dd/mm/yyyy h:mm aa"
                                        todayButton={"Today"}
                                        className={"input is-small"}
                                        placeholderText="Start date"
                                    />
                                    <p style={{ display: "inline" }}>&nbsp;-&nbsp;To:&nbsp;&nbsp;</p>
                                    <DatePicker
                                        selected={this.state.startDate}
                                        onChange={this.handleChange}
                                        showTimeSelect
                                        timeFormat="HH:mm"
                                        timeIntervals={60}
                                        dateFormat="dd/mm/yyyy h:mm aa"
                                        todayButton={"Today"}
                                        className={"input is-small"}
                                        placeholderText="Final date"
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="column is-2 has-text-centered">
                        <button className="button is-large is-info">Generate</button>
                        <button onClick={window.print()} className="button is-large is-info">Print</button>
                    </div>
                </div>
                {/*insert graphs etc here*/}
                <section className="collumns">
                    <div className="collumn">
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
                            <Doughnut data="" />
                        </div>
                    </div>
                </section>
            </section>

        )
    }
}

export default Report;


