import React, {Component} from 'react'
import Time from './Clock'

class Dash extends Component {
  constructor(props) {
    super(props);
    this.state = {
      name: "User",
    }
  }



  render() {
    return (
      <main>
        <section className="hero is-small is-dark gradient">
          <div className="hero-body">
            <div className="container">
              <h1 className="title">
                Welcome, {this.state.name};
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


