import React, { Component } from 'react'

class Settings extends Component {

    render() {
        return (
            <section className="profile-setting columns">

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
                </div>

                <div className="column ">
                    <div className="field">
                        <label className="label">Name</label>
                        <div className="control">
                            <input className="input" type="text" placeholder="Text input" value="yourname" />
                        </div>
                    </div>

                    <div className="field">
                        <label className="label">Username</label>
                        <div className="control">
                            <input className="input is-success" type="text" placeholder="Text input" value="username@gmail.com" />
                        </div>
                    </div>

                    <div className="field">
                        <label className="label">Email</label>
                        <div className="control">
                            <input className="input is-danger" type="email" placeholder="Email input" value="hello@" />
                        </div>
                        <p className="help is-danger">This email is invalid</p>
                    </div>

                    <div className="field">
                        <label className="label">Date of Birth</label>
                        <div className="control">
                            <div className="select">
                                <input daterangepicker id="test" type="text" name="birthdate" value="10/24/1984" />
                            </div>
                        </div>
                    </div>

                    <div className="field">
                        <label className="label">Message</label>
                        <div className="control">
                            <textarea className="textarea" placeholder="Textarea"></textarea>
                        </div>
                    </div>

                    <div className="field is-grouped">
                        <div className="control">
                            <button className="button is-link">Submit</button>
                        </div>
                        <div className="control">
                            <button className="button is-text">Cancel</button>
                        </div>
                    </div>

                </div>

            </section>

        )
    }
}

export default Settings;


