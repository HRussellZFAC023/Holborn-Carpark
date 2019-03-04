import React, { Component } from 'react'

/**
 * Component that renders the navigational side bar and immediately activates the Dashboard scene
 */
class SideBar extends Component {
    constructor(props) {
        super(props);
        this.state = {activeTab: window.localStorage.getItem('scene') ? window.localStorage.getItem('scene') : 'Dashboard'};
    }

    render() {
        return (
            <aside className="menu aside text-is-centerd">
                <p className="menu-label">
                    General
                </p>
                <ul className="menu-list">
                    <li><a onClick={() => this.__setScene("Dashboard")}
                           className={(this.state.activeTab === "Dashboard") ? "is-active gradient" : ""}><span className="icon">
                        <i className="fas fa-home"/>  </span>Dashboard</a></li>
                    <li><a onClick={() => this.__setScene("Report")}
                           className={(this.state.activeTab === "Report") ? "is-active gradient" : ""}><span className="icon">
                        <i className="fas fa-file-contract"/> </span>Report</a></li>
                    <li><a onClick={() => this.__setScene("Emails")}
                           className={(this.state.activeTab === "Emails") ? "is-active gradient" : ""}><span className="icon">
                        <i className="fas fa-envelope"/> </span>Report Emails</a></li>
                </ul>
                <p className="menu-label">
                    Status update
                </p>
                <ul className="menu-list">
                    <li><a onClick={() => this.__setScene("CarPark")}
                           className={(this.state.activeTab === "CarPark") ? "is-active gradient" : ""}><span className="icon">
                        <i className="fas fa-car"/> </span>Car Parks</a></li>
                    <li><a onClick={() => this.__setScene("Tickets")}
                           className={(this.state.activeTab === "Tickets") ? "is-active gradient" : ""}><span className="icon">
                        <i className="fas fa-ticket-alt"/></span>Tickets</a></li>
                </ul>
                <p className="menu-label">
                    Admin Controls
                </p>
                <ul className="menu-list">
                    <li><a onClick={() => this.__setScene("Staff")}
                        className={(this.state.activeTab === "Staff") ? "is-active" : ""}>
                        <i className="fas fa-business-time" /> Employees</a></li>
                </ul>
            </aside>
        )
    }

    __setScene(content) {
        this.setState({ activeTab: content });
        this.props.setScene(content);
    }
}

export default SideBar;

