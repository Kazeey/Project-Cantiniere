const fetch = require('node-fetch');
const mysql = require('mysql');
const configImport = require('../config');

const baseUrl = 'http://127.0.0.1:8080/lunchtime/';
const emptyValue = "Non renseigné(e)";

let con = configImport.connexionSQL;
let messageError = configImport.messageError;

methods = {
    login : async function(req, res)
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        con.connect(function(err) 
        {
            if (err) throw "La connexion à la base n'a pas fonctionnée : " + err;
            con.query("SELECT * FROM ltrole", function (err, result, fields) {
                if (err) throw "La requête n'a pas fonctionnée : " + err;
                console.log(result);
            });
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
