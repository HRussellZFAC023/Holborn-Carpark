import React, {Component} from 'react'


class Time extends Component {
  constructor(props) {
    super(props);
    this.state = {
      time: new Date().toLocaleTimeString()
    }
  }

  tick() {
    this.setState({
      time: new Date().toLocaleTimeString()
    });
  }

  componentDidMount() {
    this.intervalID = setInterval(
      () => this.tick(),
      1000
    );
  }
  componentWillUnmount() {
    clearInterval(this.intervalID);
  }

  render() {
    return (
      <p> {this.state.time} </p>
    )
  }
}

export default Time;


