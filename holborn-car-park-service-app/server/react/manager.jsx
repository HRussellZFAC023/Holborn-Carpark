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

const $ = require('jquery');


class DynamicContent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            scene:          'Dashboard',
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
        }

    }


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

    getScene() {
        switch (this.state.scene) {
            case "Dashboard":
                return <Dash username={this.state.username}/>;
            case "Settings":
                return <Settings />;
            case "Report":
                return <Report />;
            case "CarPark":
                return <Carparks />;
            case "Tickets":
                return <Tickets />;
            case "Staff":
                return <Employees />
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
