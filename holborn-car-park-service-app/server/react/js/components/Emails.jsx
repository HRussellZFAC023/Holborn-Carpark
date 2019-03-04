import React, {Component} from 'react'
import Swal from 'sweetalert2';
import ReactTable from "react-table";
import 'react-table/react-table.css'
import { Scrollbars } from 'react-custom-scrollbars';


const $ = require('jquery');

/**
 * Component that renders the automatic reports scene
 */
class Emails extends Component {
    constructor(props) {
        super(props);

        this.state = {
            carparks:        [],
            reports:         [],
            selectedCarpark: {name: ''},
            timePeriod: 1,
            ruleUpdateStatus: false
        };

        /**
         * Change selected car park (can probably make it more efficient...)
         * @param cp
         */
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

        this.addRule = () => {
            $.ajax({
                url: '/api/autoreports/' + this.state.selectedCarpark._id,
                type: 'POST',
                data: {
                    time_period: this.state.timePeriod,
                },
                success: (resp) => {
                    let data = {};
                    data._id = resp._id;
                    data.carpark_id = this.state.selectedCarpark._id;
                    data.last_sent = new Date();
                    data.time_period = this.state.timePeriod;
                    data.user_id = resp.user_id;

                    let temp = this.beautifyData([data]);

                    let temp_2 = JSON.parse(JSON.stringify(this.state.reports));

                    temp_2.push(temp[0])

                    this.setState({
                        reports: temp_2
                    });

                    Swal.fire({
                        title: 'Success',
                        html: 'Auto report successfully created! Expect your first e-mail tomorrow at 9 am(earliest).<br/><br/>Alternatively press the red button to send all emails immediately<br/>(WARNING: THIS IS A TESTING FEATURE AND IF YOU PRESS IT TOO OFTEN YOUR INBOX WILL FILL WITH SPAM :))',
                        type: 'success',
                        showCancelButton: true,
                        cancelButtonColor: '#d33',
                        cancelButtonText: 'TEST ONLY'
                    }).then((result) => {
                        if(!result.value){
                            $.ajax({
                                url: '/test/autoreporter',
                                type: 'GET',
                                success: () => {
                                    Swal.fire({
                                        title: 'Check your inbox!',
                                        type: 'success'
                                    }).then();
                                },
                                error: (xhr, status, err) => {
                                    console.error('', status, err.toString());
                                }
                            });
                        }
                    });
                },
                error: (xhr, status, err) => {
                    console.error('', status, err.toString());
                    Swal.fire({
                        title: 'Error',
                        text: 'Oops! Something went wrong, please try again!',
                        type: 'error'
                    }).then();
                }
            });
        };

        this.timePeriodChange = (e) => {
            this.setState({
                timePeriod: e.target.value
            });
        };

        /**
         * Function that returns a nice human readable date
         * @param date
         * @returns {string}
         */
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
        };

        this.beautifyData = (data) => {
            let beauty = JSON.parse(JSON.stringify(data));

            for(let i = 0; i < data.length; ++i){
                beauty[i]._id_b = data[i]._id.substring(0, data[i]._id.indexOf('-'));
                beauty[i].last_sent = this.formatDate(new Date(data[i].last_sent));
                for(let j = 0; j < this.state.carparks.length; ++j) {
                    if(this.state.carparks[j]._id === data[i].carpark_id) {
                        beauty[i].carpark_name = this.state.carparks[j].name;
                    }
                }
            }

            return beauty;
        };

        this.deleteReport = (id) => {
            $.ajax({
                url: '/api/autoreports/' + id,
                type: 'DELETE',
                success: () => {
                    let data = JSON.parse(JSON.stringify(this.state.reports));
                    for(let i = 0; i < data.length; ++i){
                        if(id === data[i]._id){
                            data.splice(id, 1);
                        }
                    }
                    this.setState({
                        reports: data,
                    });
                },
                error: (xhr, status, err) => {
                    console.error('', status, err.toString());
                }
            });
        };

        this.changePeriod = (id) => {
            let new_period = 1;

            Swal.fire({
                title: 'Select time period',
                input: 'text',
                type: 'warning',
            }).then((input) => {
                if(isNaN(input.value)){
                    Swal.fire({
                        title: 'Not a number',
                        type: 'error',
                    }).then();
                    return;
                }
                new_period = input.value;

                $.ajax({
                    url: '/api/autoreports/' + id,
                    type: 'PUT',
                    data: {
                        time_period: parseInt(new_period)
                    },
                    success: () => {
                        let data = JSON.parse(JSON.stringify(this.state.reports));
                        for(let i = 0; i < data.length; ++i){
                            if(id.toString() === data[i]._id.toString()){
                                data[i].time_period = new_period;
                            }
                        }
                        this.setState({
                            reports: data,
                        });
                    },
                    error: (xhr, status, err) => {
                        console.error('', status, err.toString());
                    }
                });
            });
        };
    }

    /**
     * Whenever the component mounts the list of available car parks should be updated
     */
    componentDidMount() {
        /**
         * Used to set the scrolling viewport of the table
         * @type {number}
         */
        this.visibleHeight = window.innerHeight
            - document.getElementById('nav-bar').scrollHeight
            - document.getElementById('auto-report-settings').scrollHeight
            - document.getElementById('auto-report-title').scrollHeight
            - 50;

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

        $.ajax({
            url: '/api/autoreports/',
            type: 'GET',
            success: (data) => {
                this.setState({
                    reports: data,
                });
            },
            error: (xhr, status, err) => {
                console.error('', status, err.toString());
            }
        });
    }

    render() {
        return(
            <main>
                <section id="auto-report-settings" className="hero is-small is-info gradient">
                    <div className="hero-body">
                        <div className="container">
                            <h1 className="title">
                                Automatic reports
                            </h1>
                            <h2 className="subtitle">
                                Here you can set when to receive emails about reports
                            </h2>
                        </div>
                    </div>
                </section>
                <section>
                    <div id="auto-report-title" className="columns">
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
                                        <p style={{ display: "inline" }}>Every:&nbsp;&nbsp;</p>
                                        <div className="select is-small">
                                            <select onChange={this.timePeriodChange}>
                                                <option value="1">1</option>
                                                <option value="7">7</option>
                                                <option value="30">30</option>
                                                <option value="365">365</option>
                                            </select>
                                        </div>
                                        <p style={{ display: "inline" }}>&nbsp;&nbsp;days</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="column is-2 has-text-centered">
                            <button onClick={this.addRule} style={{marginBottom: "8%"}} className="button is-fullwidth is-large is-info gradient">Add rule</button>
                        </div>
                    </div>
                </section>

                <section style={{marginTop: "2%"}}>
                    <Scrollbars
                        style={{height: this.visibleHeight }}
                        autoHide
                        autoHideTimeout={500}
                    >
                        <ReactTable
                            pageSizeOptions={[5, 10, 12, 15, 20, 25, 30, 35]}
                            defaultPageSize={12}
                            data={this.beautifyData(this.state.reports)}
                            columns={[{
                                Header: 'ID',
                                accessor: '_id_b',
                                Cell: props => <div className="has-text-centered">{props.value}</div>
                            }, {
                                Header: 'Time Period (in days)',
                                accessor: 'time_period',
                                Cell: props => <div className="has-text-centered">{props.value}</div>
                            }, {
                                Header: 'Last sent on',
                                accessor: 'last_sent',
                                Cell: props => <div className="has-text-centered">{props.value}</div>
                            }, {
                                Header: "Car park",
                                accessor: 'carpark_name',
                                Cell: props => <div className="has-text-centered">{props.value}</div>
                            }, {
                                Header: "Actions",
                                accessor: 'actions',
                                Cell: props =>
                                    <div className="has-text-centered">
                                        <button onClick={()=>{this.changePeriod(props.original._id)}} className="button is-small gradient has-text-white is-info" style={{marginRight: 5}}>Change Period</button>
                                        <button onClick={()=>{this.deleteReport(props.original._id)}} className="button is-small gradient has-text-white is-info">Delete</button>
                                    </div>
                            }]}
                        />
                    </Scrollbars>
                </section>
            </main>
        )
    }
}

export default Emails;