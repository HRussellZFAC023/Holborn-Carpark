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


class DynamicContent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            scene: 'Dashboard',
            showSettings: false
        };
        this.setScene = (nScene) => {
            this.setState({ scene: nScene })
        }
        this.togglePopup = () => {
            this.setState({
                showSettings: !this.state.showSettings
            });
        }

    }

   

    getScene() {
        switch (this.state.scene) {
            case "Dashboard":
                return <Dash />;
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
