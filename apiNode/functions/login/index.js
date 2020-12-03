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
        emailToFind = req.query.email; 
        passwordToFind = req.query.password; 

        if(!emailToFind || !passwordToFind)
        {
            res.send("Au moins un des deux paramètres est vide.");
            return false;
        }
      
        let query = "SELECT * FROM ltuser WHERE email = '"+ emailToFind +"' AND password='"+ passwordToFind +"';"; // Recherche l'utilisateur pour les données qui correspondent
        con.query(query, function(err, result) {
            if(result[0] != null)
                res.send(result);
            else
                res.send(userError);
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
    }
}

exports.data = methods;
