import React, {Component} from 'react'

class NavBar extends Component {



  render() {
    return (
      <nav className="navbar" role="navigation" aria-label="main navigation">
        <div className="navbar-brand">
          <a className="navbar-item brand-text has-text-white" href="">
            <h1>HOLBORN</h1>
          </a>
        </div>

        <div className="navbar-end ">
          <div className="navbar-brand">
          <a className="navbar-item brand-text has-text-white">
            User &nbsp;
            <span className="icon"> <i className="far fa-smile"/> </span>
            <span className="icon"> <i className="fas fa-caret-down"/> </span>

          </a>

          <div className="navbar-dropdown">
            <a className="navbar-item">
              logout
            </a>
            <a className="navbar-item">
              Jobs
            </a>
            <a className="navbar-item">
              Contact
            </a>
          </div>
          </div>
        </div>
      </nav>
    )
  }
}

export default NavBar;
