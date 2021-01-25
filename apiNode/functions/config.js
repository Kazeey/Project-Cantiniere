const fetch = require('node-fetch');
const mysql = require('mysql');
const nodemailer = require('nodemailer');

const baseUrl = 'http://127.0.0.1:8080/lunchtime/';

/*
*
*   Vérifie que l'api de Renaud fonctionne.
*   Si elle fonctionne utilise alors ses requêtes, si non on renvoi un message le spécifiant a l'utilisateur.
*
*/
const verification = async () => {
    let reponse;

    await fetch(baseUrl)
    .then(function() 
    {
        reponse = true;
    }).catch(function() 
    {
        reponse = false;
    });

    return reponse;
}

const messageError = "L'API JAVA n'est pas connectée.";
const userError = "Il n'y a pas d'utilisateur qui corresponde.";
const weekNumberError = "Le numéro de menu doit être compris entre 1 et 52";
const menuError = "Aucun menu trouvé";
const actionMessage = "L'action a été effectuée";


// Configuration de la connexion à la base de données.
// Si jamais les requêtes SQL ne fonctionnent pas, voir du côté de flush privileges;
const connexionSQL = mysql.createConnection({
    host: "127.0.0.1",
    user: "root",
    password: "root",
    database: "lunchtime"
});

module.exports = { verification, messageError, connexionSQL, userError, weekNumberError, menuError, actionMessage }

