import React, { Component } from 'react';

/**
 * Component used to display all existing users and functions related to them
 */
class Employees extends Component {
    constructor(props) {
        super(props);

        this.state = {
        
        };

    }

    render() {
        return (
            <main>
                <section className="hero is-small is-info gradient">
                    <div className="hero-body">
                        <div className="container">
                            <h1 className="title">
                                Employees
                            </h1>
                            <h2 className="subtitle">
                                Here you can view and manage all current employees
                            </h2>
                        </div>
                    </div>
                </section>

                <table className="table is-bordered is-striped is-narrow is-hoverable is-fullwidth">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Email</th>
                    <th>DoB</th>
                    <th>Role</th>
                </tr>
                </thead>
                </table>
            </main>
        );
    }
}

export default Employees;