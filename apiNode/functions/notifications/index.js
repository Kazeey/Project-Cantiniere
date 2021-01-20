const fetch = require('node-fetch');
const configImport = require('../config');
const request = require('request');

const baseUrl = 'http://127.0.0.1:8080/lunchtime/';
let messageError = configImport.messageError;
let transporter = configImport.transporter;

methods = {
    submit : async function(req, res) 
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        let title = req.body.title;
        let body = req.body.body;


    }
}

exports.data = methods;