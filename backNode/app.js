const express = require('express');
const app = express();
const fetch = require("node-fetch");
const cors = require('cors');
const bodyParser = require('body-parser');
const moment = require('moment');
const { uuid } = require('uuidv4');

app.use(cors())

app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: true }))

// Import des fonctions
const loginImport       = require('./functions/login/index.js');
const accountImport     = require('./functions/account/index.js');

// ------ Login ------ 
app.get('/api/login'           , loginImport.data.login);          // login to an existing account
app.post('/api/login/forgotPassword' , loginImport.data.forgotPassword); // If you have forgot your password

// ------ Account ------
app.post('/api/account/create' , accountImport.data.create);    // If you want to create an account
app.post('/api/account/update' , accountImport.data.update);    // If you want to update your informations
app.post('/api/account/delete' , accountImport.data.delete);    // If you want to delete your account

app.listen(3001, function() {
    console.log("connected");
}); 
