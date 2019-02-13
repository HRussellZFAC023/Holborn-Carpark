import React, {Component} from 'react'

class SideBar extends Component {

  render() {
    return (
      <aside className="menu aside">
        <ul className="menu-list">
          <li><a onClick={() => this.__setScene("Dash")}
                 className="is-active"><i className="fas fa-home"/> Dashboard</a></li>
          <li><a onClick={() => this.__setScene("Report")} >
            <i className="fas fa-file-contract"/> Report</a></li>
        </ul>
        <p className="menu-label">
          Status update
        </p>
        <ul className="menu-list">
          <li><a onClick={() => this.__setScene("CarPark")} >
            <i className="fas fa-car"/> Car Parks</a></li>
          <li><a onClick={() => this.__setScene("Tickets")} >
            <i className="fas fa-ticket-alt"/>Car Parks</a></li>
        </ul>
        <p className="menu-label">
          Admin Controls
        </p>
        <ul className="menu-list">
          <li><a onClick={() => this.__setScene("Happy Hour")} >
            <i className="fas fa-smile-beam"/> Happy Hour</a></li>
          <li><a onClick={() => this.__setScene("Price")} >
            <i className="fas fa-pound-sign"/> Pricing</a></li>
          <li><a onClick={() => this.__setScene("Staff")} >
            <i className="fas fa-business-time"/> Employees</a></li>
        </ul>
      </aside>
    )
  }

  __setScene(content) {
    this.props.setScene(content);
  }
}

export default SideBar;


