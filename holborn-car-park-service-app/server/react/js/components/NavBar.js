import React, {Component} from 'react'

class NavBar extends Component {

  render() {
    return (
      <nav className="navbar" role="navigation" aria-label="main navigation">
        <div className="navbar-brand">
          <a className="navbar-item title brand-text has-text-white" href="">
            <h1>HOLBORN</h1>
          </a>
        </div>

        <div className="navbar-end ">
          <div className="navbar-brand">
            <div className="navbar-item dropdown is-hoverable brand-text has-text-white">
              <div className="dropdown-trigger">
                User{/*api call their name*/}&nbsp;&nbsp;
                <span className="icon"> <i className="fas fa-caret-down fa-fw"/> </span>
                &nbsp;&nbsp;
              </div>

              <div className="dropdown-menu" id="dropdown-menu4" role="menu">
                <div className="dropdown-content">
                <a className="dropdown-item">
                Settings
                </a>
                <a className="dropdown-item">
                Logout
                </a>
                </div>
              </div>
            </div>

          </div>
        </div>
      </nav>
    )
  }
}

export default NavBar;


