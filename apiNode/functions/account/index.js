const fetch = require('node-fetch');
const configImport = require('../config');

const baseUrl = 'http://127.0.0.1:8080/lunchtime/';
const emptyValue = "Non renseigné(e)";

let messageError = configImport.messageError;

methods = {
    create : async function(req, res)
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }
    },

    update : async function(req, res)
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }
        
    }, 

    delete : async function(req, res)
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
