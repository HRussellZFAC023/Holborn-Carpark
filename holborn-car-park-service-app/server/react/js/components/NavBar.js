import React, {Component} from 'react'

class NavBar extends Component {

  render() {
    return (
      <section className="hero gradient">
        <div className="hero-body">
          <nav className="navbar" role="navigation" aria-label="main navigation">
            <div>
              <a className="navbar-brand">
                {/*<img className="grow-img" src={"/img/logo_bg_white@4x.png"} alt={"logo"}/>*/}
                <h1 className="title is-1 brand-text has-text-white">
                  <b>HOLBORN</b>
                </h1>
              </a>
            </div>
            <div className="navbar-end ">
              <div className="navbar-brand">
                <div className="navbar-item dropdown is-hoverable">
                  <div className="dropdown-trigger ">
                    <h2 className="brand-text has-text-white subtitle">
                      User
                      &nbsp;&nbsp;
                      <span className="icon"> <i className="fas fa-caret-down fa-fw"/> </span>
                      &nbsp;&nbsp;
                    </h2>
                  </div>

                  <div className="dropdown-menu" id="dropdown-menu4" role="menu">
                    <div className="dropdown-content">
                      <a onClick={() => this.props.setScene("Settings")} className="dropdown-item">
                        Settings
                      </a>
                      <a onClick={() => console.log("Logout")} className="dropdown-item">
                        Logout
                      </a>
                    </div>
                  </div>
                </div>

              </div>
            </div>
          </nav>
        </div>
      </section>
    )
  }

}

export default NavBar;


