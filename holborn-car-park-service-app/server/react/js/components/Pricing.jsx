import React, {Component} from 'react';
import {Slider} from 'material-ui-slider';
import "react-datepicker/dist/react-datepicker.css";
import Swal from 'sweetalert2';

class Pricing extends Component {

    constructor(props) {
        super(props);
        this.state = {
            price: 0,
            selectedCarpark: {name: ''}
        };
        this.priceChanged = this.dateChanged.bind(this);
    }


    dateChanged(p) {
        this.setState({price: p});
    }

    confirm() {
        Swal.fire({
            title: 'Are you sure?',
            html: 'You are about change the prices: <br />Price: <br />£' + this.state.price + '<br />Carpark: <br />' + this.state.selectedCarpark.name + '<br />The cost reflects how much is paid per hour.',
            type: 'warning',
            showCancelButton: true,
        })
            .then((yes) => {
                if (yes) {
                    Swal.fire({
                        title: 'Price Changed!',
                        type: 'success',
                    }).then();
                    this.send();
                } else {
                    Swal.fire({title: 'Succesfully Cancelled!', type: 'success'}).then();
                }
            });
    };

    send() {
        //send the data...
    }

    render() {
        return (
            <main className="hero-body has-text-centered">
                <h1 className="title is-1">Pricing</h1>
                <h2 className="subtitle is-4">Here you can change the hourly fee.</h2>
                <div className="field">
                    <h1 className="content">Input an hourly rate: £{this.state.price}</h1>
                    <Slider
                        value={this.state.value}
                        aria-labelledby="label"
                        onChange={this.priceChanged}
                    />
                </div>
                <div className="field">
                    <h1 className="content">Select a car park:</h1>
                    <div class="select">
                        <select>
                            <option>Egham</option>
                            <option>Staines</option>
                        </select>
                    </div>
                </div>
                <br/>
                <div className="content field">
                    <button onClick={() => this.confirm()} className="button is-large is-success">Change!</button>
                </div>
            </main>
        );
    }
}

export default Pricing;
