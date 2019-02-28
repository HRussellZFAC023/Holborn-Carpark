import React, { Component } from 'react'
import DatePicker from "react-datepicker";
import { Bar } from 'react-chartjs-2';
import { Doughnut } from 'react-chartjs-2';

const $ = require('jquery');


import "react-datepicker/dist/react-datepicker.css";

class Report extends Component {
    constructor(props) {
        super(props);
        this.state = {
            carparks:           [{name: ''}],
            selectedCarpark:    {name: ''},
            startDate:          null,
        };

        this.handleChange = (date) => {
            this.setState({
                startDate: date
            });
        };

        this.changeSelectedCP = this.changeSelectedCP.bind(this);
    }

    componentDidMount() {
        $.ajax({
            url: '/api/carparks/',
            type: 'GET',
            success: (data) => {
                this.setState({
                    carparks:  JSON.parse(JSON.stringify(data)),
                    selectedCarpark: JSON.parse(JSON.stringify(data[0]))
                });
            },
            error: (xhr, status, err) => {
                console.error('', status, err.toString());
            }
        });
    }

    genReport() {
        $.ajax({
            url: '',
            type: 'GET',
            success: (data) => {
                this.setState({
                    //
                });
            },
            error: (xhr, status, err) => {
                console.error('', status, err.toString());
            }
        });
    }

    changeSelectedCP(cp){
        if(this.state.selectedCarpark.name === cp.currentTarget.textContent) return;

        for (let i = 0; i < this.state.carparks.length; ++i){
            if(this.state.carparks[i].name === cp.currentTarget.textContent){
                this.setState({
                    selectedCarpark: this.state.carparks[i]
                });
            }
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
                                                    {
                                                        console.log(this.state)}{
                                                    this.state.carparks.map((cp) => {
                                                        return <li onClick={this.changeSelectedCP}>{cp.name}</li>
                                                    })
                                                    }
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </header>
                            <div className="card-content">
                                <div className="content">
                                    {this.state.selectedCarpark.name}
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
                        <button onClick={this.genReport} className="button is-fullwidth is-large is-info">Generate</button>
                        <button onClick={()=>window.print()} className="button is-fullwidth is-large is-info">Print</button>
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
                            {/* <Doughnut data="" /> */}
                        </div>
                    </div>
                </section>
            </section>

        )
    }
}

export default Report;


