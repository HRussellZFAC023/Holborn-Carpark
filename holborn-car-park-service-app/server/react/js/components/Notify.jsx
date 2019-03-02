import React, { Component } from 'react';

class Notify extends Component {
  render() {
    return (
      <div className="notification is-success">
        <button className="delete" onClick={()=>(console.log("close me"))}></button>
        {this.props.message}
    </div>
    );

  }
}

export default Notify;
