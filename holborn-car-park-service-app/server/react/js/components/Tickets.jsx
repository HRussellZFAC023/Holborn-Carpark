import React, {Component} from 'react'

class Tickets extends Component {

  render() {
    return (
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
      </table>    )
  }
}

export default Tickets;


