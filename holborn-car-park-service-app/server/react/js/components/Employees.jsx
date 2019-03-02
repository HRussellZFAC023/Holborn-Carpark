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
        );
    }
}

export default Employees;