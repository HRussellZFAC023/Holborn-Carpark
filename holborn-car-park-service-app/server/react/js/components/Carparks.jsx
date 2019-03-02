import React, {Component} from 'react'

/**
 * Component used to display all existing car parks and functions related to them
 */
class Carparks extends Component {


    render() {
        return (
            <main>
                <section className="hero is-small is-info gradient">
                    <div className="hero-body">
                        <div className="container">
                            <h1 className="title">
                                Car parks
                            </h1>
                            <h2 className="subtitle">
                                Here you can view all available car parks
                            </h2>
                        </div>
                    </div>
                </section>

                <table className="table is-bordered is-striped is-narrow is-hoverable is-fullwidth">
                    <thead>
                    <tr>
                        <th>One</th>
                        <th>Two</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>Three</td>
                        <td>Four</td>
                    </tr>
                    <tr>
                        <td>Five</td>
                        <td>Six</td>
                    </tr>
                    <tr>
                        <td>Seven</td>
                        <td>Eight</td>
                    </tr>
                    <tr>
                        <td>Nine</td>
                        <td>Ten</td>
                    </tr>
                    <tr>
                        <td>Eleven</td>
                        <td>Twelve</td>
                    </tr>
                    </tbody>
                </table>
            </main>
        )
    }
}

export default Carparks;


