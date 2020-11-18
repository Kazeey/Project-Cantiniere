let fetch = require('node-fetch');
const verificationImport = require('../verifiyConnexion');

const baseUrl = 'http://127.0.0.1:8080/lunchtime/';
let emptyValue = "Non renseigné(e)";

methods = {
    create : async function(req, res)
    {
        let isApiAvalaible = await verificationImport.verification();

        if(!isApiAvalaible)
        {
            res.send(verificationImport.messageError);
            return false; 
        }
    },

    update : async function(req, res)
    {
        let isApiAvalaible = await verificationImport.verification();

        if(!isApiAvalaible)
        {
            res.send(verificationImport.messageError);
            return false; 
        }
        
    }, 

    delete : async function(req, res)
    {
        let isApiAvalaible = await verificationImport.verification();

        if(!isApiAvalaible)
        {
            res.send(verificationImport.messageError);
            return false; 
        }

    }
}

exports.data = methods;
