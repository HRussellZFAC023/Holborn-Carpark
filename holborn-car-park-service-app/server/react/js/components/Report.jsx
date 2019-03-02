import React, { Component } from 'react'
import DatePicker from "react-datepicker";
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf'

const $ = require('jquery');

import "react-datepicker/dist/react-datepicker.css";
import ReportSection from "./ReportSection";

class Report extends Component {
    constructor(props) {
        super(props);

        this.state = {
            tickets:         [],
            carparks:        [],
            selectedCarpark: {name: ''},
            startDate:       null,
            endDate:         new Date(),
            redraw:          false,
            username:        props.username
        };

        this.day = 24 * 60 * 60 * 1000;

        this.addDays = (date, days) => {
            return new Date(date.getTime() + days * this.day);
        };

        this.populateDates = (start_d, end_d) => {
            let all = [];

            for(let i = 0; i < (end_d.getTime() - start_d.getTime()) / this.day; ++i){
                all.push(this.addDays(start_d, i));
            }

            return all;
        };

        this.handleStartDate = (date) => {
            this.setState({
                startDate: date
            });
        };

        this.handleEndDate = (date) => {
            this.setState({
                endDate: date
            });
        };

        this.changeSelectedCP = (cp) => {
            if(this.state.selectedCarpark.name === cp.currentTarget.textContent) return;

            for (let i = 0; i < this.state.carparks.length; ++i){
                if(this.state.carparks[i].name === cp.currentTarget.textContent){
                    this.setState({
                        selectedCarpark: this.state.carparks[i]
                    });
                }
            }
        };

        this.genReport = () => {
            //get tickets
            $.ajax({
                url: '/api/tickets/',
                type: 'GET',
                data: {
                    _carpark_id: this.state.selectedCarpark._id,
                    startDate:   this.state.startDate,
                    endDate:     this.state.endDate
                },
                success: (data) => {
                    this.setState({
                        tickets: data,
                        redraw: true
                    }, () => {this.setState({redraw: false})})
                },
                error: (xhr, status, err) => {
                    console.error('', status, err.toString());
                }
            });
        };

        this.printReport = () => {
            const input = document.getElementById('to-print');
            html2canvas(input)
                .then((canvas) => {
                    const imgData = canvas.toDataURL('image/png');
                    const pdf = new jsPDF({
                        unit: 'in',
                        format: [2000, 1020]
                    });
                    pdf.addImage(imgData, 'JPEG', 0, 0);
                    // pdf.output('dataurlnewwindow');
                    pdf.save("download.pdf");
                });
        };
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

    render() {
        return (
            <section>
                <div id="report-settings" className="columns">
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
                                                    this.state.carparks.map((cp) => {
                                                        return <a onClick={this.changeSelectedCP}>{cp.name}<br /></a>
                                                    })
                                                    }
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </header>
                            <div className="card-content" style={{height: "75px"}}>
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
                                     Select Time
                                </p>

                            </header>
                            <div className="card-content">
                                <div className="content">
                                    <p style={{ display: "inline" }}>From:&nbsp;&nbsp;</p>
                                    <DatePicker
                                        selected={this.state.startDate}
                                        onChange={this.handleStartDate}
                                        dateFormat="dd/MM/yyyy h:mm a"
                                        todayButton={"Today"}
                                        className={"input is-small"}
                                        placeholderText="Start date"
                                        excludeDates={this.populateDates(this.addDays(new Date(), 1), this.addDays(new Date(), 3650))}
                                    />
                                    <p style={{ display: "inline" }}>&nbsp;-&nbsp;To:&nbsp;&nbsp;</p>
                                    <DatePicker
                                        selected={this.state.endDate}
                                        onChange={this.handleEndDate}
                                        dateFormat="dd/MM/yyyy h:mm a"
                                        todayButton={"Today"}
                                        className={"input is-small"}
                                        placeholderText="Final date"
                                        excludeDates={this.populateDates(this.addDays(new Date(), 1), this.addDays(new Date(), 3650))}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="column is-2 has-text-centered">
                        <button onClick={this.genReport} style={{marginBottom: 15}} className="button is-fullwidth is-large is-info">Generate</button>
                        <button onClick={this.printReport} className="button is-fullwidth is-large is-info">Print</button>
                    </div>
                </div>

                {
                    this.state.tickets.length === 0
                    ? (<div className="has-text-centered">No ticket data for selected period</div>)
                    : <ReportSection
                            startDate={this.state.startDate}
                            endDate={this.state.endDate}
                            tickets={this.state.tickets}
                            carpark={this.state.selectedCarpark}
                            redraw={this.state.redraw}
                            username={this.state.username}
                        />
                }
            </section>

        )
    }
}

export default Report;


