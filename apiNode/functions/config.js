const fetch = require('node-fetch');
const mysql = require('mysql');

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

// Configuration de la connexion à la base de données.
const connexionSQL = mysql.createConnection({
    host: "localhost",
    user: "root",
    password: "password",
    database: "lunchtime"
});

module.exports = { verification, messageError, connexionSQL, userError }
