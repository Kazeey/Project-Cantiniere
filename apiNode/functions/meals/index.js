const fetch = require('node-fetch');
const configImport = require('../config');

const baseUrl = 'http://127.0.0.1:8080/lunchtime/';
let messageError = configImport.messageError;

methods = {
    getAllMeals : async function(req, res) 
    {
        
    },

    getMealById : async function(req, res) 
    {
        
    }
}

exports.data = methods;