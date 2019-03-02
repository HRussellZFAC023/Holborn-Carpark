import React, { Component } from 'react';
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import swal from 'sweetalert';

class HappyHour extends Component {

  constructor(props) {
    super(props);
    this.state = {
      date: new Date(),
      selectedCarpark: { name: '' }
    };
    this.dateChanged = this.dateChanged.bind(this);
  }

  dateChanged(d) {
    this.setState({ date: d });
  }

  confirm(){
    swal({
      title: "Are you sure?",
      text: 
      `You are about to create the following happy hour: \n
       Carpark: \n ${this.state.selectedCarpark.name}\n
       Date: \n ${this.state.date}\n
       For one hour, the hourly rate will be reduced to zero.
      `,
      icon: "warning",
      buttons: true,
      dangerMode: true,
    })
      .then((yes) => {
        if (yes) {
          swal("Happy Hour Activated!", {
            icon: "success",
          });
          this.send();
        } else {
          swal("Succesfully Cancelled!");
        }
      });
  };

  send() {
    //send the data...
  }

  render() {
    return (
      <main className="hero-body has-text-centered">
        <h1 className="title is-1">Happy Hour!</h1>
        <h2 className="subtitle is-4">Open the barriers and let the cars go free.</h2>
        <div className="field">
          <h1 className="content">Select a car park:</h1>
          <div class="select">
            <select>
              <option>Egham</option>
              <option>Staines</option>
            </select>
          </div>
        </div>
        <div className="field">
          <h1 className="content">Select a start time and date: </h1>
          <DatePicker
            selected={this.state.date}
            onChange={this.dateChanged}
            showTimeSelect
            dateFormat="Pp"
          />
        </div><br />
        <div className="content field" >
          <button onClick={() => this.confirm()} className="button is-large is-success">Start!</button>
        </div>
      </main>
    );
  }
}

export default HappyHour;


