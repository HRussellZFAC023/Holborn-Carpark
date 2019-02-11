import React from 'react'
import ReactDOM from 'react-dom'
import NavBar from "./js/components/NavBar.js";

const wrapper = document.getElementById("NavBar");
wrapper ? ReactDOM.render(<NavBar/>, wrapper) : false;
