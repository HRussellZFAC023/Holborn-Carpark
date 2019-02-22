import React, {Component} from 'react'
import Time from './Clock'
import $ from 'jquery';

class Dash extends Component {
    componentDidMount() {
        $.ajax({
            url: '/utility/name',
            type: 'GET',
            success: function (name) {
                this.setState({name: name});
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
                <section className="hero is-small is-dark gradient">
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
                <section className="info-tiles">
                    <div className="tile is-ancestor has-text-centered">
                        <div className="tile is-parent">
                            <article className="tile is-child box">
                                <p className="title"> {} </p>
                                <p className="subtitle">{}</p>
                            </article>
                        </div>
                        <div className="tile is-parent">
                            < article className="tile is-child box">
                                < p className="title"> {} </p>
                                <p className="subtitle">{}</p>
                            </article>
                        </div>
                        <div
                            className="tile is-parent">
                            <article className="tile is-child box">
                                <p className="title"> {} </p>
                                <p className="subtitle">{}</p>
                            </article>
                        </div>
                        <div
                            className="tile is-parent">
                            <article
                                className="tile is-child box">
                                <p className="title">{}</p>
                                < p className="subtitle"><Time/></p>
                            </article>
                        </div>
                    </div>
                </section>
            </main>
        )
    }
}

export default Dash;


