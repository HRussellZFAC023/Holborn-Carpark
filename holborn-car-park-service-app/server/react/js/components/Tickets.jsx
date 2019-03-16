import React, {Component} from 'react'
import ReactTable from "react-table";
import 'react-table/react-table.css'
import { Scrollbars } from 'react-custom-scrollbars';
import Swal from "sweetalert2";

const $ = require('jquery');

/**
 * Component used to display all existing tickets  and functions related to them
 */
class Tickets extends Component {
    constructor(props){
        super(props);

        this.state = {
            tickets: []
        };

        /**
         * Function that returns a nice human readable date
         * @param date
         * @returns {string}
         */
        this.formatDate = (date) => {
            if (new Date(date).toString() === new Date(0).toString()) return 'N/A';
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
                beauty[i]._id_b = data[i]._id;
                beauty[i].date_in = this.formatDate(new Date(data[i].date_in));
                beauty[i].date_out = this.formatDate(new Date(data[i].date_out));
                beauty[i].paid = !beauty[i].paid ? 'No' : 'Yes';
                beauty[i].valid = !beauty[i].valid ? 'No' : 'Yes';
                beauty[i].duration = !beauty[i].duration ? 'N/A' : beauty[i].duration;
                beauty[i].amount_paid = !beauty[i].amount_paid ? '0' : beauty[i].amount_paid;
            }

            return beauty;
        };

        this.changePaid = (row) => {
            Swal.fire({
                title: 'Select new paid status (true/false)',
                input: 'text',
                type: 'warning',
                inputPlaceholder: row.paid
            }).then((input) => {
                $.ajax({
                    url: '/api/tickets/' + row._id,
                    type: 'PUT',
                    data: {
                        paid: input.value
                    },
                    success: () => {
                        let data = JSON.parse(JSON.stringify(this.state.tickets));
                        for(let i = 0; i < data.length; ++i){
                            if(row._id.toString() === data[i]._id.toString()){
                                data[i].paid = (input.value === 'true');
                            }
                        }
                        this.setState({
                            tickets: this.beautifyData(data),
                        });
                    },
                    error: (xhr, status, err) => {
                        console.error('', status, err.toString());
                    }
                });
            });
        };

        this.changeValid = (row) => {
            Swal.fire({
                title: 'Select new valid status (true/false)',
                input: 'text',
                type: 'warning',
                inputPlaceholder: row.valid
            }).then((input) => {
                $.ajax({
                    url: '/api/tickets/' + row._id,
                    type: 'PUT',
                    data: {
                        valid: input.value
                    },
                    success: () => {
                        let data = JSON.parse(JSON.stringify(this.state.tickets));
                        for(let i = 0; i < data.length; ++i){
                            if(row._id.toString() === data[i]._id.toString()){
                                data[i].valid = (input.value === 'true');
                            }
                        }
                        this.setState({
                            tickets: data,
                        });
                    },
                    error: (xhr, status, err) => {
                        console.error('', status, err.toString());
                    }
                });
            });
        };

        this.changeDuration = (row) => {
            Swal.fire({
                title: 'Select new duration',
                input: 'text',
                type: 'warning',
                inputPlaceholder: row.duration
            }).then((input) => {
                if(isNaN(input.value) ){
                    Swal.fire({
                        title: 'Not a number',
                        type: 'error',
                    }).then();
                    return;
                }

                $.ajax({
                    url: '/api/tickets/' + row._id,
                    type: 'PUT',
                    data: {
                        duration: input.value
                    },
                    success: () => {
                        let data = JSON.parse(JSON.stringify(this.state.tickets));
                        for(let i = 0; i < data.length; ++i){
                            if(row._id.toString() === data[i]._id.toString()){
                                data[i].duration = input.value;
                            }
                        }
                        this.setState({
                            tickets: this.beautifyData(data),
                        });
                    },
                    error: (xhr, status, err) => {
                        console.error('', status, err.toString());
                    }
                });
            });
        };

        this.changeAmountPaid = (row) => {
            Swal.fire({
                title: 'Select new amount paid',
                input: 'text',
                type: 'warning',
                inputPlaceholder: row.amount_paid
            }).then((input) => {
                if(isNaN(input.value) ){
                    Swal.fire({
                        title: 'Not a number',
                        type: 'error',
                    }).then();
                    return;
                }

                $.ajax({
                    url: '/api/tickets/' + row._id,
                    type: 'PUT',
                    data: {
                        amount_paid: input.value
                    },
                    success: () => {
                        let data = JSON.parse(JSON.stringify(this.state.tickets));
                        for(let i = 0; i < data.length; ++i){
                            if(row._id.toString() === data[i]._id.toString()){
                                data[i].amount_paid = input.value;
                            }
                        }
                        this.setState({
                            tickets: this.beautifyData(data),
                        });
                    },
                    error: (xhr, status, err) => {
                        console.error('', status, err.toString());
                    }
                });
            });
        };
    }

    componentDidMount() {
        /**
         * Used to set the scrolling viewport of the table
         * @type {number}
         */
        this.visibleHeight = window.innerHeight
            - document.getElementById('nav-bar').scrollHeight
            - document.getElementById('ticket-title').scrollHeight
            - 50;

        $.ajax({
            url: '/api/tickets/',
            type: 'GET',
            success: (data) => {
                this.setState({
                    tickets:  JSON.parse(JSON.stringify(data))
                });
            },
            error: (xhr, status, err) => {
                console.error('', status, err.toString());
            }
        });
    }

    render() {
        return (
            <main>
                <section id="ticket-title" className="hero is-small is-info gradient">
                    <div className="hero-body">
                        <div className="container">
                            <h1 className="title">
                                Tickets
                            </h1>
                            <h2 className="subtitle">
                                Here you can view all tickets
                            </h2>
                        </div>
                    </div>
                </section>
                
                <section>

                </section>


                <section style={{marginTop: "2%"}}>
                    <Scrollbars
                        style={{height: this.visibleHeight }}
                        autoHide
                        autoHideTimeout={500}
                    >
                        <ReactTable
                            pageSizeOptions={[4, 5, 10, 15, 20, 25, 30, 35]}
                            defaultPageSize={4}
                            data={this.beautifyData(this.state.tickets)}
                            columns={[{
                                Header: 'ID',
                                accessor: '_id_b',
                                Cell: props => <div className="has-text-centered">{props.value}</div>
                            }, {
                                Header: 'Date in',
                                accessor: 'date_in',
                                Cell: props => <div className="has-text-centered">{props.value}</div>
                            }, {
                                Header: 'Date out',
                                accessor: 'date_out',
                                Cell: props => <div className="has-text-centered">{props.value}</div>
                            }, {
                                Header: "paid",
                                accessor: 'paid',
                                Cell: props => <div className="has-text-centered">{props.value}</div>
                            },{
                                Header: "valid",
                                accessor: 'valid',
                                Cell: props => <div className="has-text-centered">{props.value}</div>
                            },{
                                Header: "duration",
                                accessor: 'duration',
                                Cell: props => <div className="has-text-centered">{props.value}</div>
                            },{
                                Header: "amount_paid",
                                accessor: 'amount_paid',
                                Cell: props => <div className="has-text-centered">£{props.value}</div>
                            }, {
                                Header: "Actions",
                                accessor: 'actions',
                                Cell: props =>
                                    <div className="has-text-centered">
                                        <button onClick={()=>{this.changePaid(props.original)}} id="button-one" className="button is-small gradient has-text-white is-info" style={{marginBottom: 5}}>Change Paid</button><br/>
                                        <button onClick={()=>{this.changeValid(props.original)}} id="button-two" className="button is-small gradient has-text-white is-info" style={{marginBottom: 5}}>Change Valid</button><br/>
                                        <button onClick={()=>{this.changeDuration(props.original)}} className="button is-small gradient has-text-white is-info" style={{marginBottom: 5}}>Change Duration</button><br/>
                                        <button onClick={()=>{this.changeAmountPaid(props.original)}} className="button is-small gradient has-text-white is-info" style={{marginBottom: 5}}>Change Amount</button> <br/>
                                    </div>
                            }]}
                        />
                    </Scrollbars>
                </section>
            </main>
        )
    }
}

export default Tickets;


