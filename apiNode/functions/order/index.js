const fetch = require('node-fetch');
const configImport = require('../config');
const request = require('request');

const baseUrl = 'http://127.0.0.1:8080/lunchtime/';

methods = {
    
    addOrder : async function(req, res) {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        request.put({
            headers: {'content-type' : 'application/json'},
            url: baseUrl + 'order/add',
            body: JSON.stringify(req.body.order)
        }, 
            function(error, response, body) {
                res.send(JSON.stringify(response))
            }
        );
    }
}

exports.data = methods;