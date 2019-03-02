import React, { Component } from 'react'
import Time from './Clock'
import Notify from './Notify'
const $ = require('jquery');

class Dash extends Component {
    constructor(props) {
        super(props);
        this.state = {
            username: props.username,
            carparks: []
        };
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        // do stuff on state change
    }

    static getDerivedStateFromProps(props, state) {

        if (props.username !== state.username) {

            return { username: props.username };
        }
        // Return null to indicate no change to state.
        return null;
    }

    render() {
        return (
            <main>
                <section className="hero is-small is-info gradient">
                    <div className="hero-body">
                        <div className="container">
                            <h1 className="title">
                                Welcome, {this.state.username}
                            </h1>
                            <h2 className="subtitle">
                                Here is the current dashboard
                            </h2>
                        </div>
                    </div>
                </section>
                <section className="level manager-tiles">
                    <article className="level-item has-text-centered">
                        <div>
                            <p className="heading">Cars</p>
                            <p className="title">2000</p>
                        </div>
                    </article>
                    <article className="level-item has-text-centered">
                        <div>
                            <p className="heading">Car Parks</p>
                            <p className="title">20</p>
                        </div>
                    </article>
                    <article className="level-item has-text-centered">
                        <div>
                            <p className="heading">Revenue</p>
                            <p className="title">$$$</p>
                        </div>
                    </article>
                    <article className="level-item has-text-centered">
                        <div>
                            <p className="heading">Time</p>
                            <Time />
                        </div>
                    </article>
                </section>
                {/* these are low prioty features to make the dashboard look correct*/}
                {/* notifications should be creatable in settings */}
                <div className="columns">
                    <section className="column panel">
                        <p className="panel-heading">Customers</p>
                        <div className="panel-block">
                            <button className="button is-default is-outlined is-fullwidth">View Data</button>
                        </div>
                    </section>
                    <section className="column panel">
                        <p className="panel-heading">Notifications</p>
                        <div className="panel-block">
                            <Notify message="No new notifications" />
                        </div>
                    </section>
                </div>
                <footer class="footer">
                    <div class="content has-text-centered">
                        <p> <strong>Tip!</strong> You can create notification messages in setting </p>
                    </div>
                </footer>
            </main>
        )
    }
}

export default Dash;


