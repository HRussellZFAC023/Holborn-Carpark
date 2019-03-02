import React, {Component} from 'react'

/**
 * Component used to display a 404 page in case the user somehow tries to redirect to a non-existing scene
 */
class Missing404 extends Component {

    render() {
        return (
            <div className="hero is-fullheight">
                <div className="hero-body">
                    <div className="container">
                        <h1 className="title has-text-centered">404</h1>
                        <h2 className="subtitle has-text-centered">
                            The page you were looking for was not found... <br/>
                            <a className="card-header-icon" href="/"> Take me home, where I belong, WEST
                                VIRGINIAAAAA</a>
                            <span className="icon"> <i className="fas fa-home"/> </span>
                        </h2>
                    </div>
                </div>
            </div>
        )
    }
}

export default Missing404;





