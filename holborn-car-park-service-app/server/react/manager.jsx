import React        from 'react'
import ReactDOM     from 'react-dom'

import NavBar       from './js/components/NavBar'
import Sidebar      from './js/components/SideBar'
import Dash         from './js/components/Dash'
import Missing404   from './js/components/Missing404'
import Settings     from './js/components/Settings'
import Report       from './js/components/Report'
import Tickets      from './js/components/Tickets'
import Carparks     from './js/components/Carparks'


class DynamicContent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {scene: 'Dashboard'};
        this.setScene = (nScene) => {
            this.setState({scene: nScene})
        }
    }

    getScene() {
        switch (this.state.scene) {
            case "Dashboard":
                return <Dash/>;
            case "Settings":
                return <Settings/>;
            case "Report":
                return <Report/>;
            case "CarPark":
                return <Carparks/>;
            case "Tickets":
                return <Tickets/>;
            default:
                return <Missing404/>
        }
    }

    render() {
        return (
            <main>
                <NavBar setScene={this.setScene}/>
                <div className="columns">
                    <section className="column hero is-fullheight is-3 sidebar">
                        <Sidebar setScene={this.setScene}/>
                    </section>
                    <section className="column is-9">
                        {this.getScene()}
                    </section>
                </div>
            </main>
        )
    }
}

const dynamicWrapper = document.getElementById("DynamicContent");
dynamicWrapper ? ReactDOM.render(<DynamicContent/>, dynamicWrapper) : false;