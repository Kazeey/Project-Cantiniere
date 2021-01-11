const fetch = require('node-fetch');
const mysql = require('mysql');
const configImport = require('../config');
const util = require('util');
const callback = require('callback');
const { cpuUsage } = require('process');

const baseUrl = 'http://127.0.0.1:8080/lunchtime/';
const emptyValue = "Non renseigné(e)";

let con = configImport.connexionSQL;
let messageError = configImport.messageError;
let userError = configImport.userError;
let emailError = configImport.emailError;


methods = {
    login : async function(req, res)
    {
        let login = [];
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }
        
        // Récupération des paramètres email et password passés en POST
        emailToFind = req.query.mail; 
        passwordToFind = req.query.password; 

        if(!emailToFind || !passwordToFind)
        {
            res.send("Au moins un des deux paramètres est vide.");
            return false;
        }

        let query = "SELECT * FROM ltuser WHERE email = '"+ emailToFind +"' AND password = '"+ passwordToFind +"';"; // Recherche l'utilisateur pour les données qui correspondent
        con.query(query, function(err, result) {
            if(result[0] != null)
            {
                imageId = result[0].image_id;       
                let queryImg = "SELECT image_64 FROM ltimage WHERE id = "+ imageId +";"; // Recherche l'image qui correspond à l'utilisateur

                con.query(queryImg, function(err, resultImg)
                {
                    res.send({result, resultImg});
                });
            }
            else
                res.send({userError});
        });
    }, 

    checkEmail : async function(req, res)
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        // Récupération du paramètre email passé en POST
        emailToFind = req.query.email; 

        let query = "SELECT * FROM ltuser WHERE email = '"+ emailToFind +"';"; // Recherche l'utilisateur pour les données qui correspondent
        con.query(query, function(err, result) {
            if(result[0] != null)
                res.send("L'adresse mail existe déjà dans la base de données.");
            else
                res.send("L'adresse mail n'existe pas dans la base de données.");
        });
    },

    forgotPassword : async function(req, res)
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        // Récupère les données de l'utilisateur ciblé
        await fetch(baseUrl +  "user/find/" + userIdToFind)
        .then(response => response.json())
        .then(data => {
            if(data.exceptionMessage)
            {
                // Vérifie s'il y a une exception (donc si l'utilisateur n'est pas trouvé)
                isException.push(data.exceptionMessage);
            }
            else
            {
                // Si un utilisateur est trouvé réinitialise le tableau d'exception pour pouvoir réitérer la fonction.
                isException = [];
                // Insère toutes les données dans le tableau déclaré plus haut
                arrayDataUsers.push(data); 
            }
        })
    }
}

exports.data = methods;
