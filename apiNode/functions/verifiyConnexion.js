let fetch = require('node-fetch');

const baseUrl = 'http://127.0.0.1:8080/lunchtime/';

/*
*
*   Vérifie que l'api de Renaud fonctionne
*   Si elle fonctionne utilise alors ses requêtes, sinon utilise les requêtes de l'api Node.
*   (Car les données sont dans les deux cas retournés dans le même format)
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

module.exports = { verification }
