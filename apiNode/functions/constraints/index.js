const fetch = require('node-fetch');
const configImport = require('../config');
const request = require('request');

const baseUrl = 'http://127.0.0.1:8080/lunchtime/';
let messageError = configImport.messageError;

methods = {
    getAllConstraints : async function(req, res) 
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        let arrayConstraint = [];
        let arrayToSend = [];
        // Récupère chaque contraintes de la base
        await fetch(baseUrl + "constraint/findall")
        .then(response => response.json())
        .then(data => {
            // Insère toutes les données dans le tableau déclaré plus haut
            arrayConstraint.push(data); 
        })

        for(let i = 0; i < arrayConstraint[0].length; i++)
        {
            currrentConstraint = arrayConstraint[0][i];
            let constraint = {
                id : currrentConstraint.id,
                orderTimeLimit : currrentConstraint.orderTimeLimit,
                maximumOrderPerDay : currrentConstraint.maximumOrderPerDay,
                rateVAT : currrentConstraint.rateVAT
            };

            arrayToSend.push(constraint);
        }

        res.send(arrayToSend);
    },

    getConstraintById : async function(req, res) 
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }
        
        let arrayConstraint = [];
        let arrayToSend = [];
        let isException = [];

        // Récupération du paramètre userId passe en POST
        constraintIdToFind = req.body.constraintId; 

        // Récupère la contrainte ciblée
        await fetch(baseUrl +  "constraint/find/" + constraintIdToFind)
        .then(response => response.json())
        .then(data => {
            if(data.exceptionMessage)
            {
                // Vérifie s'il y a une exception (donc si la contrainte n'est pas trouvée)
                isException.push(data.exceptionMessage);
            }
            else
            {
                // Si une contrainte est trouvé réinitialise le tableau d'exception pour pouvoir réitérer la fonction.
                isException = [];
                // Insère toutes les données dans le tableau déclaré plus haut
                arrayConstraint.push(data); 
            }
        })

        if(isException.length != 0)
        {
            res.send("Aucune contrainte trouvée pour cet identifiant.");
            return false; 
        }

        constraintToFind = arrayConstraint[0];
        
        for(let i = 0; i < arrayConstraint.length; i++)
        {
            currrentConstraint = arrayConstraint[i];
            let constraint = {
                id : currrentConstraint.id,
                orderTimeLimit : currrentConstraint.orderTimeLimit,
                maximumOrderPerDay : currrentConstraint.maximumOrderPerDay,
                rateVAT : currrentConstraint.rateVAT
            };

            arrayToSend.push(constraint);
        }

        res.send(arrayToSend);
    },

    addConstraint : async function(req, res) 
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        let constraintToAdd = {
            orderTimeLimit : req.body.orderTimeLimit + ":00",
            maximumOrderPerDay : req.body.maximumOrderPerDay,
            rateVAT : req.body.rateVAT
        };

        request.put({
            headers: {'content-type' : 'application/json'},
            url:     baseUrl + 'constraint/add',
            body:    JSON.stringify(constraintToAdd)
        }, function(error, response, body)
        {
            console.log(body);
        });

        res.send(true);
    },

    updateConstraint : async function(req, res) 
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        let constraintId = req.body.constraintId;

        let constraintToUpdate = {
            orderTimeLimit : req.body.orderTimeLimit + ":00",
            maximumOrderPerDay : req.body.maximumOrderPerDay,
            rateVAT : req.body.rateVAT
        };

        request.patch({
            headers: {'content-type' : 'application/json'},
            url:     baseUrl + 'constraint/update/' + constraintId,
            body:    JSON.stringify(constraintToUpdate)
        }, function(error, response, body) 
        {
            console.log(body);
        })

        res.send(true);
    },

    deleteConstraint : async function(req, res) 
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        let constraintId = req.body.constraintId;

        // Supprime la contrainte ciblée
        request.delete({
            headers: {'content-type' : 'application/json'},
            url:     baseUrl + "constraint/delete/" + constraintId,
            body:    JSON.stringify(constraintId)
        }, function(error, response, body) 
        {
            console.log(body);
        })

        res.send(true);
    }
}

exports.data = methods;