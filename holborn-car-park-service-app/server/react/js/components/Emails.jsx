import React, {Component} from 'react'

const $ = require('jquery');

/**
 * Component that renders the automatic reports scene
 */
class Emails extends Component {
    constructor(props) {
        super(props);

        this.state = {
            carparks:        [],
            selectedCarpark: {name: ''},
            timePeriod: 30
        };

        /**
         * Change selected car park (can probably make it more efficient...)
         * @param cp
         */
        this.changeSelectedCP = (cp) => {
            if(this.state.selectedCarpark.name === cp.currentTarget.textContent) return;

            for (let i = 0; i < this.state.carparks.length; ++i){
                if(this.state.carparks[i].name === cp.currentTarget.textContent){
                    this.setState({
                        selectedCarpark: this.state.carparks[i]
                    });
                }
            }
        };

        this.updatePref = () => {

        };

        this.previewEmail = () => {

        };
    }

    /**
     * Whenever the component mounts the list of available car parks should be updated
     */
    componentDidMount() {
        $.ajax({
            url: '/api/carparks/',
            type: 'GET',
            success: (data) => {
                this.setState({
                    carparks:  JSON.parse(JSON.stringify(data)),
                    selectedCarpark: JSON.parse(JSON.stringify(data[0]))
                });
            },
            error: (xhr, status, err) => {
                console.error('', status, err.toString());
            }
        });
    }

    render() {
        return(
            <main>
                <section className="hero is-small is-info gradient">
                    <div className="hero-body">
                        <div className="container">
                            <h1 className="title">
                                Automatic reports
                            </h1>
                            <h2 className="subtitle">
                                Here you can set when to receive emails about reports
                            </h2>
                        </div>
                    </div>
                </section>
                <section>
                    <div className="columns">
                        <div className="column">
                            <div className="card">
                                <header className="card-header">
                                    <p className="card-header-title">
                                        Select Carpark
                                    </p>

                                    <div className="dropdown is-hoverable">
                                        <div className="dropdown-trigger">
                                            <a className="card-header-icon" aria-haspopup="true"
                                               aria-controls="dropdown-menu4">
                                                <span className="icon is-small">
                                                    <i className="fas fa-angle-down" aria-hidden="true" />
                                                </span>
                                            </a>
                                        </div>
                                        <div className="dropdown-menu" id="dropdown-menu4" role="menu">
                                            <div className="dropdown-content">
                                                <div className="dropdown-item">
                                                    <ul>
                                                        {
                                                            this.state.carparks.map((cp) => {
                                                                return <a onClick={this.changeSelectedCP}>{cp.name}<br /></a>
                                                            })
                                                        }
                                                    </ul>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </header>
                                <div className="card-content" style={{height: "75px"}}>
                                    <div className="content">
                                        {this.state.selectedCarpark.name}
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="column">
                            <div className="card">
                                <header className="card-header">
                                    <p className="card-header-title">
                                        Select Time
                                    </p>

                                </header>
                                <div className="card-content">
                                    <div className="content">
                                        <p style={{ display: "inline" }}>Every:&nbsp;&nbsp;</p>
                                        <div className="select is-small">
                                            <select>
                                                <option>1</option>
                                                <option>7</option>
                                                <option>30</option>
                                                <option>365</option>
                                            </select>
                                        </div>
                                        <p style={{ display: "inline" }}>&nbsp;&nbsp;days</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="column is-2 has-text-centered">
                                <button onClick={this.updatePref} style={{marginBottom: "8%"}} className="button is-fullwidth is-large is-info gradient">Update</button>
                            <button onClick={this.previewEmail} className="button is-fullwidth is-large is-info gradient">Preview</button>
                        </div>
                    </div>
                </section>
            </main>
        )
    }
}

export default Emails;