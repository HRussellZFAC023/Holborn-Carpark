import React, {Component} from 'react'
import ReactTable from "react-table";
import 'react-table/react-table.css'
import { Scrollbars } from 'react-custom-scrollbars';
import Swal from "sweetalert2";

const $ = require('jquery');

/**
 * Component used to display all existing car parks and functions related to them
 */
class Carparks extends Component {
    constructor(props) {
        super(props);
        this.state = {
            carparks: [],
        };

        this.beautifyData = (data) => {
            let beauty = JSON.parse(JSON.stringify(data));

            for(let i = 0; i < data.length; ++i){
                beauty[i]._id_b = data[i]._id.substring(0, data[i]._id.indexOf('-'));
            }

            return beauty;
        };

        this.changeName = (row) => {
            Swal.fire({
                title: 'Select new name',
                input: 'text',
                type: 'warning',
                inputPlaceholder: row.name
            }).then((input) => {
                $.ajax({
                    url: '/api/carparks/' + row._id,
                    type: 'PUT',
                    data: {
                        name: input.value
                    },
                    success: () => {
                        let data = JSON.parse(JSON.stringify(this.state.carparks));
                        for(let i = 0; i < data.length; ++i){
                            if(row._id.toString() === data[i]._id.toString()){
                                data[i].name = input.value;
                            }
                        }
                        this.setState({
                            carparks: data,
                        });
                    },
                    error: (xhr, status, err) => {
                        console.error('', status, err.toString());
                    }
                });
            });
        };

        this.changeRate = (row) => {
            Swal.fire({
                title: 'Select new rate',
                input: 'text',
                type: 'warning',
                inputPlaceholder: row.hour_rate
            }).then((input) => {
                if(isNaN(input.value) ){
                    Swal.fire({
                        title: 'Not a number',
                        type: 'error',
                    }).then();
                    return;
                }

                $.ajax({
                    url: '/api/carparks/' + row._id,
                    type: 'PUT',
                    data: {
                        hour_rate: parseFloat(input.value)
                    },
                    success: () => {
                        let data = JSON.parse(JSON.stringify(this.state.carparks));
                        for(let i = 0; i < data.length; ++i){
                            if(row._id.toString() === data[i]._id.toString()){
                                data[i].hour_rate = input.value;
                            }
                        }
                        this.setState({
                            carparks: data,
                        });
                    },
                    error: (xhr, status, err) => {
                        console.error('', status, err.toString());
                    }
                });
            });
        };

        this.changePostcode = (row) => {
            Swal.fire({
                title: 'Select new postcode',
                input: 'text',
                type: 'warning',
                inputPlaceholder: row.postcode
            }).then((input) => {
                $.ajax({
                    url: '/api/carparks/' + row._id,
                    type: 'PUT',
                    data: {
                        postcode: input.value
                    },
                    success: () => {
                        let data = JSON.parse(JSON.stringify(this.state.carparks));
                        for(let i = 0; i < data.length; ++i){
                            if(row._id.toString() === data[i]._id.toString()){
                                data[i].postcode = input.value;
                            }
                        }
                        this.setState({
                            carparks: data,
                        });
                    },
                    error: (xhr, status, err) => {
                        console.error('', status, err.toString());
                    }
                });
            });
        };

        this.changeSpaces = (row) => {
            Swal.fire({
                title: 'Select amount of spaces',
                input: 'text',
                type: 'warning',
                inputPlaceholder: row.parking_places
            }).then((input) => {
                $.ajax({
                    url: '/api/carparks/' + row._id,
                    type: 'PUT',
                    data: {
                        parking_places: input.value
                    },
                    success: () => {
                        let data = JSON.parse(JSON.stringify(this.state.carparks));
                        for(let i = 0; i < data.length; ++i){
                            if(row._id.toString() === data[i]._id.toString()){
                                data[i].parking_places = input.value;
                            }
                        }
                        this.setState({
                            carparks: data,
                        });
                    },
                    error: (xhr, status, err) => {
                        console.error('', status, err.toString());
                    }
                });
            });
        };

        this.startHappyHour = (row) => {
            Swal.fire({
                title: 'Are you sure?',
                text: 'Entry will be free for the next 1h.',
                type: 'warning',
                showCancelButton: true
            }).then(() => {
                $.ajax({
                    url: '/api/carparks/' + row._id,
                    type: 'PUT',
                    data: {
                        happy_hour_start: new Date(),
                        happy_hour: true
                    },
                    success: () => {
                        let data = JSON.parse(JSON.stringify(this.state.carparks));
                        for(let i = 0; i < data.length; ++i){
                            if(row._id.toString() === data[i]._id.toString()){
                                data[i].happy_hour_start = new Date();
                                data[i].happy_hour = 'true';
                            }
                        }
                        this.setState({
                            carparks: data,
                        });
                    },
                    error: (xhr, status, err) => {
                        console.error('', status, err.toString());
                    }
                });
            });
        };

        this.stopHappyHour = (row) => {
            Swal.fire({
                title: 'Are you sure?',
                text: 'Entry will be no longer be free.',
                type: 'warning',
                showCancelButton: true
            }).then((input) => {
                $.ajax({
                    url: '/api/carparks/' + row._id,
                    type: 'PUT',
                    data: {
                        happy_hour: false
                    },
                    success: () => {
                        let data = JSON.parse(JSON.stringify(this.state.carparks));
                        for(let i = 0; i < data.length; ++i){
                            if(row._id.toString() === data[i]._id.toString()){
                                data[i].happy_hour = 'false';
                            }
                        }
                        this.setState({
                            carparks: data,
                        });
                    },
                    error: (xhr, status, err) => {
                        console.error('', status, err.toString());
                    }
                });
            });
        };

        this.addCarPark = () => {
            let name = document.getElementById('new-car-park-name').value;
            let rate = parseFloat(document.getElementById('new-car-park-rate').value);
            let post = document.getElementById('new-car-park-postcode').value;
            let spaces = parseInt(document.getElementById('new-car-park-spaces').value);

            if(!name || isNaN(rate) || !post || isNaN(spaces)){
                Swal.fire({
                    title: 'Error',
                    text: 'Please check your input fields again!',
                    type: 'error'
                }).then();
                return;
            }

            $.ajax({
                url: '/api/carparks/',
                type: 'POST',
                data: {
                    name: name,
                    hour_rate: rate,
                    postcode: post,
                    parking_places: spaces,
                    happy_hour_start: new Date()
                },
                success: (resp) => {
                    let data = {};
                    data._id = resp._id;
                    data.name = name;
                    data.hour_rate = rate;
                    data.postcode = post;
                    data.parking_places = spaces;
                    data.happy_hour = false;
                    data.happy_hour_start = new Date();

                    let temp = this.beautifyData([data]);

                    let temp_2 = JSON.parse(JSON.stringify(this.state.carparks));

                    temp_2.push(temp[0]);

                    this.setState({
                        carparks: temp_2
                    });
                },
                error: (xhr, status, err) => {
                    console.error('', status, err.toString());
                }
            });
        };
    };

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
            - document.getElementById('car-park-settings').scrollHeight
            - document.getElementById('car-park-title').scrollHeight
            - 50;

        $.ajax({
            url: '/api/carparks/',
            type: 'GET',
            success: (data) => {
                this.setState({
                    carparks:  JSON.parse(JSON.stringify(data))
                });
            },
            error: (xhr, status, err) => {
                console.error('', status, err.toString());
            }
        });
    };

    render() {
        return (
            <main>
                <section id="car-park-title" className="hero is-small is-info gradient">
                    <div className="hero-body">
                        <div className="container">
                            <h1 className="title">
                                Car parks
                            </h1>
                            <h2 className="subtitle">
                                Here you can view all available car parks
                            </h2>
                        </div>
                    </div>
                </section>
                <section>
                    <div id="car-park-settings" className="columns">
                        <div className="column">
                            <div className="card">
                                <header className="card-header">
                                    <p className="card-header-title">
                                        Select Carpark Name
                                    </p>
                                </header>
                                <div className="card-content">
                                    <div className="content">
                                        <input id="new-car-park-name" className="input is-small" placeholder="Car Park Name"/>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="column">
                            <div className="card">
                                <header className="card-header">
                                    <p className="card-header-title">
                                        Select Hourly Rate
                                    </p>

                                </header>
                                <div className="card-content">
                                    <div className="content">
                                        <input id="new-car-park-rate" className="input is-small" placeholder="Hourly Rate"/>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="column">
                            <div className="card">
                                <header className="card-header">
                                    <p className="card-header-title">
                                        Select Postcode
                                    </p>

                                </header>
                                <div className="card-content">
                                    <div className="content">
                                        <input id="new-car-park-postcode" className="input is-small" placeholder="Postcode"/>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="column">
                            <div className="card">
                                <header className="card-header">
                                    <p className="card-header-title">
                                        Maximum Parking Spaces
                                    </p>

                                </header>
                                <div className="card-content">
                                    <div className="content">
                                        <input id="new-car-park-spaces" className="input is-small" placeholder="Maximum Parking Spaces"/>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="column is-2 has-text-centered">
                            <button onClick={this.addCarPark} style={{marginBottom: "8%"}} className="button is-fullwidth is-large is-info gradient">Add Car Park</button>
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
                            pageSizeOptions={[5, 10, 15, 20, 25, 30, 35]}
                            defaultPageSize={5}
                            data={this.beautifyData(this.state.carparks)}
                            columns={[{
                                Header: 'ID',
                                accessor: '_id_b',
                                Cell: props => <div className="has-text-centered">{props.value}</div>
                            }, {
                                Header: 'Name',
                                accessor: 'name',
                                Cell: props => <div className="has-text-centered">{props.value}</div>
                            }, {
                                Header: 'Rate',
                                accessor: 'hour_rate',
                                Cell: props => <div className="has-text-centered">£{props.value}</div>
                            }, {
                                Header: "Postcode",
                                accessor: 'postcode',
                                Cell: props => <div className="has-text-centered">{props.value}</div>
                            },{
                                Header: "Available Spaces",
                                accessor: 'parking_places',
                                Cell: props => <div className="has-text-centered">{props.value}</div>
                            },{
                                Header: "Happy Hour",
                                accessor: 'happy_hour',
                                Cell: props => <div className="has-text-centered">{props.value.toString() === 'true' ? 'Active' : ' Not Active'}</div>
                            }, {
                                Header: "Actions",
                                accessor: 'actions',
                                Cell: props =>
                                    <div className="has-text-centered">
                                        <button onClick={()=>{this.changeName(props.original)}} id="button-one" className="button is-small gradient has-text-white is-info" style={{marginRight: 5, marginBottom: 5}}>Change Name</button>
                                        <button onClick={()=>{this.changeRate(props.original)}} id="button-two" className="button is-small gradient has-text-white is-info">Change Rate</button><br/>
                                        <button onClick={()=>{this.changePostcode(props.original)}} className="button is-small gradient has-text-white is-info" style={{marginRight: 5, marginBottom: 5}}>Change Post</button>
                                        <button onClick={()=>{this.changeSpaces(props.original)}} className="button is-small gradient has-text-white is-info">Change Space</button> <br/>
                                        <button onClick={()=>{props.original.happy_hour.toString() === 'true' ? this.stopHappyHour(props.original) : this.startHappyHour(props.original)}} className="button is-small gradient has-text-white is-info">{props.original.happy_hour.toString() === 'true' ? 'Stop' : 'Start'} Happy Hour</button>
                                    </div>
                            }]}
                        />
                    </Scrollbars>
                </section>
            </main>
        )
    }
}

export default Carparks;


