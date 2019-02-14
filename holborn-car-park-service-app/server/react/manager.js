import React from 'react'
import ReactDOM from 'react-dom'
import NavBar from "./js/components/NavBar.js";
import Sidebar from "./js/components/SideBar.js";
import Dash from "./js/components/Dash.js"
import Oof404 from "./js/components/Oof404.js"
import Settings from "./js/components/Settings";


class DynamicContent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {scene: 'Dash'};
    this.setScene = this.setScene.bind(this)
  }

  setScene(nScene) {
    this.setState({scene: nScene})
  }

  getScene() {
    switch (this.state.scene) {
      case "Dash":
        return <Dash/>;
      case "Settings":
        return <Settings/>;
      default:
        return <Oof404/>
    }
  }

  render() {
    return (
      <main>
        <NavBar setScene={this.setScene}/>
          <div className="columns">
            <section className="column hero is-fullheight is-3 sidebar">
              <Sidebar setScene={this.setScene}/>
            </section>
            <section className="column">
              {this.getScene()}
            </section>
          </div>
      </main>
    )
  }
}

const dynamicWrapper = document.getElementById("DynamicContent");
dynamicWrapper ? ReactDOM.render(<DynamicContent/>, dynamicWrapper) : false;
