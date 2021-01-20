/*
*   Middleware de l'application 'Projet Cantinière'.
*   Sert à faire toutes les requêtes nécéssaires et/ou non données par l'api JAVA.
*   Et donc de nous faciliter la vie, en retournant toutes les informations nécéssaire d'un coup, et en limitant la logique dans le front au maximum.
*
*   Pour récupérer les paramètres qui sont passés dans les requêtes post (ex : app.post), veuillez utiliser un req.body.parametre(s).
*/

const express = require('express');
const app = express();
const cors = require('cors');
const bodyParser = require('body-parser');

app.use(cors())

app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: true }))

// Import des fonctions
const loginImport        = require('./functions/login/index.js');
const accountImport      = require('./functions/account/index.js');
const usersImport        = require('./functions/users/index.js');
const mealsImport        = require('./functions/meals/index.js');
const menuImport         = require('./functions/menu/index.js');
const constraintImport   = require('./functions/constraints/index.js');
const notificationImport = require('./functions/notifications/index.js');

// ------ Login ------ 
app.post('/api/login'               , loginImport.data.login);           // login to an existing account
app.get('/api/login/checkEmail'     , loginImport.data.checkEmail);      // Check the format of your email
app.get('/api/login/forgotpassword' , loginImport.data.forgotPassword);  // To send a new password
app.get('/api/login/blockaccount'   , loginImport.data.blockAccount);    // To send a new password
app.get('/api/login/activeaccount'  , loginImport.data.activeAccount);   // To send a new password

// ------ Account ------
app.get('/api/account/create'  , accountImport.data.create);    // If you want to create an account
app.post('/api/account/update' , accountImport.data.update);    // If you want to update informations
app.post('/api/account/delete' , accountImport.data.delete);    // If you want to delete an account

// ------ Users ------
app.get('/api/users/getAllUsers'            , usersImport.data.getAllUsers);            // Get all users from database
app.post('/api/users/getUserById/'          , usersImport.data.getUserById);            // Get a user with a given id
app.post('/api/users/getUserBySearchField' , usersImport.data.getUserBySearchField);   // Get a user with a given id

// ------ Meals ------
app.get('/api/meals/getAllMeals'    , mealsImport.data.getAllMeals);  // Get all meals from database
app.post('/api/meals/getMealById/'  , mealsImport.data.getMealById);  // Get a meals with a given id

// ------ Menu -------
app.post('/api/menu/addMenu'            , menuImport.data.addMenu);            // Add Menu
app.patch('/api/menu/updateMenu'        , menuImport.data.updateMenu);         // Update Menu
app.post('/api/menu/getMenuById'        , menuImport.data.getMenuById);        // Get Menu by Id
app.delete('/api/menu/deleteMenu'       , menuImport.data.deleteMenu);         // delete Menu
app.get('/api/menu/getAllMenus'         , menuImport.data.getAllMenus);        // Get all Menus
app.patch('/api/menu/updateMenuImage'   , menuImport.data.updateMenuImage);    // Update Menu Image
app.get('/api/menu/getMenuImage'        , menuImport.data.getMenuImage);       // Get Menu Image
app.post('/api/menu/getMenuForWeek'     , menuImport.data.getMenuForWeek);     // Get Menu for current week
app.get('/api/menu/getMenuForToday'     , menuImport.data.getMenuForToday);    // Get Menu for current day

// ------ Contraintes ------
app.get('/api/constraints/getAllConstraints'  , constraintImport.data.getAllConstraints);   // Get all the constraints
app.post('/api/constraints/getConstraintById' , constraintImport.data.getConstraintById);   // Get a constraint with a given id
app.put('/api/constraints/addConstraint'      , constraintImport.data.addConstraint);       // Add a constraint to the database
app.post('/api/constraints/updateConstraint'  , constraintImport.data.updateConstraint);    // Update a constraint in the database
app.post('/api/constraints/deleteConstraint'  , constraintImport.data.deleteConstraint);    // Delete a constraint with a given id

// ------ Notifications ------
app.post('/api/notification/submit' , notificationImport.data.submit)

app.listen(3001, function() {
    console.log("connected");
}); 
