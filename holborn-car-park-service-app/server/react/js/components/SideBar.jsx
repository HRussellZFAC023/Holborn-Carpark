import React, {Component} from 'react'

class SideBar extends Component {
    constructor(props) {
        super(props);
        this.state = {activeTab: 'Dashboard'};
    }

    render() {
        return (
            <aside className="menu aside">
                <ul className="menu-list">
                    <li><a onClick={() => this.__setScene("Dashboard")}
                           className={(this.state.activeTab === "Dashboard") ? "is-active" : ""}><i
                        className="fas fa-home"/> Dashboard</a></li>
                    <li><a onClick={() => this.__setScene("Report")}
                           className={(this.state.activeTab === "Report") ? "is-active" : ""}>
                        <i className="fas fa-file-contract"/> Report</a></li>
                </ul>
                <p className="menu-label">
                    Status update
                </p>
                <ul className="menu-list">
                    <li><a onClick={() => this.__setScene("CarPark")}
                           className={(this.state.activeTab === "CarPark") ? "is-active" : ""}>
                        <i className="fas fa-car"/> Car Parks</a></li>
                    <li><a onClick={() => this.__setScene("Tickets")}
                           className={(this.state.activeTab === "Tickets") ? "is-active" : ""}>
                        <i className="fas fa-ticket-alt"/>Tickets</a></li>
                </ul>
                <p className="menu-label">
                    Admin Controls
                </p>
                <ul className="menu-list">
                    <li><a onClick={() => this.__setScene("Happy Hour")}
                           className={(this.state.activeTab === "Happy Hour") ? "is-active" : ""}>
                        <i className="fas fa-smile-beam"/> Happy Hour</a></li>
                    <li><a onClick={() => this.__setScene("Price")}
                           className={(this.state.activeTab === "Price") ? "is-active" : ""}>
                        <i className="fas fa-pound-sign"/> Pricing</a></li>
                    <li><a onClick={() => this.__setScene("Staff")}
                           className={(this.state.activeTab === "Staff") ? "is-active" : ""}>
                        <i className="fas fa-business-time"/> Employees</a></li>
                </ul>
            </aside>
        )
    }

    __setScene(content) {
        this.setState({activeTab: content});
        this.props.setScene(content);
    }
}

export default SideBar;

