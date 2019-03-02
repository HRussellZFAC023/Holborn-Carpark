import React, { Component } from 'react'

/**
 * Component that renders the user settings page
 */
class Settings extends Component {

    render() {
        return (
            <section className="modal is-active">
                <div className="modal-background"> </div>
                <div className="modal-card">
                    <header className="modal-card-head">
                        <p className="modal-card-title">Settings</p>
                        <button onClick={() => this.props.togglePopup()} className="delete" aria-label="close"> </button>
                    </header>


                    <section className="modal-card-body">
                        <div className="columns">
                            <div className="column ">
                                <div className="tile is-parent">
                                    <article className="tile is-child notification is-info">
                                        <p className="title">Welcome</p>
                                        <p className="subtitle">Your Name</p>
                                        <figure className="image is-4by3">
                                            <img src="https://bulma.io/images/placeholders/640x480.png" />
                                        </figure>
                                    </article>
                                </div>

                                <div className="field">
                                    <label className="label">Message</label>
                                    <div className="control">
                                        <textarea className="textarea" placeholder="Textarea"></textarea>
                                    </div>
                                </div>

                            </div>

                            <div className="column ">
                                <div className="field">
                                    <label className="label">Name</label>
                                    <div className="control">
                                        <input className="input" type="text" placeholder="Text input" defaultValue="yourname" />
                                    </div>
                                </div>

                                <div className="field">
                                    <label className="label">Username</label>
                                    <div className="control">
                                        <input className="input is-success" type="text" placeholder="Text input" defaultValue="username@gmail.com" />
                                    </div>
                                </div>

                                <div className="field">
                                    <label className="label">Email</label>
                                    <div className="control">
                                        <input className="input is-danger" type="email" placeholder="Email input" defaultValue="hello@" />
                                    </div>
                                    <p className="help is-danger">This email is invalid</p>
                                </div>

                                <div className="field">
                                    <label className="label">Date of Birth</label>
                                    <div className="control">
                                        <input type="date" name="birthdate" defaultValue="1985-01-01" />
                                    </div>
                                </div>

                                <div className="field">
                                    <label className="label">Dark Theme</label>
                                    <input type="checkbox" name="dark" />
                                </div>
                                
                            </div>
                        </div>
                    </section>

                    <footer className="modal-card-foot">
                        <div className="field is-grouped">
                            <div className="control">
                                <button className="button is-link">Submit</button>
                            </div>
                            <div className="control">
                                <button onClick={() => this.props.togglePopup()} className="button is-text">Cancel</button>
                            </div>
                        </div>
                    </footer>

                </div>
            </section>

        )
    }
}

export default Settings;


