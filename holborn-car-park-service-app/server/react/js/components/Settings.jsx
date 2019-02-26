import React, { Component } from 'react'

class Settings extends Component {

    render() {
        return (
            <section class="profile-setting columns">

                <div class="column ">
                    <div class="tile is-parent">
                        <article class="tile is-child notification is-info">
                            <p class="title">Welcome</p>
                            <p class="subtitle">Your Name</p>
                            <figure class="image is-4by3">
                                <img src="https://bulma.io/images/placeholders/640x480.png" />
                            </figure>
                        </article>
                    </div>
                </div>

                <div class="column ">
                    <div class="field">
                        <label class="label">Name</label>
                        <div class="control">
                            <input class="input" type="text" placeholder="Text input" value="yourname" />
                        </div>
                    </div>

                    <div class="field">
                        <label class="label">Username</label>
                        <div class="control">
                            <input class="input is-success" type="text" placeholder="Text input" value="username@gmail.com" />
                        </div>
                    </div>

                    <div class="field">
                        <label class="label">Email</label>
                        <div class="control">
                            <input class="input is-danger" type="email" placeholder="Email input" value="hello@" />
                        </div>
                        <p class="help is-danger">This email is invalid</p>
                    </div>

                    <div class="field">
                        <label class="label">Date of Birth</label>
                        <div class="control">
                            <div class="select">
                                <input daterangepicker id="test" type="text" name="birthdate" value="10/24/1984" />
                            </div>
                        </div>
                    </div>

                    <div class="field">
                        <label class="label">Message</label>
                        <div class="control">
                            <textarea class="textarea" placeholder="Textarea"></textarea>
                        </div>
                    </div>

                    <div class="field is-grouped">
                        <div class="control">
                            <button class="button is-link">Submit</button>
                        </div>
                        <div class="control">
                            <button class="button is-text">Cancel</button>
                        </div>
                    </div>

                </div>

            </section>

        )
    }
}

export default Settings;


