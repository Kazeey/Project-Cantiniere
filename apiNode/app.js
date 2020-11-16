/*
*   Middleware de l'application 'Projet Cantinière'.
*   Sert à faire toutes les requêtes nécéssaires et/ou non données par l'api JAVA.
*   Et donc de nous faciliter la vie, en retournant toutes les informations nécéssaire d'un coup, et en limitant la logique dans le front au maximum.
*
*   Pour récupérer les paramètres qui sont passés dans les requêtes post (ex : app.post), veuillez utiliser un req.query.parametre(s).
*
*/
const express = require('express');
const app = express();
const cors = require('cors');
const bodyParser = require('body-parser');

app.use(cors())

app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: true }))

// Import des fonctions
const loginImport       = require('./functions/login/index.js');
const accountImport     = require('./functions/account/index.js');
const usersImport       = require('./functions/users/index.js');

// ------ Login ------ 
app.get('/api/login'                , loginImport.data.login);           // login to an existing account
app.get('/api/login/checkEmail'     , loginImport.data.checkEmail);      // Check the format of your email
app.get('/api/login/forgotPassword' , loginImport.data.forgotPassword);  // If you have forgot your password

// ------ Account ------
app.post('/api/account/create' , accountImport.data.create);    // If you want to create an account
app.post('/api/account/update' , accountImport.data.update);    // If you want to update informations
app.post('/api/account/delete' , accountImport.data.delete);    // If you want to delete an account

// ------ Users ------
app.get('/api/users/getAllUsers'    , usersImport.data.getAllUsers);  // Get all users from database
app.post('/api/users/getUsersById/' , usersImport.data.getUserById);  // Get a user with a giver id

app.listen(3001, function() {
    console.log("connected");
}); 
