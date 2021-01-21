const fetch = require('node-fetch');
const configImport = require('../config');
const request = require('request');
const { query } = require('express');

const baseUrl = 'http://127.0.0.1:8080/lunchtime/';
const emptyValue = "Non renseigné(e)";

let messageError = configImport.messageError;
let con = configImport.connexionSQL;

methods = {
    create : async function(req, res)
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(configImport.messageError);
            return false; 
        }
    },

    update : async function(req, res)
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(configImport.messageError);
            return false; 
        }

        let id = req.body.id;
        let name = req.body.name;
        let firstname = req.body.firstname;
        let sex = req.body.sex;
        let email = req.body.email;
        let phone = req.body.phone;
        let address = req.body.address;
        let town = req.body.town;
        let postalCode = req.body.postalCode;
        let wallet = req.body.wallet;
        let role = req.body.role;
        let status = req.body.status;
        
        let queryId = "SELECT password, image_id FROM ltuser WHERE id = " + id;
        con.query(queryId, function(err, result) {
            if(result[0] != null)
            {
                if (role == "Lunch Lady")
                {
                    role = true;
                }
                else
                {
                    role = false;
                }
        
                switch(sex)
                {
                    case "Homme" : 
                        sex = 0;
                        break;
                    case "Femme" : 
                        sex = 1;
                        break;
                    default :
                        sex = 2;
                }
                
                let updateUser = "UPDATE `lunchtime`.`ltuser` SET `status` = '"+status+"',`password` = '"+result[0].password+"',`email` = '"+email+"',`sex` = '"+sex+"',`name` = '"+name+"',`firstname` = '"+firstname+"',`wallet` = '"+wallet+"', `phone` = '"+phone+"', `address` = '"+address+"', `postal_code` = '"+postalCode+"', `town` = '"+town+"', `image_id` = '"+result[0].image_id+"' WHERE `id` = '"+id+"';"
                con.query(updateUser, function(err, result){ })
            }
        })

    }, 

    delete : async function(req, res)
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(configImport.messageError);
            return false; 
        }

    }
}

exports.data = methods;
