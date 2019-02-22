import React, {Component} from 'react'

//fixme fetch this data from api in componentDidMount()
const carParksData = ["Egham", "Staines", "Windsor", "Mayfair", "Holborn"];

class Report extends Component {
    constructor(props) {
        super(props);
        this.state = {
            carpark: carParksData[0]
        }
    }

    render() {
        return (
            <section>
                <div className="columns">
                    <div className="column is-5">
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
                      <i className="fas fa-angle-down" aria-hidden="true"/>
                   </span>
                                        </a>
                                    </div>
                                    <div className="dropdown-menu" id="dropdown-menu4" role="menu">
                                        <div className="dropdown-content">
                                            <div className="dropdown-item">
                                                <ul>
                                                    {carParksData.map(function (cp) {
                                                        return <li /*onClick={this.setState({carpark: cp})}*/>{cp}</li>
                                                    })}
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </header>
                            <div className="card-content">
                                <div className="content">
                                    {this.state.carpark}
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="column is-5">
                        <div className="card">
                            <header className="card-header">
                                <p className="card-header-title">
                                    Time
                                </p>

                                <div className="dropdown is-hoverable">
                                    <div className="dropdown-trigger">
                                        <a className="card-header-icon" aria-haspopup="true"
                                           aria-controls="dropdown-menu4">
                  <span className="icon is-small">
                      <i className="fas fa-angle-down" aria-hidden="true"/>
                   </span>
                                        </a>
                                    </div>
                                    <div className="dropdown-menu" id="dropdown-menu4" role="menu">
                                        <div className="dropdown-content">
                                            <div className="dropdown-item">
                                                <ul>
                                                    <li>possible time</li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                            </header>
                            <div className="card-content">
                                <div className="content">
                                    {new Date().getHours() - 2} : 00 - {new Date().getHours()} : 00
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="column">
                        <button className="button is-large is-info">Generate</button>
                    </div>
                </div>
                {/*insert graphs etc here*/}
            </section>

        )
    }
}

export default Report;


