import React, {Component} from 'react'
import ReactDOM from 'react-dom'


class NavBar extends Component {

  render() {
    return(
      <nav className="navbar" role="navigation" aria-label="main navigation">
        <NavItem></NavItem>
        <NavItem></NavItem>
        <NavItem></NavItem>
      </div>
    )
  }
}

ReactDOM.render(<NavBar/>, document.getElementById('NavBar'));
