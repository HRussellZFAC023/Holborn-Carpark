import React, { Component } from 'react'
import Time from './Clock'
import $ from 'jquery';

class Dash extends Component {
    componentDidMount() {
        $.ajax({
            url: '/utility/name',
            type: 'GET',
            success: function (name) {
                this.setState({ name: name });
            }.bind(this),
            error: function (xhr, status, err) {
                console.error('', status, err.toString());
            }.bind(this)
        });
    }

    constructor(props) {
        super(props);
        this.state = {
            name: '',
        }
    }

    render() {
        return (
            <main>
                <section className="hero is-small is-info gradient2">
                    <div className="hero-body">
                        <div className="container">
                            <h1 className="title">
                                Welcome, {this.state.name}
                            </h1>
                            <h2 className="subtitle">
                                Here is the current dashboard:
                            </h2>
                        </div>
                    </div>
                </section>
                <section className="level manager-tiles">
                    <article class="level-item has-text-centered">
                        <div>
                            <p class="heading">Cars</p>
                            <p class="title">2000</p>
                        </div>
                    </article>
                    <article class="level-item has-text-centered">
                        <div>
                            <p class="heading">Car Parks</p>
                            <p class="title">20</p>
                        </div>
                    </article>
                    <article class="level-item has-text-centered">
                        <div>
                            <p class="heading">Revenue</p>
                            <p class="title">$$$</p>
                        </div>
                    </article>
                    <article class="level-item has-text-centered">
                        <div>
                            <p class="heading">Time</p>
                            <p class="title"><Time/></p>
                        </div>
                    </article>
                </section>
            </main>
        )
    }
}

export default Dash;


