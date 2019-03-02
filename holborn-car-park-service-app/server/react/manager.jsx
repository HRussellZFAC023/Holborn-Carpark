import React from 'react'
import ReactDOM from 'react-dom'

import NavBar       from './js/components/NavBar'
import Sidebar      from './js/components/SideBar'
import Dash         from './js/components/Dash'
import Missing404   from './js/components/Missing404'
import Report       from './js/components/Report'
import Settings     from './js/components/Settings'
import Tickets      from './js/components/Tickets'
import Carparks     from './js/components/Carparks'
import Employees    from './js/components/Employees'
import Emails       from './js/components/Emails'

const $ = require('jquery');

/**
 * Main component which includes all other components and defines functions to switch between scenes
 */
class DynamicContent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            scene:          window.localStorage.getItem('scene') ? window.localStorage.getItem('scene') : 'Dashboard',
            showSettings:   false,
            username:       null
        };

        this.setScene = (nScene) => {
            this.setState({ scene: nScene })
        };

        this.togglePopup = () => {
            this.setState({
                showSettings: !this.state.showSettings
            });
        };
    }

    /**
     * When the component mounts the ajax request gets the username of the currently logged in user
     * Since this components only gets mounted once it ensures that the name is not queried on every re-render
     */
    componentDidMount () {
        $.ajax({
            url: '/utility/name',
            type: 'GET',
            success: (name) => {
                this.setState({
                    username: name
                });
            },
            error: (xhr, status, err) => {
                console.error('', status, err.toString());
            }
        });
    }

    /**
     * Function that switches between the different scenes
     * @returns {*}
     */
    getScene() {
        switch (this.state.scene) {
            case "Dashboard":
                window.localStorage.setItem("scene", "Dashboard");
                return <Dash username={this.state.username}/>;
            case "Settings":
                window.localStorage.setItem("scene", "Settings");
                return <Settings />;
            case "Report":
                window.localStorage.setItem("scene", "Report");
                return <Report username={this.state.username}/>;
            case "CarPark":
                window.localStorage.setItem("scene", "CarPark");
                return <Carparks />;
            case "Tickets":
                window.localStorage.setItem("scene", "Tickets");
                return <Tickets />;
            case "Staff":
                window.localStorage.setItem("scene", "Staff");
                return <Employees />;
            case "Emails":
                window.localStorage.setItem("scene", "Emails");
                return <Emails />;
            default:
                return <Missing404 />
        }
    }

    render() {
        return (
            <main>
                <NavBar togglePopup={this.togglePopup} />
                <div className="columns ">
                    <section className="column hero is-fullheight is-2 sidebar">
                        <Sidebar setScene={this.setScene} />
                    </section>
                    <section className="column">
                        <div className="container">
                            {this.getScene()}
                            {this.state.showSettings ? <Settings togglePopup={this.togglePopup} /> : null}
                        </div>
                    </section>
                </div>
            </main>
        )
    }
}

const dynamicWrapper = document.getElementById("DynamicContent");
dynamicWrapper ? ReactDOM.render(<DynamicContent />, dynamicWrapper) : false;
